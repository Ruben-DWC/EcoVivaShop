package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_pedido")
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "numero_pedido", nullable = false, unique = true, length = 20)
    private String numeroPedido;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "impuestos", precision = 10, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "direccion_envio", nullable = false, length = 500)
    private String direccionEnvio;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "notas", length = 1000)
    private String notas;

    @Column(name = "fecha_estimada_entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "numero_seguimiento", length = 50)
    private String numeroSeguimiento;

    @Column(name = "transportadora", length = 50)
    private String transportadora;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PedidoDetalle> detalles;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Pago> pagos;

    @PrePersist
    public void prePersist() {
        if (this.fechaPedido == null) {
            this.fechaPedido = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
        if (this.numeroPedido == null) {
            this.numeroPedido = this.generarNumeroPedido();
        }
        if (this.subtotal == null) {
            this.subtotal = BigDecimal.ZERO;
        }
        if (this.descuento == null) {
            this.descuento = BigDecimal.ZERO;
        }
        if (this.impuestos == null) {
            this.impuestos = BigDecimal.ZERO;
        }
        if (this.costoEnvio == null) {
            this.costoEnvio = BigDecimal.ZERO;
        }
        if (this.total == null) {
            this.total = BigDecimal.ZERO;
        }
    }

    // Métodos de utilidad
    private String generarNumeroPedido() {
        return "EM" + System.currentTimeMillis();
    }

    public void calcularTotales() {
        if (this.detalles != null && !this.detalles.isEmpty()) {
            this.subtotal = this.detalles.stream()
                .map(PedidoDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        // Calcular impuestos (19% en Colombia)
        this.impuestos = this.subtotal.multiply(BigDecimal.valueOf(0.19));
        
        // Calcular total
        this.total = this.subtotal.add(this.impuestos).add(this.costoEnvio).subtract(this.descuento);
    }

    public boolean puedeSerCancelado() {
        return "PENDIENTE".equals(this.estado) || "CONFIRMADO".equals(this.estado);
    }

    public boolean estaCompleto() {
        return "ENTREGADO".equals(this.estado);
    }

    public boolean estaEnProceso() {
        return "CONFIRMADO".equals(this.estado) || "EN_PREPARACION".equals(this.estado) || "ENVIADO".equals(this.estado);
    }

    public void confirmar() {
        if ("PENDIENTE".equals(this.estado)) {
            this.estado = "CONFIRMADO";
        }
    }

    public void enviar(String numeroSeguimiento, String transportadora) {
        if ("CONFIRMADO".equals(this.estado) || "EN_PREPARACION".equals(this.estado)) {
            this.estado = "ENVIADO";
            this.numeroSeguimiento = numeroSeguimiento;
            this.transportadora = transportadora;
        }
    }

    public void entregar() {
        if ("ENVIADO".equals(this.estado)) {
            this.estado = "ENTREGADO";
            this.fechaEntrega = LocalDateTime.now();
        }
    }

    public void cancelar() {
        if (this.puedeSerCancelado()) {
            this.estado = "CANCELADO";
        }
    }    public Integer getCantidadItems() {
        return this.detalles != null ? this.detalles.stream()
            .mapToInt(PedidoDetalle::getCantidad)
            .sum() : 0;
    }    // Constructor personalizado para casos de uso específicos
    public Pedido(Usuario usuario, String numeroPedido, String estado) {
        this.usuario = usuario;
        this.numeroPedido = numeroPedido;
        this.estado = estado;
        this.fechaPedido = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.impuestos = BigDecimal.ZERO;
        this.costoEnvio = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }
    
    // ===== CUSTOM EQUALS AND HASHCODE TO AVOID CIRCULAR REFERENCE =====
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return java.util.Objects.equals(idPedido, pedido.idPedido);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idPedido);
    }
    
    // ===== GETTERS AND SETTERS =====
    
    public Integer getIdPedido() {
        return idPedido;
    }
    
    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
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
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    
    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }
    
    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    
    public String getDireccionEnvio() {
        return direccionEnvio;
    }
    
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }
    
    public String getTelefonoContacto() {
        return telefonoContacto;
    }
    
    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public BigDecimal getImpuestos() {
        return impuestos;
    }
    
    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }
    
    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }
    
    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public Set<PedidoDetalle> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(Set<PedidoDetalle> detalles) {
        this.detalles = detalles;
    }
    
    public LocalDateTime getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }
    
    public void setFechaEstimadaEntrega(LocalDateTime fechaEstimadaEntrega) {
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
    }
    
    public String getNumeroSeguimiento() {
        return numeroSeguimiento;
    }
    
    public void setNumeroSeguimiento(String numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
    }
    
    public String getTransportadora() {
        return transportadora;
    }
    
    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }
    
    public Set<Pago> getPagos() {
        return pagos;
    }
    
    public void setPagos(Set<Pago> pagos) {
        this.pagos = pagos;
    }
}
