package ca.uwaterloo.cook_sharp.ui.screens.filter
import ca.uwaterloo.cook_sharp.domain.CuisineType
import ca.uwaterloo.cook_sharp.domain.DietType
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterViewModelTest {

    @Test
    fun meal_type_adds_meal_type_when_not_selected() {
        val viewModel = FilterViewModel()

        viewModel.meal_type(MealType.BREAKFAST)

        assertTrue(MealType.BREAKFAST in viewModel.ui_state.selectedMealTypes)
    }

    @Test
    fun meal_type_removes_meal_type_when_already_selected() {
        val viewModel = FilterViewModel()
        viewModel.meal_type(MealType.BREAKFAST)

        viewModel.meal_type(MealType.BREAKFAST)

        assertFalse(MealType.BREAKFAST in viewModel.ui_state.selectedMealTypes)
    }

    @Test
    fun update_min_calories_updates_state() {
        val viewModel = FilterViewModel()

        viewModel.update_min_calories(250)

        assertEquals(250, viewModel.ui_state.minCalories)
    }

    @Test
    fun update_max_calories_updates_state() {
        val viewModel = FilterViewModel()

        viewModel.update_max_calories(900)

        assertEquals(900, viewModel.ui_state.maxCalories)
    }

    @Test
    fun toggle_diet_adds_and_removes_diet() {
        val viewModel = FilterViewModel()
        val diet = DietType.VEGAN.label

        viewModel.toggle_diet(diet)
        assertTrue(diet in viewModel.ui_state.selectedDiets)

        viewModel.toggle_diet(diet)
        assertFalse(diet in viewModel.ui_state.selectedDiets)
    }

    @Test
    fun toggle_cuisine_adds_and_removes_cuisine() {
        val viewModel = FilterViewModel()
        val cuisine = CuisineType.ITALIAN.label

        viewModel.toggle_cuisine(cuisine)
        assertTrue(cuisine in viewModel.ui_state.selectedCuisines)

        viewModel.toggle_cuisine(cuisine)
        assertFalse(cuisine in viewModel.ui_state.selectedCuisines)
    }

    @Test
    fun update_excluded_ingredient_input_updates_state() {
        val viewModel = FilterViewModel()

        viewModel.update_excluded_ingredient_input("milk")

        assertEquals("milk", viewModel.ui_state.excludedIngredientInput)
    }

    @Test
    fun add_excluded_ingredient_from_input_adds_trimmed_ingredient_and_clears_input() {
        val viewModel = FilterViewModel()
        viewModel.update_excluded_ingredient_input("  milk  ")

        viewModel.add_excluded_ingredient_from_input()

        assertEquals(listOf("milk"), viewModel.ui_state.excludedIngredients)
        assertEquals("", viewModel.ui_state.excludedIngredientInput)
    }

    @Test
    fun add_excluded_ingredient_from_input_does_not_add_blank_input() {
        val viewModel = FilterViewModel()
        viewModel.update_excluded_ingredient_input("   ")

        viewModel.add_excluded_ingredient_from_input()

        assertTrue(viewModel.ui_state.excludedIngredients.isEmpty())
        assertEquals("   ", viewModel.ui_state.excludedIngredientInput)
    }

    @Test
    fun add_excluded_ingredient_from_input_does_not_add_duplicate_ignoring_case() {
        val viewModel = FilterViewModel()
        viewModel.update_excluded_ingredient_input("milk")
        viewModel.add_excluded_ingredient_from_input()

        viewModel.update_excluded_ingredient_input("MILK")
        viewModel.add_excluded_ingredient_from_input()

        assertEquals(1, viewModel.ui_state.excludedIngredients.size)
        assertEquals(listOf("milk"), viewModel.ui_state.excludedIngredients)
    }

    @Test
    fun remove_excluded_ingredient_removes_ingredient() {
        val viewModel = FilterViewModel()
        viewModel.update_excluded_ingredient_input("milk")
        viewModel.add_excluded_ingredient_from_input()
        viewModel.update_excluded_ingredient_input("egg")
        viewModel.add_excluded_ingredient_from_input()

        viewModel.remove_excluded_ingredient("milk")

        assertEquals(listOf("egg"), viewModel.ui_state.excludedIngredients)
    }

    @Test
    fun setFromFilterRecipe_copies_values_into_ui_state() {
        val viewModel = FilterViewModel()
        val filter = FilterRecipe(
            selectedMealTypes = setOf(MealType.BREAKFAST, MealType.DINNER),
            selectedDiets = setOf(DietType.VEGAN.label),
            selectedCuisines = setOf(CuisineType.ITALIAN.label),
            minCalories = 200,
            maxCalories = 800,
            excludedIngredients = listOf("milk", "egg")
        )

        viewModel.setFromFilterRecipe(filter)

        assertEquals(setOf(MealType.BREAKFAST, MealType.DINNER), viewModel.ui_state.selectedMealTypes)
        assertEquals(setOf(DietType.VEGAN.label), viewModel.ui_state.selectedDiets)
        assertEquals(setOf(CuisineType.ITALIAN.label), viewModel.ui_state.selectedCuisines)
        assertEquals(200, viewModel.ui_state.minCalories)
        assertEquals(800, viewModel.ui_state.maxCalories)
        assertEquals(listOf("milk", "egg"), viewModel.ui_state.excludedIngredients)
        assertEquals("", viewModel.ui_state.excludedIngredientInput)
    }

    @Test
    fun resetFilters_restores_default_state() {
        val viewModel = FilterViewModel()

        viewModel.meal_type(MealType.LUNCH)
        viewModel.update_min_calories(300)
        viewModel.update_max_calories(700)
        viewModel.toggle_diet(DietType.VEGAN.label)
        viewModel.toggle_cuisine(CuisineType.ITALIAN.label)
        viewModel.update_excluded_ingredient_input("milk")
        viewModel.add_excluded_ingredient_from_input()

        viewModel.resetFilters()

        assertEquals(FilterState(), viewModel.ui_state)
    }

    @Test
    fun option_lists_match_enum_values() {
        val viewModel = FilterViewModel()

        assertEquals(MealType.entries.toList(), viewModel.mealTypeOptions)
        assertEquals(DietType.entries.map { it.label }, viewModel.dietOptions)
        assertEquals(CuisineType.entries.map { it.label }, viewModel.cuisineOptions)
    }
}
