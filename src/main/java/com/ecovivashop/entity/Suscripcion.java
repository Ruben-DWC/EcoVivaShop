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
@Table(name = "tb_suscripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suscripcion")
    private Integer idSuscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_suscripcion", nullable = false, length = 20)
    private String tipoSuscripcion;

    @Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Column(name = "beneficios", length = 1000)
    private String beneficios;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "auto_renovacion", nullable = false)
    private Boolean autoRenovacion = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.fechaInicio == null) {
            this.fechaInicio = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = true;
        }
        if (this.autoRenovacion == null) {
            this.autoRenovacion = false;
        }
    }

    // Métodos de utilidad
    public boolean estaActiva() {
        LocalDateTime ahora = LocalDateTime.now();
        return this.estado && this.fechaInicio.isBefore(ahora) && 
               (this.fechaFin == null || this.fechaFin.isAfter(ahora));
    }

    public boolean proximaAVencer() {
        if (this.fechaFin == null) return false;
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime en7Dias = ahora.plusDays(7);
        return this.fechaFin.isBefore(en7Dias) && this.fechaFin.isAfter(ahora);
    }

    public boolean vencida() {
        if (this.fechaFin == null) return false;
        return this.fechaFin.isBefore(LocalDateTime.now());
    }

    public void cancelar(String motivo) {
        this.estado = false;
        this.fechaCancelacion = LocalDateTime.now();
        this.motivoCancelacion = motivo;
    }

    public void renovar(int meses) {
        if (this.fechaFin == null) {
            this.fechaFin = LocalDateTime.now().plusMonths(meses);
        } else {
            this.fechaFin = this.fechaFin.plusMonths(meses);
        }
    }    public String getEstadoSuscripcion() {
        if (!this.estado) {
            return "CANCELADA";
        } else if (this.vencida()) {
            return "VENCIDA";
        } else if (this.proximaAVencer()) {
            return "POR_VENCER";
        } else {
            return "ACTIVA";
        }
    }    // Constructor personalizado para casos de uso específicos
    public Suscripcion(Usuario usuario, String tipoSuscripcion, BigDecimal precioMensual) {
        this.usuario = usuario;
        this.tipoSuscripcion = tipoSuscripcion;
        this.precioMensual = precioMensual;
        this.estado = true;
        this.autoRenovacion = true;
        this.fechaInicio = LocalDateTime.now();
        this.fechaCreacion = LocalDateTime.now();
    }
}
