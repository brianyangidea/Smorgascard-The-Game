package ca.uwaterloo.cook_sharp.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.theme.*

/**
 * Main settings screen providing navigation to user profile, preferences, and account actions.
 * Functionalities:
 * - Sign out of the application
 * - View liked, personal, and received recipes
 * - Set diet preferences and food restrictions
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onLikedRecipesClick: () -> Unit,
    onFoodRestrictionsClick: () -> Unit,
    onDietPreferenceClick: () -> Unit,
    onMyRecipesClick: () -> Unit,
    onReceivedRecipesClick: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState = settingsViewModel.uiState
    val currentDiet = settingsViewModel.currentDiet

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Settings_heading(onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Functionalities:
            // - Sign out of the application
            SettingsButton(
                text = "Sign Out",
                onClick = onSignOut,
                containerColor = LikeAccent,
                contentColor = AppBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Functionalities:
            // - Access liked, personal, and received recipes
            // - Manage diet preferences and food restrictions
            SettingsButton(text = "Liked Recipes", onClick = onLikedRecipesClick)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsButton(text = "My Recipes", onClick = onMyRecipesClick)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsButton(text = "Received Recipes", onClick = onReceivedRecipesClick)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsButton(
                text = "Diet Preference",
                onClick = onDietPreferenceClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsButton(text = "Food Restrictions", onClick = onFoodRestrictionsClick)

            Spacer(modifier = Modifier.weight(1f))

            // Decorative element at the bottom of the settings screen
            Image(
                painter = painterResource(id = R.drawable.adorable_cat_fight_me_about_it),
                contentDescription = "Cute Cat",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

// Reusable top bar component for the settings screen
@Composable
private fun Settings_heading(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(FilterSelected)
            .statusBarsPadding()
            .height(45.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow),
                contentDescription = "Back",
                tint = TextOnDark,
                modifier = Modifier.size(34.dp)
            )
        }
        Text(
            text = "Adjust Your Settings",
            style = MaterialTheme.typography.titleMedium,
            color = TextOnDark,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// Styled button component for settings options
@Composable
private fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = PrimaryButtonBackground,
    contentColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    CookSharpTheme {
        SettingsScreen(
            onBack = {},
            onSignOut = {},
            onLikedRecipesClick = {},
            onFoodRestrictionsClick = {},
            onDietPreferenceClick = {},
            onMyRecipesClick = {},
            onReceivedRecipesClick = {}
        )
    }
}