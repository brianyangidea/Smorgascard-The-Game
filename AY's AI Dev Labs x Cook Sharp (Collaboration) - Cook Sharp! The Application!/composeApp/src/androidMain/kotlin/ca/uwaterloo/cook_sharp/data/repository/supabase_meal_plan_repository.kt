package ca.uwaterloo.cook_sharp.data.repository

import android.util.Log
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseClientProvider
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseDailyMeals
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseMeal
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseMealPlan
import ca.uwaterloo.cook_sharp.data.supabase.toDomain
import ca.uwaterloo.cook_sharp.domain.MealPlan
import ca.uwaterloo.cook_sharp.domain.MealType
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

/**
 * Functionalities:
 * - Load meal plans set by users to the corresponding Supabase table
 * - follow correct structure of UML that we created (meal -> dailyMeals -> MealPlans)
 */

class SupabaseMealPlanRepository : MealPlanRepository {

    private val client = SupabaseClientProvider.client

    override fun getMealPlansForWeek(userId: String, weekStartDate: LocalDate): MealPlan? =
        runBlocking { fetchPlan(userId, weekStartDate) }

    override fun setMealPlan(mealPlan: MealPlan) = runBlocking {
        replacePlan(mealPlan)
    }

    override fun setMeal(
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType: MealType,
        recipeId: Long?
    ): MealPlan? = runBlocking {
        val planRow = getOrCreatePlanRow(userId, weekStartDate)
        val dmRow = getOrCreateDailyMealsRow(planRow.id, date)

        client.from("meals").delete {
            filter {
                eq("daily_meals_id", dmRow.id)
                eq("type", mealType.name)
            }
        }

        if (recipeId != null) {
            client.from("meals").insert(
                SupabaseMeal(
                    daily_meals_id = dmRow.id,
                    type = mealType.name,
                    recipe_id = recipeId
                )
            )
        }

        fetchPlan(userId, weekStartDate)
    }

    override fun addMeal(
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType: MealType,
        recipeId: Long,
        label: String?
    ): MealPlan? = runBlocking {
        val planRow = getOrCreatePlanRow(userId, weekStartDate)
        val dmRow = getOrCreateDailyMealsRow(planRow.id, date)

        client.from("meals").insert(
            SupabaseMeal(
                daily_meals_id = dmRow.id,
                type = mealType.name,
                recipe_id = recipeId,
                label = label
            )
        )

        fetchPlan(userId, weekStartDate)
    }

    override fun removeMeal(
        userId: String,
        weekStartDate: LocalDate,
        date: LocalDate,
        mealType: MealType,
        index: Int
    ): MealPlan? = runBlocking {
        val planRow = client.from("meal_plans")
            .select { filter { eq("user_id", userId); eq("week_start_date", weekStartDate.toString()) } }
            .decodeList<SupabaseMealPlan>()
            .firstOrNull() ?: return@runBlocking null

        val dmRow = client.from("daily_meals")
            .select { filter { eq("meal_plan_id", planRow.id); eq("date", date.toString()) } }
            .decodeList<SupabaseDailyMeals>()
            .firstOrNull() ?: return@runBlocking fetchPlan(userId, weekStartDate)

        val targets = client.from("meals")
            .select { filter { eq("daily_meals_id", dmRow.id); eq("type", mealType.name) } }
            .decodeList<SupabaseMeal>()

        val toRemove = targets.getOrNull(index)
            ?: return@runBlocking fetchPlan(userId, weekStartDate)

        client.from("meals").delete { filter { eq("id", toRemove.id) } }

        fetchPlan(userId, weekStartDate)
    }

    private suspend fun fetchPlan(userId: String, weekStartDate: LocalDate): MealPlan? {
        val planRow = client.from("meal_plans")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("week_start_date", weekStartDate.toString())
                }
            }
            .decodeList<SupabaseMealPlan>()
            .firstOrNull() ?: return null

        val dailyMealsList = client.from("daily_meals")
            .select { filter { eq("meal_plan_id", planRow.id) } }
            .decodeList<SupabaseDailyMeals>()

        val dailyIds = dailyMealsList.map { it.id }
        val meals = if (dailyIds.isEmpty()) emptyList() else
            client.from("meals")
                .select { filter { isIn("daily_meals_id", dailyIds) } }
                .decodeList<SupabaseMeal>()

        val mealsByDailyId = meals.groupBy { it.daily_meals_id }

        Log.d("SUPABASE_MEAL", "Loaded plan ${planRow.id} with ${dailyMealsList.size} days, ${meals.size} meals")
        return planRow.toDomain(dailyMealsList, mealsByDailyId)
    }

    private suspend fun replacePlan(mealPlan: MealPlan) {
        val planRow = getOrCreatePlanRow(mealPlan.userId, mealPlan.weekStartDate)

        val existingDailyMeals = client.from("daily_meals")
            .select { filter { eq("meal_plan_id", planRow.id) } }
            .decodeList<SupabaseDailyMeals>()

        if (existingDailyMeals.isNotEmpty()) {
            val ids = existingDailyMeals.map { it.id }
            client.from("meals").delete { filter { isIn("daily_meals_id", ids) } }
            client.from("daily_meals").delete { filter { eq("meal_plan_id", planRow.id) } }
        }

        for (day in mealPlan.meals) {
            if (day.meals.isEmpty()) continue

            val dmRow = client.from("daily_meals")
                .insert(SupabaseDailyMeals(meal_plan_id = planRow.id, date = day.date.toString())) { select() }
                .decodeSingle<SupabaseDailyMeals>()

            val mealRows = day.meals.map { meal ->
                SupabaseMeal(
                    daily_meals_id = dmRow.id,
                    type = meal.type.name,
                    recipe_id = meal.recipeId,
                    label = meal.label
                )
            }
            client.from("meals").insert(mealRows)
        }
    }

    private suspend fun getOrCreatePlanRow(
        userId: String,
        weekStartDate: LocalDate
    ): SupabaseMealPlan {
        val existing = client.from("meal_plans")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("week_start_date", weekStartDate.toString())
                }
            }
            .decodeList<SupabaseMealPlan>()
            .firstOrNull()

        if (existing != null) return existing

        return client.from("meal_plans")
            .insert(SupabaseMealPlan(user_id = userId, week_start_date = weekStartDate.toString())) {
                select()
            }
            .decodeSingle<SupabaseMealPlan>()
    }

    private suspend fun getOrCreateDailyMealsRow(planId: Long, date: LocalDate): SupabaseDailyMeals {
        val existing = client.from("daily_meals")
            .select { filter { eq("meal_plan_id", planId); eq("date", date.toString()) } }
            .decodeList<SupabaseDailyMeals>()
            .firstOrNull()

        if (existing != null) return existing

        return client.from("daily_meals")
            .insert(SupabaseDailyMeals(meal_plan_id = planId, date = date.toString())) { select() }
            .decodeSingle<SupabaseDailyMeals>()
    }
}
