package ca.uwaterloo.cook_sharp.data

import ca.uwaterloo.cook_sharp.data.mock.GroceryListStore
import ca.uwaterloo.cook_sharp.data.mock.MockGroceryListRepository
import ca.uwaterloo.cook_sharp.data.mock.MockRecipes
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import ca.uwaterloo.cook_sharp.domain.Recipe
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class mock_grocery_list_repository_test {
    private val repo = MockGroceryListRepository()
    private lateinit var testRecipe: Recipe

    @BeforeTest
    fun setup() {
        GroceryListStore.reset()
        RecipeStore.reset()
        testRecipe = RecipeStore.recipes.first { it.id != MockRecipes.garlickyKale.id }
    }

    @Test
    fun addRecipeToGroceryList_adds_recipe() {
        val beforeCount = repo.getGroceryList().size

        repo.addRecipeToGroceryList(testRecipe.id, testRecipe.servings)

        val after = repo.getGroceryList()
        assertEquals(beforeCount + 1, after.size)
        assertTrue(after.any { it.recipe.id == testRecipe.id })
    }

    @Test
    fun addRecipeToGroceryList_servings_count_test() {
        repo.addRecipeToGroceryList(testRecipe.id, 123)
        val afterFirst = repo.getGroceryList().size

        repo.addRecipeToGroceryList(testRecipe.id, 456)
        val afterSecond = repo.getGroceryList().size

        assertEquals(afterFirst, afterSecond)
        val item = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(579, item.servings)
    }

    @Test
    fun removeRecipeFromGroceryList_removes_recipe() {
        repo.addRecipeToGroceryList(testRecipe.id, testRecipe.servings)
        val beforeCount = repo.getGroceryList().size

        repo.removeRecipeFromGroceryList(testRecipe.id)
        val after = repo.getGroceryList()

        assertEquals(beforeCount - 1, after.size)
        assertTrue(after.none { it.recipe.id == testRecipe.id })
    }

    @Test
    fun updateServings_increase_servings() {
        repo.addRecipeToGroceryList(testRecipe.id, testRecipe.servings)
        val newServings = testRecipe.servings + 2

        repo.updateServings(testRecipe.id, newServings)
        val item = repo.getGroceryList().first { it.recipe.id == testRecipe.id }

        assertEquals(newServings, item.servings)
    }

    @Test
    fun toggleIngredientCheck_check_uncheck() {
        repo.addRecipeToGroceryList(testRecipe.id, testRecipe.servings)

        repo.toggleIngredientCheck(testRecipe.id, 0)
        val afterFirst = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(true, afterFirst.checkedStates[0])

        repo.toggleIngredientCheck(testRecipe.id, 0)
        val afterSecond = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(false, afterSecond.checkedStates[0])
    }

    @Test
    fun updateIngredientChecked_check_uncheck() {
        repo.addRecipeToGroceryList(testRecipe.id, testRecipe.servings)

        repo.updateIngredientChecked(testRecipe.id, 0, true)
        var item = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(true, item.checkedStates[0])

        repo.updateIngredientChecked(testRecipe.id, 0, false)
        item = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(false, item.checkedStates[0])
    }

    @Test
    fun updateExpanded_expand_unexpand() {
        repo.addRecipeToGroceryList(testRecipe.id, testRecipe.servings)

        repo.updateExpanded(testRecipe.id, false)
        var item = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(false, item.isExpanded)

        repo.updateExpanded(testRecipe.id, true)
        item = repo.getGroceryList().first { it.recipe.id == testRecipe.id }
        assertEquals(true, item.isExpanded)
    }

    @Test
    fun addRecipeToGroceryList_add_invalid_recipe() {
        val beforeCount = repo.getGroceryList().size

        repo.addRecipeToGroceryList(-1, 2)

        val afterCount = repo.getGroceryList().size
        assertEquals(beforeCount, afterCount)
    }
}