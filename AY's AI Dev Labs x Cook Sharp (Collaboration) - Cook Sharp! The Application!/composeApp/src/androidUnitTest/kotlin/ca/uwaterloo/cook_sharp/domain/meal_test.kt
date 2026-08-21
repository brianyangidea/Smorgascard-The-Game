package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class meal_test {

    @Test
    fun create_meal_with_type_and_recipe_id() {
        val type = MealType.BREAKFAST
        val recipeId = 644387L
        val meal = Meal(1, type, recipeId = recipeId)

        assertEquals(type, meal.type)
        assertEquals(recipeId, meal.recipeId)
        assertNull(meal.label)
    }

    @Test
    fun create_meal_with_type_and_label() {
        val type = MealType.LUNCH
        val label = "my lunch"

        val meal = Meal(2, type, label = label)

        assertEquals(type, meal.type)
        assertNull(meal.recipeId)
        assertEquals(label, meal.label)
    }

    @Test
    fun create_meal_with_only_type() {
        val type = MealType.DINNER
        val meal = Meal(3, type)

        assertEquals(type, meal.type)
        assertNull(meal.recipeId)
        assertNull(meal.label)
    }

    @Test
    fun create_meal_with_type_recipeId_label() {
        val type = MealType.SNACK
        val recipeId = 644387L
        val label = "My snack"
        val meal = Meal(4, type, recipeId = recipeId, label = label)

        assertEquals(type, meal.type)
        assertEquals(recipeId, meal.recipeId)
        assertEquals(label, meal.label)
    }

    @Test
    fun two_meals_with_same_data_equal() {
        val meal1 = Meal(5, MealType.BREAKFAST, 644387L, "my breakfast")
        val meal2 = Meal(5, MealType.BREAKFAST, 644387L, "my breakfast")

        assertEquals(meal1, meal2)
    }

    @Test
    fun two_meals_with_different_data_not_equal() {
        val meal1 = Meal(7, MealType.BREAKFAST, 644387L, "my breakfast")
        val meal2 = Meal(8, MealType.DINNER, 644387L, "my dinner")

        assertNotEquals(meal1, meal2)
    }
}