package com.ecovivashop.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_pedido_detalle")
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento_unitario", precision = 10, scale = 2)
    private BigDecimal descuentoUnitario = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "notas", length = 500)
    private String notas;

    @PrePersist
    public void prePersist() {
        if (this.descuentoUnitario == null) {
            this.descuentoUnitario = BigDecimal.ZERO;
        }
        this.calcularSubtotal();
    }

    @PreUpdate
    public void preUpdate() {
        this.calcularSubtotal();
    }

    // Métodos de utilidad
    public void calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            BigDecimal precioConDescuento = this.precioUnitario.subtract(this.descuentoUnitario);
            this.subtotal = precioConDescuento.multiply(BigDecimal.valueOf(this.cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalDescuento() {
        return this.descuentoUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }

    public BigDecimal getPrecioFinalUnitario() {
        return this.precioUnitario.subtract(this.descuentoUnitario);
    }

    public BigDecimal getAhorro() {
        return this.getTotalDescuento();
    }

    public boolean tieneDescuento() {
        return this.descuentoUnitario != null && this.descuentoUnitario.compareTo(BigDecimal.ZERO) > 0;
    }    public BigDecimal getPorcentajeDescuento() {
        if (this.precioUnitario == null || this.precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }        return this.descuentoUnitario.divide(this.precioUnitario, 4, java.math.RoundingMode.HALF_UP)
               .multiply(BigDecimal.valueOf(100));
    }

    // Constructor personalizado para casos de uso específicos
    public PedidoDetalle(Pedido pedido, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoUnitario = BigDecimal.ZERO;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    // ===== CUSTOM EQUALS AND HASHCODE TO AVOID CIRCULAR REFERENCE =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoDetalle that = (PedidoDetalle) o;
        return java.util.Objects.equals(idDetalle, that.idDetalle);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idDetalle);
    }
    
    // ===== GETTERS AND SETTERS =====
    
    public Integer getIdDetalle() {
        return idDetalle;
    }
    
    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getDescuentoUnitario() {
        return descuentoUnitario;
    }
    
    public void setDescuentoUnitario(BigDecimal descuentoUnitario) {
        this.descuentoUnitario = descuentoUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
}
