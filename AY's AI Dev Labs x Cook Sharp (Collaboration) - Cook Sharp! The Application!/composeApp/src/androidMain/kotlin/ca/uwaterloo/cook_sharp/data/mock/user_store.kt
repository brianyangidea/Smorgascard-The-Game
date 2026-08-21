package ca.uwaterloo.cook_sharp.data.mock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ca.uwaterloo.cook_sharp.domain.User

object UserStore {
    private val sueFlay = User(
        id = "sueflay999",
        name = "Sue Flay",
        email = "sue.flay@gmail.com",
        password = "1234",
        profilePictureUri = "file:///android_asset/atheas_device_custom_images/sueflay.png",
        dietarypreference = "Pescatarian",
        allergies = listOf("gluten")
    )

    private val zoltanPepper = User(
        id = "pepperlover",
        name = "Zoltan Pepper",
        email = "zoltan.pepper@gmail.com",
        password = "spicy",
        profilePictureUri = "file:///android_asset/atheas_device_custom_images/zoltanpepper.png",
        dietarypreference = "Vegan",
        allergies = listOf("Seafood", "Sesame", "Soy", "Shellfish")
    )

    val allUsers = mutableStateListOf(sueFlay, zoltanPepper)

    var currentUser by mutableStateOf(sueFlay)

    fun updateUser(user: User) {
        currentUser = user
        // Update the user in the allUsers list to persist changes across logouts
        val index = allUsers.indexOfFirst { it.id == user.id }
        if (index != -1) {
            allUsers[index] = user
        } else {
            allUsers.add(user)
        }
    }
}
