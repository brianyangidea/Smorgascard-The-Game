package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.*
import java.util.Locale

@Composable
fun MergedIngredientsList(
    ingredients: List<MergedIngredient>,
    isChecked: (MergedIngredient) -> ToggleableState,
    onSetChecked: (MergedIngredient, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, PrimaryButtonBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ingredient - Amount",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(36.dp))
        }

        HorizontalDivider(color = PrimaryButtonBackground.copy(alpha = 0.3f), modifier = Modifier.padding(top = 8.dp))

        ingredients.forEach { ingredient ->
            val toggleState = isChecked(ingredient)
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
                            append(" - ")
                            append(String.format(Locale.US, "%.1f", ingredient.amount))
                            if (ingredient.unit.isNotBlank()) {
                                append(" ")
                                append(ingredient.unit)
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    textDecoration = if (toggleState == ToggleableState.On) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )
                TriStateCheckbox(
                    state = toggleState, onClick = {
                        val targetChecked = toggleState != ToggleableState.On
                        onSetChecked(ingredient, targetChecked)
                    }, modifier = Modifier.size(36.dp), colors = CheckboxDefaults.colors(
                        checkedColor = TextPrimary, uncheckedColor = TextPrimary
                    )
                )
            }
        }
    }
}
