package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.repository.GroceryListRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseGroceryListRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Functionalities:
 * - Get grocery list from repository, keep it and track size needed for each type of ingredient
 * - Expand/collapse a recipe card
 * - Check/uncheck a ingredient
 * - Increase and decrease serving size of a recipe
 * - Add and remove a recipe
 * - Get grocery list from repo
 * - switch between recipe oriented view and ingredient oriented view
 * - Check/uncheck a type of ingredient
 */

class GroceryListViewModel(
    private val repo: GroceryListRepository = SupabaseGroceryListRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    var ui_state by mutableStateOf(GroceryListState())
        private set

    init {
        refreshGroceryList()
    }

    fun toggleExpanded(recipeId: Long) {
        val item = ui_state.items.firstOrNull { it.recipe.id == recipeId } ?: return
        val newExpanded = !item.isExpanded
        ui_state = ui_state.copy(
            items = ui_state.items.map {
                if (it.recipe.id == recipeId) it.copy(isExpanded = newExpanded) else it
            })
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) { repo.updateExpanded(recipeId, newExpanded) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleIngredientCheck(recipeId: Long, ingredientIndex: Int) {
        ui_state = ui_state.copy(
            items = ui_state.items.map { item ->
                if (item.recipe.id == recipeId) {
                    val current = item.checkedStates[ingredientIndex] ?: false
                    item.copy(checkedStates = item.checkedStates + (ingredientIndex to !current))
                } else item
            })
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) { repo.toggleIngredientCheck(recipeId, ingredientIndex) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun incrementServings(recipeId: Long) {
        val item = ui_state.items.firstOrNull { it.recipe.id == recipeId } ?: return
        val newServings = item.servings + 1
        ui_state = ui_state.copy(
            items = ui_state.items.map { if (it.recipe.id == recipeId) it.copy(servings = newServings) else it })
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) { repo.updateServings(recipeId, newServings) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun decrementServings(recipeId: Long) {
        val item = ui_state.items.firstOrNull { it.recipe.id == recipeId } ?: return
        if (item.servings > 1) {
            val newServings = item.servings - 1
            ui_state = ui_state.copy(
                items = ui_state.items.map { if (it.recipe.id == recipeId) it.copy(servings = newServings) else it })
            viewModelScope.launch {
                try {
                    withContext(ioDispatcher) { repo.updateServings(recipeId, newServings) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            deleteRecipe(recipeId)
        }
    }

    fun deleteRecipe(recipeId: Long) {
        ui_state = ui_state.copy(items = ui_state.items.filter { it.recipe.id != recipeId })
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) { repo.removeRecipeFromGroceryList(recipeId) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addRecipe(recipeId: Long, servings: Int) {
        viewModelScope.launch {
            try {
                val items = withContext(ioDispatcher) { repo.addRecipeToGroceryList(recipeId, servings) }
                ui_state = ui_state.copy(items = items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshGroceryList() {
//        Log.d("GROCERY_VM", "refreshGroceryList: starting")
        viewModelScope.launch {
            try {
                val items = withContext(ioDispatcher) { repo.getGroceryList() }
//                Log.d("GROCERY_VM", "refreshGroceryList: got ${items.size} items")
                ui_state = ui_state.copy(items = items)
            } catch (e: Exception) {
//                Log.e("GROCERY_VM", "refreshGroceryList: failed", e)
            }
        }
    }

    fun switchViewMode(mode: GroceryViewMode) {
        ui_state = ui_state.copy(viewMode = mode)
    }

    fun updateMergedIngredientChecked(ingredient: MergedIngredient, checked: Boolean) {
        ui_state = ui_state.copy(
            items = ui_state.items.map { item ->
                val updated = item.checkedStates.toMutableMap()
                ingredient.refs.forEach { ref ->
                    if (ref.recipeId == item.recipe.id) {
                        updated[ref.ingredientIndex] = checked
                    }
                }
                item.copy(checkedStates = updated)
            })

        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    ingredient.refs.forEach { ref ->
                        repo.updateIngredientChecked(ref.recipeId, ref.ingredientIndex, checked)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val mergedIngredients: List<MergedIngredient> by derivedStateOf {
        val map = linkedMapOf<String, MergedIngredient>()
        for (item in ui_state.items) {
            val scaled = item.recipe.scaleIngredients(item.servings)
            for ((index, ingredient) in scaled.withIndex()) {

                val formattedName = reformatIngredientName(ingredient.name)
                val formattedUnit = reformatUnit(ingredient.unit)
                val existing = map[formattedName]

                if (existing != null) {
                    map[formattedName] = existing.copy(
                        amount = existing.amount + ingredient.amount,
                        unit = if (existing.unit.isNotBlank()) existing.unit else formattedUnit,
                        refs = existing.refs + IngredientReference(item.recipe.id, index)
                    )
                } else {
                    map[formattedName] = MergedIngredient(
                        name = ingredient.name,
                        amount = ingredient.amount,
                        unit = formattedUnit,
                        refs = listOf(IngredientReference(item.recipe.id, index))
                    )
                }
            }
        }
        map.values.toList()
    }

    private fun reformatIngredientName(name: String): String {
        return name.trim().lowercase()
    }

    private fun reformatUnit(unit: String): String {
        val reformat = unit.trim().lowercase()
        return when (reformat) {
            "cups" -> "cup"
            "tablespoons" -> "tablespoon"
            "tbsp" -> "tablespoon"
            "teaspoons" -> "teaspoon"
            "tsp" -> "teaspoon"
            "ounces" -> "oz"
            else -> reformat
        }
    }
}
