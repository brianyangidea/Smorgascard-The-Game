package ca.uwaterloo.cook_sharp.ui.screens.received_recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.ui.components.Recipe_card
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary

@Composable
fun ReceivedRecipesScreen(
    vm: ReceivedRecipesViewModel = viewModel(),
    onBack: () -> Unit,
    onRecipeClick: (Recipe) -> Unit
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
                    text = "Received Recipes",
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
        when {
            vm.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            vm.receivedRecipes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You have not received any recipes yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(vm.receivedRecipes, key = { "${it.recipe.id}-${it.senderName}-${it.message}" }) { item ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "From: ${item.senderName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )

                            item.message?.takeIf { it.isNotBlank() }?.let { message ->
                                Text(
                                    text = "\"$message\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }

                            Recipe_card(
                                recipe = item.recipe,
                                onClick = { onRecipeClick(it) },
                                isLiked = item.recipe.isLiked,
                                onToggleLike = { vm.toggleLike(it.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}