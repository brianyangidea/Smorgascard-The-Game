package ca.uwaterloo.cook_sharp.data.supabase

import kotlinx.serialization.Serializable

@Serializable
data class SupabaseNutritionTarget(
    val id: Long = 0L,
    val calories: Double = 2500.0,
    val protein: Double = 200.0,
    val carbs: Double = 300.0,
    val fat: Double = 100.0
)

@Serializable
data class SupabaseNutritionTargetInsert(
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

@Serializable
data class SupabaseNutritionGoal(
    val user_id: String,
    val nutrition_target_id: Long,
    val goal_type: String
)
