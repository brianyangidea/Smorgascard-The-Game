package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseRecipeInstruction(
    val id: Long,
    val recipe_id: Long,
    val step_number: Int,
    val instruction: String
)

@Serializable
data class SupabaseRecipeInstructionInsert(
    val recipe_id: Long,
    val step_number: Int,
    val instruction: String
)