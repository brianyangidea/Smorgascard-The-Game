package ca.uwaterloo.cook_sharp.domain

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class model_test {

    private lateinit var model: Model

    @BeforeTest
    fun setup() {
        RecipeStore.reset()
        model = Model(repo = MockRecipeRepository())
    }

    @Test
    fun getRecipeById_returns_correct_recipe() {
        val id = model.allRecipes(limit = Int.MAX_VALUE).first().id
        val r = model.getRecipeById(id)
        val notNull = assertNotNull(r)
        assertEquals(id, notNull.id)
    }

    @Test
    fun toggleLike_toggles_like() {
        val id = model.allRecipes(limit = Int.MAX_VALUE).first().id
        val before = assertNotNull(model.getRecipeById(id)).isLiked
        model.toggleLike(id)
        val after = assertNotNull(model.getRecipeById(id)).isLiked
        assertEquals(!before, after)
    }

    @Test
    fun addRecipe_adds_recipe_to_db() {
        val beforeCount = model.allRecipes(limit = Int.MAX_VALUE).size

        val input = CreateRecipeInput(
            createdByUserId = null,
            title = "Model Test Recipe",
            readyInMinutes = 10,
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
            localId = "model-test",
            remoteId = null,

            nutritionInfo = CreateNutritionInfoInput(
                calories = 200.0,
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

        val added = model.addRecipe(input)

        assertEquals(beforeCount + 1, model.allRecipes(limit = Int.MAX_VALUE).size)
        assertTrue(model.allRecipes(limit = Int.MAX_VALUE).any { it.id == added.id })
    }
}