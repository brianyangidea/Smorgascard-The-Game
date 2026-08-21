package ca.uwaterloo.cook_sharp.ui.screens.share_recipe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShareRecipeViewModelTest {

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
    fun toggleUserSelection_adds_and_removes_selected_user() {
        val vm = ShareRecipeViewModel()

        vm.toggleUserSelection("user-1")
        assertTrue("user-1" in vm.uiState.selectedUserIds)

        vm.toggleUserSelection("user-1")
        assertFalse("user-1" in vm.uiState.selectedUserIds)
    }

    @Test
    fun sendShare_without_selected_users_sets_validation_message() {
        val vm = ShareRecipeViewModel()

        vm.sendShare { }

        assertEquals("Select at least one user", vm.uiState.message)
        assertFalse(vm.uiState.isLoading)
    }

    @Test
    fun onMessageChanged_updates_personal_message() {
        val vm = ShareRecipeViewModel()

        vm.onMessageChanged("Please try this recipe")

        assertEquals("Please try this recipe", vm.uiState.personalMessage)
    }

    @Test
    fun clearMessage_clears_ui_message() {
        val vm = ShareRecipeViewModel()

        vm.sendShare { }
        assertEquals("Select at least one user", vm.uiState.message)

        vm.clearMessage()
        assertNull(vm.uiState.message)
    }

    @Test
    fun onSearchQueryChanged_updates_search_query() {
        val vm = ShareRecipeViewModel()

        vm.onSearchQueryChanged("zoltan")

        assertEquals("zoltan", vm.uiState.searchQuery)
    }
    @Test
    fun filteredUsers_filters_by_name_or_email() {
        val state = ShareRecipeUiState(
            searchQuery = "zoltan",
            allUsers = listOf(
                ShareUserItem("u1", "Sue Flay", "sue@gmail.com"),
                ShareUserItem("u2", "Zoltan Pepper", "zoltan@gmail.com")
            )
        )

        assertEquals(1, state.filteredUsers.size)
        assertEquals("u2", state.filteredUsers.first().id)
    }
}