package ca.uwaterloo.cook_sharp.ui.screens.recipe_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.supabase.LikedRecipesManager
import ca.uwaterloo.cook_sharp.data.repository.GroceryListRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseGroceryListRepository
import ca.uwaterloo.cook_sharp.domain.Model
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

/**
* Functionalities
 * - Load a recipe by its id
 * - Track loading and error states
 * - Handle like and unlike actions
 * - Support detail-screen actions such as sharing or adding to meal plans
 */
class RecipeDetailViewModel(
    private val model: Model = Model(),
    private val groceryRepo: GroceryListRepository = SupabaseGroceryListRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var ui_state by mutableStateOf(RecipeDetailUiState())
        private set

    init {
        viewModelScope.launch {
            // Wait for user to be authenticated
            var attempts = 0
            while (model.getCurrentUser() == null && attempts < 50) {
                delay(100)
                attempts++
            }

            LikedRecipesManager.likedRecipeIds.collect { likedIds ->
                val recipeId = ui_state.recipe?.id ?: return@collect
                val newIsLiked = recipeId in likedIds

                if (newIsLiked != ui_state.isLiked) {
                    ui_state = ui_state.copy(
                        isLiked = newIsLiked,
                        recipe = ui_state.recipe?.copy(isLiked = newIsLiked)
                    )
                }
            }
        }
    }

    fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            ui_state = ui_state.copy(isLoading = true)

            val recipe = withContext(ioDispatcher) {
                model.getRecipeById(recipeId)
            }

            val isCurrentlyLiked = LikedRecipesManager.isLiked(recipeId)

            val userAllergies = model.getCurrentUser()?.allergies ?: emptyList()
            val matchedAllergies = if (recipe != null) {
                userAllergies.filter { allergy -> recipe.containsAllergen(allergy) }
            } else emptyList()

            ui_state = ui_state.copy(
                recipe = recipe,
                servings = recipe?.servings ?: 1,
                isLiked = isCurrentlyLiked,
                isLoading = false,
                matchedAllergies = matchedAllergies
            )
        }
    }

    fun toggleLike() {
        val id = ui_state.recipe?.id ?: return

        val newIsLiked = !ui_state.isLiked
        ui_state = ui_state.copy(
            isLiked = newIsLiked,
            recipe = ui_state.recipe?.copy(isLiked = newIsLiked)
        )

        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    model.toggleLike(id)
                }
            } catch (e: Exception) {
                ui_state = ui_state.copy(
                    isLiked = !newIsLiked,
                    recipe = ui_state.recipe?.copy(isLiked = !newIsLiked)
                )
            }
        }
    }

    fun increment_servings() {
        ui_state = ui_state.copy(servings = ui_state.servings + 1)
    }

    fun decrement_servings() {
        if (ui_state.servings > 1) {
            ui_state = ui_state.copy(servings = ui_state.servings - 1)
        }
    }

    fun addToGroceryList() {
        val recipe = ui_state.recipe ?: return
        val servings = ui_state.servings
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    groceryRepo.addRecipeToGroceryList(recipe.id, servings)
                }
                ui_state = ui_state.copy(
                    snackbarMessage = "Added $servings serving to grocery list"
                )
            } catch (e: Exception) {
                ui_state = ui_state.copy(
                    snackbarMessage = "Failed to add to grocery list"
                )
            }
        }
    }

    fun clearMessage() {
        ui_state = ui_state.copy(snackbarMessage = null)
    }
    fun getCurrentUserId(): String? {
        return model.getCurrentUser()?.id
    }
}