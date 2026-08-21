package ca.uwaterloo.cook_sharp.domain

data class Meal(
    val id: Int,
    val type: MealType,
    val recipeId : Long? = null,
    val label: String? = null
)

