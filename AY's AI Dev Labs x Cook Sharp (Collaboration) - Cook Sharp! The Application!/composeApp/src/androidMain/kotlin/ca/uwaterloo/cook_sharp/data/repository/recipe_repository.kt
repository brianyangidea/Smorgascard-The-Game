package ca.uwaterloo.cook_sharp.data.repository
import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.MealType
interface RecipeRepository {
    fun getAllRecipes(limit: Int = 10, offset: Int = 0): List<Recipe>
    fun getRecipeById(id: Long): Recipe?
    fun toggleLike(id: Long): List<Recipe>
    fun getLikedRecipes(): List<Recipe>
    fun searchRecipes(query: String): List<Recipe>
    fun getRecipesByMealType(mealType: MealType): List<Recipe>
    fun getFilteredRecipes(filter: FilterRecipe): List<Recipe>
    fun getUserRecipes(): List<Recipe>
    fun addRecipe(input: CreateRecipeInput): Recipe
}