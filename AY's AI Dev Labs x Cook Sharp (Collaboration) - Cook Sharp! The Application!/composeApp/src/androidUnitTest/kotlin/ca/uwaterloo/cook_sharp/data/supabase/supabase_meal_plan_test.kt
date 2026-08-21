package ca.uwaterloo.cook_sharp.data.supabase

import ca.uwaterloo.cook_sharp.domain.MealType
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

class supabase_meal_plan_test {

    @Test
    fun create_supabase_meal_plan_with_default_id() {
        val plan = SupabaseMealPlan(
            user_id = "user123",
            week_start_date = "2026-03-31"
        )

        assertEquals(0L, plan.id)
        assertEquals("user123", plan.user_id)
        assertEquals("2026-03-31", plan.week_start_date)
    }

    @Test
    fun create_supabase_meal_plan_with_all_fields() {
        val plan = SupabaseMealPlan(
            id = 100L,
            user_id = "user456",
            week_start_date = "2026-03-24"
        )

        assertEquals(100L, plan.id)
        assertEquals("user456", plan.user_id)
        assertEquals("2026-03-24", plan.week_start_date)
    }

    @Test
    fun two_supabase_meal_plans_with_same_data_are_equal() {
        val plan1 = SupabaseMealPlan(
            id = 1L,
            user_id = "user123",
            week_start_date = "2026-03-31"
        )
        val plan2 = SupabaseMealPlan(
            id = 1L,
            user_id = "user123",
            week_start_date = "2026-03-31"
        )

        assertEquals(plan1, plan2)
    }

    @Test
    fun two_supabase_meal_plans_with_different_ids_are_not_equal() {
        val plan1 = SupabaseMealPlan(id = 1L, user_id = "user123", week_start_date = "2026-03-31")
        val plan2 = SupabaseMealPlan(id = 2L, user_id = "user123", week_start_date = "2026-03-31")

        assertNotEquals(plan1, plan2)
    }

    @Test
    fun create_supabase_daily_meals_with_default_id() {
        val daily = SupabaseDailyMeals(
            meal_plan_id = 100L,
            date = "2026-03-31"
        )

        assertEquals(0L, daily.id)
        assertEquals(100L, daily.meal_plan_id)
        assertEquals("2026-03-31", daily.date)
    }

    @Test
    fun create_supabase_daily_meals_with_all_fields() {
        val daily = SupabaseDailyMeals(
            id = 50L,
            meal_plan_id = 100L,
            date = "2026-04-01"
        )

        assertEquals(50L, daily.id)
        assertEquals(100L, daily.meal_plan_id)
        assertEquals("2026-04-01", daily.date)
    }

    @Test
    fun two_supabase_daily_meals_with_same_data_are_equal() {
        val daily1 = SupabaseDailyMeals(
            id = 1L,
            meal_plan_id = 100L,
            date = "2026-03-31"
        )
        val daily2 = SupabaseDailyMeals(
            id = 1L,
            meal_plan_id = 100L,
            date = "2026-03-31"
        )

        assertEquals(daily1, daily2)
    }

    @Test
    fun create_supabase_meal_with_all_fields() {
        val meal = SupabaseMeal(
            id = 1L,
            daily_meals_id = 50L,
            type = "BREAKFAST",
            recipe_id = 1000L,
            label = "Morning Eggs"
        )

        assertEquals(1L, meal.id)
        assertEquals(50L, meal.daily_meals_id)
        assertEquals("BREAKFAST", meal.type)
        assertEquals(1000L, meal.recipe_id)
        assertEquals("Morning Eggs", meal.label)
    }

    @Test
    fun create_supabase_meal_with_default_id() {
        val meal = SupabaseMeal(
            daily_meals_id = 50L,
            type = "LUNCH"
        )

        assertEquals(0L, meal.id)
        assertEquals(50L, meal.daily_meals_id)
        assertEquals("LUNCH", meal.type)
        assertNull(meal.recipe_id)
        assertNull(meal.label)
    }

    @Test
    fun create_supabase_meal_with_optional_fields_null() {
        val meal = SupabaseMeal(
            id = 2L,
            daily_meals_id = 51L,
            type = "DINNER"
        )

        assertEquals(2L, meal.id)
        assertEquals(51L, meal.daily_meals_id)
        assertEquals("DINNER", meal.type)
        assertNull(meal.recipe_id)
        assertNull(meal.label)
    }

    @Test
    fun two_supabase_meals_with_same_data_are_equal() {
        val meal1 = SupabaseMeal(
            id = 1L,
            daily_meals_id = 50L,
            type = "BREAKFAST",
            recipe_id = 1000L,
            label = "Breakfast"
        )
        val meal2 = SupabaseMeal(
            id = 1L,
            daily_meals_id = 50L,
            type = "BREAKFAST",
            recipe_id = 1000L,
            label = "Breakfast"
        )

        assertEquals(meal1, meal2)
    }

    @Test
    fun supabase_meal_plan_to_domain_with_no_meals() {
        val supabasePlan = SupabaseMealPlan(
            id = 1L,
            user_id = "user123",
            week_start_date = "2026-03-31"
        )

        val domainPlan = supabasePlan.toDomain(emptyList(), emptyMap())

        assertEquals(1, domainPlan.id)
        assertEquals("user123", domainPlan.userId)
        assertEquals(LocalDate.of(2026, 3, 31), domainPlan.weekStartDate)
        assertTrue(domainPlan.meals.isEmpty())
    }

    @Test
    fun supabase_meal_plan_to_domain_with_single_day_no_meals() {
        val supabasePlan = SupabaseMealPlan(
            id = 1L,
            user_id = "user123",
            week_start_date = "2026-03-31"
        )
        val dailyMealsList = listOf(
            SupabaseDailyMeals(id = 10L, meal_plan_id = 1L, date = "2026-03-31")
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, emptyMap())

        assertEquals(1, domainPlan.meals.size)
        assertEquals(LocalDate.of(2026, 3, 31), domainPlan.meals[0].date)
        assertTrue(domainPlan.meals[0].meals.isEmpty())
    }

    @Test
    fun supabase_meal_plan_to_domain_with_full_week_and_meals() {
        val supabasePlan = SupabaseMealPlan(
            id = 1L,
            user_id = "user123",
            week_start_date = "2026-03-30"
        )

        val dailyMealsList = (0..6).map { offset ->
            SupabaseDailyMeals(
                id = (10L + offset),
                meal_plan_id = 1L,
                date = LocalDate.of(2026, 3, 30).plusDays(offset.toLong()).toString()
            )
        }

        val mealsByDailyId = mapOf(
            10L to listOf(
                SupabaseMeal(id = 1L, daily_meals_id = 10L, type = "BREAKFAST", recipe_id = 100L, label = "Toast"),
                SupabaseMeal(id = 2L, daily_meals_id = 10L, type = "LUNCH", recipe_id = 101L)
            ),
            11L to listOf(
                SupabaseMeal(id = 3L, daily_meals_id = 11L, type = "BREAKFAST", recipe_id = 102L)
            )
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)

        assertEquals(7, domainPlan.meals.size)
        assertEquals(2, domainPlan.meals[0].meals.size)
        assertEquals(1, domainPlan.meals[1].meals.size)
        assertEquals(0, domainPlan.meals[2].meals.size)

        assertEquals("Toast", domainPlan.meals[0].meals[0].label)
        assertEquals(100L, domainPlan.meals[0].meals[0].recipeId)
        assertEquals(MealType.BREAKFAST, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun supabase_meal_plan_to_domain_sorts_daily_meals_by_date() {
        val supabasePlan = SupabaseMealPlan(
            id = 1L,
            user_id = "user123",
            week_start_date = "2026-03-30"
        )

        // Create daily meals in reverse order
        val dailyMealsList = listOf(
            SupabaseDailyMeals(id = 12L, meal_plan_id = 1L, date = "2026-04-01"),
            SupabaseDailyMeals(id = 11L, meal_plan_id = 1L, date = "2026-03-31"),
            SupabaseDailyMeals(id = 10L, meal_plan_id = 1L, date = "2026-03-30")
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, emptyMap())

        assertEquals(3, domainPlan.meals.size)
        assertEquals(LocalDate.of(2026, 3, 30), domainPlan.meals[0].date)
        assertEquals(LocalDate.of(2026, 3, 31), domainPlan.meals[1].date)
        assertEquals(LocalDate.of(2026, 4, 1), domainPlan.meals[2].date)
    }

    @Test
    fun string_to_meal_type_breakfast() {
        // Testing the private extension function via the public conversion
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "BREAKFAST", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.BREAKFAST, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun string_to_meal_type_lunch() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "LUNCH", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.LUNCH, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun string_to_meal_type_dinner() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "DINNER", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.DINNER, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun string_to_meal_type_snack() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "SNACK", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.SNACK, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun string_to_meal_type_invalid_defaults_to_snack() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "UNKNOWN_TYPE", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.SNACK, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun string_to_meal_type_with_whitespace_is_trimmed() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "  BREAKFAST  ", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.BREAKFAST, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun string_to_meal_type_case_insensitive() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(SupabaseMeal(1L, 10L, "lunch", 100L))
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(MealType.LUNCH, domainPlan.meals[0].meals[0].type)
    }

    @Test
    fun supabase_meal_plan_to_domain_preserves_meal_ids() {
        val supabasePlan = SupabaseMealPlan(1L, "user123", "2026-03-30")
        val dailyMealsList = listOf(SupabaseDailyMeals(10L, 1L, "2026-03-30"))
        val mealsByDailyId = mapOf(
            10L to listOf(
                SupabaseMeal(id = 42L, daily_meals_id = 10L, type = "BREAKFAST", recipe_id = 100L)
            )
        )

        val domainPlan = supabasePlan.toDomain(dailyMealsList, mealsByDailyId)
        assertEquals(42, domainPlan.meals[0].meals[0].id)
    }
}
