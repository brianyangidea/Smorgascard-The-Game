package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.domain.GroceryItem

/**
 * Functionalities:
 * - Define grocery list APIs implemented by mock repo and Supabase repo
 * - Get grocery lists of current user
 * - Add a recipe to grocery list
 * - Remove a recipe to grocery list
 * - Update serving size of a recipe in list
 * - Check / uncheck a ingredient in list
 * - Expand / collapse a recipe panel in list
 */

interface GroceryListRepository {
    fun getGroceryList(): List<GroceryItem>
    fun addRecipeToGroceryList(recipeId: Long, servings: Int): List<GroceryItem>
    fun removeRecipeFromGroceryList(recipeId: Long): List<GroceryItem>
    fun updateServings(recipeId: Long, servings: Int): List<GroceryItem>
    fun toggleIngredientCheck(recipeId: Long, ingredientIndex: Int): List<GroceryItem>
    fun updateIngredientChecked(recipeId: Long, ingredientIndex: Int, checked: Boolean): List<GroceryItem>
    fun updateExpanded(recipeId: Long, isExpanded: Boolean): List<GroceryItem>
}
