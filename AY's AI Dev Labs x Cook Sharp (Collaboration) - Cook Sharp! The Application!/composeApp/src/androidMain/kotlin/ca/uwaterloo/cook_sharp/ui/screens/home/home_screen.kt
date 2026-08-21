package ca.uwaterloo.cook_sharp.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.ui.components.*
import ca.uwaterloo.cook_sharp.ui.theme.*
import ca.uwaterloo.cook_sharp.domain.Recipe

@Composable
fun Home_screen(
    vm: HomeViewModel = viewModel(),
    onFilterClick: () -> Unit = {},
    onTabSelected: (BottomTab) -> Unit = {},
    onRecipeClick: (Recipe) -> Unit = {},
    onChatBotClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val state = vm.ui_state
    val recipes = state.recipes
    val user = vm.currentUser
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisibleItem >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore, state.search, state.filterRecipe, state.activeFilter) {
        if (
            shouldLoadMore &&
            !state.isLoading &&
            state.search.isBlank() &&
            state.filterRecipe == null &&
            state.activeFilter == null
        ) {
            vm.loadMoreRecipes()
        }
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            HomeHeader(
                userName = user?.name ?: "User",
                profilePictureUri = user?.profilePictureUri,
                searchValue = state.search,
                onSearchChange = vm::search_change,
                onClearSearch = vm::clear_search,
                onSettingsClick = onSettingsClick,
                onProfileClick = onProfileClick
            )
        },
        bottomBar = {
            bottom_navigation(
                selected = BottomTab.Home,
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
            recipe_filter_row(
                selected = state.filterRecipe,
                onSelected = vm::recipe_selected,
                filterActive = state.filterActive,
                onFilterClick = {
                    vm.filter_icon_clicked()
                    onFilterClick()
                }
            )

            Spacer(Modifier.height(15.dp))

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
                        Recipe_card(
                            recipe = recipe,
                            onClick = { onRecipeClick(it) },
                            isLiked = recipe.isLiked,
                            onToggleLike = { clicked -> vm.change_like(clicked.id) },
                            userAllergies = user?.allergies ?: emptyList()
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Home_screen_preview() {
    CookSharpTheme {
        Home_screen(vm = HomeViewModel())
    }
}