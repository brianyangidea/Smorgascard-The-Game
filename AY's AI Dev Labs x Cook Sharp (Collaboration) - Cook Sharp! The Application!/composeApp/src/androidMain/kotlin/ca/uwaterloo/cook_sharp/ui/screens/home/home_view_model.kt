package ca.uwaterloo.cook_sharp.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.supabase.LikedRecipesManager
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.User
import ca.uwaterloo.cook_sharp.ui.screens.filter.FilterState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Functionalities
 * - Load recipes from the DB - all API, current user, and recieved recipes
 * - Support recipe refresh and fallback - use spoonacular API when no recipes loaded from DB
 * - Manage interactions such as liking and unliking recipes
 * - implement search feature - if searched recipe not found in the DB then use spoonacular API to search the recipe
 * - handle filter logic
 */
class HomeViewModel(
    private val model: Model = Model(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    var ui_state by mutableStateOf(HomeState())
        private set

    val currentUser: User?
        get() = model.getCurrentUser()

    val visibleRecipes: List<Recipe>
        get() = ui_state.recipes

    private var currentOffset = 0
    private val pageSize = 10
    private var reachedEnd = false
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            // Wait for user to be authenticated before loading
            var attempts = 0
            while (model.getCurrentUser() == null && attempts < 50) {
                delay(100)
                attempts++
            }

            loadAllRecipes()

            LikedRecipesManager.likedRecipeIds.collect { likedIds ->
                ui_state = ui_state.copy(
                    recipes = ui_state.recipes.map { recipe ->
                        recipe.copy(isLiked = recipe.id in likedIds)
                    }
                )
            }
        }
    }

    fun loadAllRecipes() {
        currentOffset = 0
        reachedEnd = false

        viewModelScope.launch {
            ui_state = ui_state.copy(isLoading = true)
            try {
                val recipes = withContext(ioDispatcher) {
                    model.allRecipes(limit = pageSize, offset = 0)
                }

                currentOffset = recipes.size
                if (recipes.size < pageSize) reachedEnd = true

                ui_state = ui_state.copy(
                    recipes = recipes,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ui_state = ui_state.copy(
                    recipes = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun loadMoreRecipes() {
        if (ui_state.isLoading || reachedEnd) return
        if (ui_state.search.isNotBlank()) return
        if (ui_state.filterRecipe != null) return
        if (ui_state.activeFilter != null) return

        viewModelScope.launch {
            ui_state = ui_state.copy(isLoading = true)
            try {
                val moreRecipes = withContext(ioDispatcher) {
                    model.allRecipes(limit = pageSize, offset = currentOffset)
                }

                if (moreRecipes.isEmpty()) {
                    reachedEnd = true
                } else {
                    currentOffset += moreRecipes.size
                    if (moreRecipes.size < pageSize) reachedEnd = true

                    ui_state = ui_state.copy(
                        recipes = ui_state.recipes + moreRecipes,
                        isLoading = false
                    )
                    return@launch
                }

                ui_state = ui_state.copy(isLoading = false)
            } catch (e: Exception) {
                e.printStackTrace()
                ui_state = ui_state.copy(isLoading = false)
            }
        }
    }

    fun search_change(value: String) {
        ui_state = ui_state.copy(search = value)
        searchJob?.cancel()

        if (value.isBlank()) {
            loadAllRecipes()
            return
        }

        searchJob = viewModelScope.launch {
            delay(350)
            ui_state = ui_state.copy(isLoading = true)
            try {
                val recipes = withContext(ioDispatcher) {
                    model.searchRecipes(value)
                }
                ui_state = ui_state.copy(
                    recipes = recipes,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ui_state = ui_state.copy(
                    recipes = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun clear_search() {
        ui_state = ui_state.copy(
            search = "",
            filterRecipe = null
        )
        loadAllRecipes()
    }

    fun recipe_selected(meal: MealType?) {
        ui_state = ui_state.copy(filterRecipe = meal, activeFilter = null)

        if (meal == null) {
            loadAllRecipes()
            return
        }

        viewModelScope.launch {
            ui_state = ui_state.copy(isLoading = true)
            try {
                val recipes = withContext(ioDispatcher) {
                    model.recipesByMealType(meal)
                }
                ui_state = ui_state.copy(
                    recipes = recipes,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ui_state = ui_state.copy(
                    recipes = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun filter_icon_clicked() {
        ui_state = ui_state.copy(filterActive = !ui_state.filterActive)
    }

    fun change_like(recipeId: Long) {
        viewModelScope.launch {
            try {
                withContext(ioDispatcher) {
                    model.toggleLike(recipeId)
                }

                refreshCurrentRecipeList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun refreshCurrentRecipeList() {
        val currentState = ui_state

        val refreshedRecipes = withContext(ioDispatcher) {
            when {
                currentState.activeFilter != null -> {
                    model.filteredRecipes(currentState.activeFilter!!)
                }

                currentState.search.isNotBlank() -> {
                    model.searchRecipes(currentState.search)
                }

                currentState.filterRecipe != null -> {
                    model.recipesByMealType(currentState.filterRecipe!!)
                }

                else -> {
                    model.allRecipes(limit = currentOffset.coerceAtLeast(pageSize), offset = 0)
                }
            }
        }

        ui_state = ui_state.copy(recipes = refreshedRecipes)
    }

    fun applyFilter(filterState: FilterState) {
        val filter = FilterRecipe(
            selectedMealTypes = filterState.selectedMealTypes,
            minCalories = filterState.minCalories,
            maxCalories = filterState.maxCalories,
            excludedIngredients = filterState.excludedIngredients,
            selectedDiets = filterState.selectedDiets,
            selectedCuisines = filterState.selectedCuisines
        )

        ui_state = ui_state.copy(
            activeFilter = if (filter == FilterRecipe()) null else filter,
            filterActive = filter != FilterRecipe(),
            filterRecipe = null,
            search = "",
            isLoading = true
        )

        viewModelScope.launch {
            try {
                val recipes = withContext(ioDispatcher) {
                    model.filteredRecipes(filter)
                }
                ui_state = ui_state.copy(recipes = recipes, isLoading = false)
            } catch (e: Exception) {
                e.printStackTrace()
                ui_state = ui_state.copy(recipes = emptyList(), isLoading = false)
            }
        }
    }
}