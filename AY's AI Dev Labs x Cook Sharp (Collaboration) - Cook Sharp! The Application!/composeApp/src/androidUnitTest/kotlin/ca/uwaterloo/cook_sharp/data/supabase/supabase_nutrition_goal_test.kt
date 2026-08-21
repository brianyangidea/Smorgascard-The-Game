package ca.uwaterloo.cook_sharp.data.supabase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class supabase_nutrition_goal_test {

    @Test
    fun create_supabase_nutrition_target_with_default_values() {
        val target = SupabaseNutritionTarget()

        assertEquals(0L, target.id)
        assertEquals(2500.0, target.calories)
        assertEquals(200.0, target.protein)
        assertEquals(300.0, target.carbs)
        assertEquals(100.0, target.fat)
    }

    @Test
    fun create_supabase_nutrition_target_with_custom_values() {
        val target = SupabaseNutritionTarget(
            id = 123L,
            calories = 2000.0,
            protein = 150.0,
            carbs = 250.0,
            fat = 70.0
        )

        assertEquals(123L, target.id)
        assertEquals(2000.0, target.calories)
        assertEquals(150.0, target.protein)
        assertEquals(250.0, target.carbs)
        assertEquals(70.0, target.fat)
    }

    @Test
    fun two_supabase_nutrition_targets_with_same_data_are_equal() {
        val target1 = SupabaseNutritionTarget(
            id = 1L,
            calories = 2000.0,
            protein = 150.0,
            carbs = 250.0,
            fat = 70.0
        )
        val target2 = SupabaseNutritionTarget(
            id = 1L,
            calories = 2000.0,
            protein = 150.0,
            carbs = 250.0,
            fat = 70.0
        )

        assertEquals(target1, target2)
    }

    @Test
    fun two_supabase_nutrition_targets_with_different_ids_are_not_equal() {
        val target1 = SupabaseNutritionTarget(id = 1L, calories = 2000.0, protein = 150.0, carbs = 250.0, fat = 70.0)
        val target2 = SupabaseNutritionTarget(id = 2L, calories = 2000.0, protein = 150.0, carbs = 250.0, fat = 70.0)

        assertNotEquals(target1, target2)
    }

    @Test
    fun create_supabase_nutrition_target_insert_with_all_values() {
        val insert = SupabaseNutritionTargetInsert(
            calories = 2200.0,
            protein = 175.0,
            carbs = 275.0,
            fat = 75.0
        )

        assertEquals(2200.0, insert.calories)
        assertEquals(175.0, insert.protein)
        assertEquals(275.0, insert.carbs)
        assertEquals(75.0, insert.fat)
    }

    @Test
    fun two_supabase_nutrition_target_inserts_with_same_data_are_equal() {
        val insert1 = SupabaseNutritionTargetInsert(
            calories = 2200.0,
            protein = 175.0,
            carbs = 275.0,
            fat = 75.0
        )
        val insert2 = SupabaseNutritionTargetInsert(
            calories = 2200.0,
            protein = 175.0,
            carbs = 275.0,
            fat = 75.0
        )

        assertEquals(insert1, insert2)
    }

    @Test
    fun create_supabase_nutrition_goal() {
        val goal = SupabaseNutritionGoal(
            user_id = "user123",
            nutrition_target_id = 456L,
            goal_type = "MAINTAIN"
        )

        assertEquals("user123", goal.user_id)
        assertEquals(456L, goal.nutrition_target_id)
        assertEquals("MAINTAIN", goal.goal_type)
    }

    @Test
    fun two_supabase_nutrition_goals_with_same_data_are_equal() {
        val goal1 = SupabaseNutritionGoal(
            user_id = "user123",
            nutrition_target_id = 456L,
            goal_type = "CUT"
        )
        val goal2 = SupabaseNutritionGoal(
            user_id = "user123",
            nutrition_target_id = 456L,
            goal_type = "CUT"
        )

        assertEquals(goal1, goal2)
    }

    @Test
    fun two_supabase_nutrition_goals_with_different_user_ids_are_not_equal() {
        val goal1 = SupabaseNutritionGoal(
            user_id = "user123",
            nutrition_target_id = 456L,
            goal_type = "BULK"
        )
        val goal2 = SupabaseNutritionGoal(
            user_id = "user456",
            nutrition_target_id = 456L,
            goal_type = "BULK"
        )

        assertNotEquals(goal1, goal2)
    }

    @Test
    fun supabase_nutrition_goal_supports_different_goal_types() {
        val cutGoal = SupabaseNutritionGoal("user1", 1L, "CUT")
        val maintainGoal = SupabaseNutritionGoal("user1", 1L, "MAINTAIN")
        val bulkGoal = SupabaseNutritionGoal("user1", 1L, "BULK")

        assertEquals("CUT", cutGoal.goal_type)
        assertEquals("MAINTAIN", maintainGoal.goal_type)
        assertEquals("BULK", bulkGoal.goal_type)
    }
}
