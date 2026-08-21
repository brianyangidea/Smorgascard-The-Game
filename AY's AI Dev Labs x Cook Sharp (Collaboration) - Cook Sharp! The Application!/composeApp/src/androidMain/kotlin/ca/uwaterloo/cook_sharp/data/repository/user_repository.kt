package ca.uwaterloo.cook_sharp.data.repository

import ca.uwaterloo.cook_sharp.domain.User

interface UserRepository {
    fun getCurrentUser(): User?
    suspend fun updateUser(user: User)

    suspend fun login(id: String, password: String): User?
    suspend fun signup(name: String, email: String, password: String): User?
    suspend fun getAllUsers(): List<User>

    fun getDietPreference(): String?
    suspend fun setDietPreference(preference: String?)
    suspend fun getAllergyPreference(): List<String>
    suspend fun setAllergyPreference(allergies: List<String>)
}
