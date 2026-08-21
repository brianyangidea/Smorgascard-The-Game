package ca.uwaterloo.cook_sharp.data.supabase

import kotlinx.serialization.Serializable

@Serializable
data class SupabaseAllergy(
    val id: Long = 0L,
    val name: String
)

@Serializable
data class SupabaseUserAllergy(
    val user_id: String,
    val allergy_id: Long
)

@Serializable
data class SupabaseUserPreference(
    val user_id: String,
    val preference_id: Long
)
