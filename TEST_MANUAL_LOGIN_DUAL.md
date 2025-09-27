# 🧪 Test Manual del Sistema de Login Dual

## 📋 Descripción

Este test manual verifica que el sistema de login dual funciona correctamente sin necesidad de hacer llamadas HTTP reales. Simula todo el flujo de autenticación para empleados y administradores.

## 🚀 Cómo Ejecutar el Test

### Opción 1: Desde Android Studio
1. Abre el archivo `ManualTest.kt`
2. Haz clic derecho en la función `main()`
3. Selecciona "Run 'ManualTestKt'"

### Opción 2: Desde Terminal
```bash
# Compilar y ejecutar
./gradlew :composeApp:compileKotlinJs
# Luego ejecutar el archivo generado
```

## ✅ Qué Verifica el Test

### 1. 📋 Deserialización JSON
- ✅ Verifica que el JSON de Supabase se deserializa correctamente
- ✅ Confirma que el campo `password` se parsea bien
- ✅ Valida todos los campos del empleado

### 2. 🔐 Comparación de Contraseñas
- ✅ Verifica que contraseñas correctas coinciden
- ✅ Confirma que contraseñas incorrectas no coinciden
- ✅ Prueba la lógica de autenticación

### 3. 👤 Creación de Usuarios
- ✅ Crea usuarios empleados correctamente
- ✅ Crea usuarios administradores correctamente
- ✅ Asigna roles correctos (EMPLOYEE/ADMIN)
- ✅ Maneja nombres simples y compuestos

### 4. 🎭 Lógica de Roles
- ✅ Verifica navegación basada en roles
- ✅ Empleados van a EmployeeScreen
- ✅ Admins van a HomeScreen
- ✅ Confirma que los roles son diferentes

## 📊 Resultado Esperado

```
🧪 INICIANDO TEST MANUAL DEL SISTEMA DE LOGIN DUAL
==================================================

📋 Test 1: Deserialización JSON
✅ Deserialización JSON: EXITOSA
   - ID: 35
   - Nombre: jose
   - Email: jose12@sige.com
   - Password: jose12
   - Activo: true

🔐 Test 2: Comparación de Contraseñas
✅ Comparación de contraseñas: EXITOSA
   - Contraseña correcta: true
   - Contraseña incorrecta: false

👤 Test 3: Creación de Usuarios
✅ Creación de usuarios: EXITOSA
   - Usuario Empleado:
     * ID: 35
     * Nombre: jose 
     * Email: jose12@sige.com
     * Rol: EMPLOYEE
   - Usuario Admin:
     * ID: admin-uuid-123
     * Nombre: Admin User
     * Email: admin@sige.com
     * Rol: ADMIN

🎭 Test 4: Lógica de Roles
✅ Lógica de roles: EXITOSA
   - Empleado va a EmployeeScreen: true
   - Admin va a HomeScreen: true
   - Roles son diferentes: true

✅ TODOS LOS TESTS MANUALES PASARON EXITOSAMENTE!
🚀 El sistema de login dual está funcionando correctamente
```

## 🔧 Si el Test Falla

Si algún test falla, revisa:

1. **Deserialización JSON**: Verifica que el modelo `EmpleadoLoginResponse` coincida con la respuesta de Supabase
2. **Comparación de contraseñas**: Confirma que no hay espacios en blanco o caracteres especiales
3. **Creación de usuarios**: Verifica que los campos se asignen correctamente
4. **Lógica de roles**: Confirma que los enums `UserRole` estén definidos correctamente

## 📝 Notas

- Este test NO hace llamadas HTTP reales
- Simula el comportamiento esperado del sistema
- Verifica la lógica de negocio sin depender de servicios externos
- Útil para debugging y verificación rápida

## 🎯 Próximos Pasos

Después de que este test pase:

1. **Prueba el login real** con un empleado creado
2. **Verifica la navegación** según el rol del usuario
3. **Confirma que los logs** muestran la información correcta
4. **Testa escenarios de error** (contraseñas incorrectas, usuarios inactivos)
