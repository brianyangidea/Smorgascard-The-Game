package ca.uwaterloo.cook_sharp.ui.screens.signup

data class SignupState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null
) {
    val can_submit: Boolean
        get() = name.isNotBlank() && email.isNotBlank()&& password.isNotBlank() && confirmPassword.isNotBlank() && password == confirmPassword
}