package ca.uwaterloo.cook_sharp.ui.screens.signup

data class FoodAllergyUiState(
    val allergies: Set<String> = emptySet()
)