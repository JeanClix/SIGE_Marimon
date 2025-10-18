# Guía Completa para Configurar EmailJS

## 🔧 Configuración del Template de EmailJS

### **1. Modificar el Template en EmailJS**

Ve a [dashboard.emailjs.com](https://dashboard.emailjs.com/) y edita tu template `template_cc56uwo`:

#### **Subject Field**:
```
Comprobante de Pago - {{subject}} - Automotriz Marimon
```

#### **Content Field**:
```
Estimado(a) {{nombre_cliente}},

Adjuntamos su {{tipo_comprobante}} N° {{numero_comprobante}} por un total de S/ {{total}}.

{{pdf_content}}

Gracias por su preferencia.

Atentamente,
Automotriz Marimon
```

#### **Email Configuration**:
- **To Email**: `{{to_email}}`
- **From Name**: `Automotriz Marimon`
- **From Email**: Tu email configurado
- **Reply To**: `{{to_email}}`

### **2. Parámetros que Envía tu Código**

Tu código envía estos parámetros:
- `to_email`: Email del destinatario
- `subject`: Asunto del email
- `nombre_cliente`: Nombre del cliente
- `tipo_comprobante`: Tipo de comprobante (Boleta/Factura)
- `numero_comprobante`: Número del comprobante
- `total`: Total de la transacción
- `pdf_content`: Contenido HTML del PDF

### **3. Verificar Configuración**

Después de modificar el template:
1. **Guarda** el template
2. **Prueba** con "Test It" usando estos valores:
   - `to_email`: tu email
   - `subject`: "Boleta de Venta"
   - `nombre_cliente`: "Juan Pérez"
   - `tipo_comprobante`: "Boleta de Venta"
   - `numero_comprobante`: "00000001"
   - `total`: "330.4"
   - `pdf_content`: "Contenido del PDF aquí"

## 📱 Implementación de Descarga de PDFs

### **1. Servicio Android Creado**

He creado `AndroidPDFDownloader.kt` que:
- ✅ Guarda el PDF en `/storage/emulated/0/Download/Marimon/`
- ✅ Abre automáticamente el archivo con el navegador
- ✅ Maneja errores correctamente

### **2. Permisos Necesarios**

Asegúrate de que tu `AndroidManifest.xml` tenga:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### **3. Ubicación del Archivo**

El PDF se guardará en:
```
/storage/emulated/0/Download/Marimon/BOLETA_00000001_76605665.html
```

## 🚀 Próximos Pasos

### **1. Configurar Template**
1. Ve a EmailJS dashboard
2. Edita el template `template_cc56uwo`
3. Usa los campos correctos mostrados arriba
4. Guarda y prueba

### **2. Probar Descarga de PDF**
1. Genera una transacción
2. Haz clic en "Descargar PDF"
3. Verifica que se abra en el navegador
4. Revisa la carpeta Downloads/Marimon/

### **3. Probar Envío de Email**
1. Genera una transacción
2. Verifica que llegue el email
3. Revisa los logs para confirmar envío

## 🔍 Verificación en Logs

Cuando generes una transacción, verás:

```
DEBUG: Email enviado exitosamente con EmailJS
DEBUG: Parámetros enviados:
DEBUG: - to_email: jp10.02.2003@gmail.com
DEBUG: - subject: Comprobante de Pago - Boleta de Venta N° 00000001
DEBUG: - nombre_cliente: Jean
DEBUG: - tipo_comprobante: Boleta de Venta
DEBUG: - numero_comprobante: 00000001
DEBUG: - total: 330.4
DEBUG: - pdf_content: 3166 caracteres
```

## 🆘 Solución de Problemas

### **Si el email no llega**:
1. Verifica que el template esté guardado
2. Revisa que los parámetros coincidan
3. Prueba con "Test It" en EmailJS
4. Verifica que tu servicio de email esté conectado

### **Si el PDF no se descarga**:
1. Verifica permisos de almacenamiento
2. Revisa la carpeta Downloads/Marimon/
3. Verifica que el navegador esté instalado

### **Si hay errores**:
1. Revisa los logs de la aplicación
2. Verifica las credenciales de EmailJS
3. Asegúrate de que el template exista

---

**Estado**: ✅ **CONFIGURACIÓN COMPLETA**

Solo necesitas modificar el template en EmailJS y probar la funcionalidad.
