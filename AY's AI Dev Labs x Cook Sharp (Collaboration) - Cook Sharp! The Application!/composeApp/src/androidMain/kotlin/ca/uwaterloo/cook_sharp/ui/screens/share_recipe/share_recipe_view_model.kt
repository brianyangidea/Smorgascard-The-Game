package ca.uwaterloo.cook_sharp.ui.screens.share_recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.repository.SupabaseUserRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseRecipeShareRepository
import ca.uwaterloo.cook_sharp.domain.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ca.uwaterloo.cook_sharp.data.repository.RecipeShareRepository

/**
 * Functionality:
 * - Load users with whom a recipe can be shared
 * - Track selected recipient and message input
 * - Validate share inputs before sending - recipient is selected and is not the current user
 */
data class ShareUserItem(
    val id: String,
    val name: String,
    val email: String
)

data class ShareRecipeUiState(
    val isLoading: Boolean = false,
    val recipeTitle: String? = null,
    val searchQuery: String = "",
    val allUsers: List<ShareUserItem> = emptyList(),
    val selectedUserIds: Set<String> = emptySet(),
    val personalMessage: String = "",
    val message: String? = null
) {
    val filteredUsers: List<ShareUserItem>
        get() {
            if (searchQuery.isBlank()) return allUsers
            val q = searchQuery.trim().lowercase()
            return allUsers.filter {
                it.name.lowercase().contains(q) || it.email.lowercase().contains(q)
            }
        }
}

class ShareRecipeViewModel(
    private val model: Model = Model()
) : ViewModel() {

    var uiState by mutableStateOf(ShareRecipeUiState())
        private set

    private var recipeId: Long = 0L
    private val shareRepo: RecipeShareRepository = SupabaseRecipeShareRepository()

    fun load(id: Long) {
        recipeId = id
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val recipe = withContext(Dispatchers.IO) {
                model.getRecipeById(id)
            }

            val users = withContext(Dispatchers.IO) {
                loadUsersExceptCurrent()
            }

            uiState = uiState.copy(
                isLoading = false,
                recipeTitle = recipe?.title,
                allUsers = users
            )
        }
    }

    fun onSearchQueryChanged(value: String) {
        uiState = uiState.copy(searchQuery = value)
    }

    fun onMessageChanged(value: String) {
        uiState = uiState.copy(personalMessage = value)
    }

    fun toggleUserSelection(userId: String) {
        val updated = uiState.selectedUserIds.toMutableSet()
        if (!updated.add(userId)) updated.remove(userId)
        uiState = uiState.copy(selectedUserIds = updated)
    }

    fun sendShare(onSuccess: () -> Unit) {
        if (uiState.selectedUserIds.isEmpty()) {
            uiState = uiState.copy(message = "Select at least one user")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                withContext(Dispatchers.IO) {
                    shareRepo.shareRecipe(
                        recipeId = recipeId,
                        recipientUserIds = uiState.selectedUserIds.toList(),
                        message = uiState.personalMessage
                    )
                }

                uiState = uiState.copy(
                    isLoading = false,
                    message = "Recipe shared successfully"
                )

                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    message = "Failed to share recipe"
                )
            }
        }
    }

    fun clearMessage() {
        uiState = uiState.copy(message = null)
    }

    private suspend fun loadUsersExceptCurrent(): List<ShareUserItem> {
        val currentUserId = SupabaseUserRepository.getCurrentUser()?.id
        return SupabaseUserRepository.getAllUsers()
            .filter { it.id != currentUserId }
            .map {
                ShareUserItem(
                    id = it.id,
                    name = it.name,
                    email = it.email
                )
            }
    }
}