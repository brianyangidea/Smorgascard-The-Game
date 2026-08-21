package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.domain.NutritionInfo

object NutritionInfos {
    val mockNutritionInfo = listOf(
        NutritionInfo(
            id = 21,
            recipeId = 644387,
            calories = 170.0,
            protein = 5.0,
            carbs = 18.0,
            fat = 9.0,
            fiber = 3.5,
            sugar = 4.0,
            sodium = 290.0,
            saturatedFat = 1.3,
            cholesterol = 0.0,
            potassium = 420.0
        ),
        NutritionInfo(
            id = 22,
            recipeId = 715415,
            calories = 450.0,
            protein = 14.0,
            carbs = 62.0,
            fat = 16.0,
            fiber = 3.0,
            sugar = 3.5,
            sodium = 580.0,
            saturatedFat = 2.5,
            cholesterol = 5.0,
            potassium = 210.0
        ),
        NutritionInfo(
            id = 23,
            recipeId = 644287,
            calories = 320.0,
            protein = 18.0,
            carbs = 38.0,
            fat = 11.0,
            fiber = 4.5,
            sugar = 7.0,
            sodium = 430.0,
            saturatedFat = 3.0,
            cholesterol = 45.0,
            potassium = 510.0
        ),
    )
}
