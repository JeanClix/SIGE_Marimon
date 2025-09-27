# 🚀 Ejecutar Proyecto desde IntelliJ IDEA

## 📋 Configuración en IntelliJ IDEA

### **1. Abrir el Proyecto**
1. Abre **IntelliJ IDEA**
2. Selecciona **Open** o **File → Open**
3. Navega a la carpeta del proyecto: `C:\Users\LP-091\AppData\Local\Workspace\SIGE_Marimon`
4. Selecciona la carpeta y haz clic en **OK**

### **2. Configurar Gradle**
1. IntelliJ detectará automáticamente que es un proyecto Gradle
2. Aparecerá una notificación: **"Gradle project detected"**
3. Haz clic en **Import Gradle Project**
4. Espera a que se complete la importación

### **3. Configurar Run Configurations**

#### **Opción A: Usar Terminal Integrado**
1. Ve a **View → Tool Windows → Terminal**
2. En el terminal integrado, ejecuta:
   ```bash
   ./gradlew kotlinNpmInstall
   ```
3. Luego ejecuta:
   ```bash
   ./gradlew :composeApp:jsBrowserDevelopmentRun
   ```

#### **Opción B: Crear Run Configuration**
1. Ve a **Run → Edit Configurations**
2. Haz clic en **+** → **Gradle**
3. Configura:
   - **Name**: `SIGE Web App`
   - **Gradle project**: Selecciona tu proyecto
   - **Tasks**: `:composeApp:jsBrowserDevelopmentRun`
   - **Arguments**: (dejar vacío)
4. Haz clic en **OK**

### **4. Ejecutar el Proyecto**

#### **Desde Terminal Integrado:**
```bash
# Instalar dependencias NPM
./gradlew kotlinNpmInstall

# Ejecutar el proyecto
./gradlew :composeApp:jsBrowserDevelopmentRun
```

#### **Desde Run Configuration:**
1. Selecciona la configuración **SIGE Web App**
2. Haz clic en el botón **Run** (▶️) o presiona **Shift + F10**

### **5. Acceder a la Aplicación**
- Una vez ejecutado, abre tu navegador
- Ve a: `http://localhost:3000`
- Deberías ver la pantalla de login

## 🔧 Solución de Problemas Comunes

### **Error: "Task 'kotlinNpmInstall' not found"**
**Solución:**
```bash
# Usar la tarea correcta (sin prefijo de módulo)
./gradlew kotlinNpmInstall
```

### **Error: "Port 3000 already in use"**
**Solución:**
```bash
# Encontrar el proceso que usa el puerto
netstat -ano | findstr :3000

# Terminar el proceso (reemplaza PID con el número real)
taskkill /F /PID [PID_NUMBER]
```

### **Error: "Gradle project not detected"**
**Solución:**
1. Ve a **File → Project Structure**
2. En **Project**, selecciona **Gradle** como **Project SDK**
3. Haz clic en **Apply** y **OK**

### **Error: "Node.js not found"**
**Solución:**
1. Instala Node.js desde [nodejs.org](https://nodejs.org)
2. Reinicia IntelliJ IDEA
3. Ejecuta `./gradlew kotlinNodeJsSetup`

## 📱 Configuraciones Adicionales

### **Configurar Debugging**
1. Ve a **Run → Edit Configurations**
2. Selecciona tu configuración Gradle
3. En **VM options**, agrega:
   ```
   -Dkotlin.js.sourceMap=true
   ```

### **Configurar Hot Reload**
El proyecto ya tiene **Hot Module Replacement** configurado:
- Los cambios en el código se reflejan automáticamente
- No necesitas reiniciar el servidor

### **Configurar Browser**
1. Ve a **Run → Edit Configurations**
2. En **Before launch**, haz clic en **+**
3. Selecciona **Launch Web Browser**
4. Configura la URL: `http://localhost:3000`

## 🎯 Comandos Útiles

### **Comandos de Desarrollo:**
```bash
# Limpiar proyecto
./gradlew clean

# Compilar solo JavaScript
./gradlew :composeApp:compileKotlinJs

# Ejecutar tests
./gradlew :composeApp:jsBrowserTest

# Actualizar dependencias NPM
./gradlew kotlinUpgradeYarnLock
```

### **Comandos de Build:**
```bash
# Build de producción
./gradlew :composeApp:jsBrowserProductionRun

# Build para distribución
./gradlew :composeApp:jsBrowserDistribution
```

## 📖 Recursos Adicionales

- **[GUIA_EJECUCION.md](GUIA_EJECUCION.md)** - Guía completa de ejecución
- **[CONFIGURACION_SUPABASE.md](CONFIGURACION_SUPABASE.md)** - Configuración de Supabase
- **[CREAR_USUARIOS_SUPABASE.md](CREAR_USUARIOS_SUPABASE.md)** - Crear usuarios

## ✅ Checklist de Verificación

- [ ] Proyecto abierto en IntelliJ IDEA
- [ ] Gradle importado correctamente
- [ ] Dependencias NPM instaladas (`kotlinNpmInstall`)
- [ ] Servidor ejecutándose en puerto 3000
- [ ] Aplicación accesible en `http://localhost:3000`
- [ ] Login funcional con Supabase

---

**¡Tu proyecto está listo para ejecutar desde IntelliJ IDEA!** 🎉
