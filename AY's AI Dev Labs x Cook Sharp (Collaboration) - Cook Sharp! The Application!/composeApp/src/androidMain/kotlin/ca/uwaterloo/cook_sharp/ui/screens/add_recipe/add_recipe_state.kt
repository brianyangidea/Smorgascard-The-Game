package ca.uwaterloo.cook_sharp.ui.screens.add_recipe
import ca.uwaterloo.cook_sharp.domain.Ingredient

data class IngredientFormRow(
    val ingredient: Ingredient,
    val amountText: String = ""
)

data class AddRecipeUiState(
    val title: String = "",
    val readyInMinutes: Int = 0,
    val calories: Int = 0,
    val servings: Int = 0,
    val difficulty: String = "Easy",
    val mealTypes: Set<String> = emptySet(),
    val cuisineType: String = "",
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val isLowFodmap: Boolean = false,
    val isPescatarian: Boolean = false,
    val isKetogenic: Boolean = false,
    val isPaleo: Boolean = false,
    val isWhole30: Boolean = false,
    val ingredientRows: List<IngredientFormRow> = listOf(
        IngredientFormRow(
            ingredient = Ingredient(
                id = 1L,
                recipeId = 0L,
                name = "",
                amount = 0.0,
                unit = "",
                originalName = null
            ),
            amountText = ""
        )
    ),
    val instructions: List<String> = listOf(""),
    val imageUri: String? = null,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false
)