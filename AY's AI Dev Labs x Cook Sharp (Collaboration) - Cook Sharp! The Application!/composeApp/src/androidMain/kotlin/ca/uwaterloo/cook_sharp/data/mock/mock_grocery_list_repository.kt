package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.domain.GroceryItem
import ca.uwaterloo.cook_sharp.data.repository.GroceryListRepository
import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository

class MockGroceryListRepository(
    private val recipeRepo: RecipeRepository = MockRecipeRepository()
) : GroceryListRepository {

    private fun getUserItems(): MutableList<GroceryItem> {
        val userId = UserStore.currentUser.id
        return GroceryListStore.itemsByUser.getOrPut(userId) { mutableListOf() }
    }

    override fun getGroceryList(): List<GroceryItem> = getUserItems().toList()

    override fun addRecipeToGroceryList(recipeId: Long, servings: Int): List<GroceryItem> {
        val items = getUserItems()
        val recipe = recipeRepo.getRecipeById(recipeId)
        recipe?.let {
            val existingIndex = items.indexOfFirst { it.recipe.id == recipeId }
            if (existingIndex != -1) {
                val existing = items[existingIndex]
                items[existingIndex] = existing.copy(servings = existing.servings + servings)
            } else {
                items.add(
                    GroceryItem(
                        recipe = recipe, servings = servings, checkedStates = emptyMap(), isExpanded = true
                    )
                )
            }
        }
        return items.toList()
    }

    override fun removeRecipeFromGroceryList(recipeId: Long): List<GroceryItem> {
        val items = getUserItems()
        items.removeAll { it.recipe.id == recipeId }
        return items.toList()
    }

    override fun updateServings(recipeId: Long, servings: Int): List<GroceryItem> {
        val items = getUserItems()
        val index = items.indexOfFirst { it.recipe.id == recipeId }
        if (index != -1) {
            items[index] = items[index].copy(servings = servings)
        }
        return items.toList()
    }

    override fun toggleIngredientCheck(recipeId: Long, ingredientIndex: Int): List<GroceryItem> {
        val items = getUserItems()
        val index = items.indexOfFirst { it.recipe.id == recipeId }
        if (index != -1) {
            val item = items[index]
            val newCheckedStates = item.checkedStates.toMutableMap()
            newCheckedStates[ingredientIndex] = !(item.checkedStates[ingredientIndex] ?: false)
            items[index] = item.copy(checkedStates = newCheckedStates)
        }
        return items.toList()
    }

    override fun updateIngredientChecked(recipeId: Long, ingredientIndex: Int, checked: Boolean): List<GroceryItem> {
        val items = getUserItems()
        val index = items.indexOfFirst { it.recipe.id == recipeId }
        if (index != -1) {
            val item = items[index]
            val newCheckedStates = item.checkedStates.toMutableMap()
            newCheckedStates[ingredientIndex] = checked
            items[index] = item.copy(checkedStates = newCheckedStates)
        }
        return items.toList()
    }

    override fun updateExpanded(recipeId: Long, isExpanded: Boolean): List<GroceryItem> {
        val items = getUserItems()
        val index = items.indexOfFirst { it.recipe.id == recipeId }
        if (index != -1) {
            items[index] = items[index].copy(isExpanded = isExpanded)
        }
        return items.toList()
    }
}
