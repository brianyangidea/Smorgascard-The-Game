package ca.uwaterloo.cook_sharp.ui.screens.my_recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.ui.components.Recipe_card
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.foundation.lazy.items
import ca.uwaterloo.cook_sharp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun MyRecipesScreen(
    vm: MyRecipesViewModel = viewModel(),
    onRecipeClick: (Recipe) -> Unit = {},
    onBack: () -> Unit,
    onAddRecipeClick: () -> Unit = {},
) {
    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 12.dp)
                    .heightIn(min = 56.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "My Recipes",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 48.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(vm.myRecipes,key = { it.id }) { recipe ->
                        Recipe_card(
                            recipe = recipe,
                            onClick = { onRecipeClick(it) },
                            isLiked = recipe.isLiked,
                            onToggleLike = { vm.toggleLike(it.id) },
                            userAllergies = vm.currentUser?.allergies ?: emptyList()
                        )
                    }
                }
            }
            Button(
                onClick = onAddRecipeClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyRecipesButtonBackground)
            ) {
                Text("Add Recipes", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }
        }
    }
}
