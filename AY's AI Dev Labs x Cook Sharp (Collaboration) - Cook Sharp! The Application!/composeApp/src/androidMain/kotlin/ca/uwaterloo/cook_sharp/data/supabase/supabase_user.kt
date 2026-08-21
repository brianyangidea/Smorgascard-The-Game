package ca.uwaterloo.cook_sharp.data.supabase

import kotlinx.serialization.Serializable

@Serializable
data class SupabaseUser(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val profile_picture_uri: String? = null,
    val dietarypreference: String? = null
)
