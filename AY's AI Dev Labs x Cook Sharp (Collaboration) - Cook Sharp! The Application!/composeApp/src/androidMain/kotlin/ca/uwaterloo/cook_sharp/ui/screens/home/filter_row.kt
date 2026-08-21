package ca.uwaterloo.cook_sharp.ui.screens.home
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import ca.uwaterloo.cook_sharp.ui.theme.FilterSelected
import ca.uwaterloo.cook_sharp.ui.theme.Filterunselected
import ca.uwaterloo.cook_sharp.ui.theme.TextFilter
import androidx.compose.material3.Icon
import ca.uwaterloo.cook_sharp.domain.MealType

@Composable
fun recipe_filter_row(
    selected: MealType?,
    onSelected: (MealType?) -> Unit,
    onFilterClick: () -> Unit,
    filterActive: Boolean,
    modifier: Modifier = Modifier,
    filterIconRes: Int = R.drawable.edit
) {
    val filters = listOf(
        MealType.LUNCH,
        MealType.DINNER,
        MealType.BREAKFAST,
        MealType.SNACK
    )
    val filterBg = if (filterActive) FilterSelected else Filterunselected
    val iconTint = if (filterActive) Color.White else TextFilter
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onFilterClick,
            shape = RoundedCornerShape(16.dp),
            color = filterBg,
            shadowElevation = 4.dp,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(filterIconRes),
                    contentDescription = "Filter",
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(end = 14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                val isSelected = (selected == filter)
                val bg = if (isSelected) FilterSelected else Filterunselected
                val textColor = if (isSelected) Color.White else TextFilter
                val elevation = if (isSelected) 6.dp else 4.dp

                Surface(
                    onClick = { onSelected(if (isSelected) null else filter) },
                    shape = RoundedCornerShape(16.dp),
                    color = bg,
                    shadowElevation = elevation,
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = filter.displayName(),
                            style = MaterialTheme.typography.titleSmall,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

private fun MealType.displayName(): String =
    when (this) {
        MealType.BREAKFAST -> "Breakfast"
        MealType.LUNCH -> "Lunch"
        MealType.DINNER -> "Dinner"
        MealType.SNACK -> "Snack"
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun recipe_filter_row_preview() {
    CookSharpTheme {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
            Spacer(Modifier.height(24.dp))
            recipe_filter_row(
                selected = MealType.LUNCH,
                onSelected = {},
                onFilterClick = {},
                filterActive = false,
                filterIconRes = R.drawable.edit
            )
        }
    }
}
