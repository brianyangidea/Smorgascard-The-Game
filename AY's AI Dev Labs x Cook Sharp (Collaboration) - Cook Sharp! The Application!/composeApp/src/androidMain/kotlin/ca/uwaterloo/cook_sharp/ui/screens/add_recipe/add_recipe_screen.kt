package ca.uwaterloo.cook_sharp.ui.screens.add_recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import ca.uwaterloo.cook_sharp.ui.theme.DisabledButton
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.input.KeyboardType
import ca.uwaterloo.cook_sharp.ui.theme.FilterSelected
import ca.uwaterloo.cook_sharp.ui.theme.TextOnDark
import ca.uwaterloo.cook_sharp.ui.theme.blackText
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ca.uwaterloo.cook_sharp.domain.MealTypeAPI
import androidx.compose.foundation.BorderStroke
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage


@Composable
fun AddRecipeScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    vm: AddRecipeViewModel = viewModel()
) {
    val s = vm.ui_state
    val context = LocalContext.current

    LaunchedEffect(s.savedSuccessfully) {
        if (s.savedSuccessfully) onSaved()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        vm.updateImageUri(uri?.toString())
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 12.dp)
                    .heightIn(min = 56.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Add Recipe",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 48.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            RecipeImageUploadCard(
                modifier = Modifier.padding(top = 8.dp),
                imageUri = s.imageUri,
                onUploadClick = { launcher.launch("image/*") }
            )
            Spacer(Modifier.height(16.dp))

            LabeledPillField(
                label = "Title",
                value = s.title,
                placeholder = "Pina Colada",
                onValueChange = vm::updateTitle
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    LabeledPillField(
                        label = "Time Recipe",
                        value = if (s.readyInMinutes == 0) "" else s.readyInMinutes.toString(),
                        placeholder = "30 (time in minutes)",
                        onValueChange = vm::updateReadyInMinutes,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    LabeledPillField(
                        label = "Difficulty",
                        value = s.difficulty,
                        placeholder = "Easy",
                        onValueChange = vm::updateDifficulty
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LabeledPillField(
                label = "Cuisine Type",
                value = s.cuisineType,
                placeholder = "Italian",
                onValueChange = vm::updateCuisine
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    LabeledPillField(
                        label = "Calories",
                        value = if (s.calories == 0) "" else s.calories.toString(),
                        placeholder = "450",
                        onValueChange = vm::updateCalories
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    LabeledPillField(
                        label = "Servings",
                        value = if (s.servings == 0) "" else s.servings.toString(),
                        placeholder = "2",
                        onValueChange = vm::updateServings
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            SectionTitle("Ingredients")

            Spacer(Modifier.height(8.dp))
            s.ingredientRows.forEachIndexed { index, row ->
                IngredientRowPills(
                    amountText = row.amountText,
                    unitText = row.ingredient.unit,
                    nameText = row.ingredient.name,
                    onAmountChange = { vm.updateIngredientAmountText(index, it) },
                    onUnitChange = { vm.updateIngredientUnit(index, it) },
                    onNameChange = { vm.updateIngredientName(index, it) },
                    onDelete = { vm.removeIngredientRow(index) }
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))

            PillActionButton("Add Ingredient") { vm.addIngredientRow() }
            Spacer(Modifier.height(8.dp))

            SectionTitle("Instructions")
            Spacer(Modifier.height(8.dp))

            s.instructions.forEachIndexed { index, step ->
                InstructionRowPill(
                    value = step,
                    onValueChange = { vm.updateInstruction(index, it) },
                    onDelete = { vm.removeInstruction(index) }
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))

            PillActionButton("Add Instructions") { vm.addInstruction() }
            Spacer(Modifier.height(8.dp))

            SectionTitle("Dietary")
            Spacer(Modifier.height(8.dp))

            val dietToggles = listOf(
                DietToggleSpec("Vegetarian", s.isVegetarian) { vm.setVegetarian(!s.isVegetarian) },
                DietToggleSpec("Vegan", s.isVegan) { vm.setVegan(!s.isVegan) },
                DietToggleSpec("Gluten Free", s.isGlutenFree) { vm.setGlutenFree(!s.isGlutenFree) },
                DietToggleSpec("Dairy Free", s.isDairyFree) { vm.setDairyFree(!s.isDairyFree) },
                DietToggleSpec("Low FODMAP", s.isLowFodmap) { vm.setLowFodmap(!s.isLowFodmap) },
                DietToggleSpec("Pescetarian", s.isPescatarian) { vm.setPescatarian(!s.isPescatarian) },
                DietToggleSpec("Ketogenic", s.isKetogenic) { vm.setKetogenic(!s.isKetogenic) },
                DietToggleSpec("Paleo", s.isPaleo) { vm.setPaleo(!s.isPaleo) },
                DietToggleSpec("Whole30", s.isWhole30) { vm.setWhole30(!s.isWhole30) },
            )

            DietToggleGrid(
                items = dietToggles,
                columns = 2
            )
            Spacer(Modifier.height(8.dp))
            SectionTitle("Meal Type")
            Spacer(Modifier.height(8.dp))

            MealTypeDropdown(
                selected = s.mealTypes,
                onToggle = vm::toggleMealType
            )

            Spacer(Modifier.height(8.dp))

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClearButton(
                    text = "Clear",
                    onClick = { vm.reset() },
                    modifier = Modifier.weight(1f)
                )
                SaveButton(
                    text = "Save",
                    enabled = vm.canSave() && !s.isSaving,
                    onClick = { vm.saveRecipe(context) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun InstructionRowPill(
    value: String,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PillTextField(
            value = value,
            placeholder = "Add instruction...",
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        )

        Spacer(Modifier.width(10.dp))

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(DisabledButton)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = TextPrimary)
        }
    }
}

@Composable
private fun RecipeImageUploadCard(
    modifier: Modifier = Modifier,
    imageUri: String? = null,
    onUploadClick: () -> Unit
) {
    val corner = 28.dp

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(corner),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(corner))
                .border(
                    width = 2.dp,
                    color = blackText,
                    shape = RoundedCornerShape(corner)
                )
                .clickable(onClick = onUploadClick)
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected recipe image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ramen_bowl),
                    contentDescription = "Recipe image placeholder",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.55f)
                )

                Image(
                    painter = painterResource(id = R.drawable.upload_image),
                    contentDescription = "Upload recipe image",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun IngredientRowPills(
    amountText: String,
    unitText: String,
    nameText: String,
    onAmountChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PillTextField(
            value = amountText,
            placeholder = "Amt",
            onValueChange = { raw -> onAmountChange(raw.filter { it.isDigit() || it == '.' }) },
            modifier = Modifier
                .width(80.dp)
                .height(52.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(Modifier.width(10.dp))

        PillTextField(
            value = unitText,
            placeholder = "ml",
            onValueChange = onUnitChange,
            modifier = Modifier
                .width(80.dp)
                .height(52.dp)
        )

        Spacer(Modifier.width(10.dp))

        PillTextField(
            value = nameText,
            placeholder = "Ingredient",
            onValueChange = onNameChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        )

        Spacer(Modifier.width(10.dp))

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(DisabledButton)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete ingredient",
                tint = TextPrimary
            )
        }
    }
}

@Composable
private fun PillTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = TextPrimary.copy(alpha = 0.55f)
            )
        },
        modifier = modifier.height(52.dp),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DisabledButton,
            unfocusedContainerColor = DisabledButton,
            disabledContainerColor = DisabledButton,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = TextPrimary
        )
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 8.dp, start = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary
    )
}

@Composable
private fun LabeledPillField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Text(
        text = label,
        modifier = Modifier.padding(top = 8.dp, start = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary
    )
    Spacer(Modifier.height(8.dp))

    PillTextField(
        value = value,
        placeholder = placeholder,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun PillActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = blackText,
            contentColor = TextOnDark
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun TogglePill(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) FilterSelected else DisabledButton
    val fg = if (selected) TextOnDark else TextPrimary

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(18.dp),
        color = bg,
        modifier = modifier.height(44.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun MealTypeDropdown(
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = remember {
        MealTypeAPI.entries.map { it.label }
    }

    val displayText = when {
        selected.isEmpty() -> "Select meal type"
        else -> selected.joinToString(", ")
    }

    Box {
        PillTextField(
            value = displayText,
            placeholder = "Select meal type",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        Icon(
            painter = painterResource(id = R.drawable.down_arrow),
            contentDescription = "Open meal type dropdown",
            tint = TextPrimary,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .size(18.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                val isSelected = selected.contains(option)

                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onToggle(option) },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = TextPrimary
                            )
                        }
                    }
                )
            }
        }
    }
}

private data class DietToggleSpec(
    val label: String,
    val selected: Boolean,
    val onToggle: () -> Unit
)

@Composable
private fun DietToggleGrid(
    items: List<DietToggleSpec>,
    columns: Int = 2
) {
    val rows = items.chunked(columns)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    TogglePill(
                        text = item.label,
                        selected = item.selected,
                        onToggle = item.onToggle,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size < columns) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) blackText else DisabledButton,
            contentColor = if (enabled) TextOnDark else TextPrimary
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ClearButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(2.dp, blackText),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = blackText
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}