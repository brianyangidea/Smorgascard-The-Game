package ca.uwaterloo.cook_sharp.ui.screens.nutrients
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.components.BottomTab
import ca.uwaterloo.cook_sharp.ui.components.bottom_navigation
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import androidx.compose.ui.text.font.FontWeight
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.ui.components.ChatBotButton
import ca.uwaterloo.cook_sharp.R
import androidx.lifecycle.compose.LifecycleResumeEffect
import ca.uwaterloo.cook_sharp.domain.NutritionGoal
import androidx.compose.foundation.interaction.MutableInteractionSource


@Composable
fun NutrientDashboardScreen(
    onChatBotClick: () -> Unit = {},
    onTabSelected: (BottomTab) -> Unit = {},
    onGoalSettingClick : () -> Unit = {},
    nutrientsDashboardViewmodel : NutrientsDashboardViewmodel = viewModel()
) {
    LifecycleResumeEffect(Unit) {
        nutrientsDashboardViewmodel.reload()
        onPauseOrDispose { }
    }

    val uiState by nutrientsDashboardViewmodel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            bottom_navigation (
                selected = BottomTab.NutritionalDashboard,
                onSelected = { tab ->
                    onTabSelected(tab)
                }
            )
        },
        floatingActionButton = {
            ChatBotButton(
                onClick = onChatBotClick
            )
        }
    ) { paddingValues ->
        val resetInteraction = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
                .clickable(
                    enabled = uiState.selectedDayIndex != null,
                    interactionSource = resetInteraction,
                    indication = null
                ) { nutrientsDashboardViewmodel.selectDay(null) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    TitleSection(
                        dateRange = uiState.dateRange,
                        onGoalSettingClick = onGoalSettingClick,
                        isDailyView = uiState.selectedDayIndex != null,
                        onResetView = { nutrientsDashboardViewmodel.selectDay(null) }
                    )
                }

                item {
                    WeeklyAverageCard(
                        weeklyAvg = uiState.weeklyAvg,
                        weeklyBars = uiState.weeklyBars,
                        selectedDayIndex = uiState.selectedDayIndex,
                        onDayClick = { nutrientsDashboardViewmodel.selectDay(it) }
                    )
                }

                item {
                    NutrientsBreakdown(
                        goal = uiState.goal,
                        totals = uiState.totals,
                        isDaily = uiState.selectedDayIndex != null
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun TitleSection(
    dateRange: String,
    onGoalSettingClick: () -> Unit = {},
    isDailyView: Boolean = false,
    onResetView: () -> Unit = {}
) {
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDailyView) "Daily Nutrients" else "Weekly Nutrients",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Text (
            text = dateRange,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(start = 5.dp, top = 5.dp)
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clickable(){ onGoalSettingClick() }
            .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.nutrition_plan),
                contentDescription = "Set Goals",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Set your goals",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

}

@Composable
fun WeeklyAverageCard(
    weeklyAvg: Float,
    weeklyBars: List<Float>,
    selectedDayIndex: Int? = null,
    onDayClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryButtonBackground.copy(alpha = 0.75f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Text(
                text ="Weekly Overview",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text (
                text =  "${weeklyAvg.toInt()}% of weekly goal",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            WeeklyGraphs( 
                heights = weeklyBars, 
                selectedDayIndex = selectedDayIndex,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
fun WeeklyGraphs( 
    heights : List<Float>,
    selectedDayIndex: Int? = null,
    onDayClick: (Int) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val maxHeight = 80.dp

        days.forEachIndexed { index, day ->

            val h = heights.getOrNull(index)
            val isSelected = selectedDayIndex == index

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDayClick(index) }
            ) {
                Box(
                    modifier = Modifier.height(maxHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .fillMaxHeight()
                            .background(
                                color = if (isSelected) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(80.dp * (h ?: 0f))

                            .background(
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                }

                Spacer (modifier = Modifier.height(5.dp))


                Text(
                    text = day,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )

            }
        }
    }
}

@Composable
fun NutrientsBreakdown(goal : NutritionGoal, totals: NutrientTotals, isDaily: Boolean) {
    Column {
        Row {
            Image(
                painter = painterResource(R.drawable.nutrition_plan),
                contentDescription = "Nutrients",
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text (
                text = if (isDaily) "Daily Breakdown" else "Weekly Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        val multiplier = if (isDaily) 1f else 7f

        NutrientCard (
            name = "Calories",
            current = totals.calories.toInt(),
            goal = (goal.weeklyTarget.calories * multiplier).toInt(),
            unit = "kcal",
            color = Color(0xFFFF6B6B),
            label = if (isDaily) "Daily Total" else "Weekly Total"
        )

        Spacer(modifier = Modifier.height(10.dp))

        NutrientCard (
            name = "Protein",
            current = totals.protein.toInt(),
            goal = (goal.weeklyTarget.protein * multiplier).toInt(),
            color = Color(0xFF5B8DEF),
            label = if (isDaily) "Daily Total" else "Weekly Total"
        )

        Spacer(modifier = Modifier.height(10.dp))

        NutrientCard (
            name = "Carbs",
            current = totals.carbs.toInt(),
            goal = (goal.weeklyTarget.carbs * multiplier).toInt(),
            color = Color(0xFFFFA726),
            label = if (isDaily) "Daily Total" else "Weekly Total"
        )

        Spacer(modifier = Modifier.height(10.dp))

        NutrientCard (
            name = "Fat",
            current = totals.fat.toInt(),
            goal = (goal.weeklyTarget.fat * multiplier).toInt(),
            color = Color(0xFF66BB6A),
            label = if (isDaily) "Daily Total" else "Weekly Total"
        )
    }
}

@Composable
fun NutrientCard(
    name : String,
    current: Int,
    goal: Int,
    unit : String = "g",
    color: Color,
    label: String = "Weekly Total"
) {
    val percentage = if (goal > 0) ((current.toFloat() / goal.toFloat()) * 100).toInt() else 0

    Card (
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp)

        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$current / ${goal}${unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(5.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((percentage / 100f).coerceIn(0f, 1f))
                        .height(8.dp)
                        .background(
                            color = color,
                            shape = RoundedCornerShape(5.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

        }

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NutrientDashboardScreenPreview() {
    CookSharpTheme {
        NutrientDashboardScreen()
    }
}

