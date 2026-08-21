package ca.uwaterloo.cook_sharp.data.mock

import ca.uwaterloo.cook_sharp.data.repository.UserRepository
import ca.uwaterloo.cook_sharp.domain.User

class MockUserRepository : UserRepository {
    override fun getCurrentUser(): User? {
        return UserStore.currentUser
    }

    override suspend fun login(id: String, password: String): User? {
        val user = UserStore.allUsers.find { it.id == id && it.password == password }
        if (user != null) {
            UserStore.updateUser(user)
        }
        return user
    }

    override suspend fun signup(name: String, email: String, password: String): User? {
        if (UserStore.allUsers.any { it.email == email }) return null
        val newUser = User(id = email, name = name, email = email, password = password)
        UserStore.updateUser(newUser)
        return newUser
    }

    override suspend fun updateUser(user: User) {
        UserStore.updateUser(user)
    }

    override fun getDietPreference(): String? {
        return UserStore.currentUser.dietarypreference
    }

    override suspend fun setDietPreference(preference: String?) {
        val updatedUser = UserStore.currentUser.copy(dietarypreference = preference)
        UserStore.updateUser(updatedUser)
    }

    override suspend fun getAllergyPreference(): List<String> {
        return UserStore.currentUser.allergies
    }

    override suspend fun setAllergyPreference(allergies: List<String>) {
        val updatedUser = UserStore.currentUser.copy(allergies = allergies)
        UserStore.updateUser(updatedUser)
    }

    override suspend fun getAllUsers(): List<User> {
        return UserStore.allUsers
    }
}