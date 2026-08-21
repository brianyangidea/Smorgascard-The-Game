package ca.uwaterloo.cook_sharp.data.recipe_api

import ca.uwaterloo.cook_sharp.domain.Ingredient
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.NutritionInfo
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.RecipeInstruction
import ca.uwaterloo.cook_sharp.domain.RecipeSource

/**
 * Functionalities:
 * - Convert API recipe details into domain recipe
 * - Map ingredients into domain ingredients
 * - Map instructions into domain recipe instructions
 * - Convert API nutrition data into domain nutrition info
 * - Infer meal types(from dishTypes param of the api result) and recipe difficulty(based on time) from API data
 */
object APIRecipeMapper {

    fun toRecipe(details: APIRecipeDetails): Recipe {
        val recipeId = details.id

        val mappedIngredients = details.extendedIngredients.mapIndexed { index, ing ->
            Ingredient(
                id = if (ing.id != 0L) ing.id else (index + 1).toLong(),
                recipeId = recipeId,
                name = ing.name,
                amount = ing.amount,
                unit = ing.unit,
                originalName = ing.original
            )
        }

        val mappedInstructions = details.analyzedInstructions
            .flatMap { it.steps }
            .map { step ->
                RecipeInstruction(
                    id = step.number.toLong(),
                    recipeId = recipeId,
                    stepNumber = step.number,
                    instruction = step.step
                )
            }

        return Recipe(
            id = recipeId,
            createdByUserId = null,
            title = details.title,
            readyInMinutes = details.readyInMinutes,
            difficulty = inferDifficulty(details.readyInMinutes),
            image = details.image,
            servings = details.servings,
            cuisineType = details.cuisines.firstOrNull(),

            isVegetarian = details.vegetarian,
            isVegan = details.vegan,
            isGlutenFree = details.glutenFree,
            isDairyFree = details.dairyFree,
            isLowFodmap = details.lowFodmap,
            isPescatarian = false,
            isKetogenic = false,
            isPaleo = false,
            isWhole30 = false,

            source = RecipeSource.API,
            localId = "spoonacular_$recipeId",
            remoteId = recipeId,

            nutritionInfo = mapNutrition(details),
            ingredients = mappedIngredients,
            instructions = mappedInstructions,
            mealTypes = mapMealTypes(details.dishTypes),
        )
    }

    private fun mapNutrition(details: APIRecipeDetails): NutritionInfo? {
        val nutrients = details.nutrition?.nutrients ?: return null

        fun amountOf(name: String): Double? =
            nutrients.firstOrNull { it.name.equals(name, ignoreCase = true) }?.amount

        return NutritionInfo(
            id = details.id,
            recipeId = details.id,
            calories = amountOf("Calories") ?: 0.0,
            protein = amountOf("Protein") ?: 0.0,
            carbs = amountOf("Carbohydrates") ?: 0.0,
            fat = amountOf("Fat") ?: 0.0,
            fiber = amountOf("Fiber") ?: 0.0,
            sugar = amountOf("Sugar") ?: 0.0,
            sodium = amountOf("Sodium") ?: 0.0,
            saturatedFat = amountOf("Saturated Fat"),
            cholesterol = amountOf("Cholesterol"),
            potassium = amountOf("Potassium")
        )
    }

    private fun mapMealTypes(dishTypes: List<String>): List<MealType> {
        return dishTypes.mapNotNull { type ->
            when (type.trim().lowercase()) {
                "breakfast", "morning meal", "brunch" -> MealType.BREAKFAST
                "lunch" -> MealType.LUNCH
                "dinner", "main course", "main dish" -> MealType.DINNER
                "snack", "appetizer", "starter", "side dish", "salad", "soup" -> MealType.SNACK
                else -> null
            }
        }.distinct()
    }

    private fun inferDifficulty(readyInMinutes: Int): String {
        return when {
            readyInMinutes <= 20 -> "Easy"
            readyInMinutes <= 45 -> "Medium"
            else -> "Hard"
        }
    }
}