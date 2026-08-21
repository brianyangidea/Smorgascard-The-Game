package ca.uwaterloo.cook_sharp.ui.screens.add_recipe
import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import ca.uwaterloo.cook_sharp.data.mock.RecipeStore
import ca.uwaterloo.cook_sharp.domain.Model
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddRecipeViewModelTest {
    private lateinit var vm: AddRecipeViewModel

    @BeforeTest
    fun setup() {
        RecipeStore.reset()
        vm = AddRecipeViewModel(Model(repo = MockRecipeRepository()))
    }

    @Test
    fun canSave_false_when_empty() {
        val ok = vm.canSave()
        assertFalse(ok)
    }

    @Test
    fun canSave_true_when_title_ingredient_instruction_entered() {
        vm.updateTitle("Pasta")
        vm.updateServings("2")
        vm.updateReadyInMinutes("10")
        vm.updateCalories("300")
        vm.updateIngredientName(0, "Noodles")
        vm.updateIngredientUnit(0, "g")
        vm.updateIngredientAmountText(0, "100")
        vm.updateInstruction(0, "Boil water")
        val ok = vm.canSave()
        assertTrue(ok)
    }

    @Test
    fun canSave_true_when_recipe_is_ready_to_save() {
        vm.updateTitle("Toast")
        vm.updateServings("1")
        vm.updateReadyInMinutes("2")
        vm.updateCalories("100")
        vm.updateIngredientName(0, "Bread")
        vm.updateIngredientUnit(0, "slice")
        vm.updateIngredientAmountText(0, "1")
        vm.updateInstruction(0, "Toast it")

        assertTrue(vm.canSave())
    }

    @Test
    fun canSave_false_when_ingredient_name_missing() {
        vm.updateTitle("Pasta")
        vm.updateServings("2")
        vm.updateReadyInMinutes("10")
        vm.updateCalories("300")
        vm.updateIngredientName(0, "")
        vm.updateIngredientUnit(0, "g")
        vm.updateIngredientAmountText(0, "100")
        vm.updateInstruction(0, "boil water")
        assertFalse(vm.canSave())
    }

    @Test
    fun canSave_false_when_ingredient_unit_missing() {
        vm.updateTitle("Pasta")
        vm.updateServings("2")
        vm.updateReadyInMinutes("10")
        vm.updateCalories("300")
        vm.updateIngredientName(0, "noodles")
        vm.updateIngredientUnit(0, "")
        vm.updateIngredientAmountText(0, "100")
        vm.updateInstruction(0, "boil water")
        assertFalse(vm.canSave())
    }

    @Test
    fun canSave_false_when_ingredient_quantity_missing() {
        vm.updateTitle("Pasta")
        vm.updateServings("2")
        vm.updateReadyInMinutes("10")
        vm.updateCalories("300")
        vm.updateIngredientName(0, "noodles")
        vm.updateIngredientUnit(0, "g")
        vm.updateIngredientAmountText(0, "")
        vm.updateInstruction(0, "boil water")
        assertFalse(vm.canSave())
        vm.updateIngredientAmountText(0, "abc")
        assertFalse(vm.canSave())
        vm.updateIngredientAmountText(0, "0")
        assertFalse(vm.canSave())
    }
}