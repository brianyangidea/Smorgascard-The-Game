package ca.uwaterloo.cook_sharp.data.repository

import android.util.Log
import ca.uwaterloo.cook_sharp.data.recipe_api.APIRecipeRepository
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.CreateNutritionInfoInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ca.uwaterloo.cook_sharp.domain.CreateIngredientInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInstructionInput
import ca.uwaterloo.cook_sharp.domain.RecipeSource

/**
 * Functionalities:
 * - Load recipes from Supabase fall back to the recipe API when Supabase results are empty
 * - update likes and user created recipes to the Supabase repository
 * - Estimate nutrition for user created recipes
 * - Save recipes through the Supabase repository
 */
class CombinedRecipeRepository(
    private val userRepo: UserRepository = SupabaseUserRepository,
    private val supabaseRepo: RecipeRepository = SupabaseRecipeRepository(userRepo),
    private val apiRepo: APIRecipeRepository = APIRecipeRepository()
) : RecipeRepository {

    override fun getAllRecipes(limit: Int, offset: Int): List<Recipe> {
        val supabaseRecipes = runCatching { supabaseRepo.getAllRecipes(limit, offset) }
            .onFailure { Log.e("RECIPE_FALLBACK", "Supabase getAllRecipes failed", it) }
            .getOrElse { emptyList() }

        if (supabaseRecipes.isNotEmpty()) {
            Log.d("RECIPE_FALLBACK", "Using ${supabaseRecipes.size} Supabase recipes")
            return supabaseRecipes
        }

        val apiRecipes = runCatching { apiRepo.getDefaultRecipes(limit) }
            .onFailure { Log.e("RECIPE_FALLBACK", "API fallback getAllRecipes failed", it) }
            .getOrElse { emptyList() }

        if (apiRecipes.isEmpty()) {
            Log.d("RECIPE_FALLBACK", "Supabase empty and API fallback returned 0 recipes")
            return emptyList()
        }

        val persistedRecipes = apiRecipes.mapNotNull { apiRecipe ->
            persistApiRecipeIfNeeded(apiRecipe)
        }

        Log.d(
            "RECIPE_FALLBACK",
            "Supabase empty, persisted ${persistedRecipes.size} API fallback recipe(s)"
        )
        return persistedRecipes
    }

    override fun getRecipeById(id: Long): Recipe? {
        val supabaseRecipe = runCatching { supabaseRepo.getRecipeById(id) }
            .onFailure { Log.e("RECIPE_FALLBACK", "Supabase getRecipeById failed", it) }
            .getOrNull()

        if (supabaseRecipe != null) return supabaseRecipe

        val apiRecipe = runCatching { apiRepo.getRecipeById(id) }
            .onFailure { Log.e("RECIPE_FALLBACK", "API fallback getRecipeById failed", it) }
            .getOrNull()

        if (apiRecipe == null) return null

        return persistApiRecipeIfNeeded(apiRecipe)
    }

    override fun toggleLike(id: Long): List<Recipe> {
        return supabaseRepo.toggleLike(id)
    }

    override fun getLikedRecipes(): List<Recipe> {
        return supabaseRepo.getLikedRecipes()
    }

    override fun searchRecipes(query: String): List<Recipe> {
        if (query.isBlank()) return getAllRecipes()

        val supabaseResults = runCatching { supabaseRepo.searchRecipes(query) }
            .onFailure { Log.e("RECIPE_FALLBACK", "Supabase search failed", it) }
            .getOrElse { emptyList() }

        if (supabaseResults.isNotEmpty()) {
            Log.d("RECIPE_FALLBACK", "Using ${supabaseResults.size} Supabase search results")
            return supabaseResults
        }

        val apiResults = runCatching { apiRepo.searchRecipes(query, number = 1) }
            .onFailure { Log.e("RECIPE_FALLBACK", "API fallback search failed", it) }
            .getOrElse { emptyList() }

        if (apiResults.isEmpty()) {
            Log.d("RECIPE_FALLBACK", "Supabase search empty and API fallback returned 0 recipes")
            return emptyList()
        }

        val persistedRecipes = apiResults.mapNotNull { apiRecipe ->
            persistApiRecipeIfNeeded(apiRecipe)
        }

        Log.d(
            "RECIPE_FALLBACK",
            "Supabase search empty, persisted ${persistedRecipes.size} API fallback recipe(s)"
        )
        return persistedRecipes
    }

    override fun getRecipesByMealType(mealType: MealType): List<Recipe> {
        val supabaseResults = runCatching { supabaseRepo.getRecipesByMealType(mealType) }
            .onFailure { Log.e("RECIPE_FALLBACK", "Supabase meal type failed", it) }
            .getOrElse { emptyList() }

        if (supabaseResults.isNotEmpty()) {
            return supabaseResults
        }

        val fallbackFilter = FilterRecipe(selectedMealTypes = setOf(mealType))

        val apiResults = runCatching { apiRepo.getFilteredRecipes(fallbackFilter, numberPerRequest = 1) }
            .onFailure { Log.e("RECIPE_FALLBACK", "API fallback meal type failed", it) }
            .getOrElse { emptyList() }

        return apiResults.mapNotNull { apiRecipe ->
            persistApiRecipeIfNeeded(apiRecipe)
        }
    }

    override fun getFilteredRecipes(filter: FilterRecipe): List<Recipe> {
        val supabaseResults = runCatching { supabaseRepo.getFilteredRecipes(filter) }
            .onFailure { Log.e("RECIPE_FALLBACK", "Supabase filter failed", it) }
            .getOrElse { emptyList() }

        if (supabaseResults.isNotEmpty()) {
            Log.d("RECIPE_FALLBACK", "Using ${supabaseResults.size} Supabase filtered results")
            return supabaseResults
        }

        val apiResults = runCatching { apiRepo.getFilteredRecipes(filter, numberPerRequest = 1) }
            .onFailure { Log.e("RECIPE_FALLBACK", "API fallback filter failed", it) }
            .getOrElse { emptyList() }

        if (apiResults.isEmpty()) {
            Log.d("RECIPE_FALLBACK", "Supabase filter empty and API fallback returned 0 recipes")
            return emptyList()
        }

        val persistedRecipes = apiResults.mapNotNull { apiRecipe ->
            persistApiRecipeIfNeeded(apiRecipe)
        }

        Log.d(
            "RECIPE_FALLBACK",
            "Supabase filter empty, persisted ${persistedRecipes.size} API fallback recipe(s)"
        )
        return persistedRecipes
    }

    private fun persistApiRecipeIfNeeded(recipe: Recipe): Recipe? {
        val existingById = runCatching {
            supabaseRepo.getRecipeById(recipe.id)
        }.getOrNull()

        if (existingById != null) {
            return existingById
        }

        return runCatching {
            supabaseRepo.addRecipe(recipe.toCreateRecipeInput())
        }.onFailure {
            Log.e("RECIPE_FALLBACK", "Failed to persist API recipe '${recipe.title}'", it)
        }.getOrNull()
    }

    private fun Recipe.toCreateRecipeInput(): CreateRecipeInput {
        return CreateRecipeInput(
            createdByUserId = null,
            title = title,
            readyInMinutes = readyInMinutes,
            difficulty = difficulty,
            image = image,
            servings = servings,
            cuisineType = cuisineType,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            isGlutenFree = isGlutenFree,
            isDairyFree = isDairyFree,
            isLowFodmap = isLowFodmap,
            isPescatarian = isPescatarian,
            isKetogenic = isKetogenic,
            isPaleo = isPaleo,
            isWhole30 = isWhole30,
            source = RecipeSource.API,
            localId = localId.ifBlank { "api-$id" },
            remoteId = remoteId ?: id,
            nutritionInfo = nutritionInfo?.let {
                CreateNutritionInfoInput(
                    calories = it.calories,
                    protein = it.protein,
                    carbs = it.carbs,
                    fat = it.fat,
                    fiber = it.fiber,
                    sugar = it.sugar
                )
            },
            ingredients = ingredients.map { ingredient ->
                CreateIngredientInput(
                    name = ingredient.name,
                    amount = ingredient.amount,
                    unit = ingredient.unit
                )
            },
            instructions = instructions.map { instruction ->
                CreateRecipeInstructionInput(
                    stepNumber = instruction.stepNumber,
                    instruction = instruction.instruction
                )
            },
            mealTypes = mealTypes
        )
    }

    override fun getUserRecipes(): List<Recipe> {
        return supabaseRepo.getUserRecipes()
    }

    override fun addRecipe(input: CreateRecipeInput): Recipe {
        val estimatedNutrition: CreateNutritionInfoInput? =
            if (
                input.source == ca.uwaterloo.cook_sharp.domain.RecipeSource.USER &&
                input.nutritionInfo == null &&
                input.title.isNotBlank()
            ) {
                runBlocking(Dispatchers.IO) {
                    runCatching {
                        apiRepo.estimateNutritionByDishName(input.title)
                    }.onFailure {
                        Log.e("RECIPE_SAVE", "Nutrition estimation failed for '${input.title}'", it)
                    }.getOrNull()
                }
            } else {
                input.nutritionInfo
            }

        val enrichedInput = input.copy(
            nutritionInfo = estimatedNutrition
        )

        return runCatching {
            supabaseRepo.addRecipe(enrichedInput)
        }.onFailure {
            Log.e("RECIPE_SAVE", "Supabase addRecipe failed for '${input.title}'", it)
        }.getOrThrow()
    }
}