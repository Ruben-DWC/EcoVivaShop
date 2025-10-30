package com.ecovivashop.service;

import java.time.LocalDateTime;

import com.ecovivashop.entity.TransaccionPago;

/**
 * Clase que encapsula la respuesta de un procesamiento de pago
 */
public class RespuestaPago {
    
    private boolean exitoso;
    private String mensaje;
    private String codigoError;
    private String transactionId;
    private String authorizationCode;
    private String referenceNumber;
    private TransaccionPago transaccion;
    private LocalDateTime fechaProcesamiento;
    
    // Datos específicos para diferentes tipos de respuesta
    private String urlRedireccion; // Para checkouts que requieren redirección
    private String codigoQr; // Para pagos móviles
    private String codigoVerificacion; // Para confirmaciones
    
    // Constructor para respuesta exitosa
    public RespuestaPago(boolean exitoso, String mensaje, TransaccionPago transaccion) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.transaccion = transaccion;
        this.fechaProcesamiento = LocalDateTime.now();
        
        if (transaccion != null) {
            this.transactionId = transaccion.getTransactionId();
            this.authorizationCode = transaccion.getAuthorizationCode();
            this.referenceNumber = transaccion.getReferenceNumber();
        }
    }
    
    // Constructor para respuesta con error
    public RespuestaPago(boolean exitoso, String mensaje, String codigoError) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.codigoError = codigoError;
        this.fechaProcesamiento = LocalDateTime.now();
    }
    
    // Constructor completo
    public RespuestaPago(boolean exitoso, String mensaje, String codigoError, 
                        String transactionId, String authorizationCode, 
                        String referenceNumber, TransaccionPago transaccion) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.codigoError = codigoError;
        this.transactionId = transactionId;
        this.authorizationCode = authorizationCode;
        this.referenceNumber = referenceNumber;
        this.transaccion = transaccion;
        this.fechaProcesamiento = LocalDateTime.now();
    }
    
    // Métodos estáticos para crear respuestas comunes
    public static RespuestaPago exitoso(String mensaje, TransaccionPago transaccion) {
        return new RespuestaPago(true, mensaje, transaccion);
    }
    
    public static RespuestaPago error(String mensaje, String codigoError) {
        return new RespuestaPago(false, mensaje, codigoError);
    }
    
    public static RespuestaPago error(String mensaje) {
        return new RespuestaPago(false, mensaje, (String) null);
    }
    
    public static RespuestaPago pendiente(String mensaje, TransaccionPago transaccion) {
        RespuestaPago respuesta = new RespuestaPago(true, mensaje, transaccion);
        // Se considera exitoso pero pendiente de confirmación
        return respuesta;
    }
    
    public static RespuestaPago conRedireccion(String mensaje, String urlRedireccion, TransaccionPago transaccion) {
        RespuestaPago respuesta = new RespuestaPago(true, mensaje, transaccion);
        respuesta.setUrlRedireccion(urlRedireccion);
        return respuesta;
    }
    
    public static RespuestaPago conCodigoQr(String mensaje, String codigoQr, TransaccionPago transaccion) {
        RespuestaPago respuesta = new RespuestaPago(true, mensaje, transaccion);
        respuesta.setCodigoQr(codigoQr);
        return respuesta;
    }
    
    // Métodos de utilidad
    public boolean requiereRedireccion() {
        return urlRedireccion != null && !urlRedireccion.isEmpty();
    }
    
    public boolean requiereCodigoQr() {
        return codigoQr != null && !codigoQr.isEmpty();
    }
    
    public boolean requiereConfirmacion() {
        return codigoVerificacion != null && !codigoVerificacion.isEmpty();
    }
    
    public String getEstadoDescripcion() {
        if (exitoso) {
            if (requiereRedireccion()) {
                return "Redirigiendo a pasarela de pago";
            } else if (requiereCodigoQr()) {
                return "Esperando pago móvil";
            } else if (requiereConfirmacion()) {
                return "Pendiente de confirmación";
            } else {
                return "Pago procesado exitosamente";
            }
        } else {
            return "Error en el procesamiento: " + mensaje;
        }
    }
    
    // Getters y Setters
    public boolean isExitoso() {
        return exitoso;
    }
    
    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getCodigoError() {
        return codigoError;
    }
    
    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getAuthorizationCode() {
        return authorizationCode;
    }
    
    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public TransaccionPago getTransaccion() {
        return transaccion;
    }
    
    public void setTransaccion(TransaccionPago transaccion) {
        this.transaccion = transaccion;
    }
    
    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }
    
    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
    
    public String getUrlRedireccion() {
        return urlRedireccion;
    }
    
    public void setUrlRedireccion(String urlRedireccion) {
        this.urlRedireccion = urlRedireccion;
    }
    
    public String getCodigoQr() {
        return codigoQr;
    }
    
    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }
    
    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }
    
    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
    
    @Override
    public String toString() {
        return "RespuestaPago{" +
                "exitoso=" + exitoso +
                ", mensaje='" + mensaje + '\'' +
                ", codigoError='" + codigoError + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", authorizationCode='" + authorizationCode + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", fechaProcesamiento=" + fechaProcesamiento +
                '}';
    }
}
