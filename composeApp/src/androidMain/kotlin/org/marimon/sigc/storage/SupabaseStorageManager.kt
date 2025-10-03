package org.marimon.sigc.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.text.SimpleDateFormat
import java.util.*
import org.marimon.sigc.config.SupabaseConfig
import org.marimon.sigc.config.SupabaseCredentials

class SupabaseStorageManager {

    // Configuración de Supabase Storage usando las nuevas credenciales
    private val bucketName = SupabaseConfig.STORAGE_BUCKET // "productos-imagenes"
        
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Sube una imagen a Supabase Storage
     */
    suspend fun subirImagen(uri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = System.currentTimeMillis()
                val fileName = "empleado_$timestamp.jpg"

                // Obtener bytes de la imagen
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("No se puede abrir el archivo")

                val imageBytes = inputStream.readBytes()
                inputStream.close()

                // Usar FormData multipart como lo hace Supabase Dashboard
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        fileName,
                        imageBytes.toRequestBody("image/jpeg".toMediaType())
                    )
                    .build()

                val request = Request.Builder()
                    .url("https://xjqjlllzbcrpcylhnlmh.supabase.co/storage/v1/object/empleados/$fileName")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhqcWpsbGx6YmNycGN5bGhubG1oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.4c5oU47ZA7FYyVwlrVVY6_sL9cLr5pMeEs6E5R4yspE")
                    .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhqcWpsbGx6YmNycGN5bGhubG1oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.4c5oU47ZA7FYyVwlrVVY6_sL9cLr5pMeEs6E5R4yspE")
                    .build()

                // Ejecutar request
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (response.isSuccessful) {
                        // Generar URL pública
                        val publicUrl = "https://xjqjlllzbcrpcylhnlmh.supabase.co/storage/v1/object/public/empleados/$fileName"
                        publicUrl
                    } else {
                        Log.w("SupabaseStorage", "Error subiendo imagen: ${response.code}")
                        null
                    }
                }

            } catch (e: Exception) {
                Log.w("SupabaseStorage", "Error subiendo imagen", e)
                null
            }
        }
    }



    /**
     * Sube una imagen de producto a Supabase Storage
     */
    suspend fun subirImagenProducto(uri: Uri, context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = System.currentTimeMillis()
                val fileName = "producto_$timestamp.jpg"

                // Obtener bytes de la imagen
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("No se puede abrir el archivo")

                val imageBytes = inputStream.readBytes()
                inputStream.close()

                // Usar FormData multipart
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        fileName,
                        imageBytes.toRequestBody("image/jpeg".toMediaType())
                    )
                    .build()

                val request = Request.Builder()
                    .url("${SupabaseConfig.SUPABASE_URL}/storage/v1/object/$bucketName/$fileName")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer ${SupabaseConfig.SUPABASE_SERVICE_KEY}")
                    .addHeader("apikey", SupabaseConfig.SUPABASE_SERVICE_KEY)
                    .build()

                // Ejecutar request
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        // Generar URL pública
                        val publicUrl = "${SupabaseConfig.STORAGE_BASE_URL}/$fileName"
                        publicUrl
                    } else {
                        Log.w("SupabaseStorage", "Error subiendo imagen de producto: ${response.code}")
                        null
                    }
                }

            } catch (e: Exception) {
                Log.w("SupabaseStorage", "Error subiendo imagen de producto", e)
                null
            }
        }
    }

    /**
     * Elimina una imagen de Supabase Storage
     */
    suspend fun eliminarImagen(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://xjqjlllzbcrpcylhnlmh.supabase.co/storage/v1/object/empleados/$fileName")
                    .delete()
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhqcWpsbGx6YmNycGN5bGhubG1oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.4c5oU47ZA7FYyVwlrVVY6_sL9cLr5pMeEs6E5R4yspE")
                    .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhqcWpsbGx6YmNycGN5bGhubG1oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.4c5oU47ZA7FYyVwlrVVY6_sL9cLr5pMeEs6E5R4yspE")
                    .build()

                client.newCall(request).execute().use { response ->
                    response.isSuccessful
                }

            } catch (e: Exception) {
                false
            }
        }
    }
}