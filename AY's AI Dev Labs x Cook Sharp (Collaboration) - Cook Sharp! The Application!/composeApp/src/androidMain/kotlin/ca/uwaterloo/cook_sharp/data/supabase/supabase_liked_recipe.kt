package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseUserLikedRecipe(
    val user_id: String,
    val recipe_id: Long
)