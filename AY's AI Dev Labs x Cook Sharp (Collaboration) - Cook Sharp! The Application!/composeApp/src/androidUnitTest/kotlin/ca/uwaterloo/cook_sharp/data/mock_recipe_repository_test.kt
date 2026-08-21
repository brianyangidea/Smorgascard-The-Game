package ca.uwaterloo.cook_sharp.data

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository
import ca.uwaterloo.cook_sharp.domain.CreateIngredientInput
import ca.uwaterloo.cook_sharp.domain.CreateNutritionInfoInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInstructionInput
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.RecipeSource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class mock_recipe_repository_test {
    private val repo: RecipeRepository = MockRecipeRepository()

    @BeforeTest
    fun setup() {
        RecipeStore.reset()
    }

    @Test
    fun getAllRecipes_returns_recipe_data() {
        val all = repo.getAllRecipes()
        assertTrue(all.isNotEmpty())
    }

    @Test
    fun getRecipeById_returns_recipe_when_exists() {
        val id = repo.getAllRecipes().first().id
        val r = repo.getRecipeById(id)
        assertNotNull(r)
        assertEquals(id, r.id)
    }

    @Test
    fun toggleLike_toggles_like_button() {
        val id = repo.getAllRecipes().first().id
        val before = repo.getRecipeById(id)!!.isLiked

        repo.toggleLike(id)
        val after1 = repo.getRecipeById(id)!!.isLiked

        repo.toggleLike(id)
        val after2 = repo.getRecipeById(id)!!.isLiked

        assertEquals(!before, after1)
        assertEquals(before, after2)
    }

    @Test
    fun getLikedRecipes_returns_only_liked_recipes() {
        val id = repo.getAllRecipes().first().id

        repo.toggleLike(id)

        val liked = repo.getLikedRecipes()
        assertTrue(liked.isNotEmpty())
        assertTrue(liked.all { it.isLiked })
        assertTrue(liked.any { it.id == id })
    }

    @Test
    fun searchRecipes_blank_query_returns_all_recipes() {
        val all = repo.getAllRecipes(limit = Int.MAX_VALUE)
        val results = repo.searchRecipes("   ")

        assertEquals(all.size, results.size)
    }

    @Test
    fun searchRecipes_filters_by_title_ignoring_case() {
        val target = repo.getAllRecipes().first()
        val token = target.title.take(3)

        val results = repo.searchRecipes(token.lowercase())

        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.id == target.id })
    }

    @Test
    fun getRecipesByMealType_returns_matching_recipes() {
        val target = repo.getAllRecipes().first { it.mealTypes.isNotEmpty() }
        val mealType = target.mealTypes.first()

        val results = repo.getRecipesByMealType(mealType)

        assertTrue(results.isNotEmpty())
        assertTrue(results.all { mealType in it.mealTypes })
    }

    @Test
    fun addRecipe_adds_user_recipe() {
        val beforeCount = repo.getAllRecipes(limit = Int.MAX_VALUE).size

        val input = CreateRecipeInput(
            createdByUserId = "user-1",
            title = "test",
            readyInMinutes = 5,
            difficulty = "Easy",
            image = null,
            servings = 2,
            cuisineType = null,

            isVegetarian = false,
            isVegan = false,
            isGlutenFree = false,
            isDairyFree = false,
            isLowFodmap = false,
            isPescatarian = false,
            isKetogenic = false,
            isPaleo = false,
            isWhole30 = false,

            source = RecipeSource.USER,
            localId = "repo-st",
            remoteId = null,

            nutritionInfo = CreateNutritionInfoInput(
                calories = 123.0,
                protein = 0.0,
                carbs = 0.0,
                fat = 0.0,
                fiber = 0.0,
                sugar = 0.0,
                sodium = 0.0
            ),

            ingredients = listOf(
                CreateIngredientInput(
                    name = "Rice",
                    amount = 1.0,
                    unit = "cup",
                    originalName = "1 cup Rice"
                )
            ),

            instructions = listOf(
                CreateRecipeInstructionInput(
                    stepNumber = 1,
                    instruction = "Cook"
                )
            ),

            mealTypes = listOf(MealType.LUNCH)
        )

        val added = repo.addRecipe(input)

        assertEquals(beforeCount + 1, repo.getAllRecipes(limit = Int.MAX_VALUE).size)
        assertNotNull(repo.getRecipeById(added.id))
        assertEquals("test", added.title)
        assertEquals(RecipeSource.USER, added.source)
    }

    @Test
    fun getUserRecipes_returns_only_user_created_recipes() {
        repo.addRecipe(
            CreateRecipeInput(
                createdByUserId = "user-1",
                title = "My Recipe",
                readyInMinutes = 10,
                difficulty = "Easy",
                image = null,
                servings = 2,
                cuisineType = "Indian",
                isVegetarian = true,
                localId = "local-user-1",
                mealTypes = listOf(MealType.DINNER)
            )
        )

        val userRecipes = repo.getUserRecipes()

        assertTrue(userRecipes.isNotEmpty())
        assertTrue(userRecipes.all { it.source == RecipeSource.USER })
        assertTrue(userRecipes.any { it.title == "My Recipe" })
    }

    @Test
    fun getFilteredRecipes_filters_by_calorie_value() {
        val target = repo.getAllRecipes().first()
        val min = target.calories.toInt().coerceAtLeast(0)
        val max = min + 5
        val filter = FilterRecipe(minCalories = min, maxCalories = max)
        val results = repo.getFilteredRecipes(filter)

        assertTrue(results.isNotEmpty())
        results.forEach { r ->
            assertTrue(r.calories >= filter.minCalories)
            assertTrue(r.calories <= filter.maxCalories)
        }
    }

    @Test
    fun getFilteredRecipes_filters_by_meal_type() {
        val target = repo.getAllRecipes().first { it.mealTypes.isNotEmpty() }
        val meal = target.mealTypes.first()

        val filter = FilterRecipe(selectedMealTypes = setOf(meal))

        val results = repo.getFilteredRecipes(filter)

        assertTrue(results.isNotEmpty())
        results.forEach { r ->
            assertTrue(r.mealTypes.any { it == meal })
        }
    }

    @Test
    fun getFilteredRecipes_filters_by_excluded_ingredients() {
        val filter = FilterRecipe(excludedIngredients = listOf("garlic"))

        val results = repo.getFilteredRecipes(filter)

        assertTrue(results.isNotEmpty())
        assertTrue(
            results.none { recipe ->
                recipe.ingredients.any { it.name.contains("garlic", ignoreCase = true) }
            }
        )
    }

    @Test
    fun getFilteredRecipes_filters_by_selected_diet() {
        val filter = FilterRecipe(selectedDiets = setOf("Vegan"))

        val results = repo.getFilteredRecipes(filter)

        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.isVegan })
    }
}