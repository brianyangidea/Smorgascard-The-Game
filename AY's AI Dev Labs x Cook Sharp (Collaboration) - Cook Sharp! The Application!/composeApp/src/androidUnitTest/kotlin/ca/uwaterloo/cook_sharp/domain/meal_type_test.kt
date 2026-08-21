package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class meal_type_test {

    @Test
    fun meal_type_four_values() {
        val values = MealType.entries.toTypedArray()

        assertEquals(4, values.size)
    }

    @Test
    fun meal_type_contains_BREAKFAST() {
        val types = MealType.entries.toTypedArray()

        assertTrue(types.contains(MealType.BREAKFAST))
    }

    @Test
    fun meal_type_contains_LUNCH() {
        val types = MealType.entries.toTypedArray()

        assertTrue(types.contains(MealType.LUNCH))
    }

    @Test
    fun meal_type_contains_DINNER() {
        val types = MealType.entries.toTypedArray()

        assertTrue(types.contains(MealType.DINNER))
    }

    @Test
    fun meal_type_contains_SNACK() {
        val types = MealType.entries.toTypedArray()

        assertTrue(types.contains(MealType.SNACK))
    }

    @Test
    fun invalid_meal_type_string_throws_exception() {
        assertFailsWith<IllegalArgumentException> {
            MealType.valueOf("INVALID")
        }
    }
}