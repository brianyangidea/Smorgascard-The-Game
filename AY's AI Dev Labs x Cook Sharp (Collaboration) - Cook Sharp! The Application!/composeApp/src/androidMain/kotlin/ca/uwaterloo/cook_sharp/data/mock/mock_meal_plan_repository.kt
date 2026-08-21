package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.domain.Meal
import ca.uwaterloo.cook_sharp.domain.MealPlan
import java.time.LocalDate
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.data.repository.MealPlanRepository
import ca.uwaterloo.cook_sharp.data.mock.MealPlanStore


class MockMealPlanRepository : MealPlanRepository {
    override fun getMealPlansForWeek(
        userId : String,
        weekStartDate: LocalDate
    ) : MealPlan? {
        return MealPlanStore.mealPlans.firstOrNull {
            it.userId == userId && it.weekStartDate == weekStartDate
        }
    }

    override fun setMealPlan (mealPlan: MealPlan) {
        val idx = MealPlanStore.mealPlans.indexOfFirst {
            it.userId == mealPlan.userId && it.weekStartDate == mealPlan.weekStartDate
        }
        if (idx >= 0) MealPlanStore.mealPlans[idx] = mealPlan
        else MealPlanStore.mealPlans.add(mealPlan)
    }

    override fun setMeal(
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType: MealType,
        recipeId: Long?
    ): MealPlan? {
        val mealPlan = getMealPlansForWeek(userId, weekStartDate)?: return null
        val updated = mealPlan.meals.map { day ->
            if (day.date != date) day else {
                val meals = day.meals.toMutableList()
                val idx = meals.indexOfFirst { it.type == mealType }

                if (recipeId == null) {
                    if (idx >= 0 ) meals.removeAt(idx)
                } else {
                    val newMeal = Meal (
                        id = 1,
                        type = mealType,
                        recipeId = recipeId,
                        label = mealType.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                    )
                    if (idx >= 0) meals[idx] = newMeal else meals.add(newMeal)
                }
                day.copy(meals = meals)
            }
        }
        val updatedPlans = mealPlan.copy(meals = updated)
        setMealPlan(updatedPlans)
        return updatedPlans
    }

    override fun addMeal(
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType: MealType,
        recipeId: Long,
        label: String?
    ): MealPlan? {
        val mealPlan = getMealPlansForWeek(userId, weekStartDate) ?: return null
        val updatedDays = mealPlan.meals.map { day ->
            if (day.date != date) day
            else day.copy(
                meals = day.meals + Meal(
                    id = 2,
                    type = mealType,
                    recipeId = recipeId,
                    label = label ?: mealType.name.lowercase()
                )
            )
        }

        val updatedPlan = mealPlan.copy(meals = updatedDays)
        setMealPlan(updatedPlan)
        return updatedPlan
    }

    override fun removeMeal (
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType : MealType,
        index: Int) : MealPlan? {
        val mealPlan = getMealPlansForWeek(userId, weekStartDate) ?: return null
        val updatedDays = mealPlan.meals.map { day ->
            if (day.date != date) day
            else {
                val targets = day.meals.filter { it.type == mealType}
                if (index !in targets.indices) return@map day
                val toRemove = targets[index]
                day.copy(meals = day.meals - toRemove)
            }
        }
        val updatedPlan = mealPlan.copy(meals = updatedDays)
        setMealPlan(updatedPlan)
        return updatedPlan
    }
}