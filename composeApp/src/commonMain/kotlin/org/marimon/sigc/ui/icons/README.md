# 🎨 Iconos SIGE Marimon

## 📁 Ubicación de iconos
```
composeApp/src/commonMain/kotlin/org/marimon/sigc/ui/icons/
```

## 🎯 Tipos de iconos disponibles

### **Actualmente implementados (Emojis temporales):**

#### **Acciones:**
- `MarimonIcons.Edit` - ✏️ (Editar)
- `MarimonIcons.Delete` - 🗑️ (Eliminar)
- `MarimonIcons.Add` - ➕ (Agregar)
- `MarimonIcons.Search` - 🔍 (Buscar)

#### **Navegación:**
- `MarimonIcons.Back` - ◀ (Volver)
- `MarimonIcons.Forward` - ▶ (Avanzar)
- `MarimonIcons.Home` - 🏠 (Inicio)

#### **Productos específicos:**
- `MarimonIcons.Oil` - 🛢️ (Aceite)
- `MarimonIcons.Tire` - 🛞 (Llanta)
- `MarimonIcons.Filter` - 🔧 (Filtro)
- `MarimonIcons.Brake` - ⚙️ (Freno)
- `MarimonIcons.Engine` - 🔩 (Motor)
- `MarimonIcons.Battery` - 🔋 (Batería)
- `MarimonIcons.Light` - 💡 (Luz)

#### **Herramientas:**
- `MarimonIcons.Tools` - 🔧 (Herramientas)
- `MarimonIcons.Car` - 🚗 (Auto)
- `MarimonIcons.Users` - 👥 (Usuarios)
- `MarimonIcons.Clipboard` - 📋 (Portapapeles)
- `MarimonIcons.Notification` - 🔔 (Notificación)
- `MarimonIcons.Camera` - 📷 (Cámara)

## 🔧 Cómo usar

```kotlin
// Importar los iconos
import org.marimon.sigc.ui.icons.MarimonIcons

// Usar en un composable
Text(
    text = MarimonIcons.Edit,
    fontSize = 16.sp
)

// O combinar con texto
Text(
    text = "${MarimonIcons.Back} Volver",
    fontSize = 16.sp
)
```

## 📱 Próximos pasos (Futuro)

### **Opción 1: SVG Vector Drawables**
```kotlin
val Edit: ImageVector
val Delete: ImageVector
```
- Más profesionales
- Escalables sin pérdida
- Colores personalizables

### **Opción 2: PNG Assets**
```
drawable/
├── icon_edit.png
├── icon_delete.png
└── icon_add.png
```
- Compatible universalmente
- Múltiples densidades

### **Opción 3: Iconos vectoriales personalizados**
```kotlin
fun CreateSVGIcon(pathData: String): ImageVector
```
- Máxima personalización
- Diseño específico de Marimon

## 🎨 Colores Marimon aplicables
- Rojo: #FF0000
- Negro: #000000  
- Blanco: #FFFFFF

## ✅ Beneficios actuales
- ✅ Centralización de iconos
- ✅ Fácil mantenimiento
- ✅ Consistencia visual
- ✅ Compatible multiplataforma
- ✅ Sin dependencias externas
