package ca.uwaterloo.cook_sharp.domain

data class Recipe(
    val id: Long,
    val createdByUserId: String? = null,
    val title: String,
    val readyInMinutes: Int,
    val difficulty: String,
    val image: String? = null,
    val servings: Int,
    val cuisineType: String? = null,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val isLowFodmap: Boolean = false,
    val isPescatarian: Boolean = false,
    val isKetogenic: Boolean = false,
    val isPaleo: Boolean = false,
    val isWhole30: Boolean = false,
    val isLiked: Boolean = false,
    val source: RecipeSource = RecipeSource.API,
    val localId: String,
    val remoteId: Long? = null,
    val nutritionInfo: NutritionInfo? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<RecipeInstruction> = emptyList(),
    val mealTypes: List<MealType> = emptyList(),
){

    val calories: Double get() = nutritionInfo?.calories ?: 0.0
    val protein: Double get() = nutritionInfo?.protein ?: 0.0
    val carbs: Double get() = nutritionInfo?.carbs ?: 0.0
    val fat: Double get() = nutritionInfo?.fat ?: 0.0
    fun containsIngredientName(name: String): Boolean {
        val q = name.trim().lowercase()
        if (q.isBlank()) return false
        val regex = Regex("\\b${Regex.escape(q)}\\b")
        return ingredients.any { regex.containsMatchIn(it.name.trim().lowercase()) }
    }

    fun containsAllergen(allergen: String): Boolean {
        val expandedAllergens = expandAllergenCategory(allergen)
        return ingredients.any { ingredient ->
            val ingredientLower = ingredient.name.trim().lowercase()
            expandedAllergens.any { allergen ->
                val regex = Regex("\\b${Regex.escape(allergen)}\\b")
                regex.containsMatchIn(ingredientLower)
            }
        }
    }

    companion object {
        private fun expandAllergenCategory(allergen: String): Set<String> {
            val allergenLower = allergen.trim().lowercase()
            return when (allergenLower) {
                "nuts" -> setOf("nuts", "walnut", "walnuts", "almond", "almonds", "cashew", "cashews",
                    "pecan", "pecans", "pistachio", "pistachios", "macadamia", "hazelnut", "hazelnuts",
                    "brazil nut", "brazil nuts", "pine nut", "pine nuts")
                "peanuts" -> setOf("peanuts", "peanut", "groundnuts", "groundnut")
                "shellfish" -> setOf("shellfish", "shrimp", "prawns", "lobster", "crab", "crayfish",
                    "clams", "mussels", "oysters", "scallops")
                "fish" -> setOf("fish", "salmon", "tuna", "cod", "halibut", "trout", "anchovy", "anchovies")
                "dairy" -> setOf("dairy", "milk", "cheese", "butter", "cream", "yogurt", "lactose")
                "gluten" -> setOf("gluten", "wheat", "barley", "rye", "bread", "pasta")
                else -> setOf(allergenLower)
            }
        }
    }

    fun containsDietaryRestriction(diet: String): Boolean {
        return when (diet) {
            "No Dietary restriction" -> true
            "Vegetarian" -> isVegetarian
            "Vegan" -> isVegan
            "Pescatarian" -> isPescatarian
            "Gluten Free", "Gluten-Free" -> isGlutenFree
            "Lacto-Vegetarian" -> isVegetarian && !isDairyFree
            "Ovo-Vegetarian" -> isVegetarian && isDairyFree
            "Ketogenic" -> isKetogenic
            "Paleo" -> isPaleo
            "Primal" -> isPaleo
            "Low FODMAP" -> isLowFodmap
            "Whole30" -> isWhole30
            else -> false
        }
    }

    fun scaleIngredients(serving: Int): List<Ingredient> {
        require(serving > 0) { "serving size should be at lease 1" }
        val scale = serving.toDouble() / servings.toDouble()
        if (serving == servings) return ingredients
        return ingredients.map { i -> i.copy(amount = i.amount * scale)}
    }
}

data class CreateRecipeInput(
    val createdByUserId: String? = null,
    val title: String,
    val readyInMinutes: Int,
    val difficulty: String,
    val image: String? = null,
    val servings: Int,
    val cuisineType: String? = null,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false,
    val isLowFodmap: Boolean = false,
    val isPescatarian: Boolean = false,
    val isKetogenic: Boolean = false,
    val isPaleo: Boolean = false,
    val isWhole30: Boolean = false,
    val source: RecipeSource = RecipeSource.USER,
    val localId: String,
    val remoteId: Long? = null,
    val nutritionInfo: CreateNutritionInfoInput? = null,
    val ingredients: List<CreateIngredientInput> = emptyList(),
    val instructions: List<CreateRecipeInstructionInput> = emptyList(),
    val mealTypes: List<MealType> = emptyList()
)
