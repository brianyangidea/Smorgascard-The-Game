package ca.uwaterloo.cook_sharp.ui.screens.signup
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.domain.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Ingredient(
    val id: String,
    val label: String,
    @DrawableRes val iconRes: Int? = null
)
class FoodAllergyViewModel(private val model: Model = Model()) : ViewModel() {

    var ui_state by mutableStateOf(FoodAllergyUiState())
        private set

    private fun normalizeAllergyKey(value: String): String {
        return value.trim().lowercase()
    }

    init {
        model.getCurrentUser()?.let { user ->
            ui_state = ui_state.copy(
                allergies = user.allergies.map(::normalizeAllergyKey).toSet()
            )
        }
    }

    val allergyOptions = listOf(
        Ingredient("gluten", "Gluten", R.drawable.gluten),
        Ingredient("egg", "Egg", R.drawable.egg),
        Ingredient("fish", "Fish", R.drawable.fish),
        Ingredient("dairy", "Dairy", R.drawable.dairy),
        Ingredient("soy", "Soy", R.drawable.soy),
        Ingredient("nuts", "Nuts", R.drawable.nuts),
        Ingredient("sesame", "Sesame", R.drawable.sesame),
        Ingredient("mustard", "Mustard", R.drawable.mustard),
        Ingredient("celery", "Celery", R.drawable.celery),
        Ingredient("pineapple", "Pineapple", R.drawable.pineapple),
        Ingredient("steak", "Steak", R.drawable.steak),
        Ingredient("lupin", "Lupin", R.drawable.lupin),
        Ingredient("molluscs", "Molluscs", R.drawable.molluscs)
    )

    fun change_allergy(id: String) {
        val normalizedId = normalizeAllergyKey(id)
        val current = ui_state.allergies.toMutableSet()

        if (current.contains(normalizedId)) current.remove(normalizedId) else current.add(normalizedId)

        ui_state = ui_state.copy(allergies = current.toSet())
    }

    fun saveAllergies(onSaved: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    model.getCurrentUser()?.let { user ->
                        val normalized = ui_state.allergies
                            .map(::normalizeAllergyKey)
                            .distinct()
                        model.updateUser(user.copy(allergies = normalized))
                    }
                }
            }.onFailure { Log.e("ALLERGY_VM", "Failed to save allergies", it) }
            onSaved()
        }
    }
}