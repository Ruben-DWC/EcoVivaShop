package com.ecovivashop.service;

import java.math.BigDecimal;

import com.ecovivashop.entity.TransaccionPago.MetodoPago;

/**
 * Clase que encapsula la información de una solicitud de pago
 */
public class SolicitudPago {
    
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String descripcion;
    
    // Datos del cliente
    private String clienteNombres;
    private String clienteApellidos;
    private String clienteEmail;
    private String clienteTelefono;
    private String clienteDocumentoTipo;
    private String clienteDocumentoNumero;
    
    // Dirección de envío
    private String direccionEnvio;
    private String departamento;
    private String provincia;
    private String distrito;
    
    // Datos de tarjeta (para pagos con tarjeta)
    private String numeroTarjeta;
    private String nombreTarjeta;
    private String mesExpiracion;
    private String anoExpiracion;
    private String cvv;
    
    // Datos para pagos móviles (Yape/Plin)
    private String numeroTelefonoMovil;
    private String codigoConfirmacion;
    
    // URL de retorno para checkout
    private String urlRetorno;
    private String urlCancelacion;
    
    // Constructor vacío
    public SolicitudPago() {}
    
    // Constructor básico
    public SolicitudPago(MetodoPago metodoPago, BigDecimal monto, String descripcion) {
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.descripcion = descripcion;
    }
    
    // Constructor completo para tarjeta
    public SolicitudPago(MetodoPago metodoPago, BigDecimal monto, String descripcion,
                        String clienteNombres, String clienteApellidos, String clienteEmail,
                        String numeroTarjeta, String nombreTarjeta, String mesExpiracion, 
                        String anoExpiracion, String cvv) {
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.descripcion = descripcion;
        this.clienteNombres = clienteNombres;
        this.clienteApellidos = clienteApellidos;
        this.clienteEmail = clienteEmail;
        this.numeroTarjeta = numeroTarjeta;
        this.nombreTarjeta = nombreTarjeta;
        this.mesExpiracion = mesExpiracion;
        this.anoExpiracion = anoExpiracion;
        this.cvv = cvv;
    }
    
    // Métodos de validación
    public boolean esValidoParaTarjeta() {
        return metodoPago != null && 
               (metodoPago == MetodoPago.TARJETA_CREDITO || metodoPago == MetodoPago.TARJETA_DEBITO) &&
               numeroTarjeta != null && !numeroTarjeta.isEmpty() &&
               nombreTarjeta != null && !nombreTarjeta.isEmpty() &&
               mesExpiracion != null && !mesExpiracion.isEmpty() &&
               anoExpiracion != null && !anoExpiracion.isEmpty() &&
               cvv != null && !cvv.isEmpty() &&
               clienteEmail != null && !clienteEmail.isEmpty();
    }
    
    public boolean esValidoParaMovil() {
        return metodoPago != null && 
               (metodoPago == MetodoPago.YAPE || metodoPago == MetodoPago.PLIN) &&
               numeroTelefonoMovil != null && !numeroTelefonoMovil.isEmpty() &&
               clienteEmail != null && !clienteEmail.isEmpty();
    }
    
    public boolean esValidoParaEfectivo() {
        return metodoPago == MetodoPago.EFECTIVO &&
               clienteNombres != null && !clienteNombres.isEmpty() &&
               clienteEmail != null && !clienteEmail.isEmpty() &&
               direccionEnvio != null && !direccionEnvio.isEmpty();
    }
    
    // Getters y Setters
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getClienteNombres() {
        return clienteNombres;
    }
    
    public void setClienteNombres(String clienteNombres) {
        this.clienteNombres = clienteNombres;
    }
    
    public String getClienteApellidos() {
        return clienteApellidos;
    }
    
    public void setClienteApellidos(String clienteApellidos) {
        this.clienteApellidos = clienteApellidos;
    }
    
    public String getClienteEmail() {
        return clienteEmail;
    }
    
    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }
    
    public String getClienteTelefono() {
        return clienteTelefono;
    }
    
    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }
    
    public String getClienteDocumentoTipo() {
        return clienteDocumentoTipo;
    }
    
    public void setClienteDocumentoTipo(String clienteDocumentoTipo) {
        this.clienteDocumentoTipo = clienteDocumentoTipo;
    }
    
    public String getClienteDocumentoNumero() {
        return clienteDocumentoNumero;
    }
    
    public void setClienteDocumentoNumero(String clienteDocumentoNumero) {
        this.clienteDocumentoNumero = clienteDocumentoNumero;
    }
    
    public String getDireccionEnvio() {
        return direccionEnvio;
    }
    
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getDistrito() {
        return distrito;
    }
    
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
    
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }
    
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
    
    public String getNombreTarjeta() {
        return nombreTarjeta;
    }
    
    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }
    
    public String getMesExpiracion() {
        return mesExpiracion;
    }
    
    public void setMesExpiracion(String mesExpiracion) {
        this.mesExpiracion = mesExpiracion;
    }
    
    public String getAnoExpiracion() {
        return anoExpiracion;
    }
    
    public void setAnoExpiracion(String anoExpiracion) {
        this.anoExpiracion = anoExpiracion;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public String getNumeroTelefonoMovil() {
        return numeroTelefonoMovil;
    }
    
    public void setNumeroTelefonoMovil(String numeroTelefonoMovil) {
        this.numeroTelefonoMovil = numeroTelefonoMovil;
    }
    
    public String getCodigoConfirmacion() {
        return codigoConfirmacion;
    }
    
    public void setCodigoConfirmacion(String codigoConfirmacion) {
        this.codigoConfirmacion = codigoConfirmacion;
    }
    
    public String getUrlRetorno() {
        return urlRetorno;
    }
    
    public void setUrlRetorno(String urlRetorno) {
        this.urlRetorno = urlRetorno;
    }
    
    public String getUrlCancelacion() {
        return urlCancelacion;
    }
    
    public void setUrlCancelacion(String urlCancelacion) {
        this.urlCancelacion = urlCancelacion;
    }
}
