package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseIngredient(
    val id: Long,
    val recipe_id: Long,
    val name: String,
    val amount: Double,
    val unit: String? = null,
    val original_name: String? = null
)

@Serializable
data class SupabaseIngredientInsert(
    val recipe_id: Long,
    val name: String,
    val amount: Double,
    val unit: String? = null,
    val original_name: String? = null
)