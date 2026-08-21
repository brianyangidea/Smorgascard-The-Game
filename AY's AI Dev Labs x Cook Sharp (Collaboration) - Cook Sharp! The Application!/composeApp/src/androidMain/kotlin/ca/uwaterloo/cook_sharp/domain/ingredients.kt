package ca.uwaterloo.cook_sharp.domain

data class Ingredient(
    val id: Long,
    val recipeId: Long,
    val name: String,
    val amount: Double,
    val unit: String = "",
    val originalName: String? = null
)

data class CreateIngredientInput(
    val name: String,
    val amount: Double,
    val unit: String = "",
    val originalName: String? = null
)