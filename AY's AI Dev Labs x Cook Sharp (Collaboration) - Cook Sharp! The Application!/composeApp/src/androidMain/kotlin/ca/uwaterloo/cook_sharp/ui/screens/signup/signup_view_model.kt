package ca.uwaterloo.cook_sharp.ui.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.mock.UserStore
import ca.uwaterloo.cook_sharp.data.repository.SupabaseUserRepository
import ca.uwaterloo.cook_sharp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * functionalities:
 * - Track sign up form fields such as name, email, and password
 * - Validate required fields and input rules - email must have @gmail.com, and password should be atleast 6 characters
 */

class SignupViewModel(
    private val userRepo: UserRepository = SupabaseUserRepository
) : ViewModel() {

    var ui_state by mutableStateOf(SignupState())
        private set

    fun name_change(newValue: String) {
        ui_state = ui_state.copy(name = newValue, errorMessage = null)
    }

    fun email_change(newValue: String) {
        ui_state = ui_state.copy(email = newValue, errorMessage = null)
    }

    fun password_change(newValue: String) {
        ui_state = ui_state.copy(password = newValue, errorMessage = null)
    }

    fun confirm_password_change(newValue: String) {
        ui_state = ui_state.copy(confirmPassword = newValue, errorMessage = null)
    }

    fun create_account_clicked(onSuccess: () -> Unit) {
        val s = ui_state

        if (s.name.isBlank()) {
            ui_state = s.copy(errorMessage = "Please enter your name.")
            return
        }

        if (s.email.isBlank() || "@" !in s.email) {
            ui_state = s.copy(errorMessage = "Please enter a valid email.")
            return
        }

        if (s.password.isBlank() || s.password.length < 6) {
            ui_state = s.copy(errorMessage = "Password must be at least 6 characters.")
            return
        }

        if (s.password != s.confirmPassword) {
            ui_state = s.copy(errorMessage = "Passwords do not match.")
            return
        }

        // // Check if username (email) already exists
        // if (UserStore.allUsers.any { it.email == s.email }) {
        //     ui_state = s.copy(errorMessage = "An account with this email already exists.")
        //     return false
        // }

        // // Create the new user and add to UserStore
        // // Note: Using email as the initial ID for login purposes
        // val newUser = User(
        //     id = s.email,
        //     name = s.name,
        //     email = s.email,
        //     password = s.password
        // )

        // UserStore.updateUser(newUser)
        // viewModelScope.launch {
        //     try {
        //         withContext(Dispatchers.IO) { userRepo.getCurrentUser() }
        //     } catch (e: Exception) {
        //         e.printStackTrace()
        //     }
        // }

        // return true
        viewModelScope.launch {
            // try {
            //     withContext(Dispatchers.IO) { userRepo.getCurrentUser() }
            // } catch (e: Exception) {
            //     e.printStackTrace()
            // }
            // return true
            val newUser = withContext(Dispatchers.IO) {
                userRepo.signup(s.name, s.email, s.password)
            }

            if (newUser == null) {
                ui_state = s.copy(errorMessage = "An account with this email already exists.")
            } else {
                UserStore.updateUser(newUser)
                onSuccess()
            }
        }
    }
}
