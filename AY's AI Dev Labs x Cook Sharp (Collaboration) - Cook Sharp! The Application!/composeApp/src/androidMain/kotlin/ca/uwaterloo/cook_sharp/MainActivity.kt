package ca.uwaterloo.cook_sharp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import ca.uwaterloo.cook_sharp.ui.navigation.appNav
import ca.uwaterloo.cook_sharp.ui.theme.CookSharpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Copy mock images to gallery on startup
        lifecycleScope.launch {
            copyMockImagesToGallery()
        }

        setContent {
            CookSharpTheme {
                appNav()
            }
        }
    }

    private suspend fun copyMockImagesToGallery() = withContext(Dispatchers.IO) {
        val assetManager = assets
        val mockImagesDir = "atheas_device_custom_images"
        
        try {
            val files = assetManager.list(mockImagesDir) ?: return@withContext
            for (fileName in files) {
                if (fileName == "notes for folder") continue
                
                val inputStream = assetManager.open("$mockImagesDir/$fileName")
                saveImageToGallery(this@MainActivity, inputStream, fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveImageToGallery(context: Context, inputStream: InputStream, fileName: String) {
        val bitmap = BitmapFactory.decodeStream(inputStream) ?: return
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CookSharp")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
