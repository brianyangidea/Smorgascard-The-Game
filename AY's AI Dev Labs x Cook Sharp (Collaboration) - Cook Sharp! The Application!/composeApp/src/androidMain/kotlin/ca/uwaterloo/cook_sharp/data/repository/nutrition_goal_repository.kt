package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.domain.NutritionGoal


/**
 * Functionalities:
 * - work as an interface for nutrition Goal functions
 * - help users to get nutrition goal and save nutrition goal
 */
interface NutritionGoalRepository {
    suspend fun getNutritionGoal(userId: String): NutritionGoal?
    suspend fun saveNutritionGoal(goal: NutritionGoal)
}
