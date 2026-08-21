package ca.uwaterloo.cook_sharp.domain

data class NutritionGoal(
    val userId : String,
    val weeklyTarget  : NutritionTarget,
    val goalType : GoalType
)

enum class GoalType {
    CUT,
    MAINTAIN,
    BULK
}