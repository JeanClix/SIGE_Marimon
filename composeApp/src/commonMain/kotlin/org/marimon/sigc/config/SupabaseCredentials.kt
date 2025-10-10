package org.marimon.sigc.config

/**
 * Credenciales de Supabase para SIGE Marimon
 * Contiene las claves necesarias para conectarse a Supabase
 */
object SupabaseCredentials {
    
    // URL base de Supabase
    const val SUPABASE_URL = "https://toothspciydsgevyxkol.supabase.co"
    
    // Clave anónima para operaciones públicas
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRvb3Roc3BjaXlkc2dldnl4a29sIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg5MjI4NDksImV4cCI6MjA3NDQ5ODg0OX0.s3NwNuPYJxdwJ-FgXnkBYiNqWO8BtVpXuTVdQ_or5Bo"
    
    // Clave de servicio para operaciones administrativas y storage
    const val SUPABASE_SERVICE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRvb3Roc3BjaXlkc2dldnl4a29sIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3OTMyMjg0OSwiZXhwIjoyMDc0NDk4ODQ5fQ.k5k5V01RszFVjA94NdS_drlrXvG-5m50t7B_hGFQMDY"
    
    // Nombre del bucket para almacenar imágenes de productos
    const val STORAGE_BUCKET = "productos-imagenes"
}
