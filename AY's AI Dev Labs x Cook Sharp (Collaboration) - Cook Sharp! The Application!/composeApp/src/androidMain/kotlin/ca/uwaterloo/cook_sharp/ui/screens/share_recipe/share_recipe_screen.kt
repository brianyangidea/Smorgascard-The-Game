package ca.uwaterloo.cook_sharp.ui.screens.share_recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import ca.uwaterloo.cook_sharp.ui.theme.PrimaryButtonBackground
import ca.uwaterloo.cook_sharp.ui.theme.TextColourRecipeDetail
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextPrimary
import ca.uwaterloo.cook_sharp.ui.theme.lightGreenText
import ca.uwaterloo.cook_sharp.ui.theme.tabBagroundDark
import androidx.compose.ui.res.painterResource
import ca.uwaterloo.cook_sharp.R
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun ShareRecipeScreen(
    recipeId: Long,
    onBack: () -> Unit,
    vm: ShareRecipeViewModel = viewModel()
) {
    LaunchedEffect(recipeId) {
        vm.load(recipeId)
    }

    val state = vm.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
    }

    Scaffold(
        containerColor = AppBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppBackground)
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryButtonBackground)
            }
        } else {
            val selectedUsers = state.allUsers.filter { it.id in state.selectedUserIds }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppBackground)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    HeaderSection(
                        onBack = onBack
                    )

//                    Spacer(modifier = Modifier.height(20.dp))

                    SearchBar(
                        value = state.searchQuery,
                        onValueChange = vm::onSearchQueryChanged
                    )

                    if (selectedUsers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        SelectedUsersChips(
                            users = selectedUsers,
                            onRemove = vm::toggleUserSelection
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Friends",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredUsers) { user ->
                        ShareUserCard(
                            user = user,
                            selected = user.id in state.selectedUserIds,
                            onClick = { vm.toggleUserSelection(user.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                BottomSharePanel(
                    message = state.personalMessage,
                    onMessageChange = vm::onMessageChanged,
                    onSend = {
                        vm.sendShare {
                            onBack()
                        }
                    },
                    sendEnabled = state.selectedUserIds.isNotEmpty()
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 8.dp)
            .heightIn(min = 48.dp)
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
            text = "Share Recipe",
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

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextMuted
            )

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                decorationBox = { innerTextField ->
                    if (value.isBlank()) {
                        Text(
                            text = "Search users / friends",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextMuted
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun SelectedUsersChips(
    users: List<ShareUserItem>,
    onRemove: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            Surface(
                shape = RoundedCornerShape(50),
                color = lightGreenText
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onRemove(user.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = tabBagroundDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "×",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tabBagroundDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareUserCard(
    user: ShareUserItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(TextColourRecipeDetail),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = tabBagroundDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) PrimaryButtonBackground else Color.Transparent
                    )
                    .then(
                        if (!selected) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = TextMuted,
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Text(
                        text = "✓",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSharePanel(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    sendEnabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Add a note",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = {
                    Text(
                        text = "Say something about this recipe...",
                        color = TextMuted
                    )
                },
                shape = RoundedCornerShape(18.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSend,
                enabled = sendEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryButtonBackground,
                    disabledContainerColor = PrimaryButtonBackground.copy(alpha = 0.45f),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Send Recipe",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}