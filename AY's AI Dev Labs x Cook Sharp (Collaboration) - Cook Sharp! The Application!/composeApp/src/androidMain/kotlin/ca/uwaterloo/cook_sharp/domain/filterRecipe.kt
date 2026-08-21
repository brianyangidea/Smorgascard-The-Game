package ca.uwaterloo.cook_sharp.domain

data class FilterRecipe(
    val selectedMealTypes: Set<MealType> = emptySet(),
    val selectedDiets: Set<String> = emptySet(),
    val selectedCuisines: Set<String> = emptySet(),
    val minCalories: Int = 0,
    val maxCalories: Int = 2000,
    val excludedIngredients: List<String> = emptyList()
)