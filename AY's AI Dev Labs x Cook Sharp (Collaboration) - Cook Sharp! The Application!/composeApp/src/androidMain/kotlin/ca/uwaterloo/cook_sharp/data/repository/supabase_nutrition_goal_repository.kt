package ca.uwaterloo.cook_sharp.data.repository

import android.util.Log
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseClientProvider
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseNutritionGoal
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseNutritionTarget
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseNutritionTargetInsert
import ca.uwaterloo.cook_sharp.domain.GoalType
import ca.uwaterloo.cook_sharp.domain.NutritionGoal
import ca.uwaterloo.cook_sharp.domain.NutritionTarget
import io.github.jan.supabase.postgrest.from

/**
 * Functionalities:
 * - Load each uesr's nutrition goals set by users to the corresponding Supabase table
 * - follow correct structure of UML that we created (nutrition targets -> nutrition goals)
 */

class SupabaseNutritionGoalRepository : NutritionGoalRepository {

    private val client = SupabaseClientProvider.client

    override suspend fun getNutritionGoal(userId: String): NutritionGoal? {
        return runCatching {
            val goalRow = client.from("nutrition_goals")
                .select { filter { eq("user_id", userId) } }
                .decodeList<SupabaseNutritionGoal>()
                .firstOrNull() ?: return null

            val targetRow = client.from("nutrition_targets")
                .select { filter { eq("id", goalRow.nutrition_target_id) } }
                .decodeList<SupabaseNutritionTarget>()
                .firstOrNull() ?: return null

            NutritionGoal(
                userId = userId,
                weeklyTarget = NutritionTarget(
                    id = targetRow.id,
                    calories = targetRow.calories,
                    protein = targetRow.protein,
                    carbs = targetRow.carbs,
                    fat = targetRow.fat
                ),
                goalType = runCatching { GoalType.valueOf(goalRow.goal_type) }
                    .getOrDefault(GoalType.MAINTAIN)
            )
        }.onFailure { Log.e("NUTRITION_GOAL_REPO", "getNutritionGoal failed", it) }
            .getOrNull()
    }

    override suspend fun saveNutritionGoal(goal: NutritionGoal) {
        runCatching {
            val existingGoal = client.from("nutrition_goals")
                .select { filter { eq("user_id", goal.userId) } }
                .decodeList<SupabaseNutritionGoal>()
                .firstOrNull()

            if (existingGoal != null) {
                client.from("nutrition_targets")
                    .update(
                        SupabaseNutritionTargetInsert(
                            calories = goal.weeklyTarget.calories,
                            protein = goal.weeklyTarget.protein,
                            carbs = goal.weeklyTarget.carbs,
                            fat = goal.weeklyTarget.fat
                        )
                    ) { filter { eq("id", existingGoal.nutrition_target_id) } }

                client.from("nutrition_goals")
                    .update(
                        SupabaseNutritionGoal(
                            user_id = goal.userId,
                            nutrition_target_id = existingGoal.nutrition_target_id,
                            goal_type = goal.goalType.name
                        )
                    ) { filter { eq("user_id", goal.userId) } }

                Log.d("NUTRITION_GOAL_REPO", "Updated goal for user=${goal.userId}, targetId=${existingGoal.nutrition_target_id}")
            } else {
                val newTarget = client.from("nutrition_targets")
                    .insert(
                        SupabaseNutritionTargetInsert(
                            calories = goal.weeklyTarget.calories,
                            protein = goal.weeklyTarget.protein,
                            carbs = goal.weeklyTarget.carbs,
                            fat = goal.weeklyTarget.fat
                        )
                    ) { select() }
                    .decodeSingle<SupabaseNutritionTarget>()

                client.from("nutrition_goals")
                    .insert(
                        SupabaseNutritionGoal(
                            user_id = goal.userId,
                            nutrition_target_id = newTarget.id,
                            goal_type = goal.goalType.name
                        )
                    )

                Log.d("NUTRITION_GOAL_REPO", "Inserted goal for user=${goal.userId}, targetId=${newTarget.id}")
            }
        }.onFailure {
            Log.e("NUTRITION_GOAL_REPO", "saveNutritionGoal failed: ${it.message}", it)
            throw it
        }
    }
}
