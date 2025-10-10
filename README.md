# 🏢 SIGE Marimon - Sistema de Gestión Empresarial

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blue.svg)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.9.0-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Un sistema de gestión empresarial moderno desarrollado con **Kotlin Multiplatform** y **Jetpack Compose**, diseñado para ser multiplataforma y escalable.

## ✨ Características

- 🔐 **Sistema de Login** con autenticación segura
- 📱 **Multiplataforma**: Web, Android, iOS, Desktop
- 🎨 **UI Moderna** con Material Design 3
- 🏗️ **Arquitectura Limpia** con MVVM
- ⚡ **Hot Reload** para desarrollo rápido
- 🔄 **Estado Reactivo** con StateFlow

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 11 o superior
- Git

### Instalación y Ejecución

1. **Clonar el repositorio**
```bash
git clone https://github.com/JeanClix/SIGE_Marimon.git
cd SIGE_Marimon
```

2. **Ejecutar en Web** (Recomendado para desarrollo)
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

3. **Acceder a la aplicación**
- URL: `http://localhost:3000`

### 🔑 Credenciales de Prueba

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | `admin` | `admin123` |
| Usuario | `usuario` | `user123` |

## 📖 Documentación

- 📋 **[Guía de Ejecución](GUIA_EJECUCION.md)** - Instrucciones detalladas
- 🏗️ **[Estructura del Proyecto](PROJECT_STRUCTURE.md)** - Arquitectura y organización
- 🔧 **[Cambiar Puerto](GUIA_EJECUCION.md#-cambiar-puerto-del-servidor)** - Configuración de puertos

## 🏗️ Arquitectura

```
composeApp/src/commonMain/kotlin/org/marimon/sigc/
├── data/
│   ├── model/           # Modelos de datos
│   └── repository/      # Repositorios
├── ui/
│   ├── screens/         # Pantallas
│   └── components/      # Componentes reutilizables
├── navigation/          # Navegación
├── viewmodel/          # ViewModels
└── App.kt              # Punto de entrada
```

## 🛠️ Tecnologías Utilizadas

- **Kotlin Multiplatform** - Desarrollo multiplataforma
- **Jetpack Compose** - UI declarativa moderna
- **Material Design 3** - Sistema de diseño
- **StateFlow** - Manejo de estado reactivo
- **Coroutines** - Programación asíncrona
- **Gradle** - Sistema de construcción

## 📱 Plataformas Soportadas

- ✅ **Web** (JavaScript/WASM)
- ✅ **Android** (API 24+)
- ✅ **iOS** (iOS 13+)
- ✅ **Desktop** (JVM)

## 🔄 Flujo de Desarrollo

1. **Hacer cambios** en el código
2. **Hot reload automático** en el navegador
3. **Probar funcionalidades** con credenciales de prueba
4. **Commit y push** a la rama `feature/login`

## 🐛 Solución de Problemas

### Puerto en uso
```bash
# Cambiar puerto en composeApp/webpack.config.d/port.js
config.devServer.port = 4000
```

### Error de compilación
```bash
./gradlew clean
./gradlew :composeApp:compileKotlinJs
```

### Dependencias faltantes
```bash
./gradlew :composeApp:kotlinNpmInstall
```

## 📋 Roadmap

- [ ] Gestión de empleados
- [ ] Configuración del sistema
- [ ] Base de datos local
- [ ] API REST
- [ ] Notificaciones push
- [ ] Reportes y estadísticas

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Equipo

- **Desarrollador Principal**: JeanClix
- **Contribuidores**: [Lista de contribuidores](CONTRIBUTORS.md)

## 📞 Soporte

Si tienes preguntas o necesitas ayuda:

- 📧 **Email**: [tu-email@ejemplo.com]
- 🐛 **Issues**: [GitHub Issues](https://github.com/JeanClix/SIGE_Marimon/issues)
- 💬 **Discusiones**: [GitHub Discussions](https://github.com/JeanClix/SIGE_Marimon/discussions)

---

**¡Gracias por usar SIGE Marimon!** 🎉

*Desarrollado con ❤️ usando Kotlin Multiplatform y Jetpack Compose*