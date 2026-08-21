package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseGroceryItemCheckedState(
    val grocery_item_id: Long,
    val ingredient_id: Long,
    val is_checked: Boolean = false
)
