package ca.uwaterloo.cook_sharp.ui.screens.filter

import ca.uwaterloo.cook_sharp.domain.MealType

data class FilterState(
    val selectedMealTypes: Set<MealType> = emptySet(),
    val minCalories: Int = 0,
    val maxCalories: Int = 2000,
    val excludedIngredients: List<String> = emptyList(),
    val selectedDiets: Set<String> = emptySet(),
    val selectedCuisines: Set<String> = emptySet(),
    val excludedIngredientInput: String = ""
)