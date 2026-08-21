package ca.uwaterloo.cook_sharp.domain

data class RecipeInstruction(
    val id: Long,
    val recipeId: Long,
    val stepNumber: Int,
    val instruction: String
)

data class CreateRecipeInstructionInput(
    val stepNumber: Int,
    val instruction: String
)