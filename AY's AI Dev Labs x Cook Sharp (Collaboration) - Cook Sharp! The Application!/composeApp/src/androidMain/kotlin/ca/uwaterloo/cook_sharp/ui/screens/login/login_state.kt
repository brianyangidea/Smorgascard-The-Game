package ca.uwaterloo.cook_sharp.ui.screens.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null
) {
    val can_submit: Boolean
        get() = username.isNotBlank() && password.isNotBlank()
}