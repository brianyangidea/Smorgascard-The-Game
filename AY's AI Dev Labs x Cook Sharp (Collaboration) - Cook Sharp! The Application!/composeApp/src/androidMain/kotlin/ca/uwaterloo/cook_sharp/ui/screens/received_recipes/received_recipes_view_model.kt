package ca.uwaterloo.cook_sharp.ui.screens.received_recipes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.domain.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ca.uwaterloo.cook_sharp.data.repository.RecipeShareRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseRecipeShareRepository

/**
 * Functionalities:
 * - Load recipes shared with the current user
 * - toggle like should update the db and other views
 */
data class ReceivedRecipeItem(
    val recipe: Recipe,
    val senderName: String,
    val message: String?
)

class ReceivedRecipesViewModel(
    private val model: Model = Model(),
    private val shareRepo: RecipeShareRepository = SupabaseRecipeShareRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var receivedRecipes by mutableStateOf<List<ReceivedRecipeItem>>(emptyList())
        private set

    init {
        loadReceivedRecipes()
    }

    fun loadReceivedRecipes() {
        viewModelScope.launch {
            isLoading = true
            receivedRecipes = try {
                withContext(ioDispatcher) {
                    shareRepo.getReceivedRecipes().map { shared ->
                        ReceivedRecipeItem(
                            recipe = shared.recipe,
                            senderName = shared.senderName,
                            message = shared.message
                        )
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }

            isLoading = false
        }
    }

    fun toggleLike(recipeId: Long) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    model.toggleLike(recipeId)
                }
                loadReceivedRecipes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}