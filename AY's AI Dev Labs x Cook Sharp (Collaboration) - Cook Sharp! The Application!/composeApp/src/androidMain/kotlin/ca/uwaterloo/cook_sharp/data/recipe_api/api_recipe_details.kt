package ca.uwaterloo.cook_sharp.data.recipe_api

import kotlinx.serialization.Serializable

@Serializable
data class APIRecipeDetails(
    val id: Long,
    val title: String,
    val image: String? = null,
    val readyInMinutes: Int = 0,
    val servings: Int = 0,
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val dairyFree: Boolean = false,
    val lowFodmap: Boolean = false,
    val veryHealthy: Boolean = false,
    val dishTypes: List<String> = emptyList(),
    val cuisines: List<String> = emptyList(),
    val extendedIngredients: List<APIIngredient> = emptyList(),
    val analyzedInstructions: List<APIRecipeInstructions> = emptyList(),
    val nutrition: APINutrition? = null
)

@Serializable
data class APIIngredient(
    val id: Long = 0,
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = "",
    val original: String? = null
)

@Serializable
data class APIRecipeInstructions(
    val name: String = "",
    val steps: List<APIInstructionStep> = emptyList()
)

@Serializable
data class APIInstructionStep(
    val number: Int,
    val step: String
)

@Serializable
data class APINutrition(
    val nutrients: List<APINutrient> = emptyList()
)

@Serializable
data class APINutrient(
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = ""
)