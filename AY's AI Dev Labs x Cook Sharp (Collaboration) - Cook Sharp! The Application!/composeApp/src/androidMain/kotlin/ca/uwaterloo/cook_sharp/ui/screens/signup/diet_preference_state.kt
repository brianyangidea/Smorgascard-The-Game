package ca.uwaterloo.cook_sharp.ui.screens.signup

data class DietPreferenceState(
    val selectedDiet: String? = null,
    val errorMessage: String? = null
) {
    val can_continue: Boolean
        get() = selectedDiet != null
}