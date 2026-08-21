package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.domain.MealPlan
import ca.uwaterloo.cook_sharp.domain.DailyMeals
import ca.uwaterloo.cook_sharp.domain.Meal
import ca.uwaterloo.cook_sharp.domain.MealType
import java.time.LocalDate
import java.time.DayOfWeek

object MealPlans {
    private const val USER_ID = "sample_user"
    private val weekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)

    private const val RECIPE_GARLICKY_KALE = 644387L
    private const val RECIPE_PASTA = 715415L

    val thisWeekMealPlan = MealPlan(
        id = 1,
        userId = USER_ID,
        weekStartDate = weekStart,
        meals = listOf(
            DailyMeals(
                date = weekStart,
                meals = listOf(
                    Meal(id = 1, type = MealType.BREAKFAST, recipeId = RECIPE_GARLICKY_KALE, label = "Breakfast"),
                    Meal(id = 2, type = MealType.LUNCH,     recipeId = RECIPE_PASTA,        label = "Lunch"),
                    Meal(id = 3, type = MealType.DINNER,    recipeId = RECIPE_GARLICKY_KALE, label = "Dinner")
                )
            ),
            DailyMeals(
                date = weekStart.plusDays(1),
                meals = listOf(
                    Meal(id = 4, type = MealType.BREAKFAST, recipeId = RECIPE_PASTA,        label = "Breakfast"),
                    Meal(id = 5, type = MealType.LUNCH,     recipeId = RECIPE_GARLICKY_KALE, label = "Lunch"),
                    Meal(id = 6, type = MealType.DINNER,    recipeId = RECIPE_PASTA,        label = "Dinner"),
                    Meal(id = 7, type = MealType.SNACK,     recipeId = RECIPE_GARLICKY_KALE, label = "Snack")
                )
            ),
            DailyMeals(
                date = weekStart.plusDays(2),
                meals = listOf(
                    Meal(id = 8,  type = MealType.BREAKFAST, recipeId = RECIPE_GARLICKY_KALE, label = "Breakfast"),
                    Meal(id = 9,  type = MealType.DINNER,    recipeId = RECIPE_PASTA,        label = "Dinner")
                )
            ),
            DailyMeals(
                date = weekStart.plusDays(3),
                meals = listOf(
                    Meal(id = 10, type = MealType.BREAKFAST, recipeId = RECIPE_PASTA, label = "Breakfast")
                )
            ),
            DailyMeals(
                date = weekStart.plusDays(4),
                meals = listOf(
                    Meal(id = 11, type = MealType.BREAKFAST, recipeId = RECIPE_GARLICKY_KALE, label = "Breakfast"),
                    Meal(id = 12, type = MealType.LUNCH,     recipeId = RECIPE_PASTA,        label = "Lunch"),
                    Meal(id = 13, type = MealType.DINNER,    recipeId = RECIPE_GARLICKY_KALE, label = "Dinner"),
                    Meal(id = 14, type = MealType.SNACK,     recipeId = RECIPE_PASTA,        label = "Snack 1"),
                    Meal(id = 15, type = MealType.SNACK,     recipeId = RECIPE_GARLICKY_KALE, label = "Snack 2")
                )
            ),
            DailyMeals(
                date = weekStart.plusDays(5),
                meals = listOf(
                    Meal(id = 16, type = MealType.BREAKFAST, recipeId = RECIPE_PASTA,        label = "Breakfast"),
                    Meal(id = 17, type = MealType.LUNCH,     recipeId = RECIPE_GARLICKY_KALE, label = "Lunch"),
                    Meal(id = 18, type = MealType.DINNER,    recipeId = RECIPE_PASTA,        label = "Dinner")
                )
            ),
            DailyMeals(
                date = weekStart.plusDays(6),
                meals = listOf(
                    Meal(id = 19, type = MealType.LUNCH, recipeId = RECIPE_GARLICKY_KALE, label = "Lunch")
                )
            )
        )
    )
}
