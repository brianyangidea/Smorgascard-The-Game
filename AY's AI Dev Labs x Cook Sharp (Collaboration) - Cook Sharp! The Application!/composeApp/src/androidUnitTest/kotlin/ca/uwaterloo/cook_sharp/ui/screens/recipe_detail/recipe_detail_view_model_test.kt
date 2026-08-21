package ca.uwaterloo.cook_sharp.ui.screens.recipe_detail

import ca.uwaterloo.cook_sharp.data.mock.MockGroceryListRepository
import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.MockUserRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import ca.uwaterloo.cook_sharp.data.repository.GroceryListRepository
import ca.uwaterloo.cook_sharp.domain.Model
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
import kotlin.test.assertNull
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: RecipeDetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        RecipeStore.reset()
        vm = RecipeDetailViewModel(
            model = Model(repo = MockRecipeRepository()),
            ioDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadRecipe_sets_recipe_in_state() {
        val target = RecipeStore.recipes.first()
        vm.loadRecipe(target.id)
        val loaded = assertNotNull(vm.ui_state.recipe)
        assertEquals(target.id, loaded.id)
        assertEquals(target.servings, vm.ui_state.servings)
    }

    @Test
    fun toggleLike_updates_recipe_in_state() {
        val target = RecipeStore.recipes.first()
        vm.loadRecipe(target.id)
        val before = assertNotNull(vm.ui_state.recipe).isLiked
        vm.toggleLike()
        val after = assertNotNull(vm.ui_state.recipe).isLiked
        assertEquals(!before, after)
    }

    @Test
    fun increment_decrement_servings_test() {
        vm.increment_servings()
        assertEquals(2, vm.ui_state.servings)

        vm.decrement_servings()
        assertEquals(1, vm.ui_state.servings)

        vm.decrement_servings()
        assertEquals(1, vm.ui_state.servings)
    }
}
