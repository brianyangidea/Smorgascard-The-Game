package ca.uwaterloo.cook_sharp.ui.screens.login

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
import java.io.IOException

/**
* Functionalities
 * - Track login form input such as email and password
 * - check that required credentials are provided
 * - check the credentials are correct
 */
class LoginViewModel(
    private val userRepo: UserRepository = SupabaseUserRepository
) : ViewModel() {
    var ui_state by mutableStateOf(LoginState())
        private set

    fun username_change(newValue: String) {
        ui_state = ui_state.copy(
            username = newValue,
            errorMessage = null
        )
    }

    fun password_change(newValue: String) {
        ui_state = ui_state.copy(
            password = newValue,
            errorMessage = null
        )
    }

    fun login_clicked(onSuccess: () -> Unit) {
        if (!ui_state.can_submit) {
            ui_state = ui_state.copy(errorMessage = "Please enter username and password.")
            return
        }

        viewModelScope.launch {
            try {
                val authenticatedUser = withContext(Dispatchers.IO) {
                    userRepo.login(ui_state.username, ui_state.password)
                }

                if (authenticatedUser == null) {
                    ui_state = ui_state.copy(errorMessage = "Incorrect username or password.")
                } else {
                    UserStore.updateUser(authenticatedUser)
                    onSuccess()
                }
            } catch (e: IOException) {
                ui_state = ui_state.copy(errorMessage = "Network error. Please check your connection.")
            } catch (e: Exception) {
                ui_state = ui_state.copy(errorMessage = "Exception at login_view_model login_clicked()")
            }
        }
    }

    // fun login_clicked(): Boolean {
    //     if (!ui_state.can_submit) {
    //         ui_state = ui_state.copy(errorMessage = "Please enter username and password.")
    //         return false
    //     }
    //     val authenticatedUser = UserStore.allUsers.find { it.id == ui_state.username && it.password == ui_state.password }
    //     if (authenticatedUser == null) {
    //         ui_state = ui_state.copy(errorMessage = "Incorrect username or password.")
    //         return false
    //     }
    //     UserStore.updateUser(authenticatedUser)
    //     return true
    // }
}
