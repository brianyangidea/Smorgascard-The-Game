package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseRecipe(
    val id: Long,
    val created_by_user_id: String? = null,
    val title: String,
    val ready_in_minutes: Int,
    val difficulty: String,
    val image: String? = null,
    val servings: Int,
    val cuisine_type: String? = null,
    val is_vegetarian: Boolean = false,
    val is_vegan: Boolean = false,
    val is_gluten_free: Boolean = false,
    val is_dairy_free: Boolean = false,
    val is_low_fodmap: Boolean = false,
    val is_pescatarian: Boolean = false,
    val is_ketogenic: Boolean = false,
    val is_paleo: Boolean = false,
    val is_whole30: Boolean = false,
    val source: String,
    val local_id: String,
    val remote_id: Int? = null
)

@Serializable
data class SupabaseRecipeInsert(
    val created_by_user_id: String? = null,
    val title: String,
    val ready_in_minutes: Int,
    val difficulty: String,
    val image: String? = null,
    val servings: Int,
    val cuisine_type: String? = null,
    val is_vegetarian: Boolean = false,
    val is_vegan: Boolean = false,
    val is_gluten_free: Boolean = false,
    val is_dairy_free: Boolean = false,
    val is_low_fodmap: Boolean = false,
    val is_pescatarian: Boolean = false,
    val is_ketogenic: Boolean = false,
    val is_paleo: Boolean = false,
    val is_whole30: Boolean = false,
    val source: String,
    val local_id: String,
    val remote_id: Long? = null
)