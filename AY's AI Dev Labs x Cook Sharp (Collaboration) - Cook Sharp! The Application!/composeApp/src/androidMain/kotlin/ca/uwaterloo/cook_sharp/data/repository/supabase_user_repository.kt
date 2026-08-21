package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.data.supabase.SupabaseAllergy
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseClientProvider
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseUser
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseUserAllergy
import ca.uwaterloo.cook_sharp.data.supabase.SupabaseUserPreference
import ca.uwaterloo.cook_sharp.domain.User
import io.github.jan.supabase.postgrest.from

/**
 * Features:
 * - Authenticate users by ID and password
 * - Create new user accounts
 * - Keep track of the currently logged in user
 * - Update profile fields in Supabase - like preferences, image, diet
 * - Load and save diet preference and allergy selections
 * - Return all users for recipe sharing feature
 */
object SupabaseUserRepository : UserRepository {

    private val client = SupabaseClientProvider.client

    private var currentUser: User? = null

    private val dietPreferenceIdMap = mapOf(
        "No Dietary restriction" to 1L,
        "Vegetarian" to 2L,
        "Vegan" to 3L,
        "Pescatarian" to 4L,
        "Halal" to 5L,
        "Ketogenic" to 6L,
        "Gluten Free" to 7L,
        "Lacto-Vegetarian" to 8L,
        "Ovo-Vegetarian" to 9L,
        "Paleo" to 10L,
        "Primal" to 11L,
        "Low FODMAP" to 12L,
        "Whole30" to 13L
    )
    private val dietPreferenceNameMap = dietPreferenceIdMap.entries.associate { (k, v) -> v to k }

    override fun getCurrentUser(): User? = currentUser

    override suspend fun login(id: String, password: String): User? {
        return runCatching {
            val allUsers = client.from("users")
                .select()
                .decodeList<SupabaseUser>()

            val row = allUsers.firstOrNull { it.id == id }
                ?: allUsers.firstOrNull { it.email == id }

            if (row != null && row.password == password) {
                val userAllergies = runCatching {
                    val userAllergyRows = client.from("user_allergies")
                        .select { filter { eq("user_id", row.id) } }
                        .decodeList<SupabaseUserAllergy>()

                    if (userAllergyRows.isEmpty()) {
                        emptyList()
                    } else {
                        val allergyIds = userAllergyRows.map { it.allergy_id }
                        client.from("allergies")
                            .select { filter { isIn("id", allergyIds) } }
                            .decodeList<SupabaseAllergy>()
                            .map { normalizeAllergyKey(it.name) }
                    }
                }.getOrDefault(emptyList())

                val dietPreference = runCatching {
                    client.from("user_preferences")
                        .select { filter { eq("user_id", row.id) } }
                        .decodeList<SupabaseUserPreference>()
                        .firstOrNull()
                        ?.let { dietPreferenceNameMap[it.preference_id] }
                }.getOrNull()

                val user = User(
                    id = row.id,
                    name = row.name,
                    email = row.email,
                    password = row.password,
                    profilePictureUri = row.profile_picture_uri,
                    dietarypreference = dietPreference,
                    allergies = userAllergies
                )

                currentUser = user
                user
            } else {
                null
            }
        }.getOrThrow()
    }

    override suspend fun signup(name: String, email: String, password: String): User? {
        val normalizedEmail = email.trim().lowercase()
        val userId = normalizedEmail.substringBefore("@")
        return runCatching {
            val newUser = User(
                id = userId,
                name = name,
                email = email,
                password = password,
                dietarypreference = null,
                allergies = emptyList()
            )
            client.from("users").insert(
                SupabaseUser(
                    id = newUser.id,
                    name = newUser.name,
                    email = newUser.email,
                    password = newUser.password,
                    profile_picture_uri = newUser.profilePictureUri,
                    dietarypreference = newUser.dietarypreference
                )
            )
            currentUser = newUser
            newUser
        }.onFailure {}
            .getOrNull()
    }

    override suspend fun updateUser(user: User) {
        client.from("users").update(
            SupabaseUser(
                id = user.id,
                name = user.name,
                email = user.email,
                password = user.password,
                profile_picture_uri = user.profilePictureUri,
                dietarypreference = user.dietarypreference
            )
        ) { filter { eq("id", user.id) } }

        client.from("user_preferences")
            .delete { filter { eq("user_id", user.id) } }
        val preferenceId = user.dietarypreference?.let { dietPreferenceIdMap[it] }
        if (preferenceId != null) {
            client.from("user_preferences")
                .insert(SupabaseUserPreference(user_id = user.id, preference_id = preferenceId))
        }

        client.from("user_allergies")
            .delete { filter { eq("user_id", user.id) } }

        val normalizedAllergies = user.allergies
            .map(::normalizeAllergyKey)
            .distinct()

        for (allergyName in normalizedAllergies) {
            val existing = client.from("allergies")
                .select { filter { eq("name", allergyName) } }
                .decodeList<SupabaseAllergy>()
                .firstOrNull()

            val allergyId = existing?.id ?: client.from("allergies")
                .insert(SupabaseAllergy(name = allergyName)) { select() }
                .decodeSingle<SupabaseAllergy>()
                .id

            client.from("user_allergies")
                .insert(SupabaseUserAllergy(user_id = user.id, allergy_id = allergyId))
        }

        currentUser = user.copy(allergies = normalizedAllergies)
    }

    override fun getDietPreference(): String? {
        // return UserStore.currentUser.dietarypreference
        return currentUser?.dietarypreference
    }

    override suspend fun setDietPreference(preference: String?) {
        val current = currentUser ?: return
        updateUser(current.copy(dietarypreference = preference))
    }

    override suspend fun getAllergyPreference(): List<String> {
        val userId = currentUser?.id ?: return emptyList()
        val userAllergies = client.from("user_allergies")
            .select { filter { eq("user_id", userId) } }
            .decodeList<SupabaseUserAllergy>()
        if (userAllergies.isEmpty()) return emptyList()
        val allergyIds = userAllergies.map { it.allergy_id }
        return client.from("allergies")
            .select { filter { isIn("id", allergyIds) } }
            .decodeList<SupabaseAllergy>()
            .map { normalizeAllergyKey(it.name) }
    }

    override suspend fun setAllergyPreference(allergies: List<String>) {
        val userId = currentUser?.id ?: return
        val normalizedAllergies = allergies.map(::normalizeAllergyKey).distinct()
        client.from("user_allergies").delete { filter { eq("user_id", userId) } }
        for (allergyName in normalizedAllergies) {
            val existing = client.from("allergies")
                .select { filter { eq("name", allergyName) } }
                .decodeList<SupabaseAllergy>()
                .firstOrNull()
            val allergyId = existing?.id ?: client.from("allergies")
                .insert(SupabaseAllergy(name = allergyName)) { select() }
                .decodeSingle<SupabaseAllergy>().id
            client.from("user_allergies")
                .insert(SupabaseUserAllergy(user_id = userId, allergy_id = allergyId))
        }
        currentUser = currentUser?.copy(allergies = normalizedAllergies)
    }

    override suspend fun getAllUsers(): List<User> {
        return runCatching {
            client.from("users")
                .select()
                .decodeList<SupabaseUser>()
                .map {
                    User(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        password = it.password,
                        profilePictureUri = it.profile_picture_uri,
                        dietarypreference = it.dietarypreference
                    )
                }
        }.getOrDefault(emptyList())
    }

    private fun normalizeAllergyKey(value: String): String {
        return value.trim().lowercase()
    }
}
