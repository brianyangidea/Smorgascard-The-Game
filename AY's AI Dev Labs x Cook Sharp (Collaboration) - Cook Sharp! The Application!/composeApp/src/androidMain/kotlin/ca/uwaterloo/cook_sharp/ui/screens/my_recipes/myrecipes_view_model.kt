package ca.uwaterloo.cook_sharp.ui.screens.my_recipes

import android.util.Log
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

/**
 * Functionalities:
 * - Load recipes created by the current user
 * - toggle like should update the db and other views
 */
class MyRecipesViewModel(
    private val model: Model = Model(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    var myRecipes by mutableStateOf<List<Recipe>>(emptyList())
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

            loadMyRecipes()

            LikedRecipesManager.likedRecipeIds.collect { likedIds ->
                myRecipes = myRecipes.map { recipe ->
                    recipe.copy(isLiked = recipe.id in likedIds)
                }
            }
        }
    }

    private fun loadMyRecipes() {
        viewModelScope.launch {
            try {
                myRecipes = withContext(ioDispatcher) {
                    model.userRecipes()
                }
            } catch (e: Exception) {
                Log.e("MY_RECIPES_VM", "loadMyRecipes failed", e)
                myRecipes = emptyList()
            }
        }
    }

    fun toggleLike(recipeId: Long) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    model.toggleLike(recipeId)
                }
            } catch (e: Exception) {
                Log.e("MY_RECIPES_VM", "toggleLike failed", e)
            }
        }
    }
}