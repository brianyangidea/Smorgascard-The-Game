package ca.uwaterloo.cook_sharp.ui.components
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
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cook_sharp.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import ca.uwaterloo.cook_sharp.R
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage

@Composable
fun HomeHeader(
    userName: String,
    profilePictureUri: String?,
    searchValue: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 5.dp)
            .background(AppBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(PrimaryButtonBackground, CircleShape)
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                if (profilePictureUri != null) {
                    AsyncImage(
                        model = profilePictureUri,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.chef_hat),
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp),
                        tint = TextPrimary
                    )
                }
            }

            Spacer(Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Welcome,", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Text(userName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(R.drawable.totallynormalsettingsicon),
                    contentDescription = "Settings",
                    modifier = Modifier.size(26.dp),
                    tint = TextPrimary
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = searchValue,
            onValueChange = onSearchChange,
            singleLine = true,
            placeholder = { Text("Search recipes", color = TextMuted) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(22.dp),
                    tint = TextMuted
                )
            },
            trailingIcon = {
                if (searchValue.isNotBlank()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Clear search",
                            modifier = Modifier.size(18.dp),
                            tint = TextMuted
                        )
                    }
                }
            },
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface,
                focusedBorderColor = CardSurface,
                unfocusedBorderColor = CardSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeHeaderPreview() {
    CookSharpTheme {
        var search by rememberSaveable { mutableStateOf("") }
        HomeHeader(
            userName = "User",
            profilePictureUri = null,
            searchValue = search,
            onSearchChange = { search = it },
            onClearSearch = { search = "" },
            onSettingsClick = {},
            onProfileClick = {}
        )
    }
}