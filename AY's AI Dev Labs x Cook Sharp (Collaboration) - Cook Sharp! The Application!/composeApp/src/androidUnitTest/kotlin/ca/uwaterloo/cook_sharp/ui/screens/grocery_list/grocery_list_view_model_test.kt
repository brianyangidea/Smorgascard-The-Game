package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import ca.uwaterloo.cook_sharp.data.mock.GroceryListStore
import ca.uwaterloo.cook_sharp.data.mock.MockGroceryListRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        GroceryListStore.reset()
        RecipeStore.reset()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_from_repo_size_and_first_recipe() = runTest {
        val repo = MockGroceryListRepository()
        val vm = GroceryListViewModel(repo = repo, ioDispatcher = testDispatcher)
        advanceUntilIdle()

        assertEquals(repo.getGroceryList().size, vm.ui_state.items.size)
        assertEquals(repo.getGroceryList().first().recipe.id, vm.ui_state.items.first().recipe.id)
    }

    @Test
    fun toggleExpanded_ui_and_repo_expand_collapse() = runTest {
        val repo = MockGroceryListRepository()
        val vm = GroceryListViewModel(repo = repo, ioDispatcher = testDispatcher)
        advanceUntilIdle()

        val recipeId = vm.ui_state.items.first().recipe.id
        vm.toggleExpanded(recipeId)
        advanceUntilIdle()

        assertFalse(vm.ui_state.items.first().isExpanded)
        assertFalse(repo.getGroceryList().first { it.recipe.id == recipeId }.isExpanded)
    }

    @Test
    fun decrementServings_delete_recipe_when_1_serving() = runTest {
        val repo = MockGroceryListRepository()
        val recipe = RecipeStore.recipes.first { r -> repo.getGroceryList().none { it.recipe.id == r.id } }
        repo.addRecipeToGroceryList(recipe.id, 1)

        val vm = GroceryListViewModel(repo = repo, ioDispatcher = testDispatcher)
        advanceUntilIdle()

        vm.decrementServings(recipe.id)
        advanceUntilIdle()

        assertTrue(vm.ui_state.items.none { it.recipe.id == recipe.id })
        assertTrue(repo.getGroceryList().none { it.recipe.id == recipe.id })
    }

    @Test
    fun addRecipe_ui_and_repo() = runTest {
        val repo = MockGroceryListRepository()
        val before = repo.getGroceryList().size
        val recipe = RecipeStore.recipes.first { r -> repo.getGroceryList().none { it.recipe.id == r.id } }

        val vm = GroceryListViewModel(repo = repo, ioDispatcher = testDispatcher)
        advanceUntilIdle()

        vm.addRecipe(recipe.id, 3)
        advanceUntilIdle()

        assertTrue(vm.ui_state.items.any { it.recipe.id == recipe.id })
        assertEquals(before + 1, vm.ui_state.items.size)
    }
}
