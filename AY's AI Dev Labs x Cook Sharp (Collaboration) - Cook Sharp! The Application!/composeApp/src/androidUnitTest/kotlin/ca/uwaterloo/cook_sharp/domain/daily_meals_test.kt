package ca.uwaterloo.cook_sharp.domain

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class daily_meals_test {

    @Test
    fun create_daily_meals_with_date_and_four_meals() {
        val date = LocalDate.of(2024, 12, 24)
        val meals = listOf(
            Meal(id = 1, MealType.BREAKFAST, 715415),
            Meal(id = 2, MealType.LUNCH, 644387),
            Meal(id = 3, MealType.DINNER, 715415),
            Meal(id = 4,MealType.SNACK, 644387)
        )

        val dailyMeals = DailyMeals(date, meals)
        assertEquals(date, dailyMeals.date)
        assertEquals(4, dailyMeals.meals.size)
    }

    @Test
    fun create_daily_meals_with_only_snacks() {
        val date = LocalDate.of(2024, 12, 24)
        val meals = listOf(
            Meal(5,MealType.SNACK, 715415),
            Meal(6,MealType.SNACK, 644387),
        )

        val dailyMeals = DailyMeals(date, meals)
        assertEquals(date, dailyMeals.date)
        assertEquals(2, dailyMeals.meals.size)
    }

    @Test
    fun create_daily_meals_with_empty_meals() {
        val date = LocalDate.of(2024, 12, 24)
        val dailyMeals = DailyMeals(date)
        assertEquals(date, dailyMeals.date)
        assertTrue(dailyMeals.meals.isEmpty())
    }

    @Test
    fun two_daily_meals_with_same_data_equal() {
        val date = LocalDate.of(2024, 12, 24)
        val meals = listOf(Meal(7,MealType.BREAKFAST, 644387))
        val dailyMeals1 = DailyMeals(date, meals)
        val dailyMeals2 = DailyMeals(date, meals)

        assertEquals(dailyMeals1, dailyMeals2)
    }

    @Test
    fun two_daily_meals_with_diff_date_not_equal() {
        val date1 = LocalDate.of(2024, 12, 24)
        val date2 = LocalDate.of(2024, 12, 25)
        val meals = listOf(Meal(8,MealType.SNACK, 644387))
        val dailyMeals1 = DailyMeals(date1, meals)
        val dailyMeals2 = DailyMeals(date2, meals)

        assertNotEquals(dailyMeals1, dailyMeals2)
    }
}