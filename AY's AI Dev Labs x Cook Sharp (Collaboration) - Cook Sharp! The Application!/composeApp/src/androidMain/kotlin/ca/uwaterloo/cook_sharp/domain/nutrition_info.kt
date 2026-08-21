package ca.uwaterloo.cook_sharp.domain
data class NutritionInfo(
    val id: Long,
    val recipeId: Long,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val sugar: Double,
    val sodium: Double,
    val saturatedFat: Double? = null,
    val cholesterol: Double? = null,
    val potassium: Double? = null
)

data class CreateNutritionInfoInput(
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val saturatedFat: Double? = null,
    val cholesterol: Double? = null,
    val potassium: Double? = null
)