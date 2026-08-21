package ca.uwaterloo.cook_sharp.ui.navigation
import androidx.navigation.NavController
import ca.uwaterloo.cook_sharp.ui.components.BottomTab

fun handleBottomNavigation(
    navController: NavController,
    currentTab: BottomTab,
    selectedTab: BottomTab
) {
    if (currentTab == selectedTab) return

    val route = when (selectedTab) {
        BottomTab.Home -> Routes.HOME
        BottomTab.NutritionalDashboard -> Routes.NUTRIENT_DASHBOARD
        BottomTab.GroceryList -> Routes.GROCERY_LIST
        BottomTab.MealPlan -> Routes.MEAL_PLAN
    }

    navController.navigate(route) {
        popUpTo(Routes.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}