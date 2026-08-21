package ca.uwaterloo.cook_sharp.ui.screens.filter
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.LaunchedEffect
import ca.uwaterloo.cook_sharp.ui.screens.home.HomeViewModel
import ca.uwaterloo.cook_sharp.domain.MealType

@Composable
fun Filter_screen(
    homeViewModel: HomeViewModel,
    vm: FilterViewModel = viewModel(),
    onApply: (FilterState) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state = vm.ui_state

    LaunchedEffect(Unit) {
        val active = homeViewModel.ui_state.activeFilter
        if (active != null) {
            vm.setFromFilterRecipe(active)
        } else {
            vm.resetFilters()
        }
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Filter_heading(onBack = onBack)
        },
        bottomBar = {
            Filter_bottom(
                onReset = vm::resetFilters,
                onApply = { onApply(state) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(34.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                Filter_section(title = "Meal Types") {
                    MealTypeOptionsFlow(
                        items = vm.mealTypeOptions,
                        selectedItems = state.selectedMealTypes,
                        onToggle = vm::meal_type
                    )
                }
            }
            item {
                Filter_section(title = "Calorie Range") {
                    Calorie_slider(
                        minCalories = state.minCalories,
                        maxCalories = state.maxCalories,
                        onMinChange = vm::update_min_calories,
                        onMaxChange = vm::update_max_calories
                    )
                }
            }
            item {
                Filter_section(title = "Exclude Ingredients") {
                    Exclude_ingredients_input(
                        excludedIngredients = state.excludedIngredients,
                        inputText = state.excludedIngredientInput,
                        onInputChange = vm::update_excluded_ingredient_input,
                        onAdd = vm::add_excluded_ingredient_from_input,
                        onRemove = vm::remove_excluded_ingredient
                    )
                }
            }
            item {
                Filter_section(title = "Diets") {
                    Options_flow(
                        items = vm.dietOptions,
                        selectedItems = state.selectedDiets,
                        onToggle = vm::toggle_diet
                    )
                }
            }
            item {
                Filter_section(title = "Cuisine") {
                    Options_flow(
                        items = vm.cuisineOptions,
                        selectedItems = state.selectedCuisines,
                        onToggle = vm::toggle_cuisine
                    )
                }
            }
        }
    }
}

@Composable
private fun Filter_heading(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(FilterSelected)
            .statusBarsPadding()
            .height(45.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow),
                contentDescription = "Back",
                tint = TextOnDark,
                modifier = Modifier.size(34.dp)
            )
        }
        Text(
            text = "FILTER",
            style = MaterialTheme.typography.titleMedium,
            color = TextOnDark,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun Filter_bottom(
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground)
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Filterunselected,
                contentColor = FilterSelected
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "RESET",
                style = MaterialTheme.typography.labelMedium,
                color = FilterSelected
            )
        }
        Button(
            onClick = onApply,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FilterSelected,
                contentColor = TextOnDark
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "APPLY",
                style = MaterialTheme.typography.labelMedium,
                color = TextOnDark
            )
        }
    }
}

@Composable
private fun Filter_section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        content()
    }
}

@Composable
private fun Options_flow(
    items: List<String>,
    selectedItems: Set<String>,
    onToggle: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Options(
                text = item,
                selected = selectedItems.contains(item),
                onClick = { onToggle(item) }
            )
        }
    }
}

@Composable
private fun Options(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) FilterSelected else Filterunselected
    val textColor = if (selected) TextOnDark else FilterSelected

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun Calorie_slider(
    minCalories: Int,
    maxCalories: Int,
    onMinChange: (Int) -> Unit,
    onMaxChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$minCalories kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Text(
                text = "$maxCalories kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }

        RangeSlider(
            value = minCalories.toFloat()..maxCalories.toFloat(),
            onValueChange = { range ->
                onMinChange(range.start.toInt())
                onMaxChange(range.endInclusive.toInt())
            },
            valueRange = 0f..2000f,
            steps = 39,
            colors = SliderDefaults.colors(
                thumbColor = FilterSelected,
                activeTrackColor = FilterSelected,
                inactiveTrackColor = Filterunselected
            )
        )
    }
}

@Composable
private fun Exclude_ingredients_input(
    excludedIngredients: List<String>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange =onInputChange,
                placeholder = { Text("e.g., peanuts", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardSurface,
                    unfocusedContainerColor = CardSurface,
                    focusedBorderColor = FilterSelected,
                    unfocusedBorderColor = CardSurface
                ),
                shape = RoundedCornerShape(8.dp)
            )

            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .size(48.dp)
                    .background(FilterSelected, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    painter = painterResource(R.drawable.correct),
                    contentDescription = "Add ingredient",
                    tint = TextOnDark,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (excludedIngredients.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                excludedIngredients.forEach { ingredient ->
                    ExcludedIngredientChip(
                        text = ingredient,
                        onRemove = { onRemove(ingredient) }
                    )
                }
            }
        }
    }
}
@Composable
private fun ExcludedIngredientChip(
    text: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LikeAccent.copy(alpha = 0.1f))
            .border(1.dp, LikeAccent, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = LikeAccent
        )
        Icon(
            painter = painterResource(R.drawable.close),
            contentDescription = "Remove",
            tint = LikeAccent,
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}

@Composable
private fun MealTypeOptionsFlow(
    items: List<MealType>,
    selectedItems: Set<MealType>,
    onToggle: (MealType) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Options(
                text = item.displayLabel(),
                selected = selectedItems.contains(item),
                onClick = { onToggle(item) }
            )
        }
    }
}

private fun MealType.displayLabel(): String {
    return name.lowercase()
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Filter_screen_preview() {
    CookSharpTheme {
        Filter_screen(vm = FilterViewModel(),homeViewModel = HomeViewModel(),)
    }
}