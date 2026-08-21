package ca.uwaterloo.cook_sharp.data.recipe_api
import kotlinx.serialization.Serializable

@Serializable
data class APIEstimatedNutritionResponse(
    val calories: APIMacroEstimate? = null,
    val carbs: APIMacroEstimate? = null,
    val fat: APIMacroEstimate? = null,
    val protein: APIMacroEstimate? = null
)

@Serializable
data class APIMacroEstimate(
    val value: Double = 0.0,
    val unit: String = "",
    val confidenceRange95Percent: APIMacroConfidenceRange? = null
)

@Serializable
data class APIMacroConfidenceRange(
    val min: Double = 0.0,
    val max: Double = 0.0
)