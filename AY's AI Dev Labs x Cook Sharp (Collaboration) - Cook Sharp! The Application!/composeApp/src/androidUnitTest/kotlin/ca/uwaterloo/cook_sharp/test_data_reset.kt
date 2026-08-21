package ca.uwaterloo.cook_sharp
import ca.uwaterloo.cook_sharp.data.mock.MockRecipes
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore

fun resetRecipes() {
    RecipeStore.recipes.clear()
    RecipeStore.recipes.addAll(MockRecipes.recipes)
}