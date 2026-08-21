package ca.uwaterloo.cook_sharp.ui.screens.filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ca.uwaterloo.cook_sharp.domain.CuisineType
import ca.uwaterloo.cook_sharp.domain.DietType
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType

/**
 * Functionalities:
 * - Track selected filter options
 * - Apply filters to recipe data
 * - Filter by - meal type, diet type, cuisine type, calories, exclude ingredients
 */
class FilterViewModel : ViewModel() {
    var ui_state by mutableStateOf(FilterState())
        private set

    val mealTypeOptions = MealType.entries.toList()
    val dietOptions = DietType.entries.map { it.label }
    val cuisineOptions = CuisineType.entries.map { it.label }

    fun meal_type(mealType: MealType) {
        val current = ui_state.selectedMealTypes.toMutableSet()
        if (current.contains(mealType)) {
            current.remove(mealType)
        } else {
            current.add(mealType)
        }
        ui_state = ui_state.copy(selectedMealTypes = current)
    }

    fun update_min_calories(value: Int) {
        ui_state = ui_state.copy(minCalories = value)
    }

    fun update_max_calories(value: Int) {
        ui_state = ui_state.copy(maxCalories = value)
    }

    fun remove_excluded_ingredient(ingredient: String) {
        ui_state = ui_state.copy(
            excludedIngredients = ui_state.excludedIngredients - ingredient
        )
    }

    fun toggle_diet(diet: String) {
        toggle(diet, ui_state.selectedDiets) {
            ui_state = ui_state.copy(selectedDiets = it)
        }
    }

    fun toggle_cuisine(cuisine: String) {
        toggle(cuisine, ui_state.selectedCuisines) {
            ui_state = ui_state.copy(selectedCuisines = it)
        }
    }

    fun resetFilters() {
        ui_state = FilterState()
    }

    private fun toggle(
        value: String,
        currentSet: Set<String>,
        update: (Set<String>) -> Unit
    ) {
        val next = currentSet.toMutableSet().apply {
            if (contains(value)) remove(value) else add(value)
        }
        update(next)
    }

    fun setFromFilterRecipe(filter: FilterRecipe) {
        ui_state = ui_state.copy(
            selectedMealTypes = filter.selectedMealTypes,
            minCalories = filter.minCalories,
            maxCalories = filter.maxCalories,
            excludedIngredients = filter.excludedIngredients,
            selectedDiets = filter.selectedDiets,
            excludedIngredientInput = "",
            selectedCuisines = filter.selectedCuisines
        )
    }

    fun update_excluded_ingredient_input(value: String) {
        ui_state = ui_state.copy(excludedIngredientInput = value)
    }

    fun add_excluded_ingredient_from_input() {
        val ingredient = ui_state.excludedIngredientInput.trim()
        if (ingredient.isBlank()) return
        if (ui_state.excludedIngredients.any { it.equals(ingredient, ignoreCase = true) }) return

        ui_state = ui_state.copy(
            excludedIngredients = ui_state.excludedIngredients + ingredient,
            excludedIngredientInput = ""
        )
    }
}