package ca.uwaterloo.cook_sharp.ui.screens.meal_plan
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.R
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import ca.uwaterloo.cook_sharp.ui.components.BottomTab
import ca.uwaterloo.cook_sharp.ui.components.ChatBotButton
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.ui.components.bottom_navigation
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.ui.screens.home.recipe_filter_row
import ca.uwaterloo.cook_sharp.ui.screens.home.HomeViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import ca.uwaterloo.cook_sharp.ui.theme.LikeAccent
import ca.uwaterloo.cook_sharp.ui.theme.lightGreenText
import ca.uwaterloo.cook_sharp.ui.theme.iconColour


@Composable
fun MealPlanAddScreen(
    onChatBotClick: () -> Unit = {},
    onTabSelected: (BottomTab) -> Unit = {},
    onBackClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    homeViewModel: HomeViewModel,
    onRecipeQuickAdd: (Recipe) -> Unit = {},
    onRecipeOpen: (Recipe) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf<BottomTab>(BottomTab.MealPlan) }
    var search by rememberSaveable { mutableStateOf("") }
    var filterMeal by remember { mutableStateOf<MealType?>(null) }
    var filterActive by remember { mutableStateOf(false) }

    val state = homeViewModel.ui_state
    val recipes = state.recipes

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            MealAddTopBar (onBackClick = onBackClick)

        },
        bottomBar = {
            bottom_navigation(
                selected = selectedTab,
                onSelected = onTabSelected
            )
        },
        floatingActionButton = {
            ChatBotButton(
                onClick = onChatBotClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {
            Spacer(Modifier.height(15.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                MealSearchBar(
                    searchValue = search,
                    onSearchChange = {
                        search = it
                        homeViewModel.search_change(it)
                    }
                )
            }


            Spacer(Modifier.height(15.dp))
            recipe_filter_row(
                selected = filterMeal,
                onSelected = {
                    filterMeal = it
                    homeViewModel.recipe_selected(it)
                },
                filterActive = filterActive,
                onFilterClick = { onFilterClick() }
            )

            Spacer(Modifier.height(15.dp))

            val listState = rememberLazyListState()
            val shouldLoadMore = remember {
                derivedStateOf {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val total = listState.layoutInfo.totalItemsCount
                    total > 0 && lastVisible >= total - 3
                }
            }
            LaunchedEffect(shouldLoadMore.value) {
                if (shouldLoadMore.value) homeViewModel.loadMoreRecipes()
            }

            if (state.isLoading && recipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        MealPlanRecipeCard(
                            recipe = recipe,
                            isLiked = recipe.isLiked,
                            onOpenRecipe = {
                                onRecipeOpen(recipe)
                            },
                            onQuickAdd = {
                                onRecipeQuickAdd(recipe)
                            },
                            onToggleLike = { clicked ->
                                homeViewModel.change_like(clicked.id)
                            },
                            userAllergies = homeViewModel.currentUser?.allergies ?: emptyList()
                        )
                    }

                    if (state.isLoading && recipes.isNotEmpty()) {
                        item(key = "paging_loader") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealAddTopBar (onBackClick: () -> Unit) {
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
                        painter = painterResource(R.drawable.meal_plan),
                        contentDescription = "Meal Plan",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Add Your Meals",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextOnDark
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextOnDark
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryButtonBackground
        )
    )
}

@Composable
fun MealSearchBar(
    searchValue: String,
    onSearchChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchValue,
        onValueChange = onSearchChange,
        singleLine = true,
        placeholder = { Text("Search recipes", color = TextMuted) },
        leadingIcon = { Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = "Search",
            modifier = Modifier.size(22.dp),
            tint = TextMuted
        ) },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardSurface,
            unfocusedContainerColor = CardSurface,
            focusedBorderColor = CardSurface,
            unfocusedBorderColor = CardSurface
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun MealPlanRecipeCard(
    recipe: Recipe,
    isLiked: Boolean,
    onOpenRecipe: () -> Unit,
    onQuickAdd: () -> Unit,
    onToggleLike: (Recipe) -> Unit,
    userAllergies: List<String> = emptyList(),
    showQuickAdd: Boolean = true
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 2.dp, color = PrimaryButtonBackground, shape = shape)
            .clickable { onOpenRecipe() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = recipe.image?.let {
                    ImageRequest.Builder(context)
                        .data(it)
                        .crossfade(true)
                        .build()
                },
                contentDescription = recipe.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ramen_bowl),
                error = painterResource(R.drawable.ramen_bowl),
                fallback = painterResource(R.drawable.ramen_bowl)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Cook Time: ${recipe.readyInMinutes} mins",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Text(
                text = "Difficulty: ${recipe.difficulty}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Text(
                text = "Calories: ${recipe.calories.toInt()} kcal",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showQuickAdd) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = lightGreenText
                ) {
                    IconButton(onClick = onQuickAdd) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Quick add recipe",
                            tint = iconColour
                        )
                    }
                }
            }

            IconButton(
                onClick = { onToggleLike(recipe) },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (isLiked) R.drawable.heart_filled else R.drawable.heart_outline
                    ),
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = if (isLiked) LikeAccent else TextPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun MealPlanAddScreenPreview() {
    CookSharpTheme {
        MealPlanAddScreen(homeViewModel = HomeViewModel())
    }
}


