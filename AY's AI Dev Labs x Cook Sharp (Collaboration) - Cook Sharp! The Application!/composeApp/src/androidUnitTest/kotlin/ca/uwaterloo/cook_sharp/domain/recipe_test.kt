package ca.uwaterloo.cook_sharp.domain

import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import kotlin.math.abs
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class recipe_test {

    @BeforeTest
    fun setup() {
        RecipeStore.reset()
    }

    @Test
    fun scaleIngredients_fails_when_serving_less_than1() {
        val r = RecipeStore.recipes.first { it.id == 644387L }
        assertFailsWith<IllegalArgumentException> { r.scaleIngredients(0) }
        assertFailsWith<IllegalArgumentException> { r.scaleIngredients(-1) }
    }

    @Test
    fun scaleIngredients_scales_amount() {
        val r = RecipeStore.recipes.first { it.id == 644387L }
        val originalFirst = r.ingredients.first()
        val targetServings = 4
        val expected = originalFirst.amount * (targetServings.toDouble() / r.servings.toDouble())
        val scaled = r.scaleIngredients(targetServings)
        val actual = scaled.first().amount
        val eps = 1e-9

        assertTrue(
            abs(expected - actual) < eps,
            "Expected '${originalFirst.name}' amount to scale from ${originalFirst.amount} to $expected, but got $actual"
        )
    }

    @Test
    fun scaleIngredients_empty_ingredients_returns_empty() {
        val r = Recipe(
            id = 1L,
            createdByUserId = null,
            title = "T",
            readyInMinutes = 1,
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
            localId = "test-recipe",
            remoteId = null,

            nutritionInfo = null,
            ingredients = emptyList(),
            instructions = emptyList(),
            mealTypes = emptyList(),

            isLiked = false
        )

        val scaled = r.scaleIngredients(4)
        assertTrue(scaled.isEmpty())
    }

    @Test
    fun containsDietaryRestriction_checks_if_recipe_has_dietary_restriction() {
        val r = RecipeStore.recipes.first { it.id == 644387L }
        assertTrue(r.containsDietaryRestriction("Vegetarian"))
        assertTrue(r.containsDietaryRestriction("Vegan"))
        assertTrue(r.containsDietaryRestriction("Gluten Free"))
        assertTrue(r.containsDietaryRestriction("No Dietary restriction"))
        assertFalse(r.containsDietaryRestriction("Halal"))
    }

    @Test
    fun containsIngredientName_returns_false_when_query_blank() {
        val r = RecipeStore.recipes.first { it.ingredients.isNotEmpty() }
        assertFalse(r.containsIngredientName(""))
        assertFalse(r.containsIngredientName("   "))
    }

    @Test
    fun containsDietaryRestriction_unknown_diet_returns_true() {
        val r = RecipeStore.recipes.first { it.id == 657719L }
        assertFalse(r.containsDietaryRestriction("randomdiet"))
    }
}