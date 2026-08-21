package ca.uwaterloo.cook_sharp.ui.screens.nutrients

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.repository.CombinedRecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.MealPlanRepository
import ca.uwaterloo.cook_sharp.data.repository.NutritionGoalRepository
import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.UserStore
import ca.uwaterloo.cook_sharp.data.repository.SupabaseMealPlanRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseNutritionGoalRepository
import ca.uwaterloo.cook_sharp.domain.GoalType
import ca.uwaterloo.cook_sharp.domain.NutritionGoal
import ca.uwaterloo.cook_sharp.domain.NutritionTarget
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

/** functionality
 * load all the nutrient goals and current nutrient status of each user
 * each nutrient status gets updated as each user adds their meal plans
 */
data class NutrientTotals(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0
)

data class NutrientsUiState(
    val dateRange: String = "",
    val weeklyAvg: Float = 0f,
    val weeklyBars: List<Float> = emptyList(),
    val totals: NutrientTotals = NutrientTotals(),
    val dailyTotals: List<NutrientTotals> = emptyList(),
    val selectedDayIndex: Int? = null,
    val isLoading: Boolean = false,
    val goal: NutritionGoal = NutritionGoal(
        userId = "",
        weeklyTarget = NutritionTarget(),
        goalType = GoalType.MAINTAIN
    )
)

class NutrientsDashboardViewmodel(
    private val mealPlanRepo: MealPlanRepository = SupabaseMealPlanRepository(),
    private val recipeRepo: RecipeRepository = CombinedRecipeRepository(),
    private val nutritionGoalRepo: NutritionGoalRepository = SupabaseNutritionGoalRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val userId get() = UserStore.currentUser.id
    private val weekStartDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
    private val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<NutrientsUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            loadGoal()
            refreshNutrients()
        }
    }

    fun reload() {
        viewModelScope.launch {
            loadGoal()
            refreshNutrients()
        }
    }

    private suspend fun loadGoal() {
        runCatching {
            val goal = withContext(ioDispatcher) { nutritionGoalRepo.getNutritionGoal(userId) }
            if (goal != null) {
                _uiState.value = _uiState.value.copy(goal = goal)
            } else {
                _uiState.value = _uiState.value.copy(
                    goal = _uiState.value.goal.copy(userId = userId)
                )
            }
        }.onFailure { Log.e("NUTRIENTS_VM", "loadGoal failed", it) }
    }

    fun updateGoal(newGoal: NutritionGoal, onSaved: () -> Unit = {}) {
        val goalWithUserId = newGoal.copy(userId = userId)
        _uiState.value = _uiState.value.copy(goal = goalWithUserId)
        refreshNutrients()
        viewModelScope.launch {
            runCatching {
                withContext(ioDispatcher) { nutritionGoalRepo.saveNutritionGoal(goalWithUserId) }
            }.onFailure { Log.e("NUTRIENTS_VM", "saveNutritionGoal failed", it) }
            onSaved()
        }
    }

    fun selectDay(index: Int?) {
        val state = _uiState.value
        if (state.dailyTotals.isEmpty()) {
            _uiState.value = state.copy(selectedDayIndex = index)
            refreshNutrients()
            return
        }

        val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
        val currentTotals: NutrientTotals
        val newDateRange: String

        if (index != null) {
            currentTotals = state.dailyTotals[index]
            newDateRange = days[index] + ", " + formatter.format(weekStartDate.plusDays(index.toLong()))
        } else {
            currentTotals = NutrientTotals(
                state.dailyTotals.sumOf { it.calories },
                state.dailyTotals.sumOf { it.protein },
                state.dailyTotals.sumOf { it.carbs },
                state.dailyTotals.sumOf { it.fat }
            )
            newDateRange = "${formatter.format(weekStartDate)} - ${formatter.format(weekStartDate.plusDays(6))}"
        }

        _uiState.value = state.copy(
            selectedDayIndex = index,
            totals = currentTotals,
            dateRange = newDateRange
        )
    }

    fun refreshNutrients() {
        val selectedDay = _uiState.value.selectedDayIndex
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val mealPlan = withContext(ioDispatcher) {
                    mealPlanRepo.getMealPlansForWeek(userId, weekStartDate)
                }

                val computedDailyTotals = mutableListOf<NutrientTotals>()
                var totalCals = 0.0
                var totalProtein = 0.0
                var totalCarbs = 0.0
                var totalFat = 0.0

                for (i in 0 until 7) {
                    val date = weekStartDate.plusDays(i.toLong())
                    val dayMeals = mealPlan?.meals?.firstOrNull { it.date == date }?.meals ?: emptyList()

                    var dayCals = 0.0
                    var dayProtein = 0.0
                    var dayCarbs = 0.0
                    var dayFat = 0.0

                    dayMeals.forEach { meal ->
                        meal.recipeId?.let { id ->
                            try {
                                withContext(ioDispatcher) { recipeRepo.getRecipeById(id) }
                                    ?.let {
                                        dayCals += it.calories
                                        dayProtein += it.protein
                                        dayCarbs += it.carbs
                                        dayFat += it.fat
                                    }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.e("NUTRIENTS_VM", "Failed to load recipe $id", e)
                            }
                        }
                    }

                    computedDailyTotals.add(NutrientTotals(dayCals, dayProtein, dayCarbs, dayFat))
                    totalCals += dayCals
                    totalProtein += dayProtein
                    totalCarbs += dayCarbs
                    totalFat += dayFat
                }

                val dailyTargetCals = _uiState.value.goal.weeklyTarget.calories
                val weeklyBars = computedDailyTotals.map {
                    if (dailyTargetCals > 0) (it.calories / dailyTargetCals).toFloat().coerceIn(0f, 1f) else 0f
                }

                val weeklyAvg = if (dailyTargetCals > 0) (totalCals / (dailyTargetCals * 7) * 100f).toFloat() else 0f

                val currentTotals = if (selectedDay != null) {
                    computedDailyTotals[selectedDay]
                } else {
                    NutrientTotals(totalCals, totalProtein, totalCarbs, totalFat)
                }

                val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
                val newDateRange = if (selectedDay != null) {
                    days[selectedDay] + ", " + formatter.format(weekStartDate.plusDays(selectedDay.toLong()))
                } else {
                    "${formatter.format(weekStartDate)} - ${formatter.format(weekStartDate.plusDays(6))}"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    weeklyAvg = weeklyAvg,
                    weeklyBars = weeklyBars,
                    dailyTotals = computedDailyTotals,
                    totals = currentTotals,
                    dateRange = newDateRange
                )
            } catch (e: CancellationException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                throw e
            } catch (e: Exception) {
                Log.e("NUTRIENTS_VM", "refreshNutrients failed", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun buildInitialState(): NutrientsUiState {
        val today = LocalDate.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val startOfWeek = today.with(weekFields.dayOfWeek(), 1)
        val endOfWeek = today.with(weekFields.dayOfWeek(), 7)

        val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
        val dateRange = "${formatter.format(startOfWeek)} - ${formatter.format(endOfWeek)}"

        return NutrientsUiState(dateRange = dateRange)
    }
}
