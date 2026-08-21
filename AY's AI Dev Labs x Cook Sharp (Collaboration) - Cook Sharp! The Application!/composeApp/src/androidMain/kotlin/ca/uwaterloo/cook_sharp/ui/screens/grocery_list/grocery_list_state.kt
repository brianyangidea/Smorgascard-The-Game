package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import ca.uwaterloo.cook_sharp.domain.GroceryItem

enum class GroceryViewMode { RECIPES, INGREDIENTS }

data class IngredientReference(
    val recipeId: Long, val ingredientIndex: Int
)

data class MergedIngredient(
    val name: String, val amount: Double, val unit: String, val refs: List<IngredientReference> = emptyList()
)

data class GroceryListState(
    val items: List<GroceryItem> = emptyList(), val viewMode: GroceryViewMode = GroceryViewMode.RECIPES
)
