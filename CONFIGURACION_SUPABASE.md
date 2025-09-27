# 🔗 Configuración de Supabase

## 📋 Pasos para conectar tu proyecto con Supabase

### 1. **Configurar SupabaseConfig.kt**

Edita el archivo `composeApp/src/commonMain/kotlin/org/marimon/sigc/config/SupabaseConfig.kt`:

```kotlin
object SupabaseConfig {
    // Reemplaza con los valores de tu proyecto Supabase
    const val SUPABASE_URL = "https://tu-proyecto-id.supabase.co"
    const val SUPABASE_ANON_KEY = "tu-anon-key-aqui"
    
    const val DATABASE_SCHEMA = "public"
    const val AUTH_SCHEMA = "auth"
}
```

### 2. **Obtener credenciales de Supabase**

1. Ve a tu [Dashboard de Supabase](https://supabase.com/dashboard)
2. Selecciona tu proyecto
3. Ve a **Settings** → **API**
4. Copia:
   - **Project URL** → `SUPABASE_URL`
   - **anon public** key → `SUPABASE_ANON_KEY`

### 3. **Configurar Autenticación en Supabase**

1. Ve a **Authentication** → **Settings**
2. Configura las siguientes opciones:
   - **Site URL**: `http://localhost:3000` (para desarrollo)
   - **Redirect URLs**: `http://localhost:3000/**`

### 4. **Crear usuarios de prueba**

#### Opción A: Desde el Dashboard
1. Ve a **Authentication** → **Users**
2. Haz clic en **Add user**
3. Crea usuarios con email y contraseña

#### Opción B: Registro automático
El sistema permitirá que los usuarios se registren automáticamente si está habilitado.

### 5. **Configurar Base de Datos (Opcional)**

Si quieres almacenar información adicional del usuario:

```sql
-- Crear tabla de perfiles de usuario
CREATE TABLE profiles (
    id UUID REFERENCES auth.users(id) PRIMARY KEY,
    username TEXT UNIQUE,
    first_name TEXT,
    last_name TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Habilitar RLS (Row Level Security)
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;

-- Política para que los usuarios solo vean su propio perfil
CREATE POLICY "Users can view own profile" ON profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" ON profiles
    FOR UPDATE USING (auth.uid() = id);
```

### 6. **Probar la conexión**

1. Ejecuta el proyecto:
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

2. Ve a `http://localhost:3000`
3. Intenta hacer login con las credenciales de Supabase

## 🔧 Solución de Problemas

### Error: "Invalid API key"
- Verifica que copiaste correctamente la API key
- Asegúrate de usar la key "anon public", no la "service_role"

### Error: "Invalid email or password"
- Verifica que el usuario existe en Supabase
- Revisa que el email esté confirmado
- Verifica la contraseña

### Error: "Site URL not allowed"
- Agrega `http://localhost:3000` a las URLs permitidas
- Para producción, agrega tu dominio real

### Error: "Network error"
- Verifica que tu URL de Supabase sea correcta
- Revisa la conexión a internet
- Verifica que el proyecto Supabase esté activo

## 📱 Funcionalidades Implementadas

- ✅ **Login con email/contraseña**
- ✅ **Logout**
- ✅ **Verificación de sesión activa**
- ✅ **Manejo de errores**
- ✅ **Persistencia de sesión**

## 🚀 Próximos Pasos

- [ ] Implementar registro de usuarios
- [ ] Agregar recuperación de contraseña
- [ ] Implementar perfiles de usuario
- [ ] Agregar autenticación social (Google, GitHub)
- [ ] Implementar roles y permisos

---

**¡Tu proyecto ya está conectado con Supabase!** 🎉
