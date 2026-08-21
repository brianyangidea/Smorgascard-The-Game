package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseRecipeMealType(
    val recipe_id: Long,
    val meal_type: String
)