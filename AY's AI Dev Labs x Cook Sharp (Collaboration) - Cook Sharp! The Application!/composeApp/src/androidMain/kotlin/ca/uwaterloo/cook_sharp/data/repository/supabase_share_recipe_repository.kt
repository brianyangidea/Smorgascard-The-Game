package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.data.supabase.SupabaseClientProvider
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseIngredient
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseNutritionInfoRow
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseRecipe
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseRecipeInstruction
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseRecipeMealType
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseUserLikedRecipe
import ca.uwaterloo.cook_sharp.data.supabase.toDomain
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

/**
 * Functionalities:
 * - Verify whether the current user can share a given recipe
 * - Create shared recipe records in the database
 * - Load recipes received by the current user
 */
@Serializable
data class SupabaseSharedRecipe(
    val recipe_id: Long,
    val sender_user_id: String,
    val recipient_user_id: String,
    val message: String? = null
)

class SupabaseRecipeShareRepository(
    private val userRepo: UserRepository = SupabaseUserRepository
) : RecipeShareRepository {

    private val client = SupabaseClientProvider.client

    private suspend fun canCurrentUserShareRecipe(
        recipeId: Long,
        currentUserId: String
    ): Boolean {
        val recipeRow = client.from("recipes")
            .select {
                filter {
                    eq("id", recipeId)
                }
            }
            .decodeList<SupabaseRecipe>()
            .firstOrNull()
            ?: return false

        val creatorId = recipeRow.created_by_user_id?.trim()
        val normalizedCurrentUserId = currentUserId.trim()

        val isPublic = creatorId == null
        val isOwner = creatorId == normalizedCurrentUserId

        val isSharedToCurrentUser = client.from("shared_recipes")
            .select(columns = Columns.list("recipe_id")) {
                filter {
                    eq("recipe_id", recipeId)
                    eq("recipient_user_id", normalizedCurrentUserId)
                }
            }
            .decodeList<SupabaseSharedRecipe>()
            .isNotEmpty()

        return isPublic || isOwner || isSharedToCurrentUser
    }

    override fun shareRecipe(
        recipeId: Long,
        recipientUserIds: List<String>,
        message: String?
    ) = runBlocking {
        val senderId = userRepo.getCurrentUser()?.id?.trim() ?: return@runBlocking

        if (!canCurrentUserShareRecipe(recipeId, senderId)) {
            return@runBlocking
        }

        recipientUserIds
            .map { it.trim() }
            .filter { it.isNotBlank() && it != senderId }
            .distinct()
            .forEach { recipientId ->
                client.from("shared_recipes").insert(
                    SupabaseSharedRecipe(
                        recipe_id = recipeId,
                        sender_user_id = senderId,
                        recipient_user_id = recipientId,
                        message = message?.takeIf { it.isNotBlank() }
                    )
                )
            }
    }

    override fun getReceivedRecipes(): List<ReceivedSharedRecipe> = runBlocking {
        val currentUserId = userRepo.getCurrentUser()?.id ?: return@runBlocking emptyList()

        val sharedRows = client.from("shared_recipes")
            .select {
                filter {
                    eq("recipient_user_id", currentUserId)
                }
            }
            .decodeList<SupabaseSharedRecipe>()

        if (sharedRows.isEmpty()) {
            return@runBlocking emptyList()
        }

        val usersById = userRepo.getAllUsers().associateBy { it.id }
        val recipeIds = sharedRows.map { it.recipe_id }.distinct()

        val recipeRowsById = client.from("recipes")
            .select {
                filter {
                    isIn("id", recipeIds)
                }
            }
            .decodeList<SupabaseRecipe>()
            .associateBy { it.id }

        val nutritionByRecipeId = client.from("nutrition_info")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseNutritionInfoRow>()
            .associateBy { it.recipe_id }

        val ingredientsByRecipeId = client.from("ingredients")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseIngredient>()
            .groupBy { it.recipe_id }

        val instructionsByRecipeId = client.from("recipe_instructions")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseRecipeInstruction>()
            .groupBy { it.recipe_id }

        val mealTypesByRecipeId = client.from("recipe_meal_types")
            .select {
                filter {
                    isIn("recipe_id", recipeIds)
                }
            }
            .decodeList<SupabaseRecipeMealType>()
            .groupBy { it.recipe_id }

        val likedIds = runCatching {
            client.from("user_liked_recipes")
                .select {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }
                .decodeList<SupabaseUserLikedRecipe>()
                .map { it.recipe_id }
                .toSet()
        }.getOrDefault(emptySet())

        sharedRows.mapNotNull { shared ->
            val recipeRow = recipeRowsById[shared.recipe_id] ?: return@mapNotNull null

            val recipe = recipeRow.toDomain(
                nutritionRow = nutritionByRecipeId[shared.recipe_id],
                ingredientRows = ingredientsByRecipeId[shared.recipe_id].orEmpty(),
                instructionRows = instructionsByRecipeId[shared.recipe_id].orEmpty(),
                mealTypeRows = mealTypesByRecipeId[shared.recipe_id].orEmpty(),
                isLiked = shared.recipe_id in likedIds
            )

            val senderName = usersById[shared.sender_user_id]?.name ?: shared.sender_user_id

            ReceivedSharedRecipe(
                recipe = recipe,
                senderUserId = shared.sender_user_id,
                senderName = senderName,
                message = shared.message
            )
        }
    }
}