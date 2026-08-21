package ca.uwaterloo.cook_sharp.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String = "password123",
    val profilePictureUri: String? = null,
    val dietarypreference: String? = null,
    val allergies: List<String> = emptyList()
) {
    fun isAllergicTo(ingredient: String): Boolean {
        return allergies.any { it.equals(ingredient, ignoreCase = true) }
    }
}