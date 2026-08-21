package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.data.supabase.LikedRecipesManager
import ca.uwaterloo.cook_sharp.data.supabase.*
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.Recipe
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Functionalities:
 * - Load recipes from Supabase
 * - Fetch a recipe by ID
 * - Search and filter recipes
 * - Load recipes by meal type
 * - Return user created and liked recipes
 * - Toggle recipe like state for the current user
 */
class SupabaseRecipeRepository(
    private val userRepo: UserRepository = SupabaseUserRepository
) : RecipeRepository {

    private val client = SupabaseClientProvider.client

    private suspend fun buildRecipesBatch(
        recipeRows: List<SupabaseRecipe>,
        likedIds: Set<Long>
    ): List<Recipe> {
        if (recipeRows.isEmpty()) return emptyList()

        val recipeIds = recipeRows.map { it.id }

        val nutritionByRecipeId = client.from("nutrition_info")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseNutritionInfoRow>()
            .associateBy { it.recipe_id }

        val ingredientsByRecipeId = client.from("ingredients")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseIngredient>()
            .groupBy { it.recipe_id }

        val instructionsByRecipeId = client.from("recipe_instructions")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseRecipeInstruction>()
            .groupBy { it.recipe_id }

        val mealTypesByRecipeId = client.from("recipe_meal_types")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseRecipeMealType>()
            .groupBy { it.recipe_id }

        return recipeRows.map { row ->
            row.toDomain(
                nutritionRow = nutritionByRecipeId[row.id],
                ingredientRows = ingredientsByRecipeId[row.id].orEmpty(),
                instructionRows = instructionsByRecipeId[row.id].orEmpty(),
                mealTypeRows = mealTypesByRecipeId[row.id].orEmpty(),
                isLiked = row.id in likedIds
            )
        }
    }

    private suspend fun filterVisibleRecipes(
        recipeRows: List<SupabaseRecipe>,
        currentUserId: String?
    ): List<SupabaseRecipe> {
        val visibleRows = mutableListOf<SupabaseRecipe>()
        for (row in recipeRows) {
            if (isVisibleToCurrentUser(row, currentUserId)) {
                visibleRows.add(row)
            }
        }
        return visibleRows
    }

    override fun getAllRecipes(limit: Int, offset: Int): List<Recipe> = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id?.trim()

        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)

        val allRows = client.from("recipes")
            .select {
                order(column = "id", order = Order.ASCENDING)
            }
            .decodeList<SupabaseRecipe>()

        val allVisibleRows = filterVisibleRecipes(allRows, currentUserId)
        val pagedRows = allVisibleRows.drop(offset).take(limit)

        buildRecipesBatch(pagedRows, likedIds)
    }

    override fun getRecipeById(id: Long): Recipe? = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id?.trim()

        val row = client.from("recipes")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeList<SupabaseRecipe>()
            .firstOrNull()
            ?: return@runBlocking null

        if (!isVisibleToCurrentUser(row, currentUserId)) {
            return@runBlocking null
        }

        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)
        buildRecipe(row, likedIds)
    }

    override fun toggleLike(id: Long): List<Recipe> = runBlocking {
        val currentUser = userRepo.getCurrentUser()
        val currentUserId = currentUser?.id?.trim() ?: run {
            return@runBlocking emptyList()
        }

        val allRows = client.from("user_liked_recipes")
            .select()
            .decodeList<SupabaseUserLikedRecipe>()

        val matchingRows = allRows.filter {
            it.user_id.trim() == currentUserId && it.recipe_id == id
        }

        val existing = matchingRows.firstOrNull()

        try {
            if (existing == null) {
                client.from("user_liked_recipes").insert(
                    SupabaseUserLikedRecipe(user_id = currentUserId, recipe_id = id)
                )
            } else {
                client.from("user_liked_recipes").delete {
                    filter {
                        eq("user_id", currentUserId)
                        eq("recipe_id", id)
                    }
                }
            }

            val refreshedLikedIds = getLikedRecipeIdSet()
            LikedRecipesManager.setLikedRecipeIds(refreshedLikedIds)
        } catch (e: Exception) {
            throw e
        }

        emptyList()
    }

    override fun getLikedRecipes(): List<Recipe> = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id?.trim()
        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)

        if (likedIds.isEmpty()) return@runBlocking emptyList()

        val recipeRows = client.from("recipes")
            .select {
                filter {
                    isIn("id", likedIds.toList())
                }
            }
            .decodeList<SupabaseRecipe>()

        val visibleRows = filterVisibleRecipes(recipeRows, currentUserId)
        buildRecipesBatch(visibleRows, likedIds)
    }

    override fun searchRecipes(query: String): List<Recipe> = runBlocking {
        if (query.isBlank()) return@runBlocking getAllRecipes()

        val currentUserId = userRepo.getCurrentUser()?.id?.trim()

        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)

        val recipeRows = client.from("recipes")
            .select {
                filter {
                    ilike("title", "%$query%")
                }
            }
            .decodeList<SupabaseRecipe>()

        val visibleRows = filterVisibleRecipes(recipeRows, currentUserId)
        buildRecipesBatch(visibleRows, likedIds)
    }

    override fun getRecipesByMealType(mealType: MealType): List<Recipe> = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id?.trim()

        val links = client.from("recipe_meal_types")
            .select {
                filter {
                    eq("meal_type", mealType.name)
                }
            }
            .decodeList<SupabaseRecipeMealType>()

        if (links.isEmpty()) return@runBlocking emptyList()

        val recipeIds = links.map { it.recipe_id }.toSet()
        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)

        val recipeRows = client.from("recipes")
            .select {
                filter {
                    isIn("id", recipeIds.toList())
                }
            }
            .decodeList<SupabaseRecipe>()

        val visibleRows = filterVisibleRecipes(recipeRows, currentUserId)
        buildRecipesBatch(visibleRows, likedIds)
    }

    private fun Set<Long>?.intersectOrInitialize(other: Set<Long>): Set<Long> {
        return when (this) {
            null -> other
            else -> this intersect other
        }
    }

    private fun SupabaseRecipe.matchesAnySelectedDiet(selectedDiets: Set<String>): Boolean {
        return selectedDiets.any { diet ->
            when (diet) {
                "No Dietary restriction" -> true
                "Vegetarian" -> is_vegetarian
                "Vegan" -> is_vegan
                "Pescatarian" -> is_pescatarian
                "Gluten Free", "Gluten-Free" -> is_gluten_free
                "Lacto-Vegetarian" -> is_vegetarian && !is_dairy_free
                "Ovo-Vegetarian" -> is_vegetarian && is_dairy_free
                "Ketogenic" -> is_ketogenic
                "Paleo" -> is_paleo
                "Primal" -> is_paleo
                "Low FODMAP" -> is_low_fodmap
                "Whole30" -> is_whole30
                else -> false
            }
        }
    }

    override fun getFilteredRecipes(filter: FilterRecipe): List<Recipe> = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id?.trim()
        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)

        var candidateIds: Set<Long>? = null

        if (filter.selectedMealTypes.isNotEmpty()) {
            val mealTypeIds = client.from("recipe_meal_types")
                .select {
                    filter {
                        isIn("meal_type", filter.selectedMealTypes.map { it.name })
                    }
                }
                .decodeList<SupabaseRecipeMealType>()
                .map { it.recipe_id }
                .toSet()

            candidateIds = mealTypeIds
        }

        val recipeRowsForDietAndCuisine = client.from("recipes")
            .select()
            .decodeList<SupabaseRecipe>()

        if (filter.selectedCuisines.isNotEmpty()) {
            val cuisineIds = recipeRowsForDietAndCuisine
                .filter { row ->
                    row.cuisine_type != null &&
                            filter.selectedCuisines.any { selected ->
                                selected.equals(row.cuisine_type, ignoreCase = true)
                            }
                }
                .map { it.id }
                .toSet()

            candidateIds = candidateIds.intersectOrInitialize(cuisineIds)
        }

        if (filter.selectedDiets.isNotEmpty()) {
            val dietIds = recipeRowsForDietAndCuisine
                .filter { row -> row.matchesAnySelectedDiet(filter.selectedDiets) }
                .map { it.id }
                .toSet()

            candidateIds = candidateIds.intersectOrInitialize(dietIds)
        }

        if (filter.excludedIngredients.isNotEmpty()) {
            val excludedIngredientIds = client.from("ingredients")
                .select()
                .decodeList<SupabaseIngredient>()
                .filter { ingredient ->
                    filter.excludedIngredients.any { excluded ->
                        ingredient.name.contains(excluded, ignoreCase = true)
                    }
                }
                .map { it.recipe_id }
                .toSet()

            val remainingIds = when (candidateIds) {
                null -> recipeRowsForDietAndCuisine
                    .map { it.id }
                    .toSet()
                    .minus(excludedIngredientIds)

                else -> candidateIds.minus(excludedIngredientIds)
            }

            candidateIds = remainingIds
        }

        val finalRecipeRows = when {
            candidateIds == null -> recipeRowsForDietAndCuisine
            candidateIds.isEmpty() -> emptyList()
            else -> {
                client.from("recipes")
                    .select {
                        filter {
                            isIn("id", candidateIds.toList())
                        }
                    }
                    .decodeList<SupabaseRecipe>()
            }
        }

        val builtRecipes = buildRecipesBatch(finalRecipeRows, likedIds)

        builtRecipes.filter { recipe ->
            val hasOrFilters =
                filter.selectedMealTypes.isNotEmpty() ||
                        filter.selectedDiets.isNotEmpty() ||
                        filter.selectedCuisines.isNotEmpty()

            val matchesMealType =
                filter.selectedMealTypes.isNotEmpty() &&
                        recipe.mealTypes.any { it in filter.selectedMealTypes }

            val matchesDiets =
                filter.selectedDiets.isNotEmpty() &&
                        filter.selectedDiets.any { recipe.containsDietaryRestriction(it) }

            val matchesCuisine =
                filter.selectedCuisines.isNotEmpty() &&
                        (recipe.cuisineType != null &&
                                filter.selectedCuisines.any { selected ->
                                    selected.equals(recipe.cuisineType, ignoreCase = true)
                                })

            val matchesOrGroup =
                !hasOrFilters ||
                        matchesMealType ||
                        matchesDiets ||
                        matchesCuisine

            val matchesCalories =
                recipe.calories >= filter.minCalories &&
                        recipe.calories <= filter.maxCalories

            val matchesExcludedIngredients =
                filter.excludedIngredients.isEmpty() ||
                        filter.excludedIngredients.none { excluded ->
                            recipe.ingredients.any { ingredient ->
                                ingredient.name.contains(excluded, ignoreCase = true)
                            }
                        }

            matchesOrGroup &&
                    matchesCalories &&
                    matchesExcludedIngredients
        }
    }


    override fun getUserRecipes(): List<Recipe> = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id ?: return@runBlocking emptyList()

        val likedIds = getLikedRecipeIdSet()
        LikedRecipesManager.setLikedRecipeIds(likedIds)

        val recipeRows = client.from("recipes")
            .select {
                filter {
                    eq("created_by_user_id", currentUserId)
                }
            }
            .decodeList<SupabaseRecipe>()

        buildRecipesBatch(recipeRows, likedIds)
    }

    override fun addRecipe(input: CreateRecipeInput): Recipe = runBlocking {
        val newLocalId =
            if (input.localId.isBlank()) UUID.randomUUID().toString() else input.localId

        val insertedRecipe = client.from("recipes")
            .insert(
                SupabaseRecipeInsert(
                    created_by_user_id = input.createdByUserId,
                    title = input.title,
                    ready_in_minutes = input.readyInMinutes,
                    difficulty = input.difficulty,
                    image = input.image,
                    servings = input.servings,
                    cuisine_type = input.cuisineType,
                    is_vegetarian = input.isVegetarian,
                    is_vegan = input.isVegan,
                    is_gluten_free = input.isGlutenFree,
                    is_dairy_free = input.isDairyFree,
                    is_low_fodmap = input.isLowFodmap,
                    is_pescatarian = input.isPescatarian,
                    is_ketogenic = input.isKetogenic,
                    is_paleo = input.isPaleo,
                    is_whole30 = input.isWhole30,
                    source = input.source.name,
                    local_id = newLocalId,
                    remote_id = input.remoteId
                )
            ) {
                select()
            }
            .decodeSingle<SupabaseRecipe>()

        val recipeId = insertedRecipe.id

        val nutrition = input.nutritionInfo
        val shouldInsertNutrition =
            nutrition != null &&
                    (
                            nutrition.calories != 0.0 ||
                                    nutrition.protein != 0.0 ||
                                    nutrition.carbs != 0.0 ||
                                    nutrition.fat != 0.0 ||
                                    nutrition.fiber != 0.0 ||
                                    nutrition.sugar != 0.0 ||
                                    nutrition.sodium != 0.0 ||
                                    nutrition.saturatedFat != null ||
                                    nutrition.cholesterol != null ||
                                    nutrition.potassium != null
                            )

        if (shouldInsertNutrition) {
            runCatching {
                client.from("nutrition_info")
                    .insert(
                        SupabaseNutritionInfoInsert(
                            recipe_id = recipeId,
                            calories = nutrition!!.calories,
                            protein = nutrition.protein,
                            carbs = nutrition.carbs,
                            fat = nutrition.fat,
                            fiber = nutrition.fiber,
                            sugar = nutrition.sugar,
                            sodium = nutrition.sodium,
                            saturated_fat = nutrition.saturatedFat,
                            cholesterol = nutrition.cholesterol,
                            potassium = nutrition.potassium
                        )
                    )
            }.onFailure {
                android.util.Log.e(
                    "RECIPE_SAVE",
                    "Nutrition insert failed for recipeId=$recipeId title='${input.title}'",
                    it
                )
            }
        }

        input.ingredients.forEach { ingredient ->
            client.from("ingredients")
                .insert(
                    SupabaseIngredientInsert(
                        recipe_id = recipeId,
                        name = ingredient.name,
                        amount = ingredient.amount,
                        unit = ingredient.unit,
                        original_name = ingredient.originalName
                    )
                )
        }

        input.instructions.forEach { instruction ->
            client.from("recipe_instructions")
                .insert(
                    SupabaseRecipeInstructionInsert(
                        recipe_id = recipeId,
                        step_number = instruction.stepNumber,
                        instruction = instruction.instruction
                    )
                )
        }

        input.mealTypes.forEach { mealType ->
            client.from("recipe_meal_types")
                .insert(
                    SupabaseRecipeMealType(
                        recipe_id = recipeId,
                        meal_type = mealType.name
                    )
                )
        }

        return@runBlocking insertedRecipe.toDomain(
            nutritionRow = null,
            ingredientRows = emptyList(),
            instructionRows = emptyList(),
            mealTypeRows = emptyList(),
            isLiked = false
        )
    }

    private suspend fun buildRecipe(
        row: SupabaseRecipe,
        likedIds: Set<Long> = emptySet()
    ): Recipe {
        val recipeId = row.id

        val nutritionRow = client.from("nutrition_info")
            .select {
                filter {
                    eq("recipe_id", recipeId)
                }
            }
            .decodeList<SupabaseNutritionInfoRow>()
            .firstOrNull()

        val ingredientRows = client.from("ingredients")
            .select {
                filter {
                    eq("recipe_id", recipeId)
                }
            }
            .decodeList<SupabaseIngredient>()

        val instructionRows = client.from("recipe_instructions")
            .select {
                filter {
                    eq("recipe_id", recipeId)
                }
            }
            .decodeList<SupabaseRecipeInstruction>()

        val mealTypeRows = client.from("recipe_meal_types")
            .select {
                filter {
                    eq("recipe_id", recipeId)
                }
            }
            .decodeList<SupabaseRecipeMealType>()

        return row.toDomain(
            nutritionRow = nutritionRow,
            ingredientRows = ingredientRows,
            instructionRows = instructionRows,
            mealTypeRows = mealTypeRows,
            isLiked = row.id in likedIds
        )
    }

    private suspend fun getLikedRecipeIdSet(): Set<Long> {
        val currentUser = userRepo.getCurrentUser()

        if (currentUser == null) {
            return emptySet()
        }

        val currentUserId = currentUser.id

        val result = client.from("user_liked_recipes")
            .select()
            .decodeList<SupabaseUserLikedRecipe>()

        return result.filter { it.user_id == currentUserId }
            .map { it.recipe_id }
            .toSet()
    }

    private suspend fun isSharedWithCurrentUser(
        recipeId: Long,
        currentUserId: String?
    ): Boolean {
        if (currentUserId.isNullOrBlank()) return false

        return runCatching {
            client.from("shared_recipes")
                .select {
                    filter {
                        eq("recipe_id", recipeId)
                        eq("recipient_user_id", currentUserId)
                    }
                }
                .decodeList<SupabaseSharedRecipe>()
                .isNotEmpty()
        }.getOrDefault(false)
    }

    private suspend fun isVisibleToCurrentUser(
        row: SupabaseRecipe,
        currentUserId: String?
    ): Boolean {
        val creatorId = row.created_by_user_id?.trim()

        val isPublic = creatorId == null
        val isOwner = creatorId == currentUserId
        val isShared = isSharedWithCurrentUser(row.id, currentUserId)

        return isPublic || isOwner || isShared
    }
}