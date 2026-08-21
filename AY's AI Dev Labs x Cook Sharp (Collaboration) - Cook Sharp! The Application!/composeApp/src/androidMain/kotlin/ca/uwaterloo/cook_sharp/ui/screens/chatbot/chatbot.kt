package ca.uwaterloo.cook_sharp.ui.screens.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.uwaterloo.cook_sharp.R
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.ui.components.Recipe_card
import ca.uwaterloo.cook_sharp.ui.theme.AppBackground
import ca.uwaterloo.cook_sharp.ui.theme.AuthPrimaryButton
import ca.uwaterloo.cook_sharp.ui.theme.CardSurface
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import ca.uwaterloo.cook_sharp.ui.theme.PrimaryButtonBackground
import ca.uwaterloo.cook_sharp.ui.theme.TextMuted
import ca.uwaterloo.cook_sharp.ui.theme.TextOnDark
import ca.uwaterloo.cook_sharp.ui.theme.blackText
import ca.uwaterloo.cook_sharp.ui.theme.botCardColor

private val BubbleMaxWidth = 280.dp
private val RecipeBubbleMaxWidth = 420.dp

@Composable
fun ChatBotScreen(
    onBackClick: () -> Unit = {},
    onRecipeClick: (Recipe) -> Unit = {},
    viewModel: ChatBotViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    rememberCoroutineScope()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            ChatBotTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            ChatInputBar(
                messageText = uiState.messageText,
                onMessageChange = viewModel::onMessageChange,
                onSendClick = viewModel::onSendClick,
                isLoading = uiState.isLoading
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.messages) { message ->
                when (message) {
                    is ChatMessage.ChatBot -> BotMessageBubble(message.text)
                    is ChatMessage.User -> UserMessageBubble(message.text)
                    is ChatMessage.DatabaseRecipeResult -> BotDatabaseRecipeCard(
                        recipe = message.recipe,
                        onRecipeClick = onRecipeClick,
                        onToggleLike = viewModel::toggleLikeRecipe
                    )
                    is ChatMessage.AiRecipeResult -> Unit
                }
            }

            if (uiState.isLoading) {
                item {
                    BotLoadingBubble()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotTopBar(onBackClick: () -> Unit) {
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
                        painter = painterResource(R.drawable.chatbot2),
                        contentDescription = "AI Bot",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "AI Bot",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextOnDark
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
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

@Composable
fun BotMessageBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        BotAvatar()
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .widthIn(max = BubbleMaxWidth)
                .background(
                    color = botCardColor,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = blackText
            )
        }
    }
}

@Composable
fun BotLoadingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        BotAvatar()
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .background(
                    color = botCardColor,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = PrimaryButtonBackground
            )
        }
    }
}

@Composable
fun UserMessageBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = BubbleMaxWidth)
                .background(
                    color = PrimaryButtonBackground,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 4.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnDark
            )
        }
    }
}

@Composable
fun BotDatabaseRecipeCard(
    recipe: Recipe,
    onRecipeClick: (Recipe) -> Unit,
    onToggleLike: (Long) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        BotAvatar()
        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.widthIn(max = RecipeBubbleMaxWidth)) {
            Recipe_card(
                recipe = recipe,
                isLiked = recipe.isLiked,
                onClick = onRecipeClick,
                onToggleLike = { clickedRecipe -> onToggleLike(clickedRecipe.id) }
            )
        }
    }
}

@Composable
private fun AiSection(title: String, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = blackText
        )

        items.forEachIndexed { index, item ->
            Text(
                text = if (title == "Steps") "${index + 1}. $item" else "• $item",
                style = MaterialTheme.typography.bodyMedium,
                color = blackText
            )
        }
    }
}

@Composable
private fun BotAvatar() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PrimaryButtonBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.chatbot2),
            contentDescription = "AI Bot",
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun ChatInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TextOnDark)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Ask Anything!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = CardSurface,
                unfocusedIndicatorColor = CardSurface,
                focusedContainerColor = CardSurface,
                unfocusedContainerColor = CardSurface
            ),
            enabled = !isLoading
        )

        IconButton(
            onClick = onSendClick,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(TextOnDark),
            enabled = !isLoading && messageText.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = AuthPrimaryButton
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.arrow_up),
                    contentDescription = "Send",
                    tint = AuthPrimaryButton
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatbotScreenPreview() {
    CookSharpTheme {
        ChatBotScreen()
    }
}