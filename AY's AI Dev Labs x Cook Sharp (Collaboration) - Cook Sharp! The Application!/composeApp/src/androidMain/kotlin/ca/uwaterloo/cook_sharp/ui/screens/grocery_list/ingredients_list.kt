package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.*
import ca.uwaterloo.cook_sharp.domain.Ingredient
import java.util.Locale

@Composable
fun IngredientsList(
    ingredients: List<Ingredient>,
    checkedStates: Map<Int, Boolean>,
    onCheck: (Int) -> Unit
) {
    Column {
        ingredients.forEachIndexed { index, ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        append(ingredient.name)
                        if (ingredient.amount > 0.0) {
                            append(" — ")
                            append(String.format(Locale.US, "%.1f", ingredient.amount))
                            if (ingredient.unit.isNotBlank()) {
                                append(" ")
                                append(ingredient.unit)
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    textDecoration = if (checkedStates[index] == true) TextDecoration.LineThrough else null
                )
                Checkbox(
                    checked = checkedStates[index] ?: false,
                    onCheckedChange = { onCheck(index) },
                    modifier = Modifier.size(36.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = TextPrimary,
                        uncheckedColor = TextPrimary
                    )
                )
            }
        }
    }
}
