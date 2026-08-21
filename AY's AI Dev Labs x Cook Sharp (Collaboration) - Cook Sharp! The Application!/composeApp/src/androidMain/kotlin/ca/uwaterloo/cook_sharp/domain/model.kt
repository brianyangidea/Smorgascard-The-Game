package ca.uwaterloo.cook_sharp.domain

import ca.uwaterloo.cook_sharp.data.repository.CombinedRecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseUserRepository
import ca.uwaterloo.cook_sharp.data.repository.UserRepository

class Model(
    private val userRepo: UserRepository = SupabaseUserRepository,
    private val repo: RecipeRepository = CombinedRecipeRepository(userRepo),
) {

    fun allRecipes(limit: Int = 10, offset: Int = 0): List<Recipe> =
        repo.getAllRecipes(limit, offset)

    fun getRecipeById(id: Long): Recipe? = repo.getRecipeById(id)

    fun searchRecipes(query: String): List<Recipe> = repo.searchRecipes(query)

    fun recipesByMealType(mealType: MealType): List<Recipe> = repo.getRecipesByMealType(mealType)

    fun filteredRecipes(filter: FilterRecipe): List<Recipe> = repo.getFilteredRecipes(filter)

    fun likedRecipes(): List<Recipe> = repo.getLikedRecipes()

    fun userRecipes(): List<Recipe> = repo.getUserRecipes()

    fun toggleLike(recipeId: Long) {
        repo.toggleLike(recipeId)
    }

    fun addRecipe(input: CreateRecipeInput): Recipe {
        return repo.addRecipe(input)
    }

    fun getCurrentUser(): User? = userRepo.getCurrentUser()
    suspend fun updateUser(user: User) = userRepo.updateUser(user)
}