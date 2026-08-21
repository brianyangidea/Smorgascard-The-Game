package ca.uwaterloo.cook_sharp.ui.screens.meal_plan

import ca.uwaterloo.cook_sharp.data.mock.MealPlans
import ca.uwaterloo.cook_sharp.data.mock.MealPlanStore
import ca.uwaterloo.cook_sharp.data.mock.MockMealPlanRepository
import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.UserStore
import ca.uwaterloo.cook_sharp.domain.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class MealPlanViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: MealPlanViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MealPlanStore.mealPlans.clear()
        MealPlanStore.mealPlans.add(MealPlans.thisWeekMealPlan.copy(userId = UserStore.currentUser.id))
        vm = MealPlanViewModel(
            recipeRepo = MockRecipeRepository(),
            mealPlanRepo = MockMealPlanRepository(),
            ioDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setSelectedDay_updates_selectedDay() {
        vm.setSelectedDay(5)
        assertEquals(5, vm.selectedDay.value)
    }

    @Test
    fun getMealsForDay_returns_empty_when_no_meals() {
        val result = vm.getMealsForDay(7)
        assertTrue(result.isEmpty())
    }

    @Test
    fun addMeal_adds_meal_to_day() {
        vm.addMeal(
            dayIndex = 1,
            mealType = MealType.LUNCH,
            recipeId = 42L,
            label = "Test Lunch"
        )
        val meals = vm.getMealsForDay(1)
        assertTrue(meals.any { it.recipeId == 42L })
    }

    @Test
    fun getRecipeForMeal_returns_null_when_no_recipeId() {
        val meals = vm.getMealsForDay(0)
        meals.forEach { meal ->
            if (meal.recipeId == null) {
                assertNull(vm.getRecipeForMeal(meal))
            }
        }
    }

    @Test
    fun updateMealLabelById_updates_label() {
        vm.addMeal(
            dayIndex = 0,
            mealType = MealType.BREAKFAST,
            recipeId = 1,
            label = "Old Label"
        )
        val meal = vm.getMealsForDay(0).first()
        vm.updateMealLabelById(dayIndex = 0, mealId = meal.id, label = "New Label")
        val updated = vm.getMealsForDay(0).first { it.id == meal.id }
        assertEquals("New Label", updated.label)
    }

    @Test
    fun adding_recipe_from_recipe_detail_to_empty_meal_slot_adds_recipe_to_meal_plan() {
        val dayIndex = 2
        val mealType = MealType.LUNCH
        val recipeId = MealPlanStore.mealPlans
            .first()
            .meals
            .flatMap { it.meals }
            .mapNotNull { it.recipeId }
            .let { existingIds ->
                MockRecipeRepository()
                    .getAllRecipes(limit = 100)
                    .first { it.id !in existingIds }
                    .id
            }

        val beforeMeals = vm.getMealsForDay(dayIndex)
        assertTrue(beforeMeals.none { it.type == mealType })

        vm.addMeal(
            dayIndex = dayIndex,
            mealType = mealType,
            recipeId = recipeId,
            label = "Lunch"
        )

        val afterMeals = vm.getMealsForDay(dayIndex)
        val addedMeal = afterMeals.firstOrNull {
            it.type == mealType && it.recipeId == recipeId
        }

        assertNotNull(addedMeal)
        assertEquals("Lunch", addedMeal.label)

        val loadedRecipe = vm.getRecipeForMeal(addedMeal)
        assertNotNull(loadedRecipe)
        assertEquals(recipeId, loadedRecipe.id)
    }

    @Test
    fun adding_recipe_from_recipe_detail_to_existing_meal_slot_replaces_recipe_in_meal_plan() {
        val dayIndex = 0
        val existingMeal = vm.getMealsForDay(dayIndex)
            .first { it.type == MealType.BREAKFAST }

        val newRecipeId = MockRecipeRepository()
            .getAllRecipes(limit = 100)
            .first { it.id != existingMeal.recipeId }
            .id

        vm.updateMealById(
            dayIndex = dayIndex,
            mealId = existingMeal.id,
            recipeId = newRecipeId,
            label = "Breakfast"
        )

        val updatedMeal = vm.getMealsForDay(dayIndex)
            .first { it.id == existingMeal.id }

        assertEquals(newRecipeId, updatedMeal.recipeId)
        assertEquals("Breakfast", updatedMeal.label)

        val loadedRecipe = vm.getRecipeForMeal(updatedMeal)
        assertNotNull(loadedRecipe)
        assertEquals(newRecipeId, loadedRecipe.id)
    }
}
