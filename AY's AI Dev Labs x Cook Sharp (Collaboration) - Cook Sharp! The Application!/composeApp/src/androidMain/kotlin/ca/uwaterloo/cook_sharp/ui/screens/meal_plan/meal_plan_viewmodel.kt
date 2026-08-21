package ca.uwaterloo.cook_sharp.ui.screens.meal_plan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.supabase.LikedRecipesManager
import ca.uwaterloo.cook_sharp.data.repository.CombinedRecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.MealPlanRepository
import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.UserStore
import ca.uwaterloo.cook_sharp.data.repository.SupabaseMealPlanRepository
import ca.uwaterloo.cook_sharp.domain.Meal
import ca.uwaterloo.cook_sharp.domain.MealPlan
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.delay


/**
 * Functionality:
 * load correct meal plans for each signed-up users
 * keep track of all the selected meal plans for each user
 * able to delete selected meal plans for each user
 */
class MealPlanViewModel(
    private val recipeRepo: RecipeRepository = CombinedRecipeRepository(),
    private val mealPlanRepo: MealPlanRepository = SupabaseMealPlanRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val userId get() = UserStore.currentUser.id
    private val weekStartDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)

    private val _selectedDay = MutableStateFlow(0)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private val _mealPlan = MutableStateFlow<MealPlan?>(null)
    val mealPlan: StateFlow<MealPlan?> = _mealPlan.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _recipeCache = MutableStateFlow<Map<Long, Recipe>>(emptyMap())
    val recipeCache: StateFlow<Map<Long, Recipe>> = _recipeCache.asStateFlow()

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weekDates: List<LocalDate> = (0..6).map { weekStartDate.plusDays(it.toLong()) }

    init {
        viewModelScope.launch {
            // Wait for user to be authenticated
            var attempts = 0
            while (UserStore.currentUser == null && attempts < 50) {
                delay(100)
                attempts++
            }

            loadMealPlan()
        }

        viewModelScope.launch {
            // Subscribe to liked recipes changes
            LikedRecipesManager.likedRecipeIds.collect { likedIds ->
                _recipeCache.value = _recipeCache.value.mapValues { (id, recipe) ->
                    recipe.copy(isLiked = id in likedIds)
                }
            }
        }
    }

    fun reloadMealPlan() {
        viewModelScope.launch {
            loadMealPlan()
        }
    }

    private suspend fun loadMealPlan() {
        val isInitialLoad = _mealPlan.value == null
        if (isInitialLoad) {
            _recipeCache.value = emptyMap()
        }
        _isLoading.value = true

        runCatching {
            val plan = withContext(ioDispatcher) {
                mealPlanRepo.getMealPlansForWeek(userId, weekStartDate)
            }

            val recipeIds = plan?.meals
                ?.flatMap { it.meals }
                ?.mapNotNull { it.recipeId }
                ?.toSet()
                ?: emptySet()

            recipeIds.forEach { id ->
                if (id !in _recipeCache.value) {
                    runCatching {
                        withContext(ioDispatcher) { recipeRepo.getRecipeById(id) }
                    }
                        .onFailure { Log.e("MEAL_PLAN_VM", "Failed to load recipe $id", it) }
                        .getOrNull()
                        ?.let { _recipeCache.value = _recipeCache.value + (id to it) }
                }
            }

            _mealPlan.value = plan
        }.onFailure {
            Log.e("MEAL_PLAN_VM", "loadMealPlan failed", it)
        }

        _isLoading.value = false
    }

    fun setSelectedDay(dayIndex: Int) {
        _selectedDay.value = dayIndex
    }

    private fun selectedDate(dayIndex: Int): LocalDate =
        weekStartDate.plusDays(dayIndex.toLong())

    fun getMealsForDay(dayIndex: Int): List<Meal> {
        val plan = _mealPlan.value ?: return emptyList()
        val date = selectedDate(dayIndex)
        return plan.meals.firstOrNull { it.date == date }?.meals ?: emptyList()
    }

    fun getRecipeForMeal(meal: Meal): Recipe? =
        meal.recipeId?.let { _recipeCache.value[it] }

    fun toggleLike(recipeId: Long) {
        viewModelScope.launch {
            runCatching {
                withContext(ioDispatcher) { recipeRepo.toggleLike(recipeId) }
            }.onFailure { Log.e("MEAL_PLAN_VM", "toggleLike failed", it) }
        }
    }

    fun addMeal(dayIndex: Int, mealType: MealType, recipeId: Long, label: String?) {
        viewModelScope.launch {
            runCatching {
                withContext(ioDispatcher) {
                    mealPlanRepo.addMeal(
                        userId = userId,
                        weekStartDate = weekStartDate,
                        date = selectedDate(dayIndex),
                        mealType = mealType,
                        recipeId = recipeId,
                        label = label
                    )
                }
                loadMealPlan()
            }.onFailure { Log.e("MEAL_PLAN_VM", "addMeal failed", it) }
        }
    }

    fun updateMealById(dayIndex: Int, mealId: Int, recipeId: Long, label: String?) {
        viewModelScope.launch {
            runCatching {
                val plan = _mealPlan.value ?: return@runCatching
                val date = selectedDate(dayIndex)

                val updatedPlan = plan.withUpdatedDay(date) { meals ->
                    meals.map { if (it.id == mealId) it.copy(recipeId = recipeId, label = label) else it }
                }

                withContext(ioDispatcher) { mealPlanRepo.setMealPlan(updatedPlan) }
                loadMealPlan()
            }.onFailure { Log.e("MEAL_PLAN_VM", "updateMealById failed", it) }
        }
    }

    fun updateMealLabelById(dayIndex: Int, mealId: Int, label: String?) {
        viewModelScope.launch {
            runCatching {
                val plan = _mealPlan.value ?: return@runCatching
                val date = selectedDate(dayIndex)

                val updatedPlan = plan.withUpdatedDay(date) { meals ->
                    meals.map { if (it.id == mealId) it.copy(label = label) else it }
                }

                withContext(ioDispatcher) { mealPlanRepo.setMealPlan(updatedPlan) }
                loadMealPlan()
            }.onFailure { Log.e("MEAL_PLAN_VM", "updateMealLabelById failed", it) }
        }
    }

    fun deleteMealById(dayIndex: Int, mealId: Int) {
        viewModelScope.launch {
            runCatching {
                val plan = _mealPlan.value ?: return@runCatching
                val date = selectedDate(dayIndex)

                val updatedPlan = plan.withUpdatedDay(date) { meals ->
                    meals.filter { it.id != mealId }
                }

                withContext(ioDispatcher) { mealPlanRepo.setMealPlan(updatedPlan) }
                loadMealPlan()
            }.onFailure { Log.e("MEAL_PLAN_VM", "deleteMealById failed", it) }
        }
    }

    private fun MealPlan.withUpdatedDay(
        date: LocalDate,
        transform: (List<Meal>) -> List<Meal>
    ): MealPlan {
        val updatedDays = meals.map { day ->
            if (day.date == date) day.copy(meals = transform(day.meals)) else day
        }
        return copy(meals = updatedDays)
    }
}