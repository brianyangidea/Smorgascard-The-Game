package ca.uwaterloo.cook_sharp.data

import ca.uwaterloo.cook_sharp.data.mock.MockMealPlanRepository
import ca.uwaterloo.cook_sharp.domain.DailyMeals
import ca.uwaterloo.cook_sharp.domain.Meal
import ca.uwaterloo.cook_sharp.domain.MealPlan
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.data.mock.MealPlanStore
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.time.LocalDate
import java.time.DayOfWeek
import kotlin.test.assertNull

class MockMealPlanRepositoryTest {

    private lateinit var repos : MockMealPlanRepository
    private val testUserId = "test_user_123"
    private val weekStart = LocalDate.now().with(DayOfWeek.MONDAY)

    @BeforeTest
    fun setup() {
        MealPlanStore.mealPlans.clear()
        repos = MockMealPlanRepository()
    }

    @Test
    fun getMealPlansForWeek_returns_null_when_no_meal_plan_exists() {
        val result = repos.getMealPlansForWeek(testUserId, weekStart)

        assertNull(result)
    }

    @Test
    fun getMealPlansForWeek_returns_meal_plan_when_exists() {
        val mealPlan = MealPlan(
            id = 1,
            userId = testUserId,
            weekStartDate = weekStart,
            meals = emptyList()
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.getMealPlansForWeek(testUserId, weekStart)

        assertNotNull(result)
        assertEquals(testUserId, result.userId)
        assertEquals(weekStart, result.weekStartDate)
    }

    @Test
    fun getMealPlansForWeek_returns_null_for_different_user() {
        val mealPlan = MealPlan(
            id = 1,
            userId = "other_user_123",
            weekStartDate = weekStart
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.getMealPlansForWeek(testUserId, weekStart)

        assertNull(result)
    }


    @Test
    fun getMealPlansForWeek_returns_null_for_different_user_or_week() {
        MealPlanStore.mealPlans.add(MealPlan(1, "other_user", weekStart))

        val result = repos.getMealPlansForWeek(testUserId, weekStart)

        assertNull(result)
    }


    @Test
    fun setMealPlan_adds_new_meal_plan_when_not_exists() {
        val mealPlan = MealPlan(1, testUserId, weekStart)

        repos.setMealPlan(mealPlan)

        assertEquals(1, MealPlanStore.mealPlans.size)
        assertEquals(mealPlan, MealPlanStore.mealPlans[0])
    }

    @Test
    fun setMealPlan_updates_existing_meal_plan() {
        val original = MealPlan(1, testUserId, weekStart, emptyList())
        MealPlanStore.mealPlans.add(original)

        val updated = original.copy(meals = listOf(DailyMeals(weekStart)))
        repos.setMealPlan(updated)

        assertEquals(1, MealPlanStore.mealPlans.size)
        assertEquals(1, MealPlanStore.mealPlans[0].meals.size)
    }

    @Test
    fun setMealPlan_does_not_affect_other_meal_plans() {
        val plan1 = MealPlan(1, "user1", weekStart)
        val plan2 = MealPlan(2, testUserId, weekStart)
        MealPlanStore.mealPlans.addAll(listOf(plan1, plan2))

        repos.setMealPlan(plan2.copy(meals = listOf(DailyMeals(weekStart))))

        assertEquals(0, MealPlanStore.mealPlans[0].meals.size)
        assertEquals(1, MealPlanStore.mealPlans[1].meals.size)
    }

    @Test
    fun setMeal_returns_null_when_meal_plan_does_not_exist() {
        val result = repos.setMeal(
            testUserId, weekStart, weekStart, MealType.BREAKFAST, 123
        )

        assertNull(result)
    }

    @Test
    fun setMeal_adds_or_replaces_meal() {
        val mealPlan = MealPlan(
            1, testUserId, weekStart,
            listOf(DailyMeals(weekStart, listOf(Meal(1,MealType.BREAKFAST, 100))))
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.setMeal(
            testUserId, weekStart, weekStart, MealType.BREAKFAST, 200
        )

        assertNotNull(result)
        assertEquals(200, result.meals[0].meals[0].recipeId)
    }

    @Test
    fun setMeal_removes_meal_when_recipe_id_is_null() {
        val mealPlan = MealPlan(
            1, testUserId, weekStart,
            listOf(DailyMeals(weekStart, listOf(Meal(2,MealType.BREAKFAST, 100))))
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.setMeal(
            testUserId, weekStart, weekStart, MealType.BREAKFAST, null
        )

        assertNotNull(result)
        assertTrue(result.meals[0].meals.isEmpty())
    }

    @Test
    fun addMeal_returns_null_when_meal_plan_does_not_exist() {
        val result = repos.addMeal(
            testUserId, weekStart, weekStart, MealType.SNACK, 123, "Snack"
        )

        assertNull(result)
    }

    @Test
    fun addMeal_adds_meal_to_existing_day() {
        val mealPlan = MealPlan(
            1, testUserId, weekStart,
            listOf(DailyMeals(weekStart, listOf(Meal(3,MealType.BREAKFAST, 100))))
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.addMeal(
            testUserId, weekStart, weekStart, MealType.SNACK, 200, "Snack"
        )

        assertNotNull(result)
        assertEquals(2, result.meals[0].meals.size)
    }

    @Test
    fun addMeal_allows_multiple_meals_of_same_type() {
        val mealPlan = MealPlan(
            1, testUserId, weekStart,
            listOf(DailyMeals(weekStart, listOf(Meal(4,MealType.SNACK, 100))))
        )
        MealPlanStore.mealPlans.add(mealPlan)

        repos.addMeal(testUserId, weekStart, weekStart, MealType.SNACK, 200, "Snack 2")
        val result = repos.getMealPlansForWeek(testUserId, weekStart)

        assertNotNull(result)
        assertEquals(2, result.meals[0].meals.filter { it.type == MealType.SNACK }.size)
    }

    @Test
    fun removeMeal_returns_null_when_meal_plan_does_not_exist() {
        val result = repos.removeMeal(
            testUserId, weekStart, weekStart, MealType.SNACK, 0
        )

        assertNull(result)
    }

    @Test
    fun removeMeal_removes_meal_at_specified_index() {
        val meals = listOf(
            Meal(3,MealType.SNACK, 100, "Snack 1"),
            Meal(4,MealType.SNACK, 200, "Snack 2"),
            Meal(5,MealType.SNACK, 300, "Snack 3")
        )
        val mealPlan = MealPlan(
            1, testUserId, weekStart,
            listOf(DailyMeals(weekStart, meals))
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.removeMeal(
            testUserId, weekStart, weekStart, MealType.SNACK, 1
        )

        assertNotNull(result)
        val snacks = result.meals[0].meals.filter { it.type == MealType.SNACK }
        assertEquals(2, snacks.size)
        assertEquals("Snack 3", snacks[1].label)
    }

    @Test
    fun removeMeal_does_nothing_when_index_out_of_bounds() {
        val mealPlan = MealPlan(
            1, testUserId, weekStart,
            listOf(DailyMeals(weekStart, listOf(Meal(5,MealType.SNACK, 100))))
        )
        MealPlanStore.mealPlans.add(mealPlan)

        val result = repos.removeMeal(
            testUserId, weekStart, weekStart, MealType.SNACK, 5
        )

        assertNotNull(result)
        assertEquals(1, result.meals[0].meals.size)
    }
}