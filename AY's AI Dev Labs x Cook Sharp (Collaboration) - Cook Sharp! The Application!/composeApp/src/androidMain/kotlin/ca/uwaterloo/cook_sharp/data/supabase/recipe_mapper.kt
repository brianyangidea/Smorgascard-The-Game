package ca.uwaterloo.cook_sharp.data.supabase

import ca.uwaterloo.cook_sharp.domain.Ingredient
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.NutritionInfo
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.RecipeInstruction
import ca.uwaterloo.cook_sharp.domain.RecipeSource

fun SupabaseNutritionInfoRow.toDomain(): NutritionInfo {
    return NutritionInfo(
        id = id,
        recipeId = recipe_id,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        saturatedFat = saturated_fat,
        cholesterol = cholesterol,
        potassium = potassium
    )
}

fun SupabaseIngredient.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        recipeId = recipe_id,
        name = name,
        amount = amount,
        unit = unit ?: "",
        originalName = original_name
    )
}

fun SupabaseRecipeInstruction.toDomain(): RecipeInstruction {
    return RecipeInstruction(
        id = id,
        recipeId = recipe_id,
        stepNumber = step_number,
        instruction = instruction
    )
}

fun SupabaseRecipeMealType.toDomain(): MealType? {
    return when (meal_type.trim().lowercase()) {
        "breakfast" -> MealType.BREAKFAST
        "lunch" -> MealType.LUNCH
        "dinner" -> MealType.DINNER
        "snack" -> MealType.SNACK
        else -> null
    }
}

fun String.toRecipeSource(): RecipeSource {
    return when (trim().lowercase()) {
        "user" -> RecipeSource.USER
        "api" -> RecipeSource.API
        else -> RecipeSource.API
    }
}

fun SupabaseRecipe.toDomain(
    nutritionRow: SupabaseNutritionInfoRow?,
    ingredientRows: List<SupabaseIngredient>,
    instructionRows: List<SupabaseRecipeInstruction>,
    mealTypeRows: List<SupabaseRecipeMealType>,
    isLiked: Boolean = false
): Recipe {
    return Recipe(
        id = id,
        createdByUserId = created_by_user_id,
        title = title,
        readyInMinutes = ready_in_minutes,
        difficulty = difficulty,
        image = image,
        servings = servings,
        cuisineType = cuisine_type,
        isVegetarian = is_vegetarian,
        isVegan = is_vegan,
        isGlutenFree = is_gluten_free,
        isDairyFree = is_dairy_free,
        isLowFodmap = is_low_fodmap,
        isPescatarian = is_pescatarian,
        isKetogenic = is_ketogenic,
        isPaleo = is_paleo,
        isWhole30 = is_whole30,
        isLiked = isLiked,
        ingredients = ingredientRows.map { it.toDomain() },
        instructions = instructionRows
            .sortedBy { it.step_number }
            .map { it.toDomain() },
        mealTypes = mealTypeRows.mapNotNull { it.toDomain() },
        nutritionInfo = nutritionRow?.toDomain()
            ?: NutritionInfo(
                id = 0,
                recipeId = id,
                calories = 0.0,
                protein = 0.0,
                carbs = 0.0,
                fat = 0.0,
                fiber = 0.0,
                sugar = 0.0,
                sodium = 0.0,
                saturatedFat = null,
                cholesterol = null,
                potassium = null
            ),
        source = source.toRecipeSource(),
        localId = local_id,
        remoteId = remote_id?.toLong()
    )
}