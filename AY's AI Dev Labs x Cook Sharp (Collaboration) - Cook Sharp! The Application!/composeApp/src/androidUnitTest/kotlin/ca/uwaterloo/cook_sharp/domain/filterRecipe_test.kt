package ca.uwaterloo.cook_sharp.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class filterRecipe_test {

    @Test
    fun defaultFilter_has_no_selected_filter() {
        val f = FilterRecipe()
        assertTrue(f.selectedMealTypes.isEmpty())
        assertTrue(f.selectedDiets.isEmpty())
        assertTrue(f.selectedCuisines.isEmpty())
        assertTrue(f.excludedIngredients.isEmpty())
        assertTrue(f.minCalories <= f.maxCalories)
    }

    @Test
    fun filter_stores_selectios() {
        val f = FilterRecipe(
            selectedMealTypes = setOf(MealType.BREAKFAST),
            selectedDiets = setOf("Vegan"),
            minCalories = 100,
            maxCalories = 500
        )
        assertEquals(setOf(MealType.BREAKFAST), f.selectedMealTypes)
        assertEquals(setOf("Vegan"), f.selectedDiets)
        assertEquals(100, f.minCalories)
        assertEquals(500, f.maxCalories)
    }
}