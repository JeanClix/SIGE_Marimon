# 🔧 Configuración Segura de Email

## ✅ Archivos Creados

### **1. Archivo de Configuración** ✅
- **Ubicación**: `composeApp/src/androidMain/assets/email_config.properties`
- **Estado**: Agregado al .gitignore (NO se sube al repositorio)

### **2. Archivo de Ejemplo** ✅
- **Ubicación**: `email_config.properties.example`
- **Propósito**: Plantilla para otros desarrolladores

### **3. Clase EmailConfig** ✅
- **Ubicación**: `composeApp/src/androidMain/kotlin/org/marimon/sigc/services/EmailConfig.kt`
- **Funcionalidad**: Lee configuración desde assets

## 🔧 Configuración Paso a Paso

### **1. Configurar Credenciales**

**Editar `composeApp/src/androidMain/assets/email_config.properties`**:
```properties
# Configuración SMTP para Gmail
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
FROM_EMAIL=tu-email@gmail.com
FROM_PASSWORD=tu-app-password
```

### **2. Configurar Gmail**

#### **Activar 2FA**:
1. Ir a [Google Account](https://myaccount.google.com/)
2. Security → 2-Step Verification
3. Activar la verificación en dos pasos

#### **Generar App Password**:
1. En Google Account → Security → 2-Step Verification
2. App passwords → Generate password
3. Seleccionar "Mail" y "Android"
4. Copiar la contraseña generada
5. Usar esa contraseña en `FROM_PASSWORD`

### **3. Verificar Configuración**

#### **Logs Esperados**:
```
EmailConfig: Configuración de email cargada exitosamente
EmailServiceReal: Email enviado exitosamente a: cliente@email.com
```

#### **Si hay Error**:
```
EmailConfig: Error cargando configuración: [error]
EmailServiceReal: Email no configurado. Configura email_config.properties
```

## 🔒 Seguridad

### **Archivos Protegidos**:
- ✅ `email_config.properties` - Agregado al .gitignore
- ✅ `*.env` - Agregado al .gitignore
- ✅ `*.key` - Agregado al .gitignore
- ✅ `*.pem` - Agregado al .gitignore
- ✅ `*.p12` - Agregado al .gitignore
- ✅ `*.jks` - Agregado al .gitignore
- ✅ `*.keystore` - Agregado al .gitignore

### **Archivos Públicos**:
- ✅ `email_config.properties.example` - Plantilla segura
- ✅ `EmailConfig.kt` - Código sin credenciales
- ✅ `EmailServiceReal.kt` - Código sin credenciales

## 🚀 Uso

### **En el Código**:
```kotlin
// Cargar configuración
EmailConfig.loadConfig(context)

// Verificar si está configurado
if (EmailConfig.isConfigured()) {
    // Enviar email
    EmailServiceReal.sendEmailWithPDF(
        context = context,
        toEmail = "cliente@email.com",
        subject = "Comprobante de Pago",
        body = "Adjunto su comprobante",
        pdfFilePath = "/path/to/file.pdf"
    )
} else {
    // Mostrar error de configuración
    onError("Email no configurado")
}
```

### **Configuración Automática**:
- ✅ **Carga automática** desde assets
- ✅ **Verificación automática** de configuración
- ✅ **Valores por defecto** si no está configurado
- ✅ **Logs detallados** para debugging

## 📱 Flujo Completo

### **Al Enviar Email**:
1. **Se carga la configuración** desde assets
2. **Se verifica** que esté configurado
3. **Se conecta** al servidor SMTP
4. **Se envía** el email con PDF adjunto
5. **Se registra** el resultado en logs

### **Si no está configurado**:
1. **Se muestra error** claro
2. **Se usan valores por defecto** (no funcionales)
3. **Se registra** en logs para debugging

## ⚠️ Importante

### **NO SUBIR**:
- ❌ `email_config.properties` (contiene credenciales)
- ❌ Archivos con credenciales reales

### **SÍ SUBIR**:
- ✅ `email_config.properties.example` (plantilla)
- ✅ `EmailConfig.kt` (código)
- ✅ `EmailServiceReal.kt` (código)
- ✅ `.gitignore` (protección)

## 🎯 Estado Final

- ✅ **Configuración Segura**: Credenciales en archivo separado
- ✅ **Protección Git**: Archivo agregado al .gitignore
- ✅ **Plantilla**: Archivo de ejemplo para otros desarrolladores
- ✅ **Código Limpio**: Sin credenciales hardcodeadas
- ✅ **Logs Detallados**: Para debugging y verificación

**¡La configuración está lista! Solo necesitas editar `email_config.properties` con tus credenciales reales.**
