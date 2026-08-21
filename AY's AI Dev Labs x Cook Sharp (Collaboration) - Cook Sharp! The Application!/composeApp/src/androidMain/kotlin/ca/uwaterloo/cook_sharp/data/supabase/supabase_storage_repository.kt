package ca.uwaterloo.cook_sharp.data.supabase

import io.github.jan.supabase.storage.storage

object SupabaseStorageRepository {
    private val client = SupabaseClientProvider.client
    private const val RECIPE_BUCKET = "recipe-images"
    private const val AVATAR_BUCKET = "user-avatars"

    suspend fun uploadRecipeImage(localId: String, bytes: ByteArray): String {
        val path = "$localId.jpg"
        client.storage.from(RECIPE_BUCKET).upload(path, bytes)
        return client.storage.from(RECIPE_BUCKET).publicUrl(path)
    }

    suspend fun uploadUserProfileImage(imageId: String, bytes: ByteArray): String {
        val path = "$imageId.jpg"
        client.storage.from(AVATAR_BUCKET).upload(path, bytes)
        return client.storage.from(AVATAR_BUCKET).publicUrl(path)
    }
}
