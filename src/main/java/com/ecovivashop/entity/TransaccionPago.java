package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transacciones_pago")
public class TransaccionPago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Long idTransaccion;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @Column(name = "numero_pedido", unique = true)
    private String numeroPedido;
    
    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Column(name = "metodo_pago")
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;
    
    @Column(name = "estado_transaccion")
    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estadoTransaccion;
    
    @Column(name = "pasarela_pago")
    @Enumerated(EnumType.STRING)
    private PasarelaPago pasarelaPago;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "authorization_code")
    private String authorizationCode;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(name = "currency_code")
    private String currencyCode;
    
    @Column(name = "payment_token")
    private String paymentToken;
    
    // Datos del cliente
    @Column(name = "cliente_nombres")
    private String clienteNombres;
    
    @Column(name = "cliente_apellidos")
    private String clienteApellidos;
    
    @Column(name = "cliente_email")
    private String clienteEmail;
    
    @Column(name = "cliente_telefono")
    private String clienteTelefono;
    
    @Column(name = "cliente_documento_tipo")
    private String clienteDocumentoTipo;
    
    @Column(name = "cliente_documento_numero")
    private String clienteDocumentoNumero;
    
    // Dirección de envío
    @Column(name = "direccion_envio")
    private String direccionEnvio;
    
    @Column(name = "departamento")
    private String departamento;
    
    @Column(name = "provincia")
    private String provincia;
    
    @Column(name = "distrito")
    private String distrito;
    
    // Datos de tarjeta (encriptados)
    @Column(name = "tarjeta_numero_encriptado")
    private String tarjetaNumeroEncriptado;
    
    @Column(name = "tarjeta_nombre")
    private String tarjetaNombre;
    
    @Column(name = "tarjeta_mes_vencimiento")
    private String tarjetaMesVencimiento;
    
    @Column(name = "tarjeta_ano_vencimiento")
    private String tarjetaAnoVencimiento;
    
    // Fechas y timestamps
    @Column(name = "fecha_transaccion")
    private LocalDateTime fechaTransaccion;
    
    @Column(name = "fecha_autorizacion")
    private LocalDateTime fechaAutorizacion;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Información adicional
    @Column(name = "ip_cliente")
    private String ipCliente;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "mensaje_respuesta")
    private String mensajeRespuesta;
    
    @Column(name = "codigo_respuesta")
    private String codigoRespuesta;
    
    @Column(name = "intentos_pago")
    private Integer intentosPago;
    
    @Column(name = "es_suscripcion")
    private Boolean esSuscripcion;
    
    @Column(name = "id_suscripcion")
    private Long idSuscripcion;
    
    // Enums
    public enum MetodoPago {
        TARJETA_CREDITO("Tarjeta de Crédito"),
        TARJETA_DEBITO("Tarjeta de Débito"),
        YAPE("Yape"),
        PLIN("Plin"),
        TRANSFERENCIA("Transferencia Bancaria"),
        EFECTIVO("Efectivo"),
        BILLETERA_DIGITAL("Billetera Digital");
        
        private final String descripcion;
        
        MetodoPago(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum EstadoTransaccion {
        PENDIENTE("Pendiente"),
        PROCESANDO("Procesando"),
        AUTORIZADA("Autorizada"),
        COMPLETADA("Completada"),
        RECHAZADA("Rechazada"),
        CANCELADA("Cancelada"),
        REEMBOLSADA("Reembolsada"),
        ERROR("Error");
        
        private final String descripcion;
        
        EstadoTransaccion(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum PasarelaPago {
        CULQI("Culqi"),
        MERCADO_PAGO("MercadoPago"),
        PAYU("PayU"),
        IZIPAY("Izipay"),
        NIUBIZ("Niubiz"),
        PAYPAL("PayPal"),
        STRIPE("Stripe"),
        INTERNO("Sistema Interno");
        
        private final String descripcion;
        
        PasarelaPago(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Constructores
    public TransaccionPago() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estadoTransaccion = EstadoTransaccion.PENDIENTE;
        this.currencyCode = "PEN";
        this.intentosPago = 1;
        this.esSuscripcion = false;
    }
    
    // Getters y Setters
    public Long getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Long idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoTransaccion getEstadoTransaccion() {
        return estadoTransaccion;
    }

    public void setEstadoTransaccion(EstadoTransaccion estadoTransaccion) {
        this.estadoTransaccion = estadoTransaccion;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public PasarelaPago getPasarelaPago() {
        return pasarelaPago;
    }

    public void setPasarelaPago(PasarelaPago pasarelaPago) {
        this.pasarelaPago = pasarelaPago;
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
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

    public String getTarjetaNumeroEncriptado() {
        return tarjetaNumeroEncriptado;
    }

    public void setTarjetaNumeroEncriptado(String tarjetaNumeroEncriptado) {
        this.tarjetaNumeroEncriptado = tarjetaNumeroEncriptado;
    }

    public String getTarjetaNombre() {
        return tarjetaNombre;
    }

    public void setTarjetaNombre(String tarjetaNombre) {
        this.tarjetaNombre = tarjetaNombre;
    }

    public String getTarjetaMesVencimiento() {
        return tarjetaMesVencimiento;
    }

    public void setTarjetaMesVencimiento(String tarjetaMesVencimiento) {
        this.tarjetaMesVencimiento = tarjetaMesVencimiento;
    }

    public String getTarjetaAnoVencimiento() {
        return tarjetaAnoVencimiento;
    }

    public void setTarjetaAnoVencimiento(String tarjetaAnoVencimiento) {
        this.tarjetaAnoVencimiento = tarjetaAnoVencimiento;
    }

    public LocalDateTime getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public LocalDateTime getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getIpCliente() {
        return ipCliente;
    }

    public void setIpCliente(String ipCliente) {
        this.ipCliente = ipCliente;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public Integer getIntentosPago() {
        return intentosPago;
    }

    public void setIntentosPago(Integer intentosPago) {
        this.intentosPago = intentosPago;
    }

    public Boolean getEsSuscripcion() {
        return esSuscripcion;
    }

    public void setEsSuscripcion(Boolean esSuscripcion) {
        this.esSuscripcion = esSuscripcion;
    }

    public Long getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(Long idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    // Métodos de utilidad
    public boolean isExitosa() {
        return estadoTransaccion == EstadoTransaccion.COMPLETADA || 
               estadoTransaccion == EstadoTransaccion.AUTORIZADA;
    }
    
    public boolean isPendiente() {
        return estadoTransaccion == EstadoTransaccion.PENDIENTE || 
               estadoTransaccion == EstadoTransaccion.PROCESANDO;
    }
    
    public boolean isFallida() {
        return estadoTransaccion == EstadoTransaccion.RECHAZADA || 
               estadoTransaccion == EstadoTransaccion.ERROR || 
               estadoTransaccion == EstadoTransaccion.CANCELADA;
    }
    
    public String getNumeroTarjetaEnmascarado() {
        if (tarjetaNumeroEncriptado != null && tarjetaNumeroEncriptado.length() >= 4) {
            return "**** **** **** " + tarjetaNumeroEncriptado.substring(tarjetaNumeroEncriptado.length() - 4);
        }
        return "****";
    }
    
    @Override
    public String toString() {
        return "TransaccionPago{" +
                "idTransaccion=" + idTransaccion +
                ", numeroPedido='" + numeroPedido + '\'' +
                ", monto=" + monto +
                ", metodoPago=" + metodoPago +
                ", estadoTransaccion=" + estadoTransaccion +
                ", pasarelaPago=" + pasarelaPago +
                '}';
    }
}
