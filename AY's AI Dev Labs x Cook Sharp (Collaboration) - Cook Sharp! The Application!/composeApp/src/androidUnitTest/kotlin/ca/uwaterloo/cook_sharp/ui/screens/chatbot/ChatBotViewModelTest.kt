package ca.uwaterloo.cook_sharp.ui.screens.chatbot

import ca.uwaterloo.cook_sharp.data.mock.MockRecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatBotViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ChatBotViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Using MockRecipeRepository to avoid hitting real database/Supabase during unit tests
        viewModel = ChatBotViewModel(
            recipeRepository = MockRecipeRepository(),
            dbRecipeRepository = MockRecipeRepository()
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_has_welcome_message() {
        val uiState = viewModel.uiState.value
        // Expecting the default "Hello hungry user!..." message
        assertTrue(uiState.messages[0] is ChatMessage.ChatBot)
    }

    @Test
    fun onMessageChange_updates_messageText() {
        val testText = "Hello AI"
        viewModel.onMessageChange(testText)
        assertEquals(testText, viewModel.uiState.value.messageText)
    }

    @Test
    fun onSendClick_adds_user_message_and_clears_input() {
        val testText = "Suggest a recipe"
        viewModel.onMessageChange(testText)
        viewModel.onSendClick()
        
        val uiState = viewModel.uiState.value
        assertEquals("", uiState.messageText)
    }

    @Test
    fun onSendClick_send_msg_to_chatbot() {
        val testText = "Recipe?"
        viewModel.onMessageChange(testText)
        viewModel.onSendClick()
        
        val uiState = viewModel.uiState.value
        assertTrue(uiState.messages.last() is ChatMessage.User)
    }
}
