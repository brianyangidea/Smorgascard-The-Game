package ca.uwaterloo.cook_sharp.ui.screens.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.domain.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DietPreferenceViewModel(private val model: Model = Model()) : ViewModel() {

    var ui_state by mutableStateOf(DietPreferenceState())
        private set

    init {
        model.getCurrentUser()?.let { user ->
            ui_state = ui_state.copy(selectedDiet = user.dietarypreference)
        }
    }

    val dietOptions = listOf(
        "No Dietary restriction",
        "Vegetarian",
        "Vegan",
        "Pescatarian",
        "Halal",
        "Ketogenic",
        "Gluten Free",
        "Lacto-Vegetarian",
        "Ovo-Vegetarian",
        "Paleo",
        "Primal",
        "Low FODMAP",
        "Whole30"
    )

    fun diet_selected(option: String) {
        ui_state = ui_state.copy(selectedDiet = option, errorMessage = null)
    }

    fun continue_clicked(onSuccess: (String) -> Unit) {
        val diet = ui_state.selectedDiet
        if (diet == null) {
            ui_state = ui_state.copy(errorMessage = "Please select a diet preference.")
            return
        }
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    model.getCurrentUser()?.let { user ->
                        model.updateUser(user.copy(dietarypreference = diet))
                    }
                }
            }.onFailure { Log.e("DIET_PREF_VM", "Failed to save diet preference", it) }
            onSuccess(diet)
        }
    }
}
