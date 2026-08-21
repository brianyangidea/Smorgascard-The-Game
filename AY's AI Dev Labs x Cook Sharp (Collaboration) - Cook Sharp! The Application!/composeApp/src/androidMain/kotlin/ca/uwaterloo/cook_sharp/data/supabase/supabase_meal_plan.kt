package ca.uwaterloo.cook_sharp.data.supabase

import ca.uwaterloo.cook_sharp.domain.DailyMeals
import ca.uwaterloo.cook_sharp.domain.Meal
import ca.uwaterloo.cook_sharp.domain.MealPlan
import ca.uwaterloo.cook_sharp.domain.MealType
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class SupabaseMealPlan(
    val id: Long = 0,
    val user_id: String,
    val week_start_date: String
)

@Serializable
data class SupabaseDailyMeals(
    val id: Long = 0,
    val meal_plan_id: Long,
    val date: String
)

@Serializable
data class SupabaseMeal(
    val id: Long = 0,
    val daily_meals_id: Long,
    val type: String,
    val recipe_id: Long? = null,
    val label: String? = null
)

fun SupabaseMealPlan.toDomain(
    dailyMealsList: List<SupabaseDailyMeals>,
    mealsByDailyId: Map<Long, List<SupabaseMeal>>
): MealPlan {
    val dailyMeals = dailyMealsList
        .map { dm ->
            DailyMeals(
                date = LocalDate.parse(dm.date),
                meals = (mealsByDailyId[dm.id] ?: emptyList()).map { meal ->
                    Meal(
                        id = meal.id.toInt(),
                        type = meal.type.toMealType(),
                        recipeId = meal.recipe_id,
                        label = meal.label
                    )
                }
            )
        }
        .sortedBy { it.date }

    return MealPlan(
        id = id.toInt(),
        userId = user_id,
        weekStartDate = LocalDate.parse(week_start_date),
        meals = dailyMeals
    )
}

private fun String.toMealType(): MealType = when (trim().uppercase()) {
    "BREAKFAST" -> MealType.BREAKFAST
    "LUNCH"     -> MealType.LUNCH
    "DINNER"    -> MealType.DINNER
    else        -> MealType.SNACK
}
