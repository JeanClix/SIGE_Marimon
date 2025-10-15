package org.marimon.sigc.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.marimon.sigc.model.Movimiento
import org.marimon.sigc.model.MovimientoCreate
import org.marimon.sigc.model.TipoMovimiento
import org.marimon.sigc.model.Producto

class MovimientoViewModel {
    private val _movimientos = mutableStateListOf<Movimiento>()
    val movimientos: List<Movimiento> = _movimientos
    
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading.value
    
    private val _error = mutableStateOf<String?>(null)
    val error = _error.value
    
    // Datos de ejemplo para desarrollo
    private val movimientosEjemplo = listOf(
        Movimiento(
            id = 1,
            tipo = TipoMovimiento.ENTRADA,
            productoId = 1,
            empleadoId = 1,
            cantidad = 10,
            nota = "Compra inicial de stock",
            fechaRegistro = "2025-10-14T10:30:00",
            createdAt = "2025-10-14T10:30:00",
            updatedAt = "2025-10-14T10:30:00",
            activo = true,
            productoNombre = "Filtro de Aceite Premium",
            productoCodigo = "FLT-001",
            productoImagenUrl = null,
            empleadoNombre = "Juan Pérez"
        ),
        Movimiento(
            id = 2,
            tipo = TipoMovimiento.SALIDA,
            productoId = 2,
            empleadoId = 1,
            cantidad = 5,
            nota = "Venta a cliente regular",
            fechaRegistro = "2025-10-14T11:15:00",
            createdAt = "2025-10-14T11:15:00",
            updatedAt = "2025-10-14T11:15:00",
            activo = true,
            productoNombre = "Pastillas de Freno Delanteras",
            productoCodigo = "BRK-002",
            productoImagenUrl = null,
            empleadoNombre = "Juan Pérez"
        ),
        Movimiento(
            id = 3,
            tipo = TipoMovimiento.ENTRADA,
            productoId = 3,
            empleadoId = 1,
            cantidad = 20,
            nota = "Reposición de inventario",
            fechaRegistro = "2025-10-14T14:45:00",
            createdAt = "2025-10-14T14:45:00",
            updatedAt = "2025-10-14T14:45:00",
            activo = true,
            productoNombre = "Bujías de Iridio NGK",
            productoCodigo = "SPK-003",
            productoImagenUrl = null,
            empleadoNombre = "Juan Pérez"
        ),
        Movimiento(
            id = 4,
            tipo = TipoMovimiento.SALIDA,
            productoId = 1,
            empleadoId = 1,
            cantidad = 3,
            nota = "Reparación motor cliente",
            fechaRegistro = "2025-10-14T16:20:00",
            createdAt = "2025-10-14T16:20:00",
            updatedAt = "2025-10-14T16:20:00",
            activo = true,
            productoNombre = "Filtro de Aceite Premium",
            productoCodigo = "FLT-001",
            productoImagenUrl = null,
            empleadoNombre = "Juan Pérez"
        )
    )
    
    fun cargarMovimientos() {
        _isLoading.value = true
        try {
            // TODO: Implementar llamada real a la API
            _movimientos.clear()
            _movimientos.addAll(movimientosEjemplo)
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error al cargar movimientos: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    fun cargarMovimientosPorTipo(tipo: TipoMovimiento) {
        _isLoading.value = true
        try {
            // TODO: Implementar llamada real a la API
            val movimientosFiltrados = movimientosEjemplo.filter { it.tipo == tipo }
            _movimientos.clear()
            _movimientos.addAll(movimientosFiltrados)
            _error.value = null
        } catch (e: Exception) {
            _error.value = "Error al cargar movimientos: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    fun registrarMovimiento(movimiento: MovimientoCreate): Boolean {
        return try {
            // TODO: Implementar llamada real a la API
            val nuevoMovimiento = Movimiento(
                id = (_movimientos.maxOfOrNull { it.id } ?: 0) + 1,
                tipo = movimiento.tipo,
                productoId = movimiento.productoId,
                empleadoId = movimiento.empleadoId,
                cantidad = movimiento.cantidad,
                nota = movimiento.nota,
                fechaRegistro = "2025-10-14T${System.currentTimeMillis() % 86400000 / 3600000}:${System.currentTimeMillis() % 3600000 / 60000}:00",
                createdAt = "2025-10-14T${System.currentTimeMillis() % 86400000 / 3600000}:${System.currentTimeMillis() % 3600000 / 60000}:00",
                updatedAt = "2025-10-14T${System.currentTimeMillis() % 86400000 / 3600000}:${System.currentTimeMillis() % 3600000 / 60000}:00",
                activo = true,
                productoNombre = "Producto ${movimiento.productoId}",
                productoCodigo = "PROD-${movimiento.productoId}",
                empleadoNombre = "Empleado ${movimiento.empleadoId}"
            )
            
            _movimientos.add(0, nuevoMovimiento) // Agregar al inicio
            _error.value = null
            true
        } catch (e: Exception) {
            _error.value = "Error al registrar movimiento: ${e.message}"
            false
        }
    }
    
    fun obtenerMovimientosPorProducto(productoId: Int): List<Movimiento> {
        return _movimientos.filter { it.productoId == productoId }
    }
    
    fun obtenerUltimosMovimientos(limit: Int = 10): List<Movimiento> {
        return _movimientos.take(limit)
    }
}