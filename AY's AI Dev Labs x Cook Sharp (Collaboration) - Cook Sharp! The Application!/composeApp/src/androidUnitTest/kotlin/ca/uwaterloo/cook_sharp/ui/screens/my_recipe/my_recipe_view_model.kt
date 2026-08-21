package ca.uwaterloo.cook_sharp.ui.screens.my_recipes

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.MockUserRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.domain.RecipeSource
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MyRecipesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var model: Model
    private lateinit var vm: MyRecipesViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        RecipeStore.reset()
        model = Model(
            userRepo = MockUserRepository(),
            repo = MockRecipeRepository()
        )
        vm = MyRecipesViewModel(
            model = model,
            ioDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun userRecipe_appears_in_myRecipes() = runTest {
        advanceUntilIdle()

        val userRecipe = RecipeStore.recipes.first { it.source == RecipeSource.USER }
        val my = vm.myRecipes

        assertTrue(my.any { it.id == userRecipe.id })
        assertTrue(my.any { it.title == userRecipe.title })
    }

    @Test
    fun apiRecipes_do_not_appear_in_my_recipes() = runTest {
        advanceUntilIdle()

        val anyApi = RecipeStore.recipes.first { it.source == RecipeSource.API }
        val my = vm.myRecipes

        assertFalse(my.any { it.id == anyApi.id })
    }
}