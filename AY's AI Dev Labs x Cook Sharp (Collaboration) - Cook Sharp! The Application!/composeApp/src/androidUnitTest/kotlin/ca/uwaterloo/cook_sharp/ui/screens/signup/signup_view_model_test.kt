package ca.uwaterloo.cook_sharp.ui.screens.signup

import ca.uwaterloo.cook_sharp.data.mock.MockUserRepository
import ca.uwaterloo.cook_sharp.data.mock.UserStore
import ca.uwaterloo.cook_sharp.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        resetMockUsers()
    }

    @AfterTest
    fun teardown() {
        resetMockUsers()
        Dispatchers.resetMain()
    }

    private fun resetMockUsers() {
        UserStore.allUsers.clear()
        UserStore.allUsers.addAll(
            listOf(
                User(
                    id = "sueflay999",
                    name = "Sue Flay",
                    email = "sue.flay@gmail.com",
                    password = "1234",
                    dietarypreference = "Pescatarian",
                    allergies = listOf("gluten")
                ),
                User(
                    id = "pepperlover",
                    name = "Zoltan Pepper",
                    email = "zoltan.pepper@gmail.com",
                    password = "spicy",
                    dietarypreference = "Vegan",
                    allergies = listOf("Seafood", "Sesame", "Soy", "Shellfish")
                )
            )
        )
        UserStore.currentUser = UserStore.allUsers.first()
    }

    @Test
    fun create_account_clicked_sets_error_when_name_is_blank() {
        val viewModel = SignupViewModel(userRepo = MockUserRepository())

        viewModel.email_change("new.user@gmail.com")
        viewModel.password_change("abcdef")
        viewModel.confirm_password_change("abcdef")

        viewModel.create_account_clicked {}

        assertNotNull(viewModel.ui_state.errorMessage)
    }

    @Test
    fun create_account_clicked_sets_error_when_email_is_invalid() {
        val viewModel = SignupViewModel(userRepo = MockUserRepository())

        viewModel.name_change("New User")
        viewModel.email_change("invalid-email")
        viewModel.password_change("abcdef")
        viewModel.confirm_password_change("abcdef")

        viewModel.create_account_clicked {}

        assertNotNull(viewModel.ui_state.errorMessage)
    }

    @Test
    fun create_account_clicked_sets_error_when_passwords_do_not_match() {
        val viewModel = SignupViewModel(userRepo = MockUserRepository())

        viewModel.name_change("New User")
        viewModel.email_change("new.user@gmail.com")
        viewModel.password_change("abcdef")
        viewModel.confirm_password_change("abcdeg")

        viewModel.create_account_clicked {}

        assertNotNull(viewModel.ui_state.errorMessage)
    }

    @Test
    fun create_account_clicked_rejects_duplicate_mock_email() = runTest {
        val viewModel = SignupViewModel(userRepo = MockUserRepository())
        var successCalled = false
        val beforeCount = UserStore.allUsers.size

        viewModel.name_change("Another Sue")
        viewModel.email_change("sue.flay@gmail.com")
        viewModel.password_change("abcdef")
        viewModel.confirm_password_change("abcdef")

        viewModel.create_account_clicked {
            successCalled = true
        }

        withTimeout(2_000) {
            while (!successCalled && viewModel.ui_state.errorMessage == null) {
                delay(10)
            }
        }

        assertFalse(successCalled)
        assertEquals(beforeCount, UserStore.allUsers.size)
        assertNotNull(viewModel.ui_state.errorMessage)
    }

    @Test
    fun create_account_clicked_adds_new_mock_user_and_calls_success() = runTest {
        val viewModel = SignupViewModel(userRepo = MockUserRepository())
        var successCalled = false
        val beforeCount = UserStore.allUsers.size

        viewModel.name_change("New User")
        viewModel.email_change("new.user@gmail.com")
        viewModel.password_change("abcdef")
        viewModel.confirm_password_change("abcdef")

        viewModel.create_account_clicked {
            successCalled = true
        }

        withTimeout(2_000) {
            while (!successCalled) {
                delay(10)
            }
        }

        assertTrue(successCalled)
        assertEquals(beforeCount + 1, UserStore.allUsers.size)
        assertTrue(UserStore.allUsers.any { it.email == "new.user@gmail.com" })
    }
}