package ca.uwaterloo.cook_sharp.data.recipe_api
import kotlinx.serialization.Serializable

@Serializable
data class APIRecipeSearchResponse(
    val results: List<APIRecipeSummary> = emptyList(),
    val offset: Int = 0,
    val number: Int = 0,
    val totalResults: Int = 0
)
