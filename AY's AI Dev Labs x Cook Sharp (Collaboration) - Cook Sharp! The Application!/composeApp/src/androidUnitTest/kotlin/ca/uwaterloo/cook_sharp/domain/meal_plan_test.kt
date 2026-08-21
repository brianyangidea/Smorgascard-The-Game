package ca.uwaterloo.cook_sharp.domain

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class meal_plan_test {

    @Test
    fun create_meal_plan_with_full_parameters() {
        val id = 1
        val userId = "123"
        val startDate = LocalDate.of(2026, 12, 1)
        val dailyMeals = listOf(
            DailyMeals(startDate, emptyList())
        )

        val mealPlan = MealPlan(id, userId, startDate, dailyMeals)

        assertEquals(id, mealPlan.id)
        assertEquals(userId, mealPlan.userId)
        assertEquals(startDate, mealPlan.weekStartDate)
        assertEquals(1, mealPlan.meals.size)
    }

    @Test
    fun create_meal_plan_with_default_empty_meals() {
        val id = 1
        val userId = "123"
        val startDate = LocalDate.of(2026, 12, 1)
        val mealPlan = MealPlan(id, userId, startDate)
        assertTrue(mealPlan.meals.isEmpty())
    }

    @Test
    fun meal_plan_with_7_daily_meals_for_full_week() {
        val startDate = LocalDate.of(2026, 12, 1)
        val dailyMeals = (0..6).map { offset ->
            DailyMeals(startDate.plusDays(offset.toLong()), emptyList())
        }
        val mealPlan = MealPlan(1, "123", startDate, dailyMeals)
        assertEquals(7, mealPlan.meals.size)
    }

    @Test
    fun two_meal_plans_with_same_data_are_equal() {
        val date = LocalDate.of(2026, 12, 1)
        val mealPlan1 = MealPlan(1, "123", date, emptyList())
        val mealPlan2 = MealPlan(1, "123", date, emptyList())
        assertEquals(mealPlan1, mealPlan2)
    }

    @Test
    fun meal_plans_with_different_ids_are_not_equal() {
        val date = LocalDate.of(2026, 12, 1)
        val mealPlan1 = MealPlan(1, "123", date)
        val mealPlan2 = MealPlan(2, "123", date)
        assertNotEquals(mealPlan1, mealPlan2)
    }

    @Test
    fun meal_plans_with_different_users_are_not_equal() {
        val date = LocalDate.of(2026, 12, 1)
        val mealPlan1 = MealPlan(1, "123", date)
        val mealPlan2 = MealPlan(1, "456", date)
        assertNotEquals(mealPlan1, mealPlan2)
    }
}