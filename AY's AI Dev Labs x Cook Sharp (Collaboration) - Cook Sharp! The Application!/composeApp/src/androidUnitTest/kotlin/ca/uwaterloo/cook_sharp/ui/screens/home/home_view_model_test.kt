package ca.uwaterloo.cook_sharp.ui.screens.home

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.MockUserRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.ui.screens.filter.FilterState
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        RecipeStore.reset()
        vm = HomeViewModel(
            model = Model(
                userRepo = MockUserRepository(),
                repo = MockRecipeRepository()
            ),
            ioDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun visibleRecipes_shows_all_recipes() = runTest {
        advanceUntilIdle()
        val visible = vm.visibleRecipes
        assertTrue(visible.isNotEmpty())
    }

    @Test
    fun applyFilter_filters_by_calories() = runTest {
        advanceUntilIdle()

        val target = RecipeStore.recipes.first()
        val min = target.calories.toInt().coerceAtLeast(0)
        val max = min + 1

        vm.applyFilter(
            FilterState(
                minCalories = min,
                maxCalories = max
            )
        )

        advanceUntilIdle()

        val visible = vm.visibleRecipes
        assertTrue(visible.isNotEmpty())
        visible.forEach { r ->
            assertTrue(r.calories >= min)
            assertTrue(r.calories <= max)
        }
    }

    @Test
    fun change_like_toggles_Like_inRecipeStore() = runTest {
        advanceUntilIdle()

        val id = RecipeStore.recipes.first().id
        val before = RecipeStore.recipes.first { it.id == id }.isLiked

        vm.change_like(id)
        advanceUntilIdle()

        val after = RecipeStore.recipes.first { it.id == id }.isLiked
        assertEquals(!before, after)
    }
}