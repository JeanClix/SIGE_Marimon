package org.marimon.sigc.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.marimon.sigc.config.SupabaseCredentials
import java.util.concurrent.TimeUnit

class SupabaseStorageManager {
    
    // Configuración de Supabase
    //private val supabaseUrl = "https://xjqjlllzbcrpcylhnlmh.supabase.co"
    private val supabaseUrl = SupabaseCredentials.SUPABASE_URL
    //private val serviceKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhqcWpsbGx6YmNycGN5bGhubG1oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1ODk0NjgxMSwiZXhwIjoyMDc0NTIyODExfQ.4c5oU47ZA7FYyVwlrVVY6_sL9cLr5pMeEs6E5R4yspE"
    private val serviceKey = SupabaseCredentials.SUPABASE_ANON_KEY

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * Sube una imagen a Supabase Storage
     */
    suspend fun subirImagen(uri: Uri, context: Context): String? {
        return subirImagenGeneral(uri, context, "empleados", "empleado")
    }
    
    /**
     * Sube una imagen de producto a Supabase Storage
     */
    suspend fun subirImagenProducto(uri: Uri, context: Context): String? {
        return subirImagenGeneral(uri, context, "productos-imagenes", "producto")
    }
    
    /**
     * Función privada que maneja la subida de imágenes para cualquier bucket
     */
    private suspend fun subirImagenGeneral(
        uri: Uri, 
        context: Context, 
        bucket: String, 
        prefix: String
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = System.currentTimeMillis()
                val fileName = "${prefix}_$timestamp.jpg"
                
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
                    .url("$supabaseUrl/storage/v1/object/$bucket/$fileName")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer $serviceKey")
                    .addHeader("apikey", serviceKey)
                    .build()
                
                // Ejecutar request
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        // Generar URL pública
                        val publicUrl = "$supabaseUrl/storage/v1/object/public/$bucket/$fileName"
                        publicUrl
                    } else {
                        Log.w("SupabaseStorage", "Error subiendo imagen: ${response.code}")
                        null
                    }
                }
                
            } catch (e: Exception) {
                Log.e("SupabaseStorage", "Error subiendo imagen", e)
                Log.e("SupabaseStorage", "Error details: ${e.message}")
                null
            }
        }
    }

    /**
     * Elimina una imagen de empleados de Supabase Storage
     */
    suspend fun eliminarImagen(fileName: String): Boolean {
        return eliminarImagenGeneral("empleados", fileName)
    }
    
    /**
     * Elimina una imagen de producto de Supabase Storage
     */
    suspend fun eliminarImagenProducto(fileName: String): Boolean {
        return eliminarImagenGeneral("producto", fileName)
    }
    
    /**
     * Función privada que maneja la eliminación de imágenes para cualquier bucket
     */
    private suspend fun eliminarImagenGeneral(bucket: String, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$supabaseUrl/storage/v1/object/$bucket/$fileName")
                    .delete()
                    .addHeader("Authorization", "Bearer $serviceKey")
                    .addHeader("apikey", serviceKey)
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