package ca.uwaterloo.cook_sharp.ui.screens.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseStorageRepository
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.domain.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class SettingsViewModel(private val model: Model = Model()) : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
        private set

    var currentUser: User? by mutableStateOf(model.getCurrentUser())
        private set

    val currentDiet: String
        get() = currentUser?.dietarypreference ?: "None"

    val currentAllergies: String
        get() = currentUser?.allergies?.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } } ?: "None"

    fun updateProfilePicture(uri: String?, context: Context) {
        val user = currentUser ?: return
        if (uri == null) {
            val updated = user.copy(profilePictureUri = null)
            currentUser = updated
            viewModelScope.launch {
                withContext(Dispatchers.IO) { model.updateUser(updated) }
            }
            return
        }
        viewModelScope.launch {
            try {
                val imageUrl = withContext(Dispatchers.IO) {
                    val parsedUri = android.net.Uri.parse(uri)
                    val bytes = context.contentResolver.openInputStream(parsedUri)?.use { it.readBytes() }
                    if (bytes != null) {
                        SupabaseStorageRepository.uploadUserProfileImage(UUID.randomUUID().toString(), bytes)
                    } else uri
                }
                Log.d("SettingsVM", "uploadUserProfileImage succeeded: $imageUrl")
                val updated = user.copy(profilePictureUri = imageUrl)
                currentUser = updated
                withContext(Dispatchers.IO) { model.updateUser(updated) }
                Log.d("SettingsVM", "updateUser succeeded")
            } catch (e: Exception) {
                Log.e("SettingsVM", "updateProfilePicture failed", e)
            }
        }
    }
}
