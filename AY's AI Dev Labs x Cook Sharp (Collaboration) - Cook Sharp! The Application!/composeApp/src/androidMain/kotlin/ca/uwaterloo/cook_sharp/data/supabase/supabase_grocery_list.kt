package ca.uwaterloo.cook_sharp.data.supabase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class SupabaseGroceryList(
    val id: Long = 0L,
    val user_id: String,
    val group_by: String = "RECIPE"
)
