package ca.uwaterloo.cook_sharp.ui.screens.liked_recipes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.supabase.LikedRecipesManager
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.domain.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class LikedRecipesViewModel(
    private val model: Model = Model(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var likedRecipes by mutableStateOf<List<Recipe>>(emptyList())
        private set

    val currentUser: ca.uwaterloo.cook_sharp.domain.User?
        get() = model.getCurrentUser()

    init {
        viewModelScope.launch {
            // Wait for user to be authenticated
            var attempts = 0
            while (model.getCurrentUser() == null && attempts < 50) {
                delay(100)
                attempts++
            }

            loadLikedRecipes()

            LikedRecipesManager.likedRecipeIds.collect { likedIds ->
                loadLikedRecipes()
            }
        }
    }

    private fun loadLikedRecipes() {
        viewModelScope.launch {
            likedRecipes = withContext(ioDispatcher) {
                model.likedRecipes()
            }
        }
    }

    fun toggleLike(recipeId: Long) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    model.toggleLike(recipeId)
                }
                loadLikedRecipes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}