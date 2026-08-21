package ca.uwaterloo.cook_sharp.ui.screens.recipe_detail
import ca.uwaterloo.cook_sharp.domain.Recipe

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val servings: Int = 1,
    val snackbarMessage: String? = null,
    val isLiked: Boolean = false,
    val matchedAllergies: List<String> = emptyList()
)
