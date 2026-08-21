package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseNutritionInfoRow(
    val id: Long,
    val recipe_id: Long,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val saturated_fat: Double? = null,
    val cholesterol: Double? = null,
    val potassium: Double? = null,
)

@Serializable
data class SupabaseNutritionInfoInsert(
    val recipe_id: Long,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val saturated_fat: Double? = null,
    val cholesterol: Double? = null,
    val potassium: Double? = null,
)