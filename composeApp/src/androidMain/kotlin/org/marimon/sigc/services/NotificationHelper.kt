package org.marimon.sigc.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.marimon.sigc.MainActivity
import org.marimon.sigc.model.Producto

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "stock_bajo_channel"
        private const val CHANNEL_NAME = "Alertas de Stock Bajo"
        private const val CHANNEL_DESCRIPTION = "Notificaciones cuando productos tienen stock bajo"
        private const val NOTIFICATION_ID_BASE = 1000
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun verificarYNotificarStockBajo(productos: List<Producto>, umbralStock: Int = 10) {
        println("🔔 NotificationHelper - Iniciando verificación")
        println("   📦 Total productos recibidos: ${productos.size}")
        
        val productosStockBajo = productos.filter { it.activo && it.cantidad <= umbralStock && it.cantidad > 0 }
        println("   ⚠️ Productos con stock bajo encontrados: ${productosStockBajo.size}")
        
        productosStockBajo.forEach {
            println("      • ${it.nombre}: ${it.cantidad} unidades (activo: ${it.activo})")
        }
        
        if (productosStockBajo.isNotEmpty()) {
            println("   📢 Enviando notificaciones...")
            if (productosStockBajo.size == 1) {
                // Notificación individual
                println("   📱 Notificación INDIVIDUAL para: ${productosStockBajo.first().nombre}")
                notificarProductoStockBajo(productosStockBajo.first())
            } else {
                // Notificación grupal
                println("   📱 Notificación GRUPAL para ${productosStockBajo.size} productos")
                notificarVariosProductosStockBajo(productosStockBajo)
            }
        } else {
            println("   ✅ No hay productos con stock bajo (todos tienen stock > $umbralStock o están inactivos)")
        }
    }
    
    private fun notificarProductoStockBajo(producto: Producto) {
        println("      🔨 Creando notificación individual...")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Stock Bajo: ${producto.nombre}")
            .setContentText("Quedan solo ${producto.cantidad} unidades. Se recomienda reabastecer.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("El producto '${producto.nombre}' tiene un stock de ${producto.cantidad} unidades. Se recomienda realizar un pedido para evitar desabastecimiento.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        try {
            val notificationId = NOTIFICATION_ID_BASE + producto.id
            println("      📤 Enviando notificación con ID: $notificationId")
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            println("      ✅ Notificación enviada exitosamente")
        } catch (e: SecurityException) {
            println("      ❌ ERROR - Permiso de notificación denegado: ${e.message}")
        } catch (e: Exception) {
            println("      ❌ ERROR - Al enviar notificación: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun notificarVariosProductosStockBajo(productos: List<Producto>) {
        println("      🔨 Creando notificación grupal...")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Crear lista de productos con stock bajo
        val listaProductos = productos.joinToString("\n") { 
            "• ${it.nombre}: ${it.cantidad} unidades" 
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ ${productos.size} Productos con Stock Bajo")
            .setContentText("Varios productos necesitan ser reabastecidos")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Los siguientes productos tienen stock bajo:\n\n$listaProductos\n\nSe recomienda realizar pedidos pronto.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setNumber(productos.size)
            .build()
        
        try {
            println("      📤 Enviando notificación grupal con ID: $NOTIFICATION_ID_BASE")
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BASE, notification)
            println("      ✅ Notificación grupal enviada exitosamente")
        } catch (e: SecurityException) {
            println("      ❌ ERROR - Permiso de notificación denegado: ${e.message}")
        } catch (e: Exception) {
            println("      ❌ ERROR - Al enviar notificación: ${e.message}")
            e.printStackTrace()
        }
    }
    
    fun cancelarTodasLasNotificaciones() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}

