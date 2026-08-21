package ca.uwaterloo.cook_sharp.data.supabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LikedRecipesManager {
    private val _likedRecipeIds = MutableStateFlow<Set<Long>>(emptySet())
    val likedRecipeIds: StateFlow<Set<Long>> = _likedRecipeIds.asStateFlow()

    fun isLiked(recipeId: Long): Boolean = recipeId in _likedRecipeIds.value

    fun setLikedRecipeIds(ids: Set<Long>) {
        _likedRecipeIds.value = ids
    }

    fun addLiked(recipeId: Long) {
        _likedRecipeIds.value = _likedRecipeIds.value + recipeId
    }

    fun removeLiked(recipeId: Long) {
        _likedRecipeIds.value = _likedRecipeIds.value - recipeId
    }

    fun toggleLiked(recipeId: Long) {
        if (isLiked(recipeId)) removeLiked(recipeId) else addLiked(recipeId)
    }

    fun clear() {
        _likedRecipeIds.value = emptySet()
    }
}