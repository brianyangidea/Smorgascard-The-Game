package ca.uwaterloo.cook_sharp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import ca.uwaterloo.cook_sharp.R
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider

sealed class BottomTab(val label: String, val icon: Int) {
    data object Home : BottomTab("Home", R.drawable.home)
    data object GroceryList : BottomTab("Grocery List", R.drawable.grocery_list)
    data object MealPlan : BottomTab("Meal Plan",R.drawable.meal_plan)
    data object NutritionalDashboard : BottomTab("Dashboard", R.drawable.chef_hat)
}

@Composable
fun bottom_navigation(
    selected: BottomTab,
    onSelected: (BottomTab) -> Unit
) {
    val tabs = listOf(
        BottomTab.Home,
        BottomTab.GroceryList,
        BottomTab.MealPlan,
        BottomTab.NutritionalDashboard
    )

    Column(Modifier.fillMaxWidth().background(AppBackground)) {
        HorizontalDivider(
            thickness = 1.dp,
            color = TextPrimary
        )

        NavigationBar(containerColor = AppBackground) {
            tabs.forEach { tab ->
                nav_items(
                    tab = tab,
                    selected = (selected == tab),
                    onClick = { onSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.nav_items(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(
            painter = painterResource(tab.icon),
            contentDescription = tab.label,
            modifier = Modifier.size(28.dp)
        ) },
        label = { Text(
            text = tab.label,
            style = MaterialTheme.typography.bodySmall
        ) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TextMuted,
            selectedTextColor = TextPrimary,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted,
            indicatorColor = EnabledButton
        )
    )
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun bottom_navigation_preview() {
    CookSharpTheme {
        bottom_navigation(
            selected = BottomTab.Home,
            onSelected = {}
        )
    }
}
