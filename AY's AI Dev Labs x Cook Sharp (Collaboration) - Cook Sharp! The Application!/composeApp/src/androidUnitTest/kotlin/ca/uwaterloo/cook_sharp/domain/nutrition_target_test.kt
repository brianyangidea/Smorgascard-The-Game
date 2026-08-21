package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class nutrition_target_test {
    @Test
    fun create_target_with_default_values() {
        val target = NutritionTarget()

        assertEquals(2500.0, target.calories)
        assertEquals(200.0, target.protein)
        assertEquals(300.0, target.carbs)
        assertEquals(100.0, target.fat)
    }

    @Test
    fun create_target_with_custom_values() {
        val target = NutritionTarget(
            calories = 2000.0,
            protein = 150.0,
            carbs = 250.0,
            fat = 70.0
        )

        assertEquals(2000.0, target.calories)
        assertEquals(150.0, target.protein)
        assertEquals(250.0, target.carbs)
        assertEquals(70.0, target.fat)
    }

    @Test
    fun two_targets_with_same_data_equal() {
        val target1 = NutritionTarget(15,2000.0, 150.0, 250.0, 70.0)
        val target2 = NutritionTarget(15,2000.0, 150.0, 250.0, 70.0)

        assertEquals(target1, target2)
    }
}