package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseGroceryItem(
    val id: Long = 0L,
    val recipe_id: Long,
    val grocery_list_id: Long,
    val servings: Int,
    val is_expanded: Boolean = true
)
