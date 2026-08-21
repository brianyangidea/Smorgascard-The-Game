package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.domain.Recipe

data class ReceivedSharedRecipe(
    val recipe: Recipe,
    val senderUserId: String,
    val senderName: String,
    val message: String?
)

interface RecipeShareRepository {
    fun shareRecipe(
        recipeId: Long,
        recipientUserIds: List<String>,
        message: String?
    )

    fun getReceivedRecipes(): List<ReceivedSharedRecipe>
}