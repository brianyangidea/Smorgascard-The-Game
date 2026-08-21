package ca.uwaterloo.cook_sharp.ui.screens.home
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.Recipe
data class HomeState(
    val search: String = "",
    val filterRecipe: MealType? = null,
    val filterActive: Boolean = false,
    val activeFilter: FilterRecipe? = null,
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false
)