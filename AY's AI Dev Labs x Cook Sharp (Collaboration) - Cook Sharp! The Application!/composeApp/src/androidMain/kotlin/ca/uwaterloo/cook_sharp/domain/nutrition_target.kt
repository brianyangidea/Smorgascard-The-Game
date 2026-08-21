package ca.uwaterloo.cook_sharp.domain

data class NutritionTarget(
    val id: Long = 0L,
    val calories: Double = 2500.0,
    val protein: Double = 200.0,
    val carbs: Double = 300.0,
    val fat: Double = 100.0
)