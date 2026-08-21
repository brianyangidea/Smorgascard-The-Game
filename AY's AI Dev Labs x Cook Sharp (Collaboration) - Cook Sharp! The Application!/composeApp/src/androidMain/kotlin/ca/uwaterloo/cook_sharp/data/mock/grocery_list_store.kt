package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.domain.GroceryItem

object GroceryListStore {
    val itemsByUser: MutableMap<String, MutableList<GroceryItem>> = mutableMapOf(
        "sueflay999" to mutableListOf(
            GroceryItem(
                recipe = MockRecipes.garlickyKale,
                servings = 2,
                checkedStates = emptyMap(),
                isExpanded = true
            )
        )
    )

    fun reset() {
        itemsByUser.clear()
        itemsByUser["sueflay999"] = mutableListOf(
            GroceryItem(
                recipe = MockRecipes.garlickyKale,
                servings = 2,
                checkedStates = emptyMap(),
                isExpanded = true
            )
        )
    }
}
