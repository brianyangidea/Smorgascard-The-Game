package ca.uwaterloo.cook_sharp.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.ui.theme.*
import coil.compose.AsyncImage

/**
 * Functionalities:
 * - View and manage user profile details
 * - Update profile picture from gallery
 * - View current diet and allergy settings
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val user = vm.currentUser ?: return
    val context = LocalContext.current

    // Functionalities: - Launch image picker to update profile photo
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        vm.updateProfilePicture(uri?.toString(), context)
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            ProfileHeading(onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Functionalities: - Tap to change the user profile image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryButtonBackground)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                // Camera overlay icon to indicate editability
                // Box(
                //     modifier = Modifier
                //         .align(Alignment.BottomEnd)
                //         .size(32.dp)
                //         .background(FilterSelected, CircleShape)
                //         .padding(6.dp),
                //     contentAlignment = Alignment.Center
                // ) {
                //     Icon(
                //         painter = painterResource(id = R.drawable.edit),
                //         contentDescription = "Edit Profile Picture",
                //         tint = TextOnDark,
                //         modifier = Modifier.size(16.dp)
                //     )
                // }

                if (user.profilePictureUri != null) {
                    AsyncImage(
                        model = user.profilePictureUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.chef_hat),
                        contentDescription = "Profile Picture Placeholder",
                        modifier = Modifier.size(64.dp),
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Functionalities: - Display user's full name and email address
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Functionalities: - View specific dietary and allergy info cards
            ProfileInfoCard(label = "Diet Preference", value = vm.currentDiet)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoCard(
                label = "Food Restrictions",
                value = vm.currentAllergies
            )

            Image(
                painter = painterResource(id = R.drawable.cat_burrito),
                contentDescription = "Burrito Cat",
                modifier = Modifier
                    .height(300.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeading(onBack: () -> Unit) {
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
            text = "User Profile",
            style = MaterialTheme.typography.titleMedium,
            color = TextOnDark,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ProfileInfoCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TextFieldBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}