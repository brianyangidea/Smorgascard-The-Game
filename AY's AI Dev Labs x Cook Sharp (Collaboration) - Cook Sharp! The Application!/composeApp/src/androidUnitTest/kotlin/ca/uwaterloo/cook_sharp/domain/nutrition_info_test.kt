package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class nutrition_info_test {

    @Test
    fun create_nutrition_info_with_all_fields() {
        val nutrition = NutritionInfo(
            id = 1L,
            recipeId = 100L,
            calories = 500.0,
            protein = 30.0,
            carbs = 60.0,
            fat = 20.0,
            fiber = 10.0,
            sugar = 15.0,
            sodium = 300.0,
            saturatedFat = 5.0,
            cholesterol = 50.0,
            potassium = 400.0
        )

        assertEquals(1L, nutrition.id)
        assertEquals(100L, nutrition.recipeId)
        assertEquals(500.0, nutrition.calories)
        assertEquals(30.0, nutrition.protein)
        assertEquals(60.0, nutrition.carbs)
        assertEquals(20.0, nutrition.fat)
        assertEquals(10.0, nutrition.fiber)
        assertEquals(15.0, nutrition.sugar)
        assertEquals(300.0, nutrition.sodium)
        assertEquals(5.0, nutrition.saturatedFat)
        assertEquals(50.0, nutrition.cholesterol)
        assertEquals(400.0, nutrition.potassium)
    }

    @Test
    fun create_nutrition_info_with_optional_fields_null() {
        val nutrition = NutritionInfo(
            id = 2L,
            recipeId = 200L,
            calories = 300.0,
            protein = 20.0,
            carbs = 40.0,
            fat = 10.0,
            fiber = 5.0,
            sugar = 8.0,
            sodium = 150.0
        )

        assertNull(nutrition.saturatedFat)
        assertNull(nutrition.cholesterol)
        assertNull(nutrition.potassium)
    }

    @Test
    fun two_nutrition_info_with_different_data_not_equal() {
        val nutrition1 = NutritionInfo(
            1L, 100L, 500.0, 30.0, 60.0, 20.0, 10.0, 15.0, 300.0
        )

        val nutrition2 = NutritionInfo(
            2L, 101L, 600.0, 35.0, 70.0, 25.0, 12.0, 18.0, 350.0
        )

        assertNotEquals(nutrition1, nutrition2)
    }
}