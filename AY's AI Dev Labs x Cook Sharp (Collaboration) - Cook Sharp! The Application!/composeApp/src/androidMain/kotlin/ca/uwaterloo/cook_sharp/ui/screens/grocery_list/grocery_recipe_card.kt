package ca.uwaterloo.cook_sharp.ui.screens.grocery_list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.domain.GroceryItem
import ca.uwaterloo.cook_sharp.ui.components.ServingsCounter
import ca.uwaterloo.cook_sharp.ui.theme.*

@Composable
fun GroceryRecipeCard(
    item: GroceryItem,
    onExpand: () -> Unit,
    onCheck: (Int) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit = {}
) {
    val scaledIngredients = item.recipe.scaleIngredients(item.servings)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .border(2.dp, PrimaryButtonBackground, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .clickable { onDelete() }
                        .padding(horizontal = 8.dp),
                    tint = TextMuted
                )

                ServingsCounter(
                    count = item.servings,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )
            }

            if (item.isExpanded) {
                Spacer(Modifier.height(12.dp))
                IngredientsList(
                    ingredients = scaledIngredients,
                    checkedStates = item.checkedStates,
                    onCheck = onCheck
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpand() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.isExpanded) "Show Less" else "Show More",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }
    }
}
