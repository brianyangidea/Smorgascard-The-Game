package ca.uwaterloo.cook_sharp.ui.screens.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uwaterloo.cook_sharp.data.repository.CombinedRecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.RecipeRepository
import ca.uwaterloo.cook_sharp.data.repository.SupabaseUserRepository
import ca.uwaterloo.cook_sharp.domain.CreateIngredientInput
import ca.uwaterloo.cook_sharp.domain.CreateNutritionInfoInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInput
import ca.uwaterloo.cook_sharp.domain.CreateRecipeInstructionInput
import ca.uwaterloo.cook_sharp.domain.MealType
import ca.uwaterloo.cook_sharp.domain.Recipe
import ca.uwaterloo.cook_sharp.domain.RecipeSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import ca.uwaterloo.cook_sharp.data.repository.SupabaseRecipeRepository

/*
 * AI workflow
 *
 * The chatbot first uses the LLM to understand the user's message. exmple greetings, recipe request, general question, advice
 * If the user wants a recipe, the app checks the database first.
 * If a matching recipe is found, that recipe is shown.
 * If not, the LLM generates a new recipe suggestion.
 *
 * AI-generated recipes are only temporary at first.
 * When the user taps one, it is saved into the database
 * as a real recipe with source = AI.
 *
 * After saving, it works like any other recipe in the app,
 * so it can be opened and added to the meal plan.
 */

data class ChatBotUiState(
    val messageText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

private data class InterpretedUserRequest(
    val intent: String,
    val replyText: String,
    val recipeNameQuery: String?,
    val ingredients: List<String>,
    val dietPreference: String?,
    val maxCookTime: Int?,
    val strictIngredientMode: Boolean
)

class ChatBotViewModel(
    private val recipeRepository: RecipeRepository = CombinedRecipeRepository(),
    private val dbRecipeRepository: RecipeRepository = SupabaseRecipeRepository(SupabaseUserRepository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatBotUiState(
            messages = listOf(
                ChatMessage.ChatBot("Hello hungry user! How may I assist you today? \uD83D\uDE0B")
            )
        )
    )
    val uiState: StateFlow<ChatBotUiState> = _uiState.asStateFlow()

    private val client = HttpClient(Android)
    private val hfToken = "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"
    private val modelId = "meta-llama/Meta-Llama-3-8B-Instruct"

    fun onMessageChange(text: String) {
        _uiState.update { it.copy(messageText = text) }
    }

    fun onSendClick() {
        val text = uiState.value.messageText.trim()
        if (text.isBlank()) return

        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage.User(text),
                messageText = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val nextMessages = withContext(Dispatchers.IO) {
                    handleMessage(text)
                }

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + nextMessages,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + ChatMessage.ChatBot(
                            "Something went wrong: ${e.message ?: "Unknown error"}"
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleLikeRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                val updatedRecipe = withContext(Dispatchers.IO) {
                    recipeRepository.toggleLike(recipeId)
                    recipeRepository.getRecipeById(recipeId)
                } ?: return@launch

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.map { message ->
                            if (message is ChatMessage.DatabaseRecipeResult && message.recipe.id == recipeId) {
                                message.copy(recipe = updatedRecipe)
                            } else {
                                message
                            }
                        }
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun persistAiRecipe(
        aiRecipe: AiRecipe,
        canonicalRecipeName: String
    ): Recipe {
        val currentUserId = SupabaseUserRepository.getCurrentUser()?.id
            ?: throw IllegalStateException("No logged-in user found")
        val imageUrl = fetchRecipeImageUrl(canonicalRecipeName)
        val ingredientInputs = aiRecipe.ingredients
            .map { line -> line.trim() }
            .filter { it.isNotBlank() }
            .map { line ->
                CreateIngredientInput(
                    name = line,
                    amount = 0.0,
                    unit = "",
                    originalName = line
                )
            }

        val instructionInputs = aiRecipe.steps
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapIndexed { index, step ->
                CreateRecipeInstructionInput(
                    stepNumber = index + 1,
                    instruction = step
                )
            }

        val mealTypes = inferMealTypes(aiRecipe)

        val input = CreateRecipeInput(
            createdByUserId = currentUserId,
            title = canonicalRecipeName.ifBlank { aiRecipe.title },
            readyInMinutes = aiRecipe.cookTimeMinutes,
            difficulty = aiRecipe.difficulty,
            image = imageUrl,
            servings = aiRecipe.servings,
            cuisineType = aiRecipe.cuisineType,
            isVegetarian = false,
            isVegan = false,
            isGlutenFree = false,
            isDairyFree = false,
            isLowFodmap = false,
            isPescatarian = false,
            isKetogenic = false,
            isPaleo = false,
            isWhole30 = false,
            source = RecipeSource.AI,
            localId = "ai-${UUID.randomUUID()}",
            remoteId = null,
            nutritionInfo = CreateNutritionInfoInput(
                calories = aiRecipe.calories,
                protein = aiRecipe.protein,
                carbs = aiRecipe.carbs,
                fat = aiRecipe.fat,
                fiber = aiRecipe.fiber,
                sugar = aiRecipe.sugar,
                sodium = aiRecipe.sodium
            ),
            ingredients = ingredientInputs,
            instructions = instructionInputs,
            mealTypes = mealTypes
        )

        val createdRecipe = recipeRepository.addRecipe(input)
        return recipeRepository.getRecipeById(createdRecipe.id)
            ?: createdRecipe
    }

    private fun inferMealTypes(aiRecipe: AiRecipe): List<MealType> {
        val text = buildString {
            append(aiRecipe.title.lowercase())
            append(" ")
            append(aiRecipe.ingredients.joinToString(" ").lowercase())
        }

        return when {
            listOf("breakfast", "toast", "omelette", "oat", "pancake").any { it in text } ->
                listOf(MealType.BREAKFAST)

            listOf("snack", "cookie", "bar", "smoothie", "dip").any { it in text } ->
                listOf(MealType.SNACK)

            else -> listOf(MealType.DINNER)
        }
    }

    private suspend fun handleMessage(userText: String): List<ChatMessage> {
        val recipes = dbRecipeRepository.getAllRecipes(limit = 60)
        val interpreted = interpretUserMessage(userText, recipes)
        val normalizedText = userText.lowercase()

        val forcedIntent = when {
            looksLikeCookingAdvice(normalizedText) -> "COOKING_ADVICE"
            looksLikeRecipeLookup(normalizedText) -> "RECIPE_LOOKUP"
            looksLikeRecommendationRequest(normalizedText) -> "RECOMMENDATION"
            else -> interpreted.intent.uppercase()
        }

        return when (forcedIntent) {
            "GREETING" -> {
                listOf(
                    ChatMessage.ChatBot(
                        interpreted.replyText.ifBlank {
                            "Hi! What would you like to cook today?"
                        }
                    )
                )
            }

            "COOKING_ADVICE" -> {
                val advice = answerCookingAdvice(userText)
                listOf(ChatMessage.ChatBot(advice))
            }

            "RECIPE_LOOKUP" -> {
                val dbRecipe = findRecipeByTitleStrict(interpreted.recipeNameQuery ?: userText)
                if (dbRecipe != null) {
                    listOf(ChatMessage.DatabaseRecipeResult(dbRecipe))
                } else {
                    val aiRecipe = generateAiRecipe(userText = userText, canonicalRecipeName = interpreted.recipeNameQuery)
                    val savedRecipe = persistAiRecipe(aiRecipe,interpreted.recipeNameQuery?: aiRecipe.title)
                    listOf(ChatMessage.DatabaseRecipeResult(savedRecipe))
                }
            }

            "RECOMMENDATION", "AI_RECIPE_REQUEST" -> {
                val dbRecipe = recommendRecipe(
                    recipes = recipes,
                    ingredients = interpreted.ingredients,
                    dietPreference = interpreted.dietPreference,
                    maxCookTime = interpreted.maxCookTime,
                    strictIngredientMode = interpreted.strictIngredientMode
                )

                if (dbRecipe != null) {
                    listOf(ChatMessage.DatabaseRecipeResult(dbRecipe))
                } else {
                    val aiRecipe = generateAiRecipe(userText)
                    val savedRecipe = persistAiRecipe(aiRecipe, interpreted.recipeNameQuery?: aiRecipe.title)
                    listOf(ChatMessage.DatabaseRecipeResult(savedRecipe))
                }
            }

            else -> {
                val advice = answerGeneralCookingQuestion(userText)
                listOf(ChatMessage.ChatBot(advice))
            }
        }
    }

    private suspend fun interpretUserMessage(
        userText: String,
        recipes: List<Recipe>
    ): InterpretedUserRequest {
        val recipeCatalog = recipes.joinToString("\n") { recipe ->
            val ingredientPreview = recipe.ingredients
                .take(8)
                .joinToString(", ") { it.name }
                .ifBlank { "none listed" }

            val dietaryTags = buildList {
                if (recipe.isVegetarian) add("Vegetarian")
                if (recipe.isVegan) add("Vegan")
                if (recipe.isPescatarian) add("Pescatarian")
                if (recipe.isGlutenFree) add("Gluten-Free")
                if (recipe.isDairyFree) add("Dairy-Free")
                if (recipe.isKetogenic) add("Ketogenic")
                if (recipe.isPaleo) add("Paleo")
                if (recipe.isWhole30) add("Whole30")
                if (recipe.isLowFodmap) add("Low FODMAP")
            }.joinToString(", ").ifBlank { "None" }

            "- ${recipe.title} | ${recipe.readyInMinutes} mins | tags: $dietaryTags | ingredients: $ingredientPreview"
        }

        val prompt = """
            You are the intent parser for the Cook Sharp recipe chatbot.

            The app has these recipes in its database:
            $recipeCatalog

            Return ONLY valid JSON with this exact shape:
            {
              "intent": "GREETING" | "GENERAL" | "RECIPE_LOOKUP" | "RECOMMENDATION" | "AI_RECIPE_REQUEST",
              "replyText": "short friendly response",
              "recipeNameQuery": "canonical/common recipe title with corrected spelling, or empty string",
              "ingredients": ["ingredient1", "ingredient2"],
              "dietPreference": "Vegetarian | Vegan | Pescatarian | Gluten Free | Dairy Free | Ketogenic | Paleo | Whole30 | Low FODMAP | empty string",
              "maxCookTime": 0,
              "strictIngredientMode": false
            }

            Rules:
            - GREETING = hello/hi/hey
            - RECIPE_LOOKUP = asking for a specific named recipe
            - RECOMMENDATION = asking what to make, asking based on ingredients, time, diet, or meal style
            - AI_RECIPE_REQUEST = explicitly asking you to create a recipe
            - GENERAL = non-recipe conversation
            - replyText must be short
            - maxCookTime should be 0 if not specified
            - strictIngredientMode should be true for messages like "I only have", "I just have", "all I have is"
            - Do not wrap output in markdown fences
            - If the user asks for a specific named recipe, recipeNameQuery must use the corrected/common spelling of that recipe name
            - Example: if the user writes "shakshuka" but the standard/common title is "Shakshouka", return "Shakshouka"
            - Remove filler words like "give me", "recipe of", "how to make"

            User message:
            $userText
        """.trimIndent()

        val raw = callChatCompletion(
            systemPrompt = "Return only compact JSON.",
            userPrompt = prompt,
            maxTokens = 250
        )

        val parsed = JSONObject(extractJsonObject(raw))
        return InterpretedUserRequest(
            intent = parsed.optString("intent", "GENERAL"),
            replyText = parsed.optString("replyText", ""),
            recipeNameQuery = parsed.optString("recipeNameQuery", "").ifBlank { null },
            ingredients = parsed.optJSONArray("ingredients").toStringList(),
            dietPreference = parsed.optString("dietPreference", "").ifBlank { null },
            maxCookTime = parsed.optInt("maxCookTime", 0).takeIf { it > 0 },
            strictIngredientMode = parsed.optBoolean("strictIngredientMode", false)
        )
    }

    private fun recommendRecipe(
        recipes: List<Recipe>,
        ingredients: List<String>,
        dietPreference: String?,
        maxCookTime: Int?,
        strictIngredientMode: Boolean
    ): Recipe? {
        val normalizedIngredients = ingredients
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        val pantryIngredients = setOf(
            "salt", "pepper", "water", "oil", "olive oil", "butter",
            "garlic powder", "onion powder", "chili flakes",
            "oregano", "paprika", "cumin"
        )

        val filtered = recipes.filter { recipe ->
            val dietOk = dietPreference?.let { recipe.containsDietaryRestriction(it) } ?: true
            val timeOk = maxCookTime?.let { recipe.readyInMinutes <= it } ?: true
            dietOk && timeOk
        }

        if (filtered.isEmpty()) return null

        if (normalizedIngredients.isEmpty()) {
            return filtered.minByOrNull { it.readyInMinutes }
        }

        val ranked = filtered.mapNotNull { recipe ->
            val recipeIngredientNames = recipe.ingredients
                .map { it.name.trim().lowercase() }
                .filter { it.isNotBlank() }

            val matchedCount = normalizedIngredients.count { wanted ->
                recipeIngredientNames.any { recipeIng ->
                    recipeIng.contains(wanted) || wanted.contains(recipeIng)
                } || recipe.title.lowercase().contains(wanted)
            }

            if (matchedCount == 0) return@mapNotNull null

            val extraMajorIngredients = recipeIngredientNames.count { recipeIng ->
                recipeIng !in pantryIngredients &&
                        normalizedIngredients.none { wanted ->
                            recipeIng.contains(wanted) || wanted.contains(recipeIng)
                        }
            }

            val score = if (strictIngredientMode) {
                matchedCount * 10 - extraMajorIngredients * 5
            } else {
                matchedCount * 10 - extraMajorIngredients * 2
            }

            Triple(recipe, score, extraMajorIngredients)
        }

        val best = ranked
            .filter { (_, score, _) -> score > 0 }
            .sortedWith(
                compareByDescending<Triple<Recipe, Int, Int>> { it.second }
                    .thenBy { it.third }
                    .thenBy { it.first.readyInMinutes }
            )
            .firstOrNull()

        if (best == null) return null

        val (_, _, bestExtraMajorIngredients) = best

        if (strictIngredientMode && bestExtraMajorIngredients > 1) {
            return null
        }

        return best.first
    }

    private fun findRecipeByTitleStrict(query: String): Recipe? {
        val cleanedQuery = query
            .lowercase()
            .replace("can you give me the recipe of", "")
            .replace("give me the recipe of", "")
            .replace("recipe of", "")
            .replace("recipe for", "")
            .replace("show me the recipe for", "")
            .replace("show me", "")
            .replace("find", "")
            .trim()

        if (cleanedQuery.isBlank()) return null

        val results = dbRecipeRepository.searchRecipes(cleanedQuery)

        return results.firstOrNull { recipe ->
            val title = recipe.title.lowercase()
            title == cleanedQuery ||
                    title.contains(cleanedQuery) ||
                    cleanedQuery.contains(title)
        }
    }

    private suspend fun generateAiRecipe(
        userText: String,
        canonicalRecipeName: String? = null
    ): AiRecipe {
        val namedRecipeInstruction = canonicalRecipeName?.let {
            """
        The user is asking for this named recipe.
        Use this exact recipe title in the JSON title field:
        "$it"
        """.trimIndent()
        } ?: ""

        val prompt = """
        Create a practical recipe for this user request:
        "$userText"

        $namedRecipeInstruction

        Return ONLY valid JSON in this exact shape:
        {
          "title": "recipe title",
          "cookTimeMinutes": 25,
          "difficulty": "Easy",
          "servings": 2,
          "calories": 450,
          "protein": 18,
          "carbs": 42,
          "fat": 16,
          "fiber": 6,
          "sugar": 8,
          "sodium": 520,
          "ingredients": ["1 cup pasta", "2 cloves garlic"],
          "steps": ["chop the garlic", "add 1 cup water ..."],
          "cuisineType": "Middle Eastern"
        }

        Rules:
        - Respect time constraints in the user's request
        - Respect ingredient limits in the user's request
        - If the user asks for a named recipe, generate that recipe
        - If a canonical recipe title was provided, use that exact title
        - servings must be a realistic whole number
        - nutrition values should be realistic estimates for the whole recipe
        - Include 5 to 10 ingredients
        - Include 4 to 8 short steps
        - Keep difficulty to one word
        - Do not wrap output in markdown fences
    """.trimIndent()

        val raw = callChatCompletion(
            systemPrompt = "You are a cooking assistant that returns only recipe JSON.",
            userPrompt = prompt,
            maxTokens = 900
        )

        val parsed = JSONObject(extractJsonObject(raw))
        return AiRecipe(
            title = parsed.optString("title", "AI Recipe Suggestion"),
            cookTimeMinutes = parsed.optInt("cookTimeMinutes", 30),
            difficulty = parsed.optString("difficulty", "Easy"),
            servings = parsed.optInt("servings", 2).coerceAtLeast(1),
            calories = parsed.optDouble("calories", 0.0),
            protein = parsed.optDouble("protein", 0.0),
            carbs = parsed.optDouble("carbs", 0.0),
            fat = parsed.optDouble("fat", 0.0),
            fiber = parsed.optDouble("fiber", 0.0),
            sugar = parsed.optDouble("sugar", 0.0),
            sodium = parsed.optDouble("sodium", 0.0),
            ingredients = parsed.optJSONArray("ingredients").toStringList(),
            steps = parsed.optJSONArray("steps").toStringList(),
            cuisineType = parsed.optString("cuisineType", "").ifBlank { null }
        )
    }

    private suspend fun answerCookingAdvice(userText: String): String {
        val prompt = """
            Answer this cooking question clearly and briefly:
            "$userText"

            Keep the answer practical, helpful, and under 120 words.
        """.trimIndent()

        return callChatCompletion(
            systemPrompt = "You are a helpful cooking assistant.",
            userPrompt = prompt,
            maxTokens = 180
        )
    }

    private suspend fun answerGeneralCookingQuestion(userText: String): String {
        val prompt = """
            Respond helpfully to this cooking-related user message:
            "$userText"

            Keep the answer short and useful.
        """.trimIndent()

        return callChatCompletion(
            systemPrompt = "You are a helpful cooking chatbot.",
            userPrompt = prompt,
            maxTokens = 160
        )
    }

    private fun looksLikeRecipeLookup(text: String): Boolean {
        return listOf(
            "recipe of",
            "recipe for",
            "give me the recipe",
            "show me the recipe",
            "can you give me the recipe",
            "how to make",
            "how do i make"
        ).any { it in text }
    }

    private fun looksLikeRecommendationRequest(text: String): Boolean {
        return listOf(
            "what can i make",
            "what do i cook",
            "what should i make",
            "recommend",
            "suggest",
            "i only have",
            "i just have",
            "all i have is",
            "cook in",
            "quick recipe"
        ).any { it in text }
    }

    private fun looksLikeCookingAdvice(text: String): Boolean {
        return listOf(
            "can i substitute",
            "what can i substitute",
            "replace",
            "instead of",
            "can i use",
            "is it okay to use",
            "swap"
        ).any { it in text }
    }

    // Makes a network request to the HuggingFace Inference API
    private suspend fun callChatCompletion(
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int
    ): String {
        val response = client.post("https://router.huggingface.co/v1/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $hfToken")
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            val jsonBody = JSONObject().apply {
                put("model", modelId)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                })
                put("max_tokens", maxTokens)
                put("temperature", 0.2)
                put("stream", false)
            }

            setBody(jsonBody.toString())
        }

        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("LLM request failed: ${response.status.description}")
        }

        val responseText = response.bodyAsText()
        val jsonResponse = JSONObject(responseText)

        return jsonResponse.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()
    }

    private fun extractJsonObject(text: String): String {
        val fenced = Regex(
            """```(?:json)?\s*(\{.*\})\s*```""",
            RegexOption.DOT_MATCHES_ALL
        ).find(text)?.groupValues?.getOrNull(1)

        if (fenced != null) return fenced.trim()

        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start in 0 until end) {
            return text.substring(start, end + 1)
        }

        throw IllegalStateException("Model did not return valid JSON")
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (i in 0 until length()) {
                val value = optString(i).trim()
                if (value.isNotBlank()) add(value)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }

    private suspend fun fetchRecipeImageUrl(recipeTitle: String): String? {
        // get image using spooncular api
        return try {
            recipeRepository.searchRecipes(recipeTitle)
                .firstOrNull()
                ?.image
        } catch (e: Exception) {
            null
        }
    }
}