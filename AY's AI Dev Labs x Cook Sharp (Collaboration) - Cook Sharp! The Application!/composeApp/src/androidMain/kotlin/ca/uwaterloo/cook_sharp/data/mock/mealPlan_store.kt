package ca.uwaterloo.cook_sharp.data.mock

import androidx.compose.runtime.mutableStateListOf
import ca.uwaterloo.cook_sharp.data.mock.MealPlans
import ca.uwaterloo.cook_sharp.domain.MealPlan

object MealPlanStore {
    val mealPlans = mutableStateListOf<MealPlan>()
}