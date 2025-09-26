# SIGE Marimon - Sistema de Gestión Empresarial

## Estructura del Proyecto

El proyecto está organizado con una arquitectura limpia y modular:

```
composeApp/src/commonMain/kotlin/org/marimon/sigc/
├── data/
│   ├── model/           # Modelos de datos
│   │   ├── User.kt
│   │   ├── LoginRequest.kt
│   │   └── AuthResult.kt
│   └── repository/      # Repositorios de datos
│       └── AuthRepository.kt
├── ui/
│   ├── screens/         # Pantallas de la aplicación
│   │   ├── LoginScreen.kt
│   │   └── HomeScreen.kt
│   └── components/      # Componentes reutilizables
├── navigation/          # Navegación de la aplicación
│   └── AppNavigation.kt
├── viewmodel/          # ViewModels para manejo de estado
│   └── AuthViewModel.kt
└── App.kt              # Punto de entrada principal
```

## Funcionalidades Implementadas

### 🔐 Sistema de Login Simple

- **Pantalla de Login**: Interfaz moderna con Material Design 3
- **Autenticación**: Sistema simple con usuarios predefinidos
- **Validación**: Validación de campos y manejo de errores
- **Estado**: Manejo de estado reactivo con StateFlow

### 👤 Gestión de Usuarios

- **Modelo de Usuario**: Estructura completa con información personal
- **Sesión**: Manejo de sesión de usuario activo
- **Logout**: Funcionalidad de cierre de sesión

### 🎨 Interfaz de Usuario

- **Material Design 3**: Diseño moderno y consistente
- **Responsive**: Adaptable a diferentes tamaños de pantalla
- **Animaciones**: Transiciones suaves entre estados
- **Tema**: Colores y estilos personalizados

## Credenciales de Prueba

### Administrador
- **Usuario**: `admin`
- **Contraseña**: `admin123`

### Usuario Regular
- **Usuario**: `usuario`
- **Contraseña**: `user123`

## Tecnologías Utilizadas

- **Kotlin Multiplatform**: Desarrollo multiplataforma
- **Jetpack Compose**: UI declarativa moderna
- **Material Design 3**: Sistema de diseño de Google
- **StateFlow**: Manejo de estado reactivo
- **Coroutines**: Programación asíncrona

## Arquitectura

El proyecto sigue los principios de **Clean Architecture**:

1. **Data Layer**: Modelos y repositorios
2. **Domain Layer**: Lógica de negocio (ViewModels)
3. **Presentation Layer**: UI y navegación

## Cómo Ejecutar

1. Abre el proyecto en Android Studio o IntelliJ IDEA
2. Sincroniza las dependencias de Gradle
3. Ejecuta la aplicación en tu plataforma preferida:
   - Android: `./gradlew :composeApp:installDebug`
   - iOS: Abre `iosApp/iosApp.xcodeproj` en Xcode
   - Web: `./gradlew :composeApp:jsBrowserDevelopmentRun`

## Próximas Funcionalidades

- [ ] Gestión de empleados
- [ ] Configuración del sistema
- [ ] Base de datos local
- [ ] API REST
- [ ] Notificaciones push
- [ ] Reportes y estadísticas

## Contribución

Para contribuir al proyecto:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request
