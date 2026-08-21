package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class nutrition_goal_test {
    @Test
    fun create_nutrition_goal() {
        val userId = "123"
        val target = NutritionTarget(5,2000.0, 150.0, 70.0, 250.0)
        val goalType = GoalType.CUT

        val goal = NutritionGoal(userId, target, goalType)

        assertEquals(userId, goal.userId)
        assertEquals(target, goal.weeklyTarget)
        assertEquals(goalType, goal.goalType)
    }

    @Test
    fun two_goals_with_same_data_equal() {
        val target = NutritionTarget(5,2000.0, 150.0, 70.0, 250.0)

        val goal1 = NutritionGoal("123", target, GoalType.MAINTAIN)
        val goal2 = NutritionGoal("123", target, GoalType.MAINTAIN)

        assertEquals(goal1, goal2)
    }

    @Test
    fun two_goals_with_different_data_not_equal() {
        val target1 = NutritionTarget(5,2000.0, 150.0, 70.0, 250.0)
        val target2 = NutritionTarget(6,2500.0, 180.0, 80.0, 300.0)

        val goal1 = NutritionGoal("123", target1, GoalType.CUT)
        val goal2 = NutritionGoal("456", target2, GoalType.BULK)

        assertNotEquals(goal1, goal2)
    }

    @Test
    fun goal_type_values_exist() {
        val cut = GoalType.CUT
        val maintain = GoalType.MAINTAIN
        val bulk = GoalType.BULK

        assertEquals("CUT", cut.name)
        assertEquals("MAINTAIN", maintain.name)
        assertEquals("BULK", bulk.name)
    }

    @Test
    fun goal_type_from_string() {
        val goal = GoalType.valueOf("CUT")

        assertEquals(GoalType.CUT, goal)
    }

    @Test
    fun different_goal_types_not_equal() {
        val cut = GoalType.CUT
        val bulk = GoalType.BULK

        assertNotEquals(cut, bulk)
    }
}