package ca.uwaterloo.cook_sharp.ui.screens.login

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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

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
    fun login_clicked_sets_error_when_fields_are_blank() {
        val viewModel = LoginViewModel(userRepo = MockUserRepository())
        var successCalled = false

        viewModel.login_clicked {
            successCalled = true
        }

        assertEquals("Please enter username and password.", viewModel.ui_state.errorMessage)
        assertFalse(successCalled)
    }

    @Test
    fun username_change_and_password_change_update_state() {
        val viewModel = LoginViewModel(userRepo = MockUserRepository())

        viewModel.username_change("sueflay999")
        viewModel.password_change("1234")

        assertEquals("sueflay999", viewModel.ui_state.username)
        assertEquals("1234", viewModel.ui_state.password)
        assertEquals(null, viewModel.ui_state.errorMessage)
    }

    @Test
    fun login_clicked_with_wrong_credentials_sets_error() = runTest {
        val viewModel = LoginViewModel(userRepo = MockUserRepository())
        var successCalled = false

        viewModel.username_change("wrong-user")
        viewModel.password_change("wrong-pass")

        viewModel.login_clicked {
            successCalled = true
        }

        withTimeout(2_000) {
            while (viewModel.ui_state.errorMessage == null) {
                delay(10)
            }
        }

        assertFalse(successCalled)
        assertEquals("Incorrect username or password.", viewModel.ui_state.errorMessage)
        assertEquals("sueflay999", UserStore.currentUser.id)
    }

    @Test
    fun login_clicked_logs_in_with_valid_mock_user() = runTest {
        val viewModel = LoginViewModel(userRepo = MockUserRepository())
        var successCalled = false

        viewModel.username_change("sueflay999")
        viewModel.password_change("1234")

        viewModel.login_clicked {
            successCalled = true
        }

        withTimeout(2_000) {
            while (!successCalled) {
                delay(10)
            }
        }

        assertTrue(successCalled)
        assertEquals(null, viewModel.ui_state.errorMessage)
        assertEquals("sueflay999", UserStore.currentUser.id)
        assertEquals("sue.flay@gmail.com", UserStore.currentUser.email)
    }
}