# 🚀 Guía de Ejecución - SIGE Marimon

## 📋 Requisitos Previos

### Software Necesario
- **Java 11 o superior** - [Descargar OpenJDK](https://adoptium.net/)
- **Git** - [Descargar Git](https://git-scm.com/downloads)
- **Node.js 16+** - [Descargar Node.js](https://nodejs.org/) (recomendado LTS)
- **IDE Recomendado**: IntelliJ IDEA Community o Android Studio

### Verificar Instalaciones
```bash
# Verificar Java
java -version

# Verificar Git
git --version

# Verificar Node.js
node --version
npm --version
```

## 🏃‍♂️ Cómo Ejecutar el Proyecto

### 1. **Clonar el Repositorio**
```bash
git clone https://github.com/JeanClix/SIGE_Marimon.git
cd SIGE_Marimon
```

### 2. **Instalación Inicial** (Solo la primera vez)

#### Instalar dependencias de Node.js
```bash
# Instalar dependencias de Kotlin/JS
./gradlew :composeApp:kotlinNpmInstall

# O si tienes problemas, limpiar e instalar
./gradlew clean
./gradlew :composeApp:kotlinNpmInstall
```

#### Verificar configuración
```bash
# Verificar que Gradle funciona
./gradlew --version

# Compilar el proyecto
./gradlew :composeApp:compileKotlinJs
```

### 3. **Ejecutar el Proyecto Web**
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### 4. **Acceder a la Aplicación**
- **URL**: `http://localhost:3000`
- **Puerto por defecto**: 3000
- **Primera ejecución**: Puede tomar 2-5 minutos para compilar

## 🔧 Cambiar Puerto del Servidor

### Método 1: Archivo de Configuración (Recomendado)

1. **Crear/Editar archivo**: `composeApp/webpack.config.d/port.js`
2. **Agregar configuración**:
```javascript
config.devServer = config.devServer || {}
config.devServer.port = 4000  // Cambiar por el puerto deseado
config.devServer.host = 'localhost'
```

3. **Ejecutar el proyecto**:
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### Método 2: Variables de Entorno

```bash
# Windows (PowerShell)
$env:PORT=4000; ./gradlew :composeApp:jsBrowserDevelopmentRun

# Windows (CMD)
set PORT=4000 && ./gradlew :composeApp:jsBrowserDevelopmentRun

# Linux/Mac
PORT=4000 ./gradlew :composeApp:jsBrowserDevelopmentRun
```

### Método 3: Archivo gradle.properties

1. **Crear/Editar**: `gradle.properties` en la raíz del proyecto
2. **Agregar**:
```properties
# Puerto personalizado para desarrollo web
compose.webpack.devServer.port=4000
```

## 🌐 Puertos Comunes y Conflictos

### Puertos Frecuentemente Usados
- **3000**: Puerto por defecto del proyecto
- **8080**: Tomcat (puede causar conflictos)
- **4000**: Alternativa recomendada
- **5000**: Alternativa recomendada
- **8000**: Alternativa recomendada

### Resolver Conflictos de Puerto

#### Verificar puertos en uso:
```bash
# Windows
netstat -ano | findstr :3000

# Linux/Mac
lsof -i :3000
```

#### Detener procesos que usan el puerto:
```bash
# Windows (reemplazar PID con el número mostrado)
taskkill /F /PID 1234

# Linux/Mac
kill -9 1234
```

## 📱 Ejecutar en Otras Plataformas

### Android
```bash
# Requiere Android SDK configurado
./gradlew :composeApp:installDebug
```

### iOS
```bash
# Requiere Xcode en Mac
cd iosApp
open iosApp.xcodeproj
```

### Desktop (JVM)
```bash
./gradlew :composeApp:run
```

## 🔐 Credenciales de Prueba

### Administrador
- **Usuario**: `admin`
- **Contraseña**: `admin123`

### Usuario Regular
- **Usuario**: `usuario`
- **Contraseña**: `user123`

## 🛠️ Comandos Útiles

### Comandos de Instalación
```bash
# Instalar dependencias de Node.js
./gradlew :composeApp:kotlinNpmInstall

# Instalar con refresh de dependencias
./gradlew :composeApp:kotlinNpmInstall --refresh-dependencies

# Instalar dependencias de Android
./gradlew :composeApp:installDebug
```

### Comandos de Compilación
```bash
# Compilar solo JavaScript
./gradlew :composeApp:compileKotlinJs

# Compilar para WebAssembly (más rápido)
./gradlew :composeApp:compileKotlinWasmJs

# Compilar para Android
./gradlew :composeApp:assembleDebug

# Compilar todo
./gradlew build
```

### Comandos de Limpieza
```bash
# Limpiar cache de Gradle
./gradlew clean

# Limpiar cache de Node.js
rm -rf node_modules kotlin-js-store

# Limpiar cache completo de Gradle
rm -rf ~/.gradle/caches
```

### Comandos de Desarrollo
```bash
# Ejecutar en modo desarrollo web
./gradlew :composeApp:jsBrowserDevelopmentRun

# Ejecutar en modo desarrollo WebAssembly
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Ejecutar servidor backend
./gradlew :server:run

# Ver todas las tareas disponibles
./gradlew tasks

# Ver tareas específicas del módulo
./gradlew :composeApp:tasks
```

### Comandos de Debugging
```bash
# Ejecutar con logs detallados
./gradlew :composeApp:jsBrowserDevelopmentRun --info

# Ejecutar con stacktrace completo
./gradlew :composeApp:jsBrowserDevelopmentRun --stacktrace

# Verificar configuración
./gradlew :composeApp:jsBrowserDevelopmentRun --dry-run
```

### Detener Servidor
```bash
# Presionar Ctrl+C en la terminal donde se ejecuta
# O usar taskkill en Windows
taskkill /F /IM java.exe
```

## 🐛 Solución de Problemas

### Error: "Puerto ya en uso"
1. Cambiar puerto usando los métodos anteriores
2. O detener el proceso que usa el puerto

### Error: "SDK location not found" (Android)
1. Instalar Android SDK
2. Configurar variable `ANDROID_HOME`

### Error: "Compilation failed"
```bash
# Limpiar cache completo
./gradlew clean

# Reinstalar dependencias
./gradlew :composeApp:kotlinNpmInstall

# Recompilar
./gradlew :composeApp:compileKotlinJs
```

### Error: "Node modules not found"
```bash
# Eliminar cache de Node.js
rm -rf node_modules
rm -rf kotlin-js-store

# Reinstalar dependencias
./gradlew :composeApp:kotlinNpmInstall
```

### Error: "KotlinNpmInstall failed"
```bash
# Limpiar todo y reinstalar
./gradlew clean
./gradlew :composeApp:kotlinNpmInstall --refresh-dependencies

# Si persiste, eliminar cache de Gradle
rm -rf ~/.gradle/caches
./gradlew :composeApp:kotlinNpmInstall
```

### Error: "Webpack compilation failed"
```bash
# Limpiar build
./gradlew clean

# Reinstalar dependencias
./gradlew :composeApp:kotlinNpmInstall

# Recompilar
./gradlew :composeApp:compileKotlinJs

# Ejecutar nuevamente
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### Error: "Java version incompatible"
```bash
# Verificar versión de Java
java -version

# Si es menor a Java 11, instalar Java 11+
# Configurar JAVA_HOME si es necesario
export JAVA_HOME=/path/to/java11
```

### Error: "Permission denied" (Linux/Mac)
```bash
# Dar permisos de ejecución
chmod +x gradlew

# Ejecutar con sudo si es necesario
sudo ./gradlew :composeApp:jsBrowserDevelopmentRun
```

### Problemas Específicos de Windows

#### Error: "Gradle daemon not found"
```cmd
# En Windows CMD
gradlew.bat :composeApp:jsBrowserDevelopmentRun

# En PowerShell
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

#### Error: "Java not found"
```cmd
# Verificar JAVA_HOME
echo %JAVA_HOME%

# Configurar JAVA_HOME si es necesario
set JAVA_HOME=C:\Program Files\Java\jdk-11
```

#### Error: "Node.js not found"
```cmd
# Verificar PATH de Node.js
where node

# Reinstalar Node.js desde https://nodejs.org/
```

#### Error: "Puerto 8080 en uso" (Tomcat)
```cmd
# Verificar qué proceso usa el puerto
netstat -ano | findstr :8080

# Detener proceso específico
taskkill /F /PID [PID_NUMBER]

# O cambiar puerto del proyecto
```

## 📁 Estructura del Proyecto

```
SIGE_Marimon/
├── composeApp/                 # Aplicación principal
│   ├── src/commonMain/kotlin/ # Código compartido
│   │   └── org/marimon/sigc/
│   │       ├── data/          # Modelos y repositorios
│   │       ├── ui/            # Pantallas y componentes
│   │       ├── navigation/    # Sistema de navegación
│   │       └── viewmodel/     # ViewModels
│   └── webpack.config.d/      # Configuración de webpack
├── shared/                    # Módulo compartido
├── server/                    # Servidor backend
└── iosApp/                   # Aplicación iOS
```

## 🔄 Flujo de Desarrollo

1. **Hacer cambios** en el código
2. **El servidor se recarga automáticamente** (hot reload)
3. **Refrescar el navegador** para ver cambios
4. **Probar funcionalidades** con las credenciales

## 📝 Notas Importantes

- ⚠️ **Primera ejecución**: Puede tomar varios minutos para instalar dependencias
- 🔄 **Hot Reload**: Los cambios se reflejan automáticamente
- 🌐 **Solo Web**: Esta guía se enfoca en la ejecución web
- 🔒 **Credenciales**: Usar las credenciales de prueba proporcionadas

## 🆘 Soporte

Si encuentras problemas:
1. Verificar que Java 11+ esté instalado
2. Limpiar cache con `./gradlew clean`
3. Verificar que no haya conflictos de puerto
4. Revisar logs de error en la terminal

---

**¡Disfruta desarrollando con SIGE Marimon!** 🎉
