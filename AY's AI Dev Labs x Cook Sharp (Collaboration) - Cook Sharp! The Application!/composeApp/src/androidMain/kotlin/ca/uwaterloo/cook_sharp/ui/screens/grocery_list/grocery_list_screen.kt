package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.domain.GroceryItem
import ca.uwaterloo.cook_sharp.ui.components.*
import ca.uwaterloo.cook_sharp.ui.theme.*

@Composable
fun GroceryListScreen(
    onTabSelected: (BottomTab) -> Unit, vm: GroceryListViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        vm.refreshGroceryList()
    }

    val state = vm.ui_state

    Scaffold(containerColor = AppBackground, topBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 18.dp, bottom = 12.dp)
        ) {
            Text(
                text = "Grocery List",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            ViewModeToggle(
                selected = state.viewMode, onSelect = { vm.switchViewMode(it) })
        }
    }, bottomBar = {
        bottom_navigation(
            selected = BottomTab.GroceryList, onSelected = onTabSelected
        )
    }) { padding ->
        if (state.items.isEmpty()) {
            EmptyGroceryListMessage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else if (state.viewMode == GroceryViewMode.RECIPES) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.items) { item ->
                    GroceryRecipeCard(
                        item = item,
                        onExpand = { vm.toggleExpanded(item.recipe.id) },
                        onCheck = { index -> vm.toggleIngredientCheck(item.recipe.id, index) },
                        onIncrement = { vm.incrementServings(item.recipe.id) },
                        onDecrement = { vm.decrementServings(item.recipe.id) },
                        onDelete = { vm.deleteRecipe(item.recipe.id) })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    MergedIngredientsList(
                        ingredients = vm.mergedIngredients,
                        isChecked = { merged -> calculateMergedToggleState(merged, state.items) },
                        onSetChecked = vm::updateMergedIngredientChecked
                    )
                }
            }
        }
    }
}

private fun calculateMergedToggleState(
    merged: MergedIngredient,
    items: List<GroceryItem>
): ToggleableState {
    val states = merged.refs.map { ref ->
        items.firstOrNull { it.recipe.id == ref.recipeId }
            ?.checkedStates
            ?.get(ref.ingredientIndex)
            ?: false
    }

    return when {
        states.isEmpty() -> ToggleableState.Off
        states.all { it } -> ToggleableState.On
        states.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }
}

@Composable
private fun EmptyGroceryListMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(horizontal = 24.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Your grocery list is empty.\nAdd a recipe to get started!",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )
    }
}

@Composable
private fun ViewModeToggle(
    selected: GroceryViewMode, onSelect: (GroceryViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .padding(4.dp)
    ) {
        GroceryViewMode.entries.forEach { mode ->
            val isSelected = mode == selected
            val label = if (mode == GroceryViewMode.RECIPES) "Recipes" else "Ingredients"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) PrimaryButtonBackground else Color.Transparent)
                    .clickable { onSelect(mode) }
                    .padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) Color.White else TextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroceryListScreenPreview() {
    CookSharpTheme {
        GroceryListScreen(
            onTabSelected = {}, vm = GroceryListViewModel()
        )
    }
}
