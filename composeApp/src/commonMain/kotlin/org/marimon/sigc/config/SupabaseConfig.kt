package org.marimon.sigc.config

object SupabaseConfig {
    // Configuración desde SupabaseCredentials
    const val SUPABASE_URL = SupabaseCredentials.SUPABASE_URL
    const val SUPABASE_ANON_KEY = SupabaseCredentials.SUPABASE_ANON_KEY
    const val SUPABASE_SERVICE_KEY = SupabaseCredentials.SUPABASE_SERVICE_KEY
    
    // Storage configuration
    const val STORAGE_BUCKET = SupabaseCredentials.STORAGE_BUCKET
    const val STORAGE_BASE_URL = "$SUPABASE_URL/storage/v1/object/public/$STORAGE_BUCKET"
    
    // Configuración adicional
    const val DATABASE_SCHEMA = "public"
    const val AUTH_SCHEMA = "auth"
}
