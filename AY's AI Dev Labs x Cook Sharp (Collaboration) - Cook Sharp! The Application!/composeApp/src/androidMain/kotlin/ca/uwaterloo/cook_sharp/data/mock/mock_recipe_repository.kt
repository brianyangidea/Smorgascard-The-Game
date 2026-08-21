package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.Ingredient
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.NutritionInfo
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.RecipeInstruction
import ca.uwaterloo.cook_sharp.domain.RecipeSource

class MockRecipeRepository : RecipeRepository {

    override fun getAllRecipes(limit: Int, offset: Int): List<Recipe> =
        RecipeStore.recipes.drop(offset).take(limit)

    override fun getRecipeById(id: Long): Recipe? =
        RecipeStore.recipes.firstOrNull { it.id == id }

    override fun toggleLike(id: Long): List<Recipe> {
        val idx = RecipeStore.recipes.indexOfFirst { it.id == id }
        if (idx >= 0) {
            RecipeStore.recipes[idx] = RecipeStore.recipes[idx].copy(isLiked = !RecipeStore.recipes[idx].isLiked)
        }
        return RecipeStore.recipes
    }

    override fun getLikedRecipes(): List<Recipe> =
        RecipeStore.recipes.filter { it.isLiked }

    override fun searchRecipes(query: String): List<Recipe> =
        if (query.isBlank()) RecipeStore.recipes
        else RecipeStore.recipes.filter { it.title.contains(query, ignoreCase = true) }

    override fun getRecipesByMealType(mealType: MealType): List<Recipe> =
        RecipeStore.recipes.filter { mealType in it.mealTypes }

    override fun getFilteredRecipes(filter: FilterRecipe): List<Recipe> =
        RecipeStore.recipes.filter { recipe ->
            (filter.selectedMealTypes.isEmpty() ||
                    recipe.mealTypes.any { mt -> mt in filter.selectedMealTypes }) &&
                    (recipe.calories >= filter.minCalories && recipe.calories <= filter.maxCalories) &&
                    (filter.selectedDiets.isEmpty() ||
                            filter.selectedDiets.all { recipe.containsDietaryRestriction(it) }) &&
                    (filter.selectedCuisines.isEmpty() ||
                            (recipe.cuisineType != null &&
                                    filter.selectedCuisines.any { it.equals(recipe.cuisineType, ignoreCase = true) })) &&
                    (filter.excludedIngredients.isEmpty() ||
                            filter.excludedIngredients.none { excluded ->
                                recipe.ingredients.any { ing ->
                                    ing.name.contains(excluded, ignoreCase = true)
                                }
                            })
        }

    override fun addRecipe(input: CreateRecipeInput): Recipe {
        val nextRecipeId = (RecipeStore.recipes.maxOfOrNull { it.id } ?: 0L) + 1L

        val ingredients = input.ingredients.mapIndexed { index, ing ->
            Ingredient(
                id = (index + 1).toLong(),
                recipeId = nextRecipeId,
                name = ing.name,
                amount = ing.amount,
                unit = ing.unit,
                originalName = ing.originalName
            )
        }

        val instructions = input.instructions.mapIndexed { index, step ->
            RecipeInstruction(
                id = (index + 1).toLong(),
                recipeId = nextRecipeId,
                stepNumber = step.stepNumber,
                instruction = step.instruction
            )
        }

        val nutrition = input.nutritionInfo?.let {
            NutritionInfo(
                id = 1L,
                recipeId = nextRecipeId,
                calories = it.calories,
                protein = it.protein,
                carbs = it.carbs,
                fat = it.fat,
                fiber = it.fiber,
                sugar = it.sugar,
                sodium = it.sodium,
                saturatedFat = it.saturatedFat,
                cholesterol = it.cholesterol,
                potassium = it.potassium
            )
        }

        val recipe = Recipe(
            id = nextRecipeId,
            createdByUserId = input.createdByUserId,
            title = input.title,
            readyInMinutes = input.readyInMinutes,
            difficulty = input.difficulty,
            image = input.image,
            servings = input.servings,
            cuisineType = input.cuisineType,
            isVegetarian = input.isVegetarian,
            isVegan = input.isVegan,
            isGlutenFree = input.isGlutenFree,
            isDairyFree = input.isDairyFree,
            isLowFodmap = input.isLowFodmap,
            isPescatarian = input.isPescatarian,
            isKetogenic = input.isKetogenic,
            isPaleo = input.isPaleo,
            isWhole30 = input.isWhole30,
            source = input.source,
            localId = input.localId,
            remoteId = input.remoteId,
            nutritionInfo = nutrition,
            ingredients = ingredients,
            instructions = instructions,
            mealTypes = input.mealTypes
        )

        RecipeStore.recipes.add(recipe)
        return recipe
    }

    override fun getUserRecipes(): List<Recipe> =
        RecipeStore.recipes.filter { recipe -> recipe.source == RecipeSource.USER }
}