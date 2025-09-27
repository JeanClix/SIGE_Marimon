# 🎯 Comandos Correctos para IntelliJ IDEA

## ✅ **Comandos que SÍ funcionan:**

### **1. Instalar dependencias NPM:**
```bash
./gradlew kotlinNpmInstall
```
❌ **NO uses:** `./gradlew :composeApp:kotlinNpmInstall`

### **2. Ejecutar el proyecto:**
```bash
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### **3. Limpiar proyecto:**
```bash
./gradlew clean
```

### **4. Compilar solo JavaScript:**
```bash
./gradlew :composeApp:compileKotlinJs
```

## 🔧 **Solución de Problemas:**

### **Error: "Port 3000 already in use"**
```bash
# Encontrar proceso
netstat -ano | findstr :3000

# Terminar proceso (reemplaza PID con el número real)
taskkill /F /PID [PID_NUMBER]
```

### **Error: "Task not found"**
- Usa `kotlinNpmInstall` (sin prefijo `:composeApp:`)
- Usa `:composeApp:jsBrowserDevelopmentRun` (con prefijo)

## 📋 **Secuencia Correcta:**

1. **Abrir terminal en IntelliJ:**
   - `View → Tool Windows → Terminal`

2. **Ejecutar comandos en orden:**
   ```bash
   # Paso 1: Instalar dependencias
   ./gradlew kotlinNpmInstall
   
   # Paso 2: Ejecutar proyecto
   ./gradlew :composeApp:jsBrowserDevelopmentRun
   ```

3. **Acceder a la aplicación:**
   - Abrir navegador en `http://localhost:3000`

## 🚀 **Estado Actual:**
- ✅ **Dependencias instaladas** - `kotlinNpmInstall` ejecutado
- ✅ **Puerto liberado** - Proceso anterior terminado
- ✅ **Servidor ejecutándose** - En segundo plano
- ✅ **Aplicación disponible** - En `http://localhost:3000`

## 📖 **Documentación Completa:**
- **[EJECUTAR_INTELLIJ.md](EJECUTAR_INTELLIJ.md)** - Guía completa
- **[CONFIGURACION_SUPABASE.md](CONFIGURACION_SUPABASE.md)** - Configuración Supabase
- **[CREAR_USUARIOS_SUPABASE.md](CREAR_USUARIOS_SUPABASE.md)** - Crear usuarios

---

**¡Tu proyecto está ejecutándose correctamente!** 🎉
