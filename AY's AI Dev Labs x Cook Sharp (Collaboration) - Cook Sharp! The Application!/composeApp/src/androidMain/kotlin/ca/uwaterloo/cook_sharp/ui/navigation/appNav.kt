package ca.uwaterloo.cook_sharp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ca.uwaterloo.cook_sharp.ui.components.BottomTab
import ca.uwaterloo.cook_sharp.ui.screens.add_recipe.AddRecipeScreen
import ca.uwaterloo.cook_sharp.ui.screens.chatbot.ChatBotScreen
import ca.uwaterloo.cook_sharp.ui.screens.filter.Filter_screen
import ca.uwaterloo.cook_sharp.ui.screens.grocery_list.GroceryListScreen
import ca.uwaterloo.cook_sharp.ui.screens.home.Home_screen
import ca.uwaterloo.cook_sharp.ui.screens.home.HomeViewModel
import ca.uwaterloo.cook_sharp.ui.screens.liked_recipes.LikedRecipesScreen
import ca.uwaterloo.cook_sharp.ui.screens.login.Login_screen
import ca.uwaterloo.cook_sharp.ui.screens.meal_plan.MealPlanAddScreen
import ca.uwaterloo.cook_sharp.ui.screens.meal_plan.MealPlanScreen
import ca.uwaterloo.cook_sharp.ui.screens.share_recipe.ShareRecipeScreen
import ca.uwaterloo.cook_sharp.ui.screens.meal_plan.MealPlanViewModel
import ca.uwaterloo.cook_sharp.ui.screens.nutrients.NutrientDashboardScreen
import ca.uwaterloo.cook_sharp.ui.screens.recipe_detail.RecipeDetailScreen
import ca.uwaterloo.cook_sharp.ui.screens.settings.SettingsScreen
import ca.uwaterloo.cook_sharp.ui.screens.signup.Diet_preference_screen
import ca.uwaterloo.cook_sharp.ui.screens.signup.Food_allergy_screen
import ca.uwaterloo.cook_sharp.ui.screens.signup.Signup_screen
import ca.uwaterloo.cook_sharp.ui.screens.my_recipes.MyRecipesScreen
import ca.uwaterloo.cook_sharp.domain.Recipe
import android.net.Uri
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.ui.screens.nutrients.NutrientGoalScreen
import ca.uwaterloo.cook_sharp.ui.screens.nutrients.NutrientsDashboardViewmodel
import ca.uwaterloo.cook_sharp.ui.screens.settings.ProfileScreen
import ca.uwaterloo.cook_sharp.ui.screens.received_recipes.ReceivedRecipesScreen

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val DIET_PREFERENCE = "diet_preference"
    const val FOOD_ALLERGY = "food_allergy"
    const val HOME = "home"

    const val CHATBOT = "chatbot"
    const val FILTER = "filter"
    const val MEAL_PLAN = "meal_plan?pendingRecipeId={pendingRecipeId}"
    const val Liked_RECIPES = "liked_recipes"
    const val MY_RECIPES = "my_recipes"
    const val GROCERY_LIST = "grocery_list"
    const val ADD_RECIPE = "add_recipe"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val NUTRIENT_DASHBOARD = "nutrient_dashboard"
    const val NUTRIENT_GOAL = "nutrient_goal"
    const val SHARE_RECIPE = "share_recipe"
    const val SHARE_RECIPE_ROUTE = "$SHARE_RECIPE/{recipeId}"

    const val RECIPE_DETAIL = "recipe_detail"
    const val RECIPE_DETAIL_ROUTE =
        "$RECIPE_DETAIL/{recipeId}?fromMealPlan={fromMealPlan}&dayIndex={dayIndex}&mealType={mealType}&mealId={mealId}&label={label}"

    fun shareRecipe(recipeId: Long) = "$SHARE_RECIPE/$recipeId"

    fun recipeDetail(recipeId: Long): String {
        return "$RECIPE_DETAIL/$recipeId?fromMealPlan=false&dayIndex=-1&mealType=&mealId=0&label="
    }

    fun recipeDetailForMealPlan(
        recipeId: Long,
        dayIndex: Int,
        mealType: String,
        mealId: Int?,
        label: String
    ): String {
        val safeMealType = Uri.encode(mealType)
        val safeLabel = Uri.encode(label)
        val safeMealId = mealId ?: 0

        return "$RECIPE_DETAIL/$recipeId" +
                "?fromMealPlan=true" +
                "&dayIndex=$dayIndex" +
                "&mealType=$safeMealType" +
                "&mealId=$safeMealId" +
                "&label=$safeLabel"
    }

    const val MEAL_PLAN_ADD = "meal_plan_add/{dayIndex}/{mealType}?mealId={mealId}&label={label}"
    const val RECEIVED_RECIPES = "received_recipes"

    fun mealPlanWithPendingRecipe(recipeId: Long): String {
        return "meal_plan?pendingRecipeId=$recipeId"
    }

    fun mealPlanAdd(dayIndex: Int, mealType: String, mealId: Int?, label: String): String {
        val idPart = mealId?.let { "&mealId=$it" } ?: ""
        return "meal_plan_add/$dayIndex/$mealType?label=${Uri.encode(label)}$idPart"
    }
}

@Composable
fun appNav() {
    val navController = rememberNavController()
    val onChatBotClick = { navController.navigate(Routes.CHATBOT) }
    val mealPlanViewModel : MealPlanViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val nutrientsDashboardViewmodel : NutrientsDashboardViewmodel = viewModel()


    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            Login_screen(
                onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                onLoginClick = {
                    homeViewModel.loadAllRecipes()
                    mealPlanViewModel.reloadMealPlan()
                    nutrientsDashboardViewmodel.reload()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.SIGNUP) {
            Signup_screen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Routes.DIET_PREFERENCE)
                }
            )
        }

        composable(Routes.DIET_PREFERENCE) {
            Diet_preference_screen(
                onContinue = { selectedDiet ->
                    if (navController.previousBackStackEntry?.destination?.route == Routes.SETTINGS) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Routes.FOOD_ALLERGY)
                    }
                }
            )
        }

        composable(Routes.FOOD_ALLERGY) {
            Food_allergy_screen(
                onDone = {
                    if (navController.previousBackStackEntry?.destination?.route == Routes.SETTINGS) {
                        navController.popBackStack()
                    } else {
                        homeViewModel.loadAllRecipes()
                        mealPlanViewModel.reloadMealPlan()
                        nutrientsDashboardViewmodel.reload()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Routes.CHATBOT) {
            ChatBotScreen(
                onBackClick = { navController.popBackStack() },
                onRecipeClick = { recipe ->
                    navController.navigate(Routes.recipeDetail(recipe.id))
                }
            )
        }

        composable(Routes.HOME) {
            Home_screen(
                vm = homeViewModel,
                onFilterClick = {
                    navController.navigate(Routes.FILTER)
                },
                onTabSelected = { tab ->
                    handleBottomNavigation(navController, BottomTab.Home, tab)
                },
                onRecipeClick = { recipe ->
                    navController.navigate(Routes.recipeDetail(recipe.id))
                },
                onChatBotClick = onChatBotClick,
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(
            route = Routes.MEAL_PLAN,
            arguments = listOf(
                navArgument("pendingRecipeId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val pendingRecipeId = backStackEntry.arguments?.getLong("pendingRecipeId") ?: 0L

            MealPlanScreen(
                onChatBotClick = onChatBotClick,
                onTabSelected = { tab ->
                    handleBottomNavigation(navController, BottomTab.MealPlan, tab)
                },
                onMealAddClick = { dayIndex, mealType, mealId, label ->
                    navController.navigate(Routes.mealPlanAdd(dayIndex, mealType, mealId, label))
                },
                pendingRecipeId = pendingRecipeId.takeIf { it != 0L },
                onPendingRecipePlaced = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("meal_plan_add_success", true)

                    navController.popBackStack()
                },
                mealPlanViewModel = mealPlanViewModel
            )
        }

        composable(Routes.Liked_RECIPES) {
            LikedRecipesScreen(
                onRecipeClick = { recipe: Recipe ->
                    navController.navigate(Routes.recipeDetail(recipe.id))
                },
                onBack = {
                    val popped = navController.popBackStack(Routes.SETTINGS, inclusive = false)
                    if (!popped) navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                }
            )
        }

        composable(Routes.MY_RECIPES) {
            MyRecipesScreen(
                onBack = { navController.popBackStack() },
                onRecipeClick = { navController.navigate(Routes.recipeDetail(it.id)) },
                onAddRecipeClick = { navController.navigate(Routes.ADD_RECIPE) }
            )
        }

        composable(Routes.GROCERY_LIST) {
            GroceryListScreen(
                onTabSelected = { tab ->
                    handleBottomNavigation(navController, BottomTab.GroceryList, tab)
                }
            )
        }

        composable(Routes.FILTER) {
            Filter_screen(
                homeViewModel = homeViewModel,
                onApply = { filterState ->
                    homeViewModel.applyFilter(filterState)
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.RECIPE_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType },
                navArgument("fromMealPlan") { type = NavType.BoolType; defaultValue = false },
                navArgument("dayIndex") { type = NavType.IntType; defaultValue = -1 },
                navArgument("mealType") { type = NavType.StringType; defaultValue = "" },
                navArgument("mealId") { type = NavType.IntType; defaultValue = 0 },
                navArgument("label") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            val fromMealPlan = backStackEntry.arguments?.getBoolean("fromMealPlan") ?: false
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: -1
            val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
            val mealId = backStackEntry.arguments?.getInt("mealId") ?: 0
            val label = backStackEntry.arguments?.getString("label") ?: ""
            val mealPlanAddSuccess =
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<Boolean>("meal_plan_add_success") == true

            RecipeDetailScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onShareWithFriend = { id ->
                    navController.navigate(Routes.shareRecipe(id))
                },
                showAddToMealPlan = true,
                onAddToMealPlan = if (fromMealPlan) {
                    {
                        val typeEnum = runCatching {
                            MealType.valueOf(mealType)
                        }.getOrElse {
                            MealType.SNACK
                        }

                        if (mealId == 0) {
                            mealPlanViewModel.addMeal(dayIndex, typeEnum, recipeId, label)
                        } else {
                            mealPlanViewModel.updateMealById(dayIndex, mealId, recipeId, label)
                        }

                        navController.popBackStack(Routes.MEAL_PLAN, inclusive = false)
                    }
                } else {
                    {
                        navController.navigate(Routes.mealPlanWithPendingRecipe(recipeId))
                    }
                },
                mealPlanAddSuccess = mealPlanAddSuccess,
                onMealPlanAddSuccessShown = {
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.remove<Boolean>("meal_plan_add_success")
                }
            )

        }

        composable(
            route = Routes.SHARE_RECIPE_ROUTE,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L

            ShareRecipeScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NUTRIENT_DASHBOARD) {
            NutrientDashboardScreen(
                nutrientsDashboardViewmodel = nutrientsDashboardViewmodel,
                onTabSelected = { tab ->
                    handleBottomNavigation(navController, BottomTab.NutritionalDashboard, tab)
                },
                onChatBotClick = onChatBotClick,
                onGoalSettingClick = { navController.navigate(Routes.NUTRIENT_GOAL)}
            )
        }

        composable(
            route = Routes.MEAL_PLAN_ADD,
            arguments = listOf(
                navArgument("dayIndex") { type = NavType.IntType },
                navArgument("mealType") { type = NavType.StringType },
                navArgument("mealId") { type = NavType.IntType; defaultValue = 0 },
                navArgument("label") { type = NavType.StringType; defaultValue = "" }
            )
        ) { stackEntry ->
            val dayIndex = stackEntry.arguments?.getInt("dayIndex") ?: 0
            val mealType = stackEntry.arguments?.getString("mealType") ?: "SNACK"
            val mealId = stackEntry.arguments?.getInt("mealId") ?: 0
            val label = stackEntry.arguments?.getString("label") ?: ""

            MealPlanAddScreen(
                onRecipeQuickAdd = { recipe: Recipe ->
                    val typeEnum = runCatching {
                        MealType.valueOf(mealType)
                    }.getOrElse {
                        MealType.SNACK
                    }

                    if (mealId == 0) {
                        mealPlanViewModel.addMeal(dayIndex, typeEnum, recipe.id, label)
                    } else {
                        mealPlanViewModel.updateMealById(dayIndex, mealId, recipe.id, label)
                    }

                    navController.popBackStack()
                },
                onRecipeOpen = { recipe: Recipe ->
                    navController.navigate(
                        Routes.recipeDetailForMealPlan(
                            recipeId = recipe.id,
                            dayIndex = dayIndex,
                            mealType = mealType,
                            mealId = if (mealId == 0) null else mealId,
                            label = label
                        )
                    )
                },
                onFilterClick = {
                    navController.navigate(Routes.FILTER)
                },
                onChatBotClick = onChatBotClick,
                onTabSelected = { tab ->
                    handleBottomNavigation(navController, BottomTab.MealPlan, tab)
                },
                onBackClick = { navController.popBackStack() },
                homeViewModel = homeViewModel
            )
        }

        composable(Routes.NUTRIENT_GOAL) {
            NutrientGoalScreen(
                nutrientsDashboardViewmodel = nutrientsDashboardViewmodel,
                onBackClick = { navController.popBackStack() },
                onTabSelected = {
                    tab -> handleBottomNavigation(navController, BottomTab.NutritionalDashboard, tab)
                },
                onChatBotClick = onChatBotClick
            )
        }

        composable(Routes.ADD_RECIPE) {
            AddRecipeScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(Routes.MY_RECIPES) {
                        popUpTo(Routes.ADD_RECIPE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RECEIVED_RECIPES) {
            ReceivedRecipesScreen(
                onBack = { navController.popBackStack() },
                onRecipeClick = { recipe ->
                    navController.navigate(Routes.recipeDetail(recipe.id))
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onLikedRecipesClick = { navController.navigate(Routes.Liked_RECIPES) },
                onMyRecipesClick = { navController.navigate(Routes.MY_RECIPES) },
                onReceivedRecipesClick = { navController.navigate(Routes.RECEIVED_RECIPES) },
                onFoodRestrictionsClick = { navController.navigate(Routes.FOOD_ALLERGY) },
                onDietPreferenceClick = { navController.navigate(Routes.DIET_PREFERENCE) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}