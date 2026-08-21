package ca.uwaterloo.cook_sharp.data.recipe_api

import kotlinx.serialization.Serializable

@Serializable
data class APIRecipeSummary(
    val id: Long,
    val title: String,
    val image: String? = null,
    val imageType: String? = null
)