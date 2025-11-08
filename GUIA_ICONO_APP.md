# 📱 Guía: Cómo Agregar el Ícono de la App Android

## 🎯 Ubicación de los Íconos

Los íconos de Android se colocan en las carpetas **mipmap** dentro de:

```
composeApp/src/androidMain/res/
├── mipmap-mdpi/       (48x48 px)
├── mipmap-hdpi/       (72x72 px)
├── mipmap-xhdpi/      (96x96 px)
├── mipmap-xxhdpi/     (144x144 px)
├── mipmap-xxxhdpi/    (192x192 px)
└── mipmap-anydpi-v26/ (Adaptive Icon - XML)
```

---

## 📝 Nombres de Archivos

Debes crear estos archivos:

1. **Ícono principal:**
   - `ic_launcher.png` (ícono redondo/cuadrado)
   - `ic_launcher_round.png` (versión circular)

2. **Ícono adaptativo (Android 8+):**
   - `ic_launcher_foreground.png` (capa frontal)
   - `ic_launcher_background.png` (capa fondo)

---

## 🎨 Formato Recomendado

### ✅ **FORMATO: PNG**
- **No usar SVG directamente**
- Usar PNG con transparencia (32-bit RGBA)
- Fondo transparente para el foreground

### 📐 Tamaños Necesarios:

| Densidad | Tamaño | Carpeta |
|----------|--------|---------|
| MDPI | 48x48 | mipmap-mdpi |
| HDPI | 72x72 | mipmap-hdpi |
| XHDPI | 96x96 | mipmap-xhdpi |
| XXHDPI | 144x144 | mipmap-xxhdpi |
| XXXHDPI | 192x192 | mipmap-xxxhdpi |

---

## 🚀 Opción Rápida: Herramientas Online

### **Opción 1: Android Asset Studio**
🔗 https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html

1. Sube tu imagen/logo
2. Ajusta el diseño
3. Descarga el ZIP con todos los tamaños
4. Extrae y copia las carpetas `mipmap-*` a tu proyecto

### **Opción 2: Icon Kitchen**
🔗 https://icon.kitchen/

1. Sube tu logo
2. Personaliza colores y estilo
3. Descarga el paquete completo
4. Copia a tu proyecto

---

## 📂 Estructura Completa del Proyecto

```
composeApp/src/androidMain/res/
├── mipmap-mdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
├── mipmap-hdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
├── mipmap-xhdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
├── mipmap-xxhdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
├── mipmap-xxxhdpi/
│   ├── ic_launcher.png
│   └── ic_launcher_round.png
└── mipmap-anydpi-v26/
    ├── ic_launcher.xml
    └── ic_launcher_round.xml
```

---

## 🔧 Archivos XML (Adaptive Icon)

Si usas ícono adaptativo, crea estos XML en `mipmap-anydpi-v26/`:

### `ic_launcher.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@mipmap/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

### `ic_launcher_round.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@mipmap/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

---

## ⚙️ Configurar en AndroidManifest.xml

Tu `AndroidManifest.xml` debe tener:

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:label="@string/app_name"
    ...>
</application>
```

---

## 🎨 Recomendaciones de Diseño

1. **Tamaño del logo:** 1024x1024 px (máster)
2. **Margen de seguridad:** Deja 10-15% de espacio alrededor
3. **Colores:** Usa colores sólidos y contrastantes
4. **Simplicidad:** Diseños simples funcionan mejor en tamaños pequeños
5. **Prueba:** Verifica cómo se ve en diferentes fondos y dispositivos

---

## ✅ Checklist Final

- [ ] Crear/obtener logo en 1024x1024 px
- [ ] Generar todos los tamaños (usar herramienta online)
- [ ] Copiar carpetas `mipmap-*` al proyecto
- [ ] Verificar nombres de archivos
- [ ] Limpiar y reconstruir el proyecto
- [ ] Probar en dispositivo/emulador
- [ ] Verificar en diferentes launchers

---

## 🚀 Pasos Rápidos:

1. Ve a: https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
2. Sube tu logo/imagen
3. Descarga el ZIP
4. Extrae y copia las carpetas a `composeApp/src/androidMain/res/`
5. Sync y Build el proyecto
6. ¡Listo! 🎉

---

## 📌 Notas Importantes:

- ⚠️ **NO usar SVG directamente** en mipmap (Android no lo soporta)
- ⚠️ Los nombres deben ser **exactamente** `ic_launcher` y `ic_launcher_round`
- ⚠️ Minúsculas y sin espacios
- ⚠️ Solo letras, números y guiones bajos
- ✅ Después de agregar, haz **Clean Project** → **Rebuild Project**

