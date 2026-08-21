package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.data.repository.NutritionGoalRepository
import ca.uwaterloo.cook_sharp.domain.NutritionGoal

class MockNutritionGoalRepository : NutritionGoalRepository {
    private var stored: NutritionGoal? = null

    override suspend fun getNutritionGoal(userId: String): NutritionGoal? = stored

    override suspend fun saveNutritionGoal(goal: NutritionGoal) {
        stored = goal
    }
}
