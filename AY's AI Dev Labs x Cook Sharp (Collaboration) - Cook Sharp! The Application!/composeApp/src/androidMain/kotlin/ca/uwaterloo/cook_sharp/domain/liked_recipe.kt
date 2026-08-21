package ca.uwaterloo.cook_sharp.domain

data class LikedRecipe(
    val userId: String,
    val recipeId: Long
)