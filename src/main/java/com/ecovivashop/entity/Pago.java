package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "numero_transaccion", length = 100)
    private String numeroTransaccion;

    @Column(name = "entidad_financiera", length = 100)
    private String entidadFinanciera;

    @Column(name = "numero_aprobacion", length = 50)
    private String numeroAprobacion;

    @Column(name = "codigo_respuesta", length = 10)
    private String codigoRespuesta;

    @Column(name = "mensaje_respuesta", length = 500)
    private String mensajeRespuesta;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "ip_cliente", length = 45)
    private String ipCliente;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "datos_adicionales", length = 1000)
    private String datosAdicionales;

    @PrePersist
    public void prePersist() {
        if (this.fechaPago == null) {
            this.fechaPago = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
        if (this.referenciaPago == null) {
            this.referenciaPago = this.generarReferenciaPago();
        }
    }

    // Métodos de utilidad
    private String generarReferenciaPago() {
        return "PAY" + System.currentTimeMillis();
    }

    public void aprobar(String numeroAprobacion, String numeroTransaccion) {
        this.estado = "APROBADO";
        this.numeroAprobacion = numeroAprobacion;
        this.numeroTransaccion = numeroTransaccion;
        this.fechaAprobacion = LocalDateTime.now();
    }

    public void rechazar(String codigoRespuesta, String mensajeRespuesta) {
        this.estado = "RECHAZADO";
        this.codigoRespuesta = codigoRespuesta;
        this.mensajeRespuesta = mensajeRespuesta;
    }

    public void cancelar() {
        if ("PENDIENTE".equals(this.estado)) {
            this.estado = "CANCELADO";
        }
    }

    public boolean estaAprobado() {
        return "APROBADO".equals(this.estado);
    }

    public boolean estaPendiente() {
        return "PENDIENTE".equals(this.estado);
    }

    public boolean estaRechazado() {
        return "RECHAZADO".equals(this.estado);
    }

    public boolean estaCancelado() {
        return "CANCELADO".equals(this.estado);
    }

    public boolean esExitoso() {
        return this.estaAprobado();
    }

    public String getDescripcionEstado() {
        switch (this.estado) {
            case "APROBADO":
                return "Pago aprobado exitosamente";
            case "RECHAZADO":
                return "Pago rechazado: " + (this.mensajeRespuesta != null ? this.mensajeRespuesta : "Sin especificar");
            case "CANCELADO":
                return "Pago cancelado";
            case "PENDIENTE":
            default:
                return "Pago en proceso";
        }
    }

    public boolean esTarjetaCredito() {
        return "TARJETA_CREDITO".equals(this.metodoPago);
    }

    public boolean esTarjetaDebito() {
        return "TARJETA_DEBITO".equals(this.metodoPago);
    }

    public boolean esPSE() {
        return "PSE".equals(this.metodoPago);
    }

    public boolean esEfectivo() {
        return "EFECTIVO".equals(this.metodoPago);
    }    // Constructor personalizado para casos de uso específicos
    public Pago(Pedido pedido, String metodoPago, BigDecimal monto, String estado) {
        this.pedido = pedido;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;        this.fechaPago = LocalDateTime.now();
    }
}
