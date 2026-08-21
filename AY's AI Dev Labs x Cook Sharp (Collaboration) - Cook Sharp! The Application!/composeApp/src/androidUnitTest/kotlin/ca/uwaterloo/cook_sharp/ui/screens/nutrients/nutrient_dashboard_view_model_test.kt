package ca.uwaterloo.cook_sharp.ui.screens.nutrients

import androidx.compose.runtime.snapshots.Snapshot
import ca.uwaterloo.cook_sharp.MainDispatcher
import ca.uwaterloo.cook_sharp.data.mock.MealPlanStore
import ca.uwaterloo.cook_sharp.data.mock.MockMealPlanRepository
import ca.uwaterloo.cook_sharp.data.mock.MockNutritionGoalRepository
import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.domain.GoalType
import ca.uwaterloo.cook_sharp.domain.NutritionGoal
import ca.uwaterloo.cook_sharp.domain.NutritionTarget
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NutrientsDashboardViewmodelTest {

    @get:Rule
    val mainDispatcher = MainDispatcher()
    private fun TestScope.createVm(): NutrientsDashboardViewmodel {
        Snapshot.sendApplyNotifications()
        Snapshot.withMutableSnapshot {
            MealPlanStore.mealPlans.clear()
        }

        val vm = NutrientsDashboardViewmodel(
            mealPlanRepo = MockMealPlanRepository(),
            recipeRepo = MockRecipeRepository(),
            nutritionGoalRepo = MockNutritionGoalRepository(),
            ioDispatcher = UnconfinedTestDispatcher()
        )
        advanceUntilIdle()
        return vm
    }

    @Test
    fun initialState_dateRange_isNotBlank() = runTest {
        val vm = createVm()
        assertFalse(vm.uiState.value.dateRange.isBlank())
    }

    @Test
    fun initialState_weeklyBars_hasSevenEntries_andValuesInRange() = runTest {
        val vm = createVm()
        val bars = vm.uiState.value.weeklyBars

        assertEquals(7, bars.size)
        bars.forEach { bar -> assertTrue(bar in 0f..1f) }
    }

    @Test
    fun initialState_weeklyAvg_isZero() = runTest {
        val vm = createVm()
        assertEquals(0f, vm.uiState.value.weeklyAvg, 0.01f)
    }

    @Test
    fun refreshNutrients_populatesWeeklyBars_andAvgMatches() = runTest {
        val vm = createVm()

        vm.refreshNutrients()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(7, state.weeklyBars.size)
        state.weeklyBars.forEach { bar -> assertTrue(bar in 0f..1f) }

        val expected = state.weeklyBars.average().toFloat() * 100f
        assertEquals(expected, state.weeklyAvg, 0.01f)
    }

    @Test
    fun updateGoal_updates_calories() = runTest {
        val vm = createVm()
        vm.updateGoal(makeGoal(calories = 3000.0))
        advanceUntilIdle()
        assertEquals(3000.0, vm.uiState.value.goal.weeklyTarget.calories, 0.0)
    }

    @Test
    fun updateGoal_updates_protein() = runTest {
        val vm = createVm()
        vm.updateGoal(makeGoal(protein = 200.0))
        advanceUntilIdle()
        assertEquals(200.0, vm.uiState.value.goal.weeklyTarget.protein, 0.0)
    }

    @Test
    fun updateGoal_updates_carbs() = runTest {
        val vm = createVm()
        vm.updateGoal(makeGoal(carbs = 350.0))
        advanceUntilIdle()
        assertEquals(350.0, vm.uiState.value.goal.weeklyTarget.carbs, 0.0)
    }

    @Test
    fun updateGoal_updates_fat() = runTest {
        val vm = createVm()
        vm.updateGoal(makeGoal(fat = 90.0))
        advanceUntilIdle()
        assertEquals(90.0, vm.uiState.value.goal.weeklyTarget.fat, 0.0)
    }

    @Test
    fun updateGoal_preserves_goalType() = runTest {
        val vm = createVm()
        vm.updateGoal(makeGoal(goalType = GoalType.CUT))
        advanceUntilIdle()
        assertEquals(GoalType.CUT, vm.uiState.value.goal.goalType)
    }

    @Test
    fun updateGoal_canBeCalledMultipleTimes_latestWins() = runTest {
        val vm = createVm()
        vm.updateGoal(makeGoal(calories = 1500.0))
        vm.updateGoal(makeGoal(calories = 2500.0))
        advanceUntilIdle()
        assertEquals(2500.0, vm.uiState.value.goal.weeklyTarget.calories, 0.0)
    }

    private fun makeGoal(
        calories: Double = 2000.0,
        protein: Double = 100.0,
        carbs: Double = 250.0,
        fat: Double = 60.0,
        goalType: GoalType = GoalType.MAINTAIN
    ) = NutritionGoal(
        userId = "123",
        weeklyTarget = NutritionTarget(
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat
        ),
        goalType = goalType
    )
}