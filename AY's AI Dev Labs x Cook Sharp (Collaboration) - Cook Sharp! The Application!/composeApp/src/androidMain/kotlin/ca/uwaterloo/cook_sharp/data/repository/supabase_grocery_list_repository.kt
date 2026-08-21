package ca.uwaterloo.cook_sharp.data.repository

import android.util.Log
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseClientProvider
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseGroceryItem
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseGroceryItemCheckedState
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseGroceryList
import ca.uwaterloo.cook_sharp.domain.GroceryItem
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

/**
 * Functionalities:
 * - Save user's grocery list and item states in Supabase, implemented from GroceryListRepository
 * - Clear cache
 * - Get grocery lists of current user from Supabase
 * - Add a recipe to grocery list
 * - Remove a recipe to grocery list
 * - Update serving size of a recipe in list
 * - Check / uncheck a ingredient in list
 * - Expand / collapse a recipe panel in list
 */

class SupabaseGroceryListRepository(
    private val userRepo: UserRepository = SupabaseUserRepository,
    private val recipeRepo: RecipeRepository = SupabaseRecipeRepository()
) : GroceryListRepository {

    private val client = SupabaseClientProvider.client

    private var currentGroceryListId: Long? = null
    private var currentUser: String? = null

    private fun resetGroceryListCacheForUser(userId: String?) {
        if (currentUser != userId) {
            Log.d("GROCERY_REPO", "resetGroceryListCacheForUser: user changed from $currentUser to $userId")
            currentGroceryListId = null
            currentUser = userId
        }
    }

    private suspend fun getOrCreateGroceryListId(): Long? {
        val userId = userRepo.getCurrentUser()?.id?.trim()
        resetGroceryListCacheForUser(userId)

        if (currentGroceryListId != null) {
            Log.d("GROCERY_REPO", "getOrCreateGroceryListId: using id=$currentGroceryListId")
            return currentGroceryListId
        }

        if (userId == null) {
            Log.w("GROCERY_REPO", "getOrCreateGroceryListId: currentUser is null")
            return null
        }

        Log.d("GROCERY_REPO", "getOrCreateGroceryListId: looking up userId=$userId")
        val existing = client.from("grocery_lists")
            .select()
            .decodeList<SupabaseGroceryList>()
            .firstOrNull { it.user_id == userId }

        if (existing != null) {
            Log.d("GROCERY_REPO", "getOrCreateGroceryListId: found existing.id=${existing.id}")
            currentGroceryListId = existing.id
            currentUser = userId
            return existing.id
        }

        Log.d("GROCERY_REPO", "getOrCreateGroceryListId: no list found, creating new one")
        return try {
            val id = client.from("grocery_lists").insert(SupabaseGroceryList(user_id = userId)) { select() }
                .decodeSingle<SupabaseGroceryList>().id
            Log.d("GROCERY_REPO", "getOrCreateGroceryListId: created new list id=$id")
            currentGroceryListId = id
            currentUser = userId
            id
        } catch (e: Exception) {
            Log.e("GROCERY_REPO", "getOrCreateGroceryListId: insert failed, retrying select", e)
            val id = client.from("grocery_lists").select().decodeList<SupabaseGroceryList>()
                .firstOrNull { it.user_id == userId }?.id
            Log.d("GROCERY_REPO", "getOrCreateGroceryListId: fallback select result=$id")
            currentGroceryListId = id
            currentUser = userId
            id
        }
    }

    private suspend fun getGroceryItemByRecipeId(recipeId: Long, groceryListId: Long): SupabaseGroceryItem? =
        client.from("grocery_items")
            .select { filter { eq("recipe_id", recipeId); eq("grocery_list_id", groceryListId) } }
            .decodeList<SupabaseGroceryItem>().firstOrNull()

    override fun getGroceryList(): List<GroceryItem> = runBlocking {
        val groceryListId = getOrCreateGroceryListId()
        if (groceryListId == null) {
            Log.w("GROCERY_REPO", "getGroceryList: groceryListId is null, returning empty")
            return@runBlocking emptyList()
        }
        val groceryItems = client.from("grocery_items").select { filter { eq("grocery_list_id", groceryListId) } }
            .decodeList<SupabaseGroceryItem>()
        Log.d("GROCERY_REPO", "getGroceryList: found ${groceryItems.size} items for listId=$groceryListId")

        groceryItems.mapNotNull { item ->
            val recipe = recipeRepo.getRecipeById(item.recipe_id)
            if (recipe == null) {
                Log.w("GROCERY_REPO", "getGroceryList: recipe not found for recipe_id=${item.recipe_id}, skipping")
                return@mapNotNull null
            }
            val checkedStates =
                client.from("grocery_item_checked_states").select { filter { eq("grocery_item_id", item.id) } }
                    .decodeList<SupabaseGroceryItemCheckedState>()
            val ingredientIdToIndex = recipe.ingredients.mapIndexed { idx, ing -> ing.id to idx }.toMap()
            val checkedPairs = mutableListOf<Pair<Int, Boolean>>()
            for (state in checkedStates) {
                val ingredientIndex = ingredientIdToIndex[state.ingredient_id]
                if (ingredientIndex != null) {
                    checkedPairs.add(ingredientIndex to true)
                }
            }
            val checkedMap = checkedPairs.toMap()
            GroceryItem(
                recipe = recipe, servings = item.servings, checkedStates = checkedMap, isExpanded = item.is_expanded
            )
        }
    }

    override fun addRecipeToGroceryList(recipeId: Long, servings: Int): List<GroceryItem> = runBlocking {
        Log.d("GROCERY_REPO", "addRecipeToGroceryList: recipeId=$recipeId, servings=$servings")
        val groceryListId = getOrCreateGroceryListId()
        if (groceryListId == null) {
            Log.e("GROCERY_REPO", "addRecipeToGroceryList: groceryListId is null, aborting")
            return@runBlocking emptyList()
        }
        val existing = getGroceryItemByRecipeId(recipeId, groceryListId)
        if (existing != null) {
            Log.d(
                "GROCERY_REPO",
                "addRecipeToGroceryList: updating existing item, new servings=${existing.servings + servings}"
            )
            client.from("grocery_items")
                .update({ set("servings", existing.servings + servings) }) { filter { eq("id", existing.id) } }
        } else {
            Log.d("GROCERY_REPO", "addRecipeToGroceryList: inserting new item")
            client.from("grocery_items").insert(
                SupabaseGroceryItem(
                    recipe_id = recipeId, grocery_list_id = groceryListId, servings = servings, is_expanded = true
                )
            )
        }
        getGroceryList()
    }

    override fun removeRecipeFromGroceryList(recipeId: Long): List<GroceryItem> = runBlocking {
        val groceryListId = getOrCreateGroceryListId() ?: return@runBlocking emptyList()
        val item = getGroceryItemByRecipeId(recipeId, groceryListId)
        if (item != null) {
            client.from("grocery_item_checked_states").delete { filter { eq("grocery_item_id", item.id) } }
            client.from("grocery_items").delete { filter { eq("id", item.id) } }
        }
        getGroceryList()
    }

    override fun updateServings(recipeId: Long, servings: Int): List<GroceryItem> = runBlocking {
        val groceryListId = getOrCreateGroceryListId() ?: return@runBlocking emptyList()
        client.from("grocery_items").update({ set("servings", servings) }) {
            filter { eq("recipe_id", recipeId); eq("grocery_list_id", groceryListId) }
        }
        getGroceryList()
    }

    override fun toggleIngredientCheck(recipeId: Long, ingredientIndex: Int): List<GroceryItem> = runBlocking {
        val groceryListId = getOrCreateGroceryListId() ?: return@runBlocking emptyList()
        val groceryItem = getGroceryItemByRecipeId(recipeId, groceryListId) ?: return@runBlocking emptyList()
        val ingredient = recipeRepo.getRecipeById(recipeId)?.ingredients?.getOrNull(ingredientIndex)
            ?: return@runBlocking emptyList()
        val existing = client.from("grocery_item_checked_states")
            .select { filter { eq("grocery_item_id", groceryItem.id); eq("ingredient_id", ingredient.id) } }
            .decodeList<SupabaseGroceryItemCheckedState>().firstOrNull()
        updateIngredientChecked(recipeId, ingredientIndex, existing == null)
    }

    override fun updateIngredientChecked(recipeId: Long, ingredientIndex: Int, checked: Boolean): List<GroceryItem> =
        runBlocking {
            val groceryListId = getOrCreateGroceryListId() ?: return@runBlocking emptyList()
            val groceryItem = getGroceryItemByRecipeId(recipeId, groceryListId) ?: return@runBlocking emptyList()
            val ingredient = recipeRepo.getRecipeById(recipeId)?.ingredients?.getOrNull(ingredientIndex)
                ?: return@runBlocking emptyList()

            val existing = client.from("grocery_item_checked_states")
                .select { filter { eq("grocery_item_id", groceryItem.id); eq("ingredient_id", ingredient.id) } }
                .decodeList<SupabaseGroceryItemCheckedState>().firstOrNull()
                
            val isCurrentlyChecked = existing != null

            if (checked == isCurrentlyChecked) return@runBlocking getGroceryList()

            if (checked) {
                client.from("grocery_item_checked_states").insert(
                    SupabaseGroceryItemCheckedState(
                        grocery_item_id = groceryItem.id, ingredient_id = ingredient.id, is_checked = true
                    )
                )
            } else {
                client.from("grocery_item_checked_states").delete {
                        filter {
                            eq("grocery_item_id", groceryItem.id)
                            eq("ingredient_id", ingredient.id)
                        }
                    }
            }

            getGroceryList()
        }

    override fun updateExpanded(recipeId: Long, isExpanded: Boolean): List<GroceryItem> = runBlocking {
        val groceryListId = getOrCreateGroceryListId() ?: return@runBlocking emptyList()
        client.from("grocery_items").update({ set("is_expanded", isExpanded) }) {
            filter { eq("recipe_id", recipeId); eq("grocery_list_id", groceryListId) }
        }
        getGroceryList()
    }
}
