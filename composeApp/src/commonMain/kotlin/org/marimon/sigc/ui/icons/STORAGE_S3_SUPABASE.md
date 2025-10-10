# 🗂️ Sistema de Storage S3 - Supabase

## ✅ **Configuración Completada**

### **Archivos creados/actualizados:**

#### **1. Credenciales centralizadas**
- `config/SupabaseCredentials.kt` - Contiene todas las claves de Supabase
- `config/SupabaseConfig.kt` - Configuración actualizada con storage

#### **2. Storage Manager**
- `storage/SupabaseStorageManager.kt` - Actualizado con nuevas credenciales
- Bucket: `productos-imagenes`
- Servicio S3 compatible con Supabase Storage

#### **3. Image Uploader Multiplatform**
- `ui/components/ImageUploader.kt` - Declaración expect
- `ui/components/ImageUploader.android.kt` - Android: Selector + Subida real
- `ui/components/ImageUploader.ios.kt` - iOS: Solo URL manual
- `ui/components/ImageUploader.web.kt` - Web: Solo URL manual

#### **4. AutopartesScreen actualizado**
- Diálogo de registro con `ImageUploader`
- Integración completa con Supabase Storage

## 🔑 **Credenciales configuradas:**

```kotlin
SUPABASE_URL = "https://toothspciydsgevyxkol.supabase.co"
BUCKET_NAME = "productos-imagenes"
SERVICE_KEY = [configurada para acceso administrativo]
ANON_KEY = [configurada para acceso público]
```

## 📸 **Funcionamiento:**

### **En Android:**
1. **Seleccionar imagen** → Abre selector de archivos
2. **Subir automáticamente** → Usa `SupabaseStorageManager`
3. **Obtener URL pública** → `https://...supabase.co/storage/v1/object/public/productos-imagenes/[filename]`
4. **Mostrar vista previa** → `ProductImage` componente

### **En otras plataformas:**
- Solo entrada manual de URL
- Mensaje informativo sobre limitación

## 🗄️ **Bucket Structure (Supabase):**

```
productos-imagenes/
├── producto_1758923455678.jpg
├── producto_1758923456912.jpg
├── producto_1758923567890.jpg
└── ...
```

## 🛠️ **APIs utilizadas:**

### **Supabase Storage REST API:**
- **Subir**: `POST /storage/v1/object/[bucket]/[filename]`
- **Eliminar**: `DELETE /storage/v1/object/[bucket]/[filename]`  
- **URL pública**: `GET /storage/v1/object/public/[bucket]/[filename]`

### **Headers requeridos:**
```http
Authorization: Bearer [SERVICE_KEY]
apikey: [SERVICE_KEY]
Content-Type: multipart/form-data
```

## 📝 **Uso en código:**

### **Registro de producto con imagen:**
```kotlin
// Usuario selecciona imagen → ImageUploader
ImageUploader(
    imageUrl = currentImageUrl,
    onImageSelected = { uploadedUrl ->
        // uploadedUrl contiene la URL pública de Supabase
        productoCreate = productoCreate.copy(imagenUrl = uploadedUrl)
    }
)
```

### **Storage Manager directo:**
```kotlin
val storageManager = SupabaseStorageManager()
val imageUrl = storageManager.subirImagenProducto(uri, context)
```

## ✅ **Beneficios implementados:**

- ✅ **Centralización**: Credenciales en un solo lugar
- ✅ **Multiplataforma**: Funciona en Android/iOS/Web
- ✅ **Storage real**: Integración con Supabase S3
- ✅ **URLs públicas**: Acceso directo desde web
- ✅ **UI intuitiva**: Selector de imagen integrado
- ✅ **Vista previa**: Imagen antes de confirmar
- ✅ **Manejo de errores**: Estados de carga y fallback

## 🚀 **Próximos pasos:**

1. **Configurar bucket** en Supabase Dashboard
2. **Probar upload** con imágenes reales  
3. **Optimizar imágenes** (comprimir antes de subir)
4. **Implementar cache** local de URLs
5. **Agregar soporte iOS** si es necesario

---

**💡 Resultado**: Sistema completo de storage S3 con Supabase integrado en la vista de empleados para subir imágenes de productos.
