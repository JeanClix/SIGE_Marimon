package org.marimon.sigc.services

import org.marimon.sigc.model.Transaccion

/**
 * Interfaz común para servicios de PDF en multiplataforma
 */
expect class PDFService {
    suspend fun downloadPDF(transaccion: Transaccion): Boolean
    suspend fun downloadPDFWithEmail(transaccion: Transaccion, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}): Boolean
}

/**
 * Implementación común que delega a la implementación específica de la plataforma
 */
object PDFServiceManager {
    private var pdfService: PDFService? = null
    
    fun initialize(service: PDFService) {
        pdfService = service
    }
    
    suspend fun downloadPDF(transaccion: Transaccion): Boolean {
        return pdfService?.downloadPDF(transaccion) ?: false
    }
    
    suspend fun downloadPDFWithEmail(transaccion: Transaccion, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}): Boolean {
        return pdfService?.downloadPDFWithEmail(transaccion, onSuccess, onError) ?: false
    }
}
