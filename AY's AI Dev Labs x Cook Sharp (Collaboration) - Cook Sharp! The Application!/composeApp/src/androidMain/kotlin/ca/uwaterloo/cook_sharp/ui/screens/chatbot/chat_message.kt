package ca.uwaterloo.cook_sharp.ui.screens.chatbot

import ca.uwaterloo.cook_sharp.domain.Recipe

sealed class ChatMessage {
    data class ChatBot(val text: String) : ChatMessage()
    data class User(val text: String) : ChatMessage()
    data class DatabaseRecipeResult(
        val recipe: Recipe
    ) : ChatMessage()
    data class AiRecipeResult(
        val recipe: AiRecipe
    ) : ChatMessage()
}

data class AiRecipe(
    val title: String,
    val cookTimeMinutes: Int,
    val difficulty: String,
    val servings: Int,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val sugar: Double,
    val sodium: Double,
    val ingredients: List<String>,
    val steps: List<String>,
    val cuisineType: String?
)