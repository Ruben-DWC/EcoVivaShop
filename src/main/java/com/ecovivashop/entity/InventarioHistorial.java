package com.ecovivashop.entity;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_inventario_historial")
@NoArgsConstructor
@AllArgsConstructor
public class InventarioHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventario", nullable = false)
    private Inventario inventario;

    @Column(name = "tipo_cambio", length = 50, nullable = false)
    private String tipoCambio; // 'AUMENTO', 'DISMINUCION', 'ACTUALIZACION', 'CREACION'

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;

    @Column(name = "cambio_cantidad")
    private Integer cambioCantidad; // Puede ser positivo (aumento) o negativo (disminución)

    @Column(name = "stock_minimo_anterior")
    private Integer stockMinimoAnterior;

    @Column(name = "stock_minimo_nuevo")
    private Integer stockMinimoNuevo;

    @Column(name = "stock_maximo_anterior")
    private Integer stockMaximoAnterior;

    @Column(name = "stock_maximo_nuevo")
    private Integer stockMaximoNuevo;

    @Column(name = "ubicacion_anterior", length = 100)
    private String ubicacionAnterior;

    @Column(name = "ubicacion_nueva", length = 100)
    private String ubicacionNueva;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "usuario", length = 100, nullable = false)
    private String usuario;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "ip_usuario", length = 45)
    private String ipUsuario;

    @PrePersist
    public void prePersist() {
        if (this.fechaCambio == null) {
            this.fechaCambio = LocalDateTime.now();
        }
    }

    // Constructor para cambios de stock
    public InventarioHistorial(Inventario inventario, String tipoCambio, Integer stockAnterior,
                              Integer stockNuevo, Integer cambioCantidad, String motivo, String usuario) {
        this.inventario = inventario;
        this.tipoCambio = tipoCambio;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.cambioCantidad = cambioCantidad;
        this.motivo = motivo;
        this.usuario = usuario;
        this.fechaCambio = LocalDateTime.now();
    }

    // Constructor para cambios de configuración
    public InventarioHistorial(Inventario inventario, String tipoCambio,
                              Integer stockMinimoAnterior, Integer stockMinimoNuevo,
                              Integer stockMaximoAnterior, Integer stockMaximoNuevo,
                              String ubicacionAnterior, String ubicacionNueva,
                              String motivo, String usuario) {
        this.inventario = inventario;
        this.tipoCambio = tipoCambio;
        this.stockMinimoAnterior = stockMinimoAnterior;
        this.stockMinimoNuevo = stockMinimoNuevo;
        this.stockMaximoAnterior = stockMaximoAnterior;
        this.stockMaximoNuevo = stockMaximoNuevo;
        this.ubicacionAnterior = ubicacionAnterior;
        this.ubicacionNueva = ubicacionNueva;
        this.motivo = motivo;
        this.usuario = usuario;
        this.fechaCambio = LocalDateTime.now();
        // Para cambios de configuración, el stock no cambia
        this.stockAnterior = inventario.getStock();
        this.stockNuevo = inventario.getStock();
        this.cambioCantidad = 0;
    }

    // ===== GETTERS AND SETTERS =====

    public Integer getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public Integer getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public Integer getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public Integer getCambioCantidad() {
        return cambioCantidad;
    }

    public void setCambioCantidad(Integer cambioCantidad) {
        this.cambioCantidad = cambioCantidad;
    }

    public Integer getStockMinimoAnterior() {
        return stockMinimoAnterior;
    }

    public void setStockMinimoAnterior(Integer stockMinimoAnterior) {
        this.stockMinimoAnterior = stockMinimoAnterior;
    }

    public Integer getStockMinimoNuevo() {
        return stockMinimoNuevo;
    }

    public void setStockMinimoNuevo(Integer stockMinimoNuevo) {
        this.stockMinimoNuevo = stockMinimoNuevo;
    }

    public Integer getStockMaximoAnterior() {
        return stockMaximoAnterior;
    }

    public void setStockMaximoAnterior(Integer stockMaximoAnterior) {
        this.stockMaximoAnterior = stockMaximoAnterior;
    }

    public Integer getStockMaximoNuevo() {
        return stockMaximoNuevo;
    }

    public void setStockMaximoNuevo(Integer stockMaximoNuevo) {
        this.stockMaximoNuevo = stockMaximoNuevo;
    }

    public String getUbicacionAnterior() {
        return ubicacionAnterior;
    }

    public void setUbicacionAnterior(String ubicacionAnterior) {
        this.ubicacionAnterior = ubicacionAnterior;
    }

    public String getUbicacionNueva() {
        return ubicacionNueva;
    }

    public void setUbicacionNueva(String ubicacionNueva) {
        this.ubicacionNueva = ubicacionNueva;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getIpUsuario() {
        return ipUsuario;
    }

    public void setIpUsuario(String ipUsuario) {
        this.ipUsuario = ipUsuario;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    public String getTipoCambioFormateado() {
        return switch (this.tipoCambio) {
            case "AUMENTO" -> "Aumento de Stock";
            case "DISMINUCION" -> "Disminución de Stock";
            case "ACTUALIZACION" -> "Actualización de Configuración";
            case "CREACION" -> "Creación de Inventario";
            default -> this.tipoCambio;
        };
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public String getCambioFormateado() {
        if ("AUMENTO".equals(this.tipoCambio) || "DISMINUCION".equals(this.tipoCambio)) {
            return String.format("%+d unidades", this.cambioCantidad);
        } else if ("ACTUALIZACION".equals(this.tipoCambio)) {
            StringBuilder cambios = new StringBuilder();
            if (!java.util.Objects.equals(this.stockMinimoAnterior, this.stockMinimoNuevo)) {
                cambios.append(String.format("Stock mínimo: %d → %d",
                    this.stockMinimoAnterior != null ? this.stockMinimoAnterior.intValue() : 0,
                    this.stockMinimoNuevo != null ? this.stockMinimoNuevo.intValue() : 0));
            }
            if (!java.util.Objects.equals(this.stockMaximoAnterior, this.stockMaximoNuevo)) {
                if (cambios.length() > 0) cambios.append(", ");
                cambios.append(String.format("Stock máximo: %s → %s",
                    this.stockMaximoAnterior != null ? this.stockMaximoAnterior.toString() : "Sin límite",
                    this.stockMaximoNuevo != null ? this.stockMaximoNuevo.toString() : "Sin límite"));
            }
            if (!java.util.Objects.equals(this.ubicacionAnterior, this.ubicacionNueva)) {
                if (cambios.length() > 0) cambios.append(", ");
                cambios.append(String.format("Ubicación: %s → %s",
                    this.ubicacionAnterior != null ? this.ubicacionAnterior : "Sin ubicación",
                    this.ubicacionNueva != null ? this.ubicacionNueva : "Sin ubicación"));
            }
            return cambios.length() > 0 ? cambios.toString() : "Sin cambios específicos";
        }
        return "";
    }
}