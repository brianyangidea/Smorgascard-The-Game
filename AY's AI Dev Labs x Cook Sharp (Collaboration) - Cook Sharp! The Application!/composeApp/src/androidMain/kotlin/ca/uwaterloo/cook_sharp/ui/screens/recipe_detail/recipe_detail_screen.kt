package ca.uwaterloo.cook_sharp.ui.screens.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.domain.Ingredient
import ca.uwaterloo.cook_sharp.domain.RecipeInstruction
import ca.uwaterloo.cook_sharp.ui.components.ServingsCounter
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import ca.uwaterloo.cook_sharp.ui.theme.LikeAccent
import ca.uwaterloo.cook_sharp.ui.theme.PrimaryButtonBackground
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import ca.uwaterloo.cook_sharp.ui.theme.tabBagroundDark
import ca.uwaterloo.cook_sharp.ui.theme.tabBagroundLight
import ca.uwaterloo.cook_sharp.ui.theme.TextColourRecipeDetail
import ca.uwaterloo.cook_sharp.ui.theme.toggleDark
import ca.uwaterloo.cook_sharp.ui.theme.lightGreenText
import ca.uwaterloo.cook_sharp.ui.theme.iconColour
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.util.Locale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable

private const val TAB_INGREDIENTS = 0
private const val TAB_INSTRUCTIONS = 1

@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onBack: () -> Unit,
    onShareWithFriend: (Long) -> Unit,
    showAddToMealPlan: Boolean = false,
    onAddToMealPlan: (() -> Unit)? = null,
    mealPlanAddSuccess: Boolean = false,
    onMealPlanAddSuccessShown: () -> Unit = {},
    vm: RecipeDetailViewModel = viewModel()
) {
    LaunchedEffect(recipeId) {
        vm.loadRecipe(recipeId)
    }

    val state = vm.ui_state
    val recipe = state.recipe
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(TAB_INGREDIENTS) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mealPlanAddSuccess) {
        if (mealPlanAddSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Successfully added to meal plan")
            }
            onMealPlanAddSuccessShown()
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            vm.clearMessage()
        }
    }

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryButtonBackground)
                }
            }

            recipe == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Recipe could not be loaded",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }

            else -> {
                val scaledIngredients = recipe.scaleIngredients(state.servings)
                val canShare = vm.getCurrentUserId() != null

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(recipe.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ramen_bowl),
                        error = painterResource(R.drawable.ramen_bowl)
                    )

                    TopActionBar(
                        isLiked = state.isLiked,
                        onBack = onBack,
                        onToggleLike = { vm.toggleLike() },
                        showAddToMealPlan = showAddToMealPlan,
                        onAddToMealPlan = onAddToMealPlan
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 220.dp),
                        color = AppBackground,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .navigationBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${recipe.readyInMinutes} min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            if (state.matchedAllergies.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                AllergyWarningBanner(state.matchedAllergies)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            NutritionRow(
                                recipe = recipe,
                                selectedServings = state.servings
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Servings",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                ServingsCounter(
                                    count = state.servings,
                                    onIncrement = { vm.increment_servings() },
                                    onDecrement = { vm.decrement_servings() }
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            SegmentedTabs(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            if (selectedTab == TAB_INGREDIENTS) {
                                IngredientsSection(scaledIngredients)
                            } else {
                                InstructionsSection(recipe.instructions)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { vm.addToGroceryList() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(58.dp),
                                        shape = RoundedCornerShape(18.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = tabBagroundDark
                                        )
                                    ) {
                                        Text(
                                            text = "Add to Grocery List",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White
                                        )
                                    }

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (canShare) {
                                            FilledTonalIconButton(
                                                onClick = { onShareWithFriend(recipe.id) },
                                                modifier = Modifier
                                                    .size(58.dp)
                                                    .clip(RoundedCornerShape(18.dp)),
                                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                    containerColor = lightGreenText,
                                                    contentColor = iconColour
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share Recipe"
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopActionBar(
    isLiked: Boolean,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    showAddToMealPlan: Boolean,
    onAddToMealPlan: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        CircleIconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircleIconButton(onClick = onToggleLike) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = if (isLiked) LikeAccent else TextPrimary
                )
            }

            if (showAddToMealPlan && onAddToMealPlan != null) {
                CircleIconButton(onClick = onAddToMealPlan) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Meal Plan",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.92f),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun NutritionRow(
    recipe: ca.uwaterloo.cook_sharp.domain.Recipe,
    selectedServings: Int
) {
    val baseServings = recipe.servings.coerceAtLeast(1)
    val scale = selectedServings.toDouble() / baseServings.toDouble()

    val scaledCarbs = recipe.carbs * scale
    val scaledFat = recipe.fat * scale
    val scaledProtein = recipe.protein * scale
    val scaledCalories = recipe.calories * scale

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NutritionChip(
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = tabBagroundDark,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "${scaledCarbs.toInt()}g Carbs"
            )
            NutritionChip(
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = tabBagroundDark,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "${scaledFat.toInt()}g Fats"
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NutritionChip(
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = tabBagroundDark,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "${scaledProtein.toInt()}g Proteins"
            )
            NutritionChip(
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = tabBagroundDark,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "${scaledCalories.toInt()} Calories"
            )
        }
    }
}

@Composable
private fun NutritionChip(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(TextColourRecipeDetail)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(lightGreenText),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = tabBagroundDark
        )
    }
}

@Composable
private fun SegmentedTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(tabBagroundLight)
            .padding(4.dp)
    ) {
        SegmentTabButton(
            text = "Ingredients",
            selected = selectedTab == TAB_INGREDIENTS,
            modifier = Modifier.weight(1f)
        ) {
            onTabSelected(TAB_INGREDIENTS)
        }

        SegmentTabButton(
            text = "Instructions",
            selected = selectedTab == TAB_INSTRUCTIONS,
            modifier = Modifier.weight(1f)
        ) {
            onTabSelected(TAB_INSTRUCTIONS)
        }
    }
}

@Composable
private fun SegmentTabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) toggleDark else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) Color.White else iconColour
        )
    }
}

@Composable
private fun IngredientsSection(ingredients: List<Ingredient>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ingredients.forEachIndexed { index, ingredient ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(toggleDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp)
                ) {
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )

                    val subtitle = buildString {
                        if (ingredient.amount > 0.0) {
                            append(String.format(Locale.US, "%.1f", ingredient.amount))
                        }
                        if (ingredient.unit.isNotBlank()) {
                            if (isNotBlank()) append(" ")
                            append(ingredient.unit)
                        }
                    }

                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = lightGreenText
            )
        }
    }
}

@Composable
private fun AllergyWarningBanner(matchedAllergies: List<String>) {
    val allergyText = matchedAllergies.joinToString(", ") { it.replaceFirstChar { c -> c.uppercase() } }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFF6B6B).copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFCC0000),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "Allergy Warning",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFCC0000)
            )
            Text(
                text = "Contains: $allergyText",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCC0000)
            )
        }
    }
}

@Composable
private fun InstructionsSection(steps: List<RecipeInstruction>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        steps.forEach { step ->
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(toggleDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.stepNumber.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = step.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}