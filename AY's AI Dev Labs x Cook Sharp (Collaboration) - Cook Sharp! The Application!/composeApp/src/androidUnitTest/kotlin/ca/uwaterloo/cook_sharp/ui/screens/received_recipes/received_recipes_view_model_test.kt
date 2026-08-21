package ca.uwaterloo.cook_sharp.ui.screens.received_recipes

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.RecipeShareRepository
import ca.uwaterloo.cook_sharp.data.repository.ReceivedSharedRecipe
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
class ReceivedRecipesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadReceivedRecipes_populates_receivedRecipes_from_share_repo() = runTest {
        val recipe = MockRecipeRepository().getAllRecipes(limit = 100).first()

        val fakeRepo = object : RecipeShareRepository {
            override fun shareRecipe(
                recipeId: Long,
                recipientUserIds: List<String>,
                message: String?
            ) = Unit

            override fun getReceivedRecipes(): List<ReceivedSharedRecipe> {
                return listOf(
                    ReceivedSharedRecipe(
                        recipe = recipe,
                        senderUserId = "sender-1",
                        senderName = "Sue Flay",
                        message = "Try this one"
                    )
                )
            }
        }

        val vm = ReceivedRecipesViewModel(
            shareRepo = fakeRepo,
            ioDispatcher = testDispatcher
        )

        advanceUntilIdle()

        assertFalse(vm.isLoading)
        assertEquals(1, vm.receivedRecipes.size)
        assertEquals(recipe.id, vm.receivedRecipes.first().recipe.id)
        assertEquals("Sue Flay", vm.receivedRecipes.first().senderName)
        assertEquals("Try this one", vm.receivedRecipes.first().message)
    }

    @Test
    fun loadReceivedRecipes_handles_repo_failure_and_keeps_list_empty() = runTest {
        val fakeRepo = object : RecipeShareRepository {
            override fun shareRecipe(
                recipeId: Long,
                recipientUserIds: List<String>,
                message: String?
            ) = Unit

            override fun getReceivedRecipes(): List<ReceivedSharedRecipe> {
                throw IllegalStateException("share repo failed")
            }
        }

        val vm = ReceivedRecipesViewModel(
            shareRepo = fakeRepo,
            ioDispatcher = testDispatcher
        )

        advanceUntilIdle()

        assertTrue(vm.receivedRecipes.isEmpty())
    }
}