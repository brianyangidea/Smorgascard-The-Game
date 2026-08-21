package ca.uwaterloo.cook_sharp.data.mock
import androidx.compose.runtime.mutableStateListOf
import ca.uwaterloo.cook_sharp.domain.Recipe

object RecipeStore {
    val recipes = mutableStateListOf<Recipe>().apply {
        if (isEmpty()) addAll(MockRecipes.recipes)
    }

    fun reset() {
        recipes.clear()
        recipes.addAll(MockRecipes.recipes)
    }
}
