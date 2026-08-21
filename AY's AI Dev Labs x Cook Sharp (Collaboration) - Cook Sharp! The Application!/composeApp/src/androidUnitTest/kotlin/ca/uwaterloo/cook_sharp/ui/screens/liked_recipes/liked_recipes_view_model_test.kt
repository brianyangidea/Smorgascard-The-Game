package ca.uwaterloo.cook_sharp.ui.screens.liked_recipes

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.MockUserRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LikedRecipesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: LikedRecipesViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        RecipeStore.reset()
        vm = LikedRecipesViewModel(
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
    fun after_toggling_like_recipe_appears_in_LikedRecipes() = runTest {
        advanceUntilIdle()

        val target = RecipeStore.recipes.firstOrNull { !it.isLiked }
            ?: RecipeStore.recipes.first()

        vm.toggleLike(target.id)
        advanceUntilIdle()

        val nowLikedInStore = RecipeStore.recipes.first { it.id == target.id }.isLiked
        assertTrue(nowLikedInStore)

        assertTrue(vm.likedRecipes.any { it.id == target.id })
    }

    @Test
    fun unlike_Recipe_removes_from_like_recipes() = runTest {
        advanceUntilIdle()

        val target = RecipeStore.recipes.firstOrNull { it.isLiked } ?: RecipeStore.recipes.first()

        if (!target.isLiked) {
            vm.toggleLike(target.id)
            advanceUntilIdle()
        }

        vm.toggleLike(target.id)
        advanceUntilIdle()

        assertFalse(vm.likedRecipes.any { it.id == target.id })
    }
}