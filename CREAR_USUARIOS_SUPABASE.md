# 👤 Crear Usuarios en Supabase

## 📋 Pasos para crear usuarios de prueba

### **Opción 1: Desde el Dashboard de Supabase**

1. **Accede a tu proyecto**:
   - Ve a [supabase.com/dashboard](https://supabase.com/dashboard)
   - Selecciona tu proyecto `toothspciydsgevyxkol`

2. **Crear usuario**:
   - Ve a **Authentication** → **Users**
   - Haz clic en **Add user**
   - Completa los campos:
     - **Email**: `admin@sige.com`
     - **Password**: `admin123`
     - **Confirm password**: `admin123`
   - Haz clic en **Create user**

3. **Crear segundo usuario**:
   - Repite el proceso con:
     - **Email**: `usuario@sige.com`
     - **Password**: `user123`
     - **Confirm password**: `user123`

### **Opción 2: Usando SQL (Avanzado)**

Si prefieres usar SQL, puedes ejecutar esto en el **SQL Editor**:

```sql
-- Crear usuarios directamente en la tabla auth.users
-- NOTA: Esto requiere permisos especiales y no es recomendado para producción

-- Usuario admin
INSERT INTO auth.users (
    instance_id,
    id,
    aud,
    role,
    email,
    encrypted_password,
    email_confirmed_at,
    created_at,
    updated_at,
    confirmation_token,
    email_change,
    email_change_token_new,
    recovery_token
) VALUES (
    '00000000-0000-0000-0000-000000000000',
    gen_random_uuid(),
    'authenticated',
    'authenticated',
    'admin@sige.com',
    crypt('admin123', gen_salt('bf')),
    NOW(),
    NOW(),
    NOW(),
    '',
    '',
    '',
    ''
);

-- Usuario normal
INSERT INTO auth.users (
    instance_id,
    id,
    aud,
    role,
    email,
    encrypted_password,
    email_confirmed_at,
    created_at,
    updated_at,
    confirmation_token,
    email_change,
    email_change_token_new,
    recovery_token
) VALUES (
    '00000000-0000-0000-0000-000000000000',
    gen_random_uuid(),
    'authenticated',
    'authenticated',
    'usuario@sige.com',
    crypt('user123', gen_salt('bf')),
    NOW(),
    NOW(),
    NOW(),
    '',
    '',
    '',
    ''
);
```

### **Opción 3: Habilitar Registro Automático**

Si quieres que los usuarios puedan registrarse automáticamente:

1. Ve a **Authentication** → **Settings**
2. En **User Signups**, habilita **Enable email confirmations**
3. O deshabilita las confirmaciones si quieres registro inmediato

## 🔧 Configuración Adicional

### **Configurar URLs Permitidas**

1. Ve a **Authentication** → **Settings**
2. En **Site URL**, agrega: `http://localhost:3000`
3. En **Redirect URLs**, agrega: `http://localhost:3000/**`

### **Configurar Políticas de Seguridad (RLS)**

Si quieres crear una tabla de perfiles adicional:

```sql
-- Crear tabla de perfiles
CREATE TABLE profiles (
    id UUID REFERENCES auth.users(id) PRIMARY KEY,
    username TEXT UNIQUE,
    first_name TEXT,
    last_name TEXT,
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Habilitar RLS
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad
CREATE POLICY "Users can view own profile" ON profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" ON profiles
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile" ON profiles
    FOR INSERT WITH CHECK (auth.uid() = id);
```

## 🧪 Probar la Conexión

Una vez creados los usuarios:

1. **Ejecuta el proyecto**:
   ```bash
   ./gradlew :composeApp:jsBrowserDevelopmentRun
   ```

2. **Abre el navegador**:
   - Ve a `http://localhost:3000`

3. **Prueba el login**:
   - **Admin**: `admin@sige.com` / `admin123`
   - **Usuario**: `usuario@sige.com` / `user123`

## 🚨 Solución de Problemas

### **Error: "Invalid email or password"**
- Verifica que el usuario existe en Supabase
- Revisa que el email esté confirmado
- Verifica la contraseña

### **Error: "Site URL not allowed"**
- Agrega `http://localhost:3000` a las URLs permitidas
- Verifica la configuración en Authentication → Settings

### **Error: "Network error"**
- Verifica que tu URL de Supabase sea correcta
- Revisa la conexión a internet
- Verifica que el proyecto Supabase esté activo

---

**¡Una vez creados los usuarios, tu sistema de autenticación estará completamente funcional!** 🎉
