package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.domain.MealPlan
import java.time.LocalDate
import ca.uwaterloo.cook_sharp.domain.MealType

/**
 * Functionalities:
 * - Repo Interface for managing weekly meal plans
 */

interface MealPlanRepository {
    fun getMealPlansForWeek(
        userId : String,
        weekStartDate: LocalDate
    ) : MealPlan?

    fun setMealPlan (mealPlan: MealPlan)

    fun setMeal(
        userId: String,
        weekStartDate: LocalDate,
        date : LocalDate,
        mealType : MealType,
        recipeId : Long?
    ) : MealPlan?

    fun addMeal(
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType : MealType,
        recipeId: Long,
        label : String? = null
    ) : MealPlan?

    fun removeMeal (
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType: MealType,
        index: Int
    ) : MealPlan?
}
