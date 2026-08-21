package ca.uwaterloo.cook_sharp.data.recipe_api

import ca.uwaterloo.cook_sharp.domain.FilterRecipe
import ca.uwaterloo.cook_sharp.domain.Recipe
import kotlinx.serialization.json.Json
import ca.uwaterloo.cook_sharp.domain.CreateNutritionInfoInput

/**
 * Functionalities:
 * - Search recipes using the API
 * - Fetch full recipe details by recipe ID
 * - Convert API recipe data into recipe objects
 * - Retrieve default and filtered recipe lists
 * - Estimate nutrition information for a recipe title - this is used in add my recipe flow based on the name of user
 *      added recipe we estimate the nutrition
 */
class APIRecipeRepository(
    private val apiClient: ApiClient = ApiClient(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun searchRaw(query: String, number: Int = 10): APIRecipeSearchResponse {
        val body = apiClient.get(
            path = "/recipes/complexSearch",
            queryParams = mapOf(
                "query" to query,
                "number" to number.toString()
            )
        )
        return json.decodeFromString<APIRecipeSearchResponse>(body)
    }

    private fun searchRawWithParams(queryParams: Map<String, String>): APIRecipeSearchResponse {
        val body = apiClient.get(
            path = "/recipes/complexSearch",
            queryParams = queryParams
        )
        return json.decodeFromString<APIRecipeSearchResponse>(body)
    }

    fun getRecipeDetailsRaw(recipeId: Long): APIRecipeDetails {
        val body = apiClient.get(
            path = "/recipes/$recipeId/information",
            queryParams = mapOf(
                "includeNutrition" to "true"
            )
        )
        return json.decodeFromString<APIRecipeDetails>(body)
    }

    fun getRecipeById(recipeId: Long): Recipe {
        return APIRecipeMapper.toRecipe(getRecipeDetailsRaw(recipeId))
    }

    fun searchRecipes(query: String, number: Int = 10): List<Recipe> {
        val searchResponse = searchRaw(query, number)
        return searchResponse.results.mapNotNull { summary ->
            runCatching { getRecipeById(summary.id) }.getOrNull()
        }
    }

    fun getDefaultRecipes(number: Int = 20): List<Recipe> {
        val searchResponse = searchRaw(query = "", number = number)
        return searchResponse.results.mapNotNull { summary ->
            runCatching { getRecipeById(summary.id) }.getOrNull()
        }
    }

    fun getFilteredRecipes(filter: FilterRecipe, numberPerRequest: Int = 1): List<Recipe> {
        if (filter == FilterRecipe()) return getDefaultRecipes(numberPerRequest)

        val baseParams = buildMap<String, String> {
            put("number", "1")

            if (filter.minCalories > 0) {
                put("minCalories", filter.minCalories.toString())
            }
            if (filter.maxCalories < 2000) {
                put("maxCalories", filter.maxCalories.toString())
            }

            if (filter.excludedIngredients.isNotEmpty()) {
                put(
                    "excludeIngredients",
                    filter.excludedIngredients.joinToString(",") { it.trim() }
                )
            }
        }

        val queryParamSets = mutableListOf<Map<String, String>>()

        filter.selectedMealTypes.forEach { mealType ->
            queryParamSets += baseParams + mapOf(
                "type" to mealType.name.toApiParamValue()
            )
        }

        filter.selectedDiets.forEach { diet ->
            queryParamSets += baseParams + mapOf(
                "diet" to diet.toApiDietValue()
            )
        }

        filter.selectedCuisines.forEach { cuisine ->
            queryParamSets += baseParams + mapOf(
                "cuisine" to cuisine.toApiParamValue()
            )
        }

        if (queryParamSets.isEmpty()) {
            queryParamSets += baseParams
        }

        val apiRecipes = queryParamSets.flatMap { params ->
            val searchResponse = searchRawWithParams(params)

            searchResponse.results.mapNotNull { summary ->
                runCatching { getRecipeById(summary.id) }.getOrNull()
            }
        }.distinctBy { it.id }

        return apiRecipes
            .filter { it.matchesFallbackFilter(filter) }
            .take(1)
    }

    private fun String.toApiParamValue(): String {
        return trim().lowercase()
    }

    private fun String.toApiDietValue(): String {
        return trim()
            .lowercase()
            .replace("-", " ")
    }

    fun estimateNutritionByDishName(title: String): CreateNutritionInfoInput? {
        if (title.isBlank()) return null

        val body = apiClient.get(
            path = "/recipes/guessNutrition",
            queryParams = mapOf(
                "title" to title
            )
        )

        val response = json.decodeFromString<APIEstimatedNutritionResponse>(body)

        val calories = response.calories?.value ?: 0.0
        val protein = response.protein?.value ?: 0.0
        val carbs = response.carbs?.value ?: 0.0
        val fat = response.fat?.value ?: 0.0

        if (calories == 0.0 && protein == 0.0 && carbs == 0.0 && fat == 0.0) {
            return null
        }

        return CreateNutritionInfoInput(
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = 0.0,
            sugar = 0.0,
            sodium = 0.0,
            saturatedFat = null,
            cholesterol = null,
            potassium = null
        )
    }

    private fun Recipe.matchesFallbackFilter(filter: FilterRecipe): Boolean {
        val hasOrFilters =
            filter.selectedMealTypes.isNotEmpty() ||
                    filter.selectedDiets.isNotEmpty() ||
                    filter.selectedCuisines.isNotEmpty()

        val matchesMealType =
            filter.selectedMealTypes.isNotEmpty() &&
                    mealTypes.any { it in filter.selectedMealTypes }

        val matchesDiets =
            filter.selectedDiets.isNotEmpty() &&
                    filter.selectedDiets.any { containsDietaryRestriction(it) }

        val matchesCuisine =
            filter.selectedCuisines.isNotEmpty() &&
                    cuisineType != null &&
                    filter.selectedCuisines.any { selected ->
                        selected.equals(cuisineType, ignoreCase = true)
                    }

        val matchesOrGroup =
            !hasOrFilters ||
                    matchesMealType ||
                    matchesDiets ||
                    matchesCuisine

        val matchesCalories =
            calories >= filter.minCalories &&
                    calories <= filter.maxCalories

        val matchesExcludedIngredients =
            filter.excludedIngredients.isEmpty() ||
                    filter.excludedIngredients.none { excluded ->
                        ingredients.any { ingredient ->
                            ingredient.name.contains(excluded, ignoreCase = true)
                        }
                    }

        return matchesOrGroup &&
                matchesCalories &&
                matchesExcludedIngredients
    }
}