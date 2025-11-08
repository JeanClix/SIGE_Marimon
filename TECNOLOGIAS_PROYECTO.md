# 🚀 SIGE Marimon - Stack Tecnológico Completo
## Sistema Integral de Gestión Empresarial

---

## 📱 **1. ARQUITECTURA DEL PROYECTO**

### **Kotlin Multiplatform (KMP)**
- **Framework Principal**: Kotlin Multiplatform Mobile
- **Versión**: Kotlin 2.0+
- **Plataformas Objetivo**:
  - ✅ Android (Principal)
  - 🔄 iOS (Configurado)
  - 🌐 Web (JS/WASM)

### **Arquitectura MVVM**
- **ViewModels**: Gestión de estado y lógica de negocio
- **Composables**: Interfaz de usuario declarativa
- **Repository Pattern**: Separación de fuentes de datos

---

## 🎨 **2. FRONTEND - INTERFAZ DE USUARIO**

### **Jetpack Compose Multiplatform**
```kotlin
// UI Toolkit Moderno
- compose.runtime
- compose.foundation
- compose.material3
- compose.ui
- compose.components.resources
```

**Características Implementadas:**
- ✅ **UI Declarativa**: Componentes reutilizables y reactivos
- ✅ **Material Design 3**: Diseño moderno y consistente
- ✅ **Navegación**: Sistema de navegación entre pantallas
- ✅ **Estados**: Gestión reactiva de estados con `remember` y `mutableStateOf`
- ✅ **Animaciones**: Transiciones suaves entre vistas

### **Componentes Personalizados**
```kotlin
- TopBar personalizada con gradientes
- BottomNavigationBar con íconos
- Cards con elevación y sombras
- Diálogos modales (Crear/Editar/Eliminar)
- Formularios con validación
```

---

## 🌐 **3. BACKEND - COMUNICACIÓN Y DATOS**

### **Supabase** (Backend as a Service)
```kotlin
// Base de Datos PostgreSQL en la nube
- Autenticación de usuarios
- Base de datos relacional
- Storage para imágenes
- APIs REST automáticas
```

**URL Base**: `https://toothspciydsgevyxkol.supabase.co`

### **Ktor Client** (Cliente HTTP)
```kotlin
dependencies {
    implementation("io.ktor.client:ktor-client-core")
    implementation("io.ktor.client:ktor-client-content-negotiation")
    implementation("io.ktor.client:ktor-client-cio") // Android
}
```

**Características:**
- ✅ Llamadas HTTP asíncronas
- ✅ Serialización JSON automática
- ✅ Manejo de errores
- ✅ Headers personalizados para autenticación

---

## 📊 **4. VISUALIZACIÓN DE DATOS**

### **A. Gráficos Nativos (Canvas de Compose)**

**Gráfico de Barras:**
```kotlin
// Implementación con Canvas nativo
- Box + fillMaxHeight() para barras
- Colores dinámicos según datos
- Etiquetas interactivas
- Responsive al tamaño de pantalla
```

**Gráfico Circular (Pie Chart):**
```kotlin
// Implementación con drawArc()
- Canvas nativo de Compose
- Segmentos coloreados
- Efecto de dona (donut chart)
- Leyendas con porcentajes y conteos
```

**Ventajas:**
- ✅ Sin dependencias externas
- ✅ Totalmente personalizable
- ✅ Compatible con KMP
- ✅ Ligero y rápido

### **B. Dashboards Embebidos (WebView)**

**Power BI Dashboard:**
```kotlin
// Dashboard KPI embebido
- WebView de Android
- Configuración para vista móvil
- JavaScript injection para ajustes
- User Agent personalizado (iPhone)
```

**Streamlit Dashboard:**
```kotlin
// Dashboard de Ventas
- Aplicación Python embebida
- Visualizaciones interactivas
- Análisis en tiempo real
```

---

## 🔔 **5. NOTIFICACIONES PUSH**

### **Android Notification System**
```kotlin
// Sistema nativo de notificaciones
- NotificationManager
- NotificationChannel (Android 13+)
- NotificationCompat.Builder
```

**Características Implementadas:**
- ✅ **Alertas de Stock Bajo**: Notificación cuando productos ≤ 10 unidades
- ✅ **Notificaciones Individuales**: Para un producto
- ✅ **Notificaciones Grupales**: Para múltiples productos
- ✅ **Prioridad Alta**: Con vibración
- ✅ **Permisos Runtime**: Solicitud de permisos en Android 13+
- ✅ **Deep Links**: Abrir app al tocar notificación

---

## 📷 **6. GESTIÓN DE IMÁGENES**

### **Coil** (Image Loading)
```kotlin
implementation("io.coil-kt:coil-compose:2.4.0")
```

**Funcionalidades:**
- ✅ Carga asíncrona de imágenes
- ✅ Caché automático
- ✅ Transformaciones (crop, resize)
- ✅ Placeholders durante carga
- ✅ Soporte para URLs remotas

### **Supabase Storage**
- ✅ Almacenamiento de imágenes de productos
- ✅ URLs públicas para acceso
- ✅ Gestión de uploads desde la app

---

## 📄 **7. GENERACIÓN DE PDFs**

### **iText7**
```kotlin
implementation("com.itextpdf:itext7-core:7.2.5")
```

**Características:**
- ✅ Generación dinámica de reportes
- ✅ Tablas con datos de productos
- ✅ Estilos personalizados
- ✅ Headers y footers
- ✅ Descarga directa en dispositivo

---

## 📧 **8. SISTEMA DE EMAIL**

### **JavaMail API**
```kotlin
implementation("com.sun.mail:android-mail:1.6.7")
implementation("com.sun.mail:android-activation:1.6.7")
```

**Funcionalidades:**
- ✅ Envío de emails desde la app
- ✅ Adjuntar PDFs generados
- ✅ Recuperación de contraseñas
- ✅ Configuración SMTP (Gmail)

---

## 🔐 **9. AUTENTICACIÓN Y SEGURIDAD**

### **Sistema de Login**
```kotlin
// Autenticación con Supabase
- Login con email y contraseña
- Validación de credenciales
- Sesiones persistentes
- Recuperación de contraseña
```

**Seguridad Implementada:**
- ✅ Encriptación HTTPS
- ✅ Tokens de autenticación
- ✅ Validación de inputs
- ✅ Manejo seguro de credenciales

---

## 🗄️ **10. GESTIÓN DE DATOS**

### **Modelos de Datos**

**Producto:**
```kotlin
data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val especificaciones: String?,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String?,
    val activo: Boolean
)
```

**Empleado:**
```kotlin
data class Empleado(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val telefono: String,
    val cargo: String,
    val salario: Double,
    val fechaContratacion: String,
    val activo: Boolean
)
```

### **Operaciones CRUD**
- ✅ **Create**: Crear nuevos registros
- ✅ **Read**: Listar y buscar
- ✅ **Update**: Editar información
- ✅ **Delete**: Soft delete (marcar como inactivo)

---

## 📱 **11. FUNCIONALIDADES PRINCIPALES**

### **A. Gestión de Productos**
1. ✅ Registro de productos con imagen
2. ✅ Edición de información
3. ✅ Control de stock
4. ✅ Activar/Desactivar productos
5. ✅ Lista scrolleable con búsqueda
6. ✅ Visualización con cards

### **B. Reportes y Análisis**
1. ✅ **Reporte de Productos**:
   - Gráfico de barras (top 8 productos)
   - Gráfico circular (activos vs inactivos)
   - Estadísticas en tiempo real
   - Filtro por mayor/menor stock
   - Producto destacado
   - Botón de actualizar

2. ✅ **Dashboard KPI**:
   - Power BI embebido
   - Métricas empresariales
   - Vista responsiva

3. ✅ **Dashboard de Ventas**:
   - Streamlit embebido
   - Análisis de ventas
   - Gráficos interactivos

### **C. Notificaciones Inteligentes**
1. ✅ Detección automática de stock bajo
2. ✅ Alertas en tiempo real
3. ✅ Verificación después de cada cambio
4. ✅ Botón manual de prueba
5. ✅ Logs detallados para debugging

### **D. Gestión de Empleados**
1. ✅ Registro de empleados
2. ✅ Edición de información
3. ✅ Control de estado (activo/inactivo)
4. ✅ Visualización de datos

---

## 🎨 **12. DISEÑO Y UX**

### **Material Design 3**
- ✅ Paleta de colores consistente
- ✅ Tipografía Material
- ✅ Componentes elevados (elevation)
- ✅ Bordes redondeados
- ✅ Sombras y gradientes

### **Colores Principales**
```kotlin
Primary: Color(0xFFE53935)    // Rojo
Secondary: Color(0xFF2196F3)  // Azul
Success: Color(0xFF4CAF50)    // Verde
Warning: Color(0xFFFF9800)    // Naranja
```

### **Características UX**
- ✅ Feedback visual inmediato
- ✅ Mensajes de éxito/error
- ✅ Loading states con spinners
- ✅ Confirmación de acciones destructivas
- ✅ Auto-dismiss de mensajes (3 segundos)

---

## 🔧 **13. HERRAMIENTAS DE DESARROLLO**

### **IDE y Build Tools**
```
- Android Studio Hedgehog+
- Gradle 8.5
- Kotlin Gradle Plugin
- Compose Compiler Plugin
```

### **Control de Versiones**
```
- Git
- GitHub (repositorio)
```

### **Testing y Debugging**
```
- Logcat para logs
- Preview en Compose
- Emulador Android (API 36)
```

---

## 📦 **14. DEPENDENCIAS COMPLETAS**

### **Android Main**
```kotlin
// UI
androidx.activity.compose:1.8.0
navigation-compose:2.7.7

// Networking
ktor-client-cio
okhttp3:4.11.0

// Images
coil-compose:2.4.0

// PDF
itext7-core:7.2.5

// Email
android-mail:1.6.7
```

### **Common Main**
```kotlin
// Compose Multiplatform
compose.runtime
compose.foundation
compose.material3
compose.ui

// Networking
ktor-client-core
ktor-client-content-negotiation
ktor-serialization-kotlinx-json

// Lifecycle
lifecycle-viewmodel-compose
lifecycle-runtime-compose
```

---

## 🚀 **15. CARACTERÍSTICAS TÉCNICAS DESTACADAS**

### **A. Kotlin Multiplatform**
- ✅ Código compartido entre plataformas
- ✅ Lógica de negocio unificada
- ✅ Separación clara: `commonMain` / `androidMain`

### **B. Compose Moderno**
- ✅ UI declarativa (menos código)
- ✅ Recomposición inteligente
- ✅ Estados inmutables
- ✅ Preview en tiempo real

### **C. Arquitectura Reactiva**
```kotlin
// Estados reactivos
var productos by remember { mutableStateOf(emptyList()) }

// LaunchedEffect para operaciones asíncronas
LaunchedEffect(Unit) {
    viewModel.cargarProductos()
}
```

### **D. Inyección de JavaScript**
```kotlin
webView.evaluateJavascript("""
    // Modificar comportamiento del sitio embebido
    document.querySelector('meta[name=viewport]')
        .content = 'width=400';
""", null)
```

---

## 📊 **16. MÉTRICAS DEL PROYECTO**

### **Líneas de Código**
```
Total: ~5,000 líneas
- Kotlin: 4,500 líneas
- XML: 500 líneas
```

### **Archivos Principales**
```
- 15+ Composables principales
- 10+ ViewModels
- 8+ Data Models
- 5+ Services/Helpers
```

### **Pantallas Implementadas**
```
1. Login
2. Home/Dashboard
3. Gestión de Productos
4. Gestión de Empleados
5. Reportes de Productos
6. Dashboard KPI
7. Dashboard de Ventas
8. Recuperación de Contraseña
```

---

## 🎯 **17. VENTAJAS COMPETITIVAS**

### **Tecnológicas**
1. ✅ **Kotlin Multiplatform**: Código reutilizable
2. ✅ **Jetpack Compose**: UI moderna y mantenible
3. ✅ **Supabase**: Backend sin servidor
4. ✅ **Canvas Nativo**: Gráficos sin dependencias
5. ✅ **Notificaciones Inteligentes**: Gestión proactiva

### **Funcionales**
1. ✅ **Gestión Completa**: Productos y empleados
2. ✅ **Reportes Visuales**: Análisis en tiempo real
3. ✅ **Notificaciones**: Alertas automáticas
4. ✅ **Multiplataforma**: Preparado para iOS/Web
5. ✅ **Offline-Ready**: Arquitectura preparada para caché

---

## 🔮 **18. POSIBLES MEJORAS FUTURAS**

### **Corto Plazo**
- [ ] Modo offline con Room Database
- [ ] Sincronización automática
- [ ] Búsqueda avanzada con filtros
- [ ] Exportar reportes a Excel

### **Mediano Plazo**
- [ ] Autenticación biométrica
- [ ] Chat entre usuarios
- [ ] Historial de cambios
- [ ] Dashboard personalizable

### **Largo Plazo**
- [ ] Inteligencia Artificial (predicción de stock)
- [ ] Integración con sistemas externos
- [ ] App para iOS nativa
- [ ] Version Web completa

---

## 📚 **19. DOCUMENTACIÓN Y RECURSOS**

### **Documentos del Proyecto**
```
✅ README.md (Descripción general)
✅ GUIA_ICONO_APP.md (Iconos de la app)
✅ TECNOLOGIAS_PROYECTO.md (Este documento)
```

### **Configuraciones**
```
✅ AndroidManifest.xml (Permisos y configuración)
✅ build.gradle.kts (Dependencias)
✅ local.properties (Credenciales locales)
```

---

## 🎓 **20. CONCLUSIÓN**

### **Stack Tecnológico Resumen**

| Categoría | Tecnología |
|-----------|-----------|
| **Lenguaje** | Kotlin |
| **Framework** | Kotlin Multiplatform |
| **UI** | Jetpack Compose |
| **Backend** | Supabase (PostgreSQL) |
| **Networking** | Ktor Client |
| **Imágenes** | Coil |
| **PDF** | iText7 |
| **Email** | JavaMail |
| **Notificaciones** | Android Native |
| **Gráficos** | Canvas (Compose nativo) |
| **Dashboards** | Power BI + Streamlit |

### **Logros Técnicos**
1. ✅ App Android funcional y moderna
2. ✅ Integración completa con backend
3. ✅ Sistema de notificaciones inteligente
4. ✅ Reportes visuales personalizados
5. ✅ Arquitectura escalable y mantenible

---

## 🏛️ **21. ARQUITECTURA DE LA APLICACIÓN**

### **Patrón MVVM (Model-View-ViewModel)**

```
┌─────────────────────────────────────────────────────────┐
│                         VIEW                             │
│              (Jetpack Compose UI)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │ LoginScreen│ │ProductList│ │ Reports  │             │
│  └──────────┘  └──────────┘  └──────────┘             │
└─────────────────────────────────────────────────────────┘
                        ↕ (observa estados)
┌─────────────────────────────────────────────────────────┐
│                      VIEWMODEL                           │
│          (Lógica de presentación)                        │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │ ProductoVM   │  │ EmpleadoVM   │                    │
│  │              │  │              │                    │
│  │ - productos  │  │ - empleados  │                    │
│  │ - cargar()   │  │ - cargar()   │                    │
│  │ - crear()    │  │ - crear()    │                    │
│  └──────────────┘  └──────────────┘                    │
└─────────────────────────────────────────────────────────┘
                        ↕ (llama funciones)
┌─────────────────────────────────────────────────────────┐
│                       MODEL                              │
│                 (Datos y lógica)                         │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │ Producto     │  │ Empleado     │                    │
│  │ data class   │  │ data class   │                    │
│  └──────────────┘  └──────────────┘                    │
└─────────────────────────────────────────────────────────┘
                        ↕ (HTTP requests)
┌─────────────────────────────────────────────────────────┐
│                    REMOTE DATA                           │
│                   (Supabase API)                         │
│               PostgreSQL Database                         │
└─────────────────────────────────────────────────────────┘
```

### **Flujo de Datos**

```kotlin
// 1. USER INTERACTION (View)
Button(onClick = { 
    viewModel.cargarProductos() 
})

// 2. VIEWMODEL procesa
class ProductoViewModel : ViewModel() {
    val productos = mutableStateListOf<Producto>()
    
    fun cargarProductos() {
        viewModelScope.launch {
            // 3. Llama al backend
            val response = httpClient.get(SUPABASE_URL)
            // 4. Actualiza estado
            productos.clear()
            productos.addAll(response)
        }
    }
}

// 5. VIEW reacciona automáticamente
@Composable
fun ProductList(viewModel: ProductoViewModel) {
    val productos = viewModel.productos
    // UI se actualiza automáticamente
}
```

---

## 🗂️ **22. ESTRUCTURA DEL PROYECTO**

### **Organización de Carpetas (Kotlin Multiplatform)**

```
SIGE_Marimon/
│
├── composeApp/
│   ├── src/
│   │   ├── commonMain/          ← Código compartido
│   │   │   └── kotlin/
│   │   │       └── org.marimon.sigc/
│   │   │           ├── model/           # Modelos de datos
│   │   │           │   ├── Producto.kt
│   │   │           │   ├── Empleado.kt
│   │   │           │   └── Usuario.kt
│   │   │           │
│   │   │           ├── viewmodel/       # ViewModels
│   │   │           │   ├── ProductoViewModel.kt
│   │   │           │   └── EmpleadoViewModel.kt
│   │   │           │
│   │   │           └── config/          # Configuración
│   │   │               ├── SupabaseClient.kt
│   │   │               └── SupabaseConfig.kt
│   │   │
│   │   └── androidMain/         ← Código específico Android
│   │       └── kotlin/
│   │           └── org.marimon.sigc/
│   │               ├── ui/              # Pantallas principales
│   │               │   ├── LoginActivity.kt
│   │               │   ├── MainActivity.kt
│   │               │   └── screens/
│   │               │       └── LoginScreen.kt
│   │               │
│   │               ├── Producto/        # Módulo Productos
│   │               │   ├── AdminRProducto.kt
│   │               │   ├── CrearProducto.kt
│   │               │   ├── EditarProducto.kt
│   │               │   └── ReporteProductos.kt
│   │               │
│   │               ├── Empleado/        # Módulo Empleados
│   │               │   ├── AdminREmpleado.kt
│   │               │   ├── CEmpleado.kt
│   │               │   └── EEmpleado.kt
│   │               │
│   │               ├── services/        # Servicios
│   │               │   ├── NotificationHelper.kt
│   │               │   ├── EmailService.kt
│   │               │   └── PDFService.kt
│   │               │
│   │               ├── DashboardKPI.kt
│   │               ├── DashboardVentas.kt
│   │               ├── Navigation.kt
│   │               └── SharedComponents.kt
│   │
│   └── build.gradle.kts            # Dependencias
│
├── shared/                         # Módulo compartido
│
└── gradle/
```

---

## 🎯 **23. PATRONES DE DISEÑO UTILIZADOS**

### **1. MVVM (Model-View-ViewModel)**
```kotlin
// Separación clara de responsabilidades

MODEL         → Define la estructura de datos
VIEWMODEL     → Maneja lógica y estados
VIEW          → Solo UI, no lógica de negocio
```

**Ventajas:**
- ✅ Código testeable
- ✅ Separación de responsabilidades
- ✅ Fácil mantenimiento

### **2. Repository Pattern**
```kotlin
// ViewModel NO accede directamente a la base de datos
// Lo hace a través de funciones específicas

class ProductoViewModel {
    fun cargarProductos() {
        // Abstracción del acceso a datos
        httpClient.get(url)
    }
}
```

### **3. Observer Pattern**
```kotlin
// Estados observables con Compose

var productos by remember { mutableStateOf(emptyList()) }

// La UI se actualiza automáticamente cuando cambia
```

### **4. Singleton Pattern**
```kotlin
// Cliente HTTP único en toda la app

object SupabaseClient {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
}
```

### **5. Factory Pattern**
```kotlin
// Crear ViewModels con remember

@Composable
fun ProductoScreen() {
    val viewModel = remember { ProductoViewModel() }
}
```

---

## 🔄 **24. CICLO DE VIDA DE UNA PANTALLA**

### **Ejemplo: Gestión de Productos**

```kotlin
@Composable
fun AdminRProductoApp(
    currentRoute: String = "circulo",
    onNavigate: (String) -> Unit = {}
) {
    // 1. INICIALIZACIÓN
    val productoViewModel = remember { ProductoViewModel() }
    
    // 2. CARGA INICIAL
    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }
    
    // 3. OBSERVACIÓN DE DATOS
    val productos: List<Producto> = productoViewModel.productos
    
    // 4. RENDERIZADO
    AdminScreenLayout(
        title = "Registro de Productos",
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) {
        // 5. COMPONENTES HIJOS
        ProductoListScreen(
            productos = productos,
            productoViewModel = productoViewModel
        )
    }
}
```

### **Flujo Detallado:**

```
1. Usuario abre pantalla
   ↓
2. Composable se inicializa
   ↓
3. remember{} crea ViewModel
   ↓
4. LaunchedEffect dispara carga de datos
   ↓
5. ViewModel hace request a Supabase
   ↓
6. Datos llegan y actualizan mutableStateListOf
   ↓
7. Compose detecta cambio de estado
   ↓
8. UI se recompone automáticamente
   ↓
9. Usuario ve los datos
```

---

## 🧩 **25. COMPONENTES REUTILIZABLES**

### **Estructura de Componentes**

```
SharedComponents.kt  ← Componentes base
│
├── TopBar()            # Barra superior personalizada
├── CustomBottomNavBar() # Navegación inferior
├── AdminScreenLayout() # Layout estándar
└── LoadingSpinner()    # Indicador de carga
```

### **Ejemplo de Componente Reutilizable:**

```kotlin
@Composable
fun AdminScreenLayout(
    title: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit  // Slot API
) {
    Scaffold(
        topBar = { TopBar(title = title) },
        bottomBar = { 
            CustomBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            ) 
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()  // Contenido dinámico
        }
    }
}
```

**Ventaja:**
- ✅ Escribes una vez, usas en todas las pantallas
- ✅ Cambios centralizados
- ✅ Consistencia visual

---

## 🔐 **26. MANEJO DE ESTADO**

### **Estados en Compose**

```kotlin
// 1. ESTADO LOCAL (Simple)
var showDialog by remember { mutableStateOf(false) }

// 2. ESTADO OBSERVABLE (Lista)
val productos = mutableStateListOf<Producto>()

// 3. ESTADO DERIVADO (Calculado)
val productosActivos = remember(productos) {
    productos.filter { it.activo }
}

// 4. ESTADO CON EFECTO (Asíncrono)
LaunchedEffect(productos.size) {
    // Se ejecuta cuando cambia el tamaño
    verificarStockBajo(productos)
}
```

### **Flujo de Estados:**

```
User Action → Estado cambia → Recomposición → UI actualizada
    ↓              ↓               ↓              ↓
 onClick()    mutableStateOf   Compose detecta   Nueva UI
```

---

## 🌐 **27. CAPA DE NETWORKING**

### **Arquitectura de Red**

```
App Layer
    ↓
ViewModel Layer
    ↓
HTTP Client (Ktor)
    ↓
Supabase API
    ↓
PostgreSQL Database
```

### **Configuración del Cliente:**

```kotlin
// SupabaseClient.kt (Singleton)
object SupabaseClient {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        install(Logging) {
            level = LogLevel.INFO
        }
    }
}
```

### **Request típico:**

```kotlin
// GET con headers
val response: HttpResponse = httpClient.get(url) {
    header("apikey", SUPABASE_ANON_KEY)
    header("Authorization", "Bearer $SUPABASE_ANON_KEY")
}

// POST con body
httpClient.post(url) {
    headers {
        append("apikey", SUPABASE_ANON_KEY)
        append("Content-Type", "application/json")
    }
    setBody(producto)
}
```

---

## 📱 **28. NAVEGACIÓN ENTRE PANTALLAS**

### **Sistema de Rutas**

```kotlin
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val PRODUCTOS = "circulo"
    const val EMPLEADOS = "r_empleado"
    const val VENTAS = "ventas"
    const val REPORTES = "reportes"
    const val DASHBOARD_KPI = "grafico"
}
```

### **Navegación con BottomBar:**

```kotlin
@Composable
fun CustomBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { onNavigate(Routes.HOME) },
            icon = { Icon(...) },
            label = { Text("Home") }
        )
        // ... más items
    }
}
```

### **Flujo de Navegación:**

```
Login Screen
    ↓ (credenciales correctas)
Home Screen
    ↓ (click en nav item)
    ├── Productos
    ├── Empleados
    ├── Reportes
    └── Dashboards
```

---

## 🎨 **29. THEMING Y ESTILOS**

### **Sistema de Diseño**

```kotlin
// Colores Consistentes
object AppColors {
    val Primary = Color(0xFFE53935)      // Rojo
    val Secondary = Color(0xFF2196F3)    // Azul
    val Success = Color(0xFF4CAF50)      // Verde
    val Warning = Color(0xFFFF9800)      // Naranja
    val Error = Color(0xFFF44336)        // Rojo Error
    val Background = Color(0xFFF5F5F5)   // Gris claro
}

// Shapes Reutilizables
object AppShapes {
    val roundedCornerTop = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 32.dp,
        bottomEnd = 32.dp
    )
}

// Espaciados Consistentes
object AppSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val xlarge = 32.dp
}
```

---

## 🔄 **30. GESTIÓN DE ERRORES**

### **Manejo de Excepciones**

```kotlin
fun cargarProductos() {
    viewModelScope.launch {
        try {
            // Intenta cargar datos
            val response = httpClient.get(url)
            productos.clear()
            productos.addAll(parseResponse(response))
            
        } catch (e: Exception) {
            // Maneja errores
            when (e) {
                is UnknownHostException -> {
                    showError("Sin conexión a internet")
                }
                is HttpRequestTimeoutException -> {
                    showError("Tiempo de espera agotado")
                }
                else -> {
                    showError("Error: ${e.message}")
                    println("❌ Error cargando: ${e.message}")
                }
            }
        }
    }
}
```

### **Feedback Visual:**

```kotlin
// Estados de UI
var isLoading by remember { mutableStateOf(false) }
var errorMessage by remember { mutableStateOf<String?>(null) }

if (isLoading) {
    CircularProgressIndicator()
}

errorMessage?.let { error ->
    AlertDialog(
        title = { Text("Error") },
        text = { Text(error) },
        onDismissRequest = { errorMessage = null }
    )
}
```

---

## 👨‍💻 **Desarrollado con:**
- 💻 Android Studio
- 🎨 Jetpack Compose
- 🚀 Kotlin Multiplatform
- ☁️ Supabase
- 📊 Canvas (Gráficos nativos)
- 🏛️ Arquitectura MVVM
- 🧩 Componentes Reutilizables
- 🔄 Estados Reactivos

**¡Sistema completo y funcional para gestión empresarial!** 🎉

