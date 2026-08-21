package ca.uwaterloo.cook_sharp.domain

data class GroceryItem(
    val recipe: Recipe,
    val servings: Int,
    val checkedStates: Map<Int, Boolean> = emptyMap(),
    val isExpanded: Boolean = true
)
