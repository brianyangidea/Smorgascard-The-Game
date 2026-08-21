package ca.uwaterloo.cook_sharp.ui.screens.meal_plan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.ui.components.ChatBotButton
import ca.uwaterloo.cook_sharp.ui.components.bottom_navigation
import ca.uwaterloo.cook_sharp.ui.components.BottomTab
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import ca.uwaterloo.cook_sharp.ui.components.Recipe_card
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ca.uwaterloo.cook_sharp.domain.MealType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.data.mock.UserStore

/**
 * Screen for viewing and managing the weekly meal plan.
 * Allows users to select days, add recipes to specific meal slots, and edit meal labels.
 */
@Composable
fun MealPlanScreen(
    onChatBotClick: () -> Unit = {},
    onTabSelected : (BottomTab) -> Unit = {},
    onMealAddClick: (dayIndex: Int, mealType: String, mealId: Int?, label: String) -> Unit =  { _, _, _, _ -> },
    pendingRecipeId: Long? = null,
    onPendingRecipePlaced: () -> Unit = {},
    mealPlanViewModel : MealPlanViewModel = viewModel()
) {
    val selectedDay by mealPlanViewModel.selectedDay.collectAsState()
    val mealPlan by mealPlanViewModel.mealPlan.collectAsState()
    val recipeCache by mealPlanViewModel.recipeCache.collectAsState()
    val dayMeals = remember(mealPlan, selectedDay) {
        mealPlanViewModel.getMealsForDay(selectedDay)
    }

    Scaffold(
        containerColor = AppBackground,
        floatingActionButton = {
            ChatBotButton(onClick = onChatBotClick)
        },
        bottomBar = {
            bottom_navigation(
                selected = BottomTab.MealPlan,
                onSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
                .verticalScroll(scrollState)
        ) {
            val monthYear = remember {
                mealPlanViewModel.weekDates[0].format(
                    java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH)
                )
            }
            // Title and date display
            Text(
                text = "Plan Your Week",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = monthYear,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Horizontal row for day selection
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                itemsIndexed(mealPlanViewModel.days) { index, day ->
                    DayBox(
                        day = day,
                        date = mealPlanViewModel.weekDates[index].dayOfMonth,
                        isSelected = index == selectedDay,
                        onClick = { mealPlanViewModel.setSelectedDay(index) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            
            // Instruction banner when a recipe is being moved into the meal plan
            if (pendingRecipeId != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = lightGreenText),
                    border = BorderStroke(1.dp, PrimaryButtonBackground)
                ) {
                    Text(
                        text = "Select a meal slot to add this recipe",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // List of meal slots (Breakfast, Lunch, Dinner, etc.)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {

                val grouped = dayMeals.groupBy { it.type }
                val baseTypes = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)

                baseTypes.forEach { type ->
                    val meal = grouped[type]?.firstOrNull()
                    val recipe = meal?.recipeId?.let { recipeCache[it] }

                    val heading = (meal?.label ?: type.name.lowercase())
                        .replaceFirstChar { it.uppercase() }

                    MealCard(
                        recipe = recipe,
                        mealType = heading,
                        selectedDay = selectedDay,
                        mealId = meal?.id ?: 0,
                        mealTypeEnum = type,
                        onMealAddClick = onMealAddClick,
                        onMealDeleteClick = if (meal != null) {
                            { mealPlanViewModel.deleteMealById(selectedDay, meal.id) }
                        } else null,
                        onToggleLike = { clicked -> mealPlanViewModel.toggleLike(clicked.id) },
                        pendingRecipeId = pendingRecipeId,
                        onPendingRecipePlaced = onPendingRecipePlaced,
                        mealPlanViewModel = mealPlanViewModel
                    )
                }

                // Displaying additional meal slots beyond the base types
                val extras = buildList {
                    grouped.forEach { (type, mealsOfType) ->
                        if (type in baseTypes) addAll(mealsOfType.drop(1)) else addAll(mealsOfType)
                    }
                }

                extras.forEach { meal ->
                    val recipe = meal.recipeId?.let { recipeCache[it] }
                    val heading = (meal.label ?: meal.type.name.lowercase())
                        .replaceFirstChar { it.uppercase() }

                    MealCard(
                        recipe = recipe,
                        mealType = heading,
                        selectedDay = selectedDay,
                        mealId = meal.id,
                        mealTypeEnum = meal.type,
                        onMealAddClick = onMealAddClick,
                        onMealDeleteClick = { mealPlanViewModel.deleteMealById(selectedDay, meal.id) },
                        onToggleLike = { clicked -> mealPlanViewModel.toggleLike(clicked.id) },
                        pendingRecipeId = pendingRecipeId,
                        onPendingRecipePlaced = onPendingRecipePlaced,
                        mealPlanViewModel = mealPlanViewModel
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button to manually add extra meal slots
                if (pendingRecipeId == null) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onMealAddClick(selectedDay, MealType.SNACK.name, null, "Snack") }
                    ) {
                        Text("Add meal")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


/**
 * Composable for a single meal slot, which can either display a recipe or an 'add' prompt.
 */
@Composable
private fun MealCard(
    recipe: Recipe? = null,
    mealType: String,
    selectedDay: Int,
    mealId: Int,
    mealTypeEnum: MealType,
    onMealAddClick: (Int, String, Int?, String) -> Unit,
    onMealDeleteClick: (() -> Unit)?,
    onToggleLike: (Recipe) -> Unit,
    pendingRecipeId: Long? = null,
    onPendingRecipePlaced: () -> Unit = {},
    mealPlanViewModel: MealPlanViewModel
) {
    var label by remember(mealId, selectedDay) { mutableStateOf(mealType) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Field for editing the custom label of the meal slot
            MealLabelField(
                modifier = Modifier.weight(1f),
                value = label,
                onValueChange = { newText ->
                    label = newText
                    if (mealId != 0) {
                        mealPlanViewModel.updateMealLabelById(
                            dayIndex = selectedDay,
                            mealId = mealId,
                            label = newText.trim()
                        )
                    }
                }
            )

            // Button to remove this meal slot from the day
            if (onMealDeleteClick != null) {
                IconButton(
                    onClick = onMealDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.trashcan),
                        contentDescription = "Delete meal",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (recipe == null) {
            // Empty slot state
            MealAddCard(
                mealType = mealType,
                onClick = {
                    if (pendingRecipeId != null) {
                        mealPlanViewModel.addMeal(
                            selectedDay,
                            mealTypeEnum,
                            pendingRecipeId,
                            label.trim()
                        )
                        onPendingRecipePlaced()
                    } else {
                        onMealAddClick(selectedDay, mealTypeEnum.name, 0, label.trim())
                    }
                }
            )
        } else {
            // Recipe assigned state
            Recipe_card(
                recipe = recipe,
                onClick = { _ ->
                    if (pendingRecipeId != null) {
                        mealPlanViewModel.updateMealById(
                            selectedDay,
                            mealId,
                            pendingRecipeId,
                            label.trim()
                        )
                        onPendingRecipePlaced()
                    } else {
                        onMealAddClick(selectedDay, mealTypeEnum.name, mealId, label.trim())
                    }
                },
                isLiked = recipe.isLiked,
                onToggleLike = onToggleLike,
                userAllergies = UserStore.currentUser?.allergies ?: emptyList()
            )
        }
    }
}

// Text field for editing meal labels with an icon
@Composable
private fun MealLabelField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .heightIn(min = 32.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.pen),
            contentDescription = "Edit label",
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

// Visual representation of a single day in the weekly calendar
@Composable
fun DayBox(
    day: String,
    date: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Filterselected.copy(alpha = 0.5f)
                else PrimaryButtonBackground.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) TextOnDark else TextPrimary
            )
            Text(
                text = date.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TextOnDark else TextPrimary
            )
        }
    }
}

// Placeholder card displayed when no recipe is assigned to a meal slot
@Composable
fun MealAddCard(
    mealType: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = TextMuted,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextMuted
                    )
                }

                Text(
                    text = mealType,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MealPlanScreenPreview() {
    CookSharpTheme {
        MealPlanScreen()
    }
}