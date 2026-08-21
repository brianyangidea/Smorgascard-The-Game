package ca.uwaterloo.cook_sharp.ui.screens.add_recipe

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseStorageRepository
import ca.uwaterloo.cook_sharp.domain.Ingredient
import ca.uwaterloo.cook_sharp.domain.RecipeSource
import java.util.UUID
import ca.uwaterloo.cook_sharp.domain.Model
import ca.uwaterloo.cook_sharp.domain.CreateIngredientInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInstructionInput
import ca.uwaterloo.cook_sharp.domain.MealType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Functionalities
 * - Track recipe creation form fields
 * - Manage ingredients, instructions, name, calories, meal type, cuisine type
 * - Validate user input before submission - each field should have a value
 * - Submit a new recipe to the data layer - as a user created recipe
 */
class AddRecipeViewModel(private val model: Model = Model()) : ViewModel() {
    var ui_state by mutableStateOf(AddRecipeUiState())
        private set

    fun updateTitle(v: String) = set { copy(title = v) }

    fun updateReadyInMinutes(v: String) =
        set { copy(readyInMinutes = v.filter { it.isDigit() }.toIntOrNull() ?: 0) }

    fun updateCalories(v: String) =
        set { copy(calories = v.filter { it.isDigit() }.toIntOrNull() ?: 0) }

    fun updateServings(v: String) = set { copy(servings = v.filter { it.isDigit() }.toIntOrNull() ?: 0) }

    fun updateDifficulty(v: String) = set { copy(difficulty = v) }

    fun toggleMealType(mt: String) = set {
        copy(mealTypes = if (mealTypes.contains(mt)) mealTypes - mt else mealTypes + mt)
    }

    fun updateCuisine(v: String) = set { copy(cuisineType = v) }

    fun setVegetarian(v: Boolean) = set { copy(isVegetarian = v) }
    fun setVegan(v: Boolean) = set { copy(isVegan = v) }
    fun setGlutenFree(v: Boolean) = set { copy(isGlutenFree = v) }
    fun setDairyFree(v: Boolean) = set { copy(isDairyFree = v) }
    fun setLowFodmap(v: Boolean) = set { copy(isLowFodmap = v) }
    fun setPescatarian(v: Boolean) = set { copy(isPescatarian = v) }
    fun setKetogenic(v: Boolean) = set { copy(isKetogenic = v) }
    fun setPaleo(v: Boolean) = set { copy(isPaleo = v) }
    fun setWhole30(v: Boolean) = set { copy(isWhole30 = v) }
    fun addInstruction() = set { copy(instructions = instructions + "") }

    fun updateInstruction(index: Int, text: String) = set {
        if (index !in instructions.indices) return@set this
        copy(instructions = instructions.mapIndexed { i, s -> if (i == index) text else s })
    }

    fun removeInstruction(index: Int) = set {
        if (index !in instructions.indices) return@set this
        copy(instructions = instructions.toMutableList().also { it.removeAt(index) })
    }

    fun updateImageUri(uri: String?) = set { copy(imageUri = uri) }

    fun addIngredientRow() = set {
        val nextId = (ingredientRows.maxOfOrNull { it.ingredient.id } ?: 0L) + 1L
        copy(
            ingredientRows = ingredientRows + IngredientFormRow(
                ingredient = Ingredient(
                    id = nextId,
                    recipeId = 0L,
                    name = "",
                    amount = 0.0,
                    unit = "",
                    originalName = null
                ),
                amountText = ""
            )
        )
    }

    fun removeIngredientRow(index: Int) = set {
        if (ingredientRows.size <= 1) return@set this
        if (index !in ingredientRows.indices) return@set this
        val list = ingredientRows.toMutableList()
        list.removeAt(index)
        copy(ingredientRows = list)
    }

    fun updateIngredientName(index: Int, name: String) = set {
        copy(ingredientRows = ingredientRows.mapIndexed { i, row ->
            if (i == index) row.copy(ingredient = row.ingredient.copy(name = name)) else row
        })
    }

    fun updateIngredientUnit(index: Int, unit: String) = set {
        copy(ingredientRows = ingredientRows.mapIndexed { i, row ->
            if (i == index) row.copy(ingredient = row.ingredient.copy(unit = unit)) else row
        })
    }

    fun updateIngredientAmountText(index: Int, amt: String) = set {
        val cleaned = amt.filter { it.isDigit() || it == '.' }
        copy(ingredientRows = ingredientRows.mapIndexed { i, row ->
            if (i == index) row.copy(amountText = cleaned) else row
        })
    }

    fun canSave(): Boolean {
        val s = ui_state

        val hasAtLeastOneValidIngredient = s.ingredientRows.any { row ->
            val name = row.ingredient.name.trim().isNotBlank()
            val unit = row.ingredient.unit.trim().isNotBlank()
            val amt = row.amountText.trim().toDoubleOrNull()
            val amount = (amt != null && amt > 0.0)
            name && unit && amount
        }

        val hasInstructions = s.instructions.any { it.trim().isNotBlank() }

        return s.title.isNotBlank() && hasAtLeastOneValidIngredient && hasInstructions
    }

    fun saveRecipe(context: Context) {
        if (!canSave()) return
        val s = ui_state
        ui_state = ui_state.copy(isSaving = true)

        val ingredientsInput: List<CreateIngredientInput> = s.ingredientRows
            .mapNotNull { row ->
                val name = row.ingredient.name.trim()
                val unit = row.ingredient.unit.trim()
                val amountText = row.amountText.trim()
                val amount = amountText.toDoubleOrNull()

                if (name.isBlank() || unit.isBlank() || amount == null || amount <= 0.0) {
                    null
                } else {
                    CreateIngredientInput(
                        name = name,
                        amount = amount,
                        unit = unit,
                        originalName = "$amountText $unit $name".trim()
                    )
                }
            }

        val instructionsInput: List<CreateRecipeInstructionInput> = s.instructions
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapIndexed { index, text ->
                CreateRecipeInstructionInput(
                    stepNumber = index + 1,
                    instruction = text
                )
            }

        val mealTypes: List<MealType> = s.mealTypes
            .mapNotNull { it.toMealTypeOrNull() }
            .distinct()

        val localId = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                val imageUrl: String? = withContext(Dispatchers.IO) {
                    s.imageUri?.let { uriString ->
                        runCatching {
                            val uri = android.net.Uri.parse(uriString)
                            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            if (bytes != null) {
                                SupabaseStorageRepository.uploadRecipeImage(localId, bytes)
                            } else null
                        }.getOrNull()
                    }
                }

                val input = CreateRecipeInput(
                    createdByUserId = model.getCurrentUser()?.id,
                    title = s.title.trim(),
                    readyInMinutes = s.readyInMinutes,
                    difficulty = s.difficulty,
                    image = imageUrl ?: s.imageUri,
                    servings = s.servings,
                    cuisineType = s.cuisineType.ifBlank { null },

                    isVegetarian = s.isVegetarian,
                    isVegan = s.isVegan,
                    isGlutenFree = s.isGlutenFree,
                    isDairyFree = s.isDairyFree,
                    isLowFodmap = s.isLowFodmap,
                    isPescatarian = s.isPescatarian,
                    isKetogenic = s.isKetogenic,
                    isPaleo = s.isPaleo,
                    isWhole30 = s.isWhole30,

                    source = RecipeSource.USER,
                    localId = localId,
                    remoteId = null,

                    nutritionInfo = null,
                    ingredients = ingredientsInput,
                    instructions = instructionsInput,
                    mealTypes = mealTypes
                )

                withContext(Dispatchers.IO) { model.addRecipe(input) }
                ui_state = ui_state.copy(isSaving = false, savedSuccessfully = true)
            } catch (e: Exception) {
                e.printStackTrace()
                ui_state = ui_state.copy(isSaving = false)
            }
        }
    }


    fun reset() {
        ui_state = AddRecipeUiState()
    }

    private inline fun set(update: AddRecipeUiState.() -> AddRecipeUiState) {
        ui_state = ui_state.update()
    }

    private fun String.toMealTypeOrNull(): MealType? =
        when (trim().uppercase()) {
            "BREAKFAST" -> MealType.BREAKFAST
            "LUNCH" -> MealType.LUNCH
            "DINNER" -> MealType.DINNER
            "SNACK" -> MealType.SNACK
            else -> null
        }
}