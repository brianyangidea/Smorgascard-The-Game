package ca.uwaterloo.cook_sharp.ui.screens.nutrients

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import ca.uwaterloo.cook_sharp.ui.components.BottomTab
import ca.uwaterloo.cook_sharp.ui.components.bottom_navigation
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import androidx.compose.ui.text.font.FontWeight
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.ui.components.ChatBotButton
import ca.uwaterloo.cook_sharp.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip


@Composable
fun NutrientGoalScreen(
    onChatBotClick: () -> Unit = {},
    onBackClick : () -> Unit = {},
    onTabSelected: (BottomTab) -> Unit = {},
    nutrientsDashboardViewmodel: NutrientsDashboardViewmodel = viewModel()
) {

    val uiState by nutrientsDashboardViewmodel.uiState.collectAsState()

    var calories by remember(uiState.goal) { mutableStateOf(uiState.goal.weeklyTarget.calories.toInt().toString())}
    var protein by remember(uiState.goal) { mutableStateOf(uiState.goal.weeklyTarget.protein.toInt().toString())}
    var carbs by remember(uiState.goal){ mutableStateOf(uiState.goal.weeklyTarget.carbs.toInt().toString())}
    var fat by remember(uiState.goal) { mutableStateOf(uiState.goal.weeklyTarget.fat.toInt().toString())}

    val caloriesError = calories.toIntOrNull() == null || calories.toInt() <= 0
    val proteinError = protein.toIntOrNull() == null || protein.toInt() <= 0
    val carbsError = carbs.toIntOrNull() == null || carbs.toInt() <= 0
    val fatError = fat.toIntOrNull() == null || fat.toInt() <= 0

    val isValid = !caloriesError && !proteinError && !carbsError && !fatError

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            NutGoalTopBar(onBackClick = onBackClick)
        },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item { Text (
                    text = "Set your daily nutrition targets",
                    color = TextPrimary
                ) }

                item { Spacer(modifier = Modifier.height(4.dp)) }

                item {
                    GoalCard(
                        "Calories",
                        "Recommended: 1,800 - 2,500 kcal/day",
                        calories,
                        "kcal",
                        Color(0xFFFF6B6B),
                        isError = caloriesError
                    ) {
                        calories = it
                    }
                }

                item {
                    GoalCard(
                        "Protein",
                        "Recommended: 50 - 200g/day",
                        protein,
                        "g",
                        Color(0xFF5B8DEF),
                        isError = proteinError
                    ) {
                        protein = it
                    }
                }

                item {
                    GoalCard(
                        "Carbohydrates",
                        "Recommended: 200 - 300 g/day",
                        carbs,
                        "g",
                        Color(0xFFFFA726),
                        isError = carbsError
                    ) {
                        carbs = it
                    }
                }

              item {
                  GoalCard(
                      "Fat",
                      "Recommended: 45 - 75 g/day",
                      fat,
                      "g",
                      Color(0xFF66BB6A),
                      isError = fatError
                  ) {
                      fat = it
                  }
              }

            item { Spacer(modifier = Modifier.height(12.dp)) }

                item  {
                    Button(
                        onClick = {
                            if (isValid) {
                                nutrientsDashboardViewmodel.updateGoal(
                                    uiState.goal.copy(
                                        weeklyTarget = uiState.goal.weeklyTarget.copy(
                                            calories = calories.toDouble(),
                                            protein = protein.toDouble(),
                                            carbs = carbs.toDouble(),
                                            fat = fat.toDouble()
                                        )
                                    ),
                                    onSaved = { onBackClick() }
                                )
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryButtonBackground,
                            disabledContainerColor = PrimaryButtonBackground.copy(alpha = 0.4f)
                        )
                    ) {
                        Text (
                            text = "Save Goals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

            item { Spacer(modifier = Modifier.height(24.dp))}

        }
    }
}

@Composable
fun GoalCard(label : String, description : String, value : String, unit : String, mColor : Color, isError : Boolean, onValueChange : (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)) {
            Column (
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box (
                        modifier = Modifier.size(12.dp).background(
                            color = mColor,
                            shape = RoundedCornerShape(3.dp)
                        )
                    )
                    Text(
                            text =label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                    )
                }
                Text (description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                OutlinedTextField(
                    value = value,
                    onValueChange = { input : String ->
                        if (input.all(Char::isDigit)) onValueChange(input)
                    },
                    isError = isError,
                    suffix = {
                        Text(unit, color = TextMuted)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = mColor,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    supportingText = if (isError) {
                        { Text("Please enter a valid number", color = TextPrimary) }
                    } else null
                )

            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutGoalTopBar (onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TextOnDark),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.nutrition_plan),
                        contentDescription = "Nutrition Goal",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Nutrition Goal",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextOnDark

                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = "Back",
                    tint = TextOnDark,
                    modifier = Modifier.size(34.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryButtonBackground
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NutrientDashboardScreenPreview() {
    CookSharpTheme {
        NutrientGoalScreen()
    }
}