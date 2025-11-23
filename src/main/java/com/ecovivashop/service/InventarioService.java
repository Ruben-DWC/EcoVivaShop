package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.InventarioRepository;
import com.ecovivashop.repository.ProductoRepository;

@Service
@Transactional
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    // Constructor manual
    public InventarioService(InventarioRepository inventarioRepository, ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }

    // Métodos CRUD básicos
    public List<Inventario> findAll() {
        return this.inventarioRepository.findAll();
    }
    
    public Optional<Inventario> findById(Integer id) {
        return this.inventarioRepository.findById(id);
    }
    
    public Optional<Inventario> findByProductoId(Integer idProducto) {
        return this.inventarioRepository.findByProductoId(idProducto);
    }
    
    public Inventario save(Inventario inventario) {
        return this.inventarioRepository.save(inventario);
    }
    
    // Métodos de negocio
    public List<Inventario> obtenerInventariosActivos() {
        return this.inventarioRepository.findByEstadoTrue();
    }
    
    public List<Inventario> obtenerConStock() {
        return this.inventarioRepository.findConStock();
    }
    
    public List<Inventario> obtenerAgotados() {
        return this.inventarioRepository.findAgotados();
    }
    
    public List<Inventario> obtenerConStockBajo() {
        return this.inventarioRepository.findConStockBajo();
    }
    
    public List<Inventario> obtenerEnEstadoCritico() {
        return this.inventarioRepository.findEnEstadoCritico();
    }
    
    public List<Inventario> obtenerAlertasInventario() {
        return this.inventarioRepository.obtenerAlertasInventario();
    }
    
    // Gestión de stock
    public void actualizarStock(Integer idProducto, Integer nuevoStock, String usuarioActualizacion) {
        Optional<Inventario> inventarioExistente = this.inventarioRepository.findByProductoId(idProducto);
        
        if (inventarioExistente.isEmpty()) {
            throw new RuntimeException("Inventario no encontrado para el producto ID: " + idProducto);
        }
        
        Inventario inventario = inventarioExistente.get();
        inventario.setStock(nuevoStock);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion(usuarioActualizacion);
        
        this.inventarioRepository.save(inventario);
    }
    
    public void reducirStock(Integer idProducto, Integer cantidad, String usuarioActualizacion) {
        System.out.println("🔄 [SERVICE] reducirStock called - idProducto: " + idProducto + ", cantidad: " + cantidad + ", usuario: " + usuarioActualizacion);

        Optional<Inventario> inventarioExistente = this.inventarioRepository.findByProductoId(idProducto);

        if (inventarioExistente.isEmpty()) {
            System.err.println("❌ [SERVICE] Inventario no encontrado para el producto ID: " + idProducto);
            throw new RuntimeException("Inventario no encontrado para el producto ID: " + idProducto);
        }

        System.out.println("✅ [SERVICE] Inventario encontrado, stock actual: " + inventarioExistente.get().getStock());

        Inventario inventario = inventarioExistente.get();

        if (inventario.getStock() < cantidad) {
            System.err.println("❌ [SERVICE] Stock insuficiente. Disponible: " + inventario.getStock() + ", Solicitado: " + cantidad);
            throw new RuntimeException("Stock insuficiente. Disponible: " + inventario.getStock() + ", Solicitado: " + cantidad);
        }

        inventario.reducirStock(cantidad);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion(usuarioActualizacion);

        System.out.println("🔄 [SERVICE] Saving inventario with new stock: " + inventario.getStock());
        this.inventarioRepository.save(inventario);

        System.out.println("✅ [SERVICE] reducirStock completed successfully");
    }
    
    public void aumentarStock(Integer idProducto, Integer cantidad, String usuarioActualizacion) {
        System.out.println("🔄 [SERVICE] aumentarStock called - idProducto: " + idProducto + ", cantidad: " + cantidad + ", usuario: " + usuarioActualizacion);

        Optional<Inventario> inventarioExistente = this.inventarioRepository.findByProductoId(idProducto);

        if (inventarioExistente.isEmpty()) {
            System.err.println("❌ [SERVICE] Inventario no encontrado para el producto ID: " + idProducto);
            throw new RuntimeException("Inventario no encontrado para el producto ID: " + idProducto);
        }

        System.out.println("✅ [SERVICE] Inventario encontrado, stock actual: " + inventarioExistente.get().getStock());

        Inventario inventario = inventarioExistente.get();
        inventario.aumentarStock(cantidad);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion(usuarioActualizacion);

        System.out.println("🔄 [SERVICE] Saving inventario with new stock: " + inventario.getStock());
        this.inventarioRepository.save(inventario);

        System.out.println("✅ [SERVICE] aumentarStock completed successfully");
    }
    
    public void ajustarStock(Integer idProducto, Integer cantidadAjuste, String motivo, String usuarioActualizacion) {
        System.out.println("🔄 [SERVICE] ajustarStock called - idProducto: " + idProducto + ", cantidadAjuste: " + cantidadAjuste + ", motivo: " + motivo + ", usuario: " + usuarioActualizacion);

        if (cantidadAjuste > 0) {
            System.out.println("🔄 [SERVICE] Calling aumentarStock with cantidad: " + cantidadAjuste);
            this.aumentarStock(idProducto, cantidadAjuste, usuarioActualizacion + " - " + motivo);
        } else if (cantidadAjuste < 0) {
            System.out.println("🔄 [SERVICE] Calling reducirStock with cantidad: " + Math.abs(cantidadAjuste));
            this.reducirStock(idProducto, Math.abs(cantidadAjuste), usuarioActualizacion + " - " + motivo);
        } else {
            System.out.println("⚠️ [SERVICE] cantidadAjuste is zero, no adjustment needed");
        }

        System.out.println("✅ [SERVICE] ajustarStock completed successfully");
    }
    
    // Crear inventario para nuevo producto
    public Inventario crearInventario(Integer idProducto, Integer stockInicial, Integer stockMinimo, 
                                     Integer stockMaximo, String ubicacion, String usuarioCreacion) {
        
        Optional<Producto> producto = this.productoRepository.findById(idProducto);
        if (producto.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + idProducto);
        }
        
        // Verificar si ya existe inventario para este producto
        Optional<Inventario> inventarioExistente = this.inventarioRepository.findByProductoId(idProducto);
        if (inventarioExistente.isPresent()) {
            throw new RuntimeException("Ya existe inventario para el producto ID: " + idProducto);
        }
        
        Inventario inventario = new Inventario();
        inventario.setProducto(producto.get());
        inventario.setStock(stockInicial != null ? stockInicial : 0);
        inventario.setStockMinimo(stockMinimo != null ? stockMinimo : 5);
        inventario.setStockMaximo(stockMaximo);
        inventario.setUbicacion(ubicacion);
        inventario.setEstado(true);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion(usuarioCreacion);
        
        return this.inventarioRepository.save(inventario);
    }
    
    // Actualizar configuración de inventario
    public Inventario actualizarConfiguracion(Integer idInventario, Integer stockMinimo, Integer stockMaximo, 
                                            String ubicacion, String usuarioActualizacion) {
        
        Optional<Inventario> inventarioExistente = this.inventarioRepository.findById(idInventario);
        if (inventarioExistente.isEmpty()) {
            throw new RuntimeException("Inventario no encontrado con ID: " + idInventario);
        }
        
        Inventario inventario = inventarioExistente.get();
        inventario.setStockMinimo(stockMinimo);
        inventario.setStockMaximo(stockMaximo);
        inventario.setUbicacion(ubicacion);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion(usuarioActualizacion);
        
        return this.inventarioRepository.save(inventario);
    }
    
    // Validaciones
    public boolean verificarDisponibilidad(Integer idProducto, Integer cantidad) {
        Boolean disponibilidad = this.inventarioRepository.verificarDisponibilidad(idProducto, cantidad);
        return disponibilidad != null ? disponibilidad : false;
    }
    
    public Integer obtenerStockDisponible(Integer idProducto) {
        Optional<Inventario> inventario = this.inventarioRepository.findByProductoId(idProducto);
        return inventario.map(Inventario::getStock).orElse(0);
    }
    
    public boolean necesitaReposicion(Integer idProducto) {
        Optional<Inventario> inventario = this.inventarioRepository.findByProductoId(idProducto);
        return inventario.map(Inventario::necesitaReposicion).orElse(false);
    }
    
    public boolean estaAgotado(Integer idProducto) {
        Optional<Inventario> inventario = this.inventarioRepository.findByProductoId(idProducto);
        return inventario.map(Inventario::agotado).orElse(true);
    }
    
    public boolean stockCritico(Integer idProducto) {
        Optional<Inventario> inventario = this.inventarioRepository.findByProductoId(idProducto);
        return inventario.map(Inventario::stockCritico).orElse(false);
    }
    
    public String obtenerEstadoStock(Integer idProducto) {
        Optional<Inventario> inventario = this.inventarioRepository.findByProductoId(idProducto);
        return inventario.map(Inventario::getEstadoStock).orElse("NO_DISPONIBLE");
    }
    
    // Estadísticas
    public Long contarConStock() {
        return this.inventarioRepository.contarConStock();
    }
    
    public Long contarAgotados() {
        return this.inventarioRepository.contarAgotados();
    }
    
    public Long contarConStockBajo() {
        return this.inventarioRepository.contarConStockBajo();
    }
    
    public Long obtenerStockTotal() {
        return this.inventarioRepository.obtenerStockTotal();
    }
    
    public Double obtenerStockPromedio() {
        return this.inventarioRepository.obtenerStockPromedio();
    }
    
    // Ubicaciones
    public List<String> obtenerUbicaciones() {
        return this.inventarioRepository.findUbicaciones();
    }
    
    public List<Inventario> obtenerPorUbicacion(String ubicacion) {
        return this.inventarioRepository.findByUbicacionActivos(ubicacion);
    }
    
    // Movimientos de inventario masivos
    public void realizarInventarioFisico(List<AjusteInventario> ajustes, String usuarioActualizacion) {
        for (AjusteInventario ajuste : ajustes) {
            try {
                this.actualizarStock(ajuste.getIdProducto(), ajuste.getNuevoStock(), 
                              usuarioActualizacion + " - Inventario Físico");
            } catch (Exception e) {
                System.err.println("Error ajustando inventario para producto " + ajuste.getIdProducto() + ": " + e.getMessage());
            }
        }
    }
    
    // ========== MÉTODOS CON PAGINACIÓN ==========
    
    public Page<Inventario> obtenerInventariosActivos(Pageable pageable) {
        return this.inventarioRepository.findByEstadoTrue(pageable);
    }
    
    public Page<Inventario> obtenerConStock(Pageable pageable) {
        return this.inventarioRepository.findConStock(pageable);
    }
    
    public Page<Inventario> obtenerAgotados(Pageable pageable) {
        return this.inventarioRepository.findAgotados(pageable);
    }
    
    public Page<Inventario> obtenerConStockBajo(Pageable pageable) {
        return this.inventarioRepository.findConStockBajo(pageable);
    }
    
    public Page<Inventario> obtenerEnEstadoCritico(Pageable pageable) {
        return this.inventarioRepository.findEnEstadoCritico(pageable);
    }
    
    public Page<Inventario> obtenerAlertasInventario(Pageable pageable) {
        return this.inventarioRepository.obtenerAlertasInventario(pageable);
    }
    
    // Clase auxiliar para ajustes de inventario
    public static class AjusteInventario {
        private Integer idProducto;
        private Integer nuevoStock;
        private String observaciones;
        
        public AjusteInventario() {}
        
        public AjusteInventario(Integer idProducto, Integer nuevoStock) {
            this.idProducto = idProducto;
            this.nuevoStock = nuevoStock;
        }
        
        public AjusteInventario(Integer idProducto, Integer nuevoStock, String observaciones) {
            this.idProducto = idProducto;
            this.nuevoStock = nuevoStock;
            this.observaciones = observaciones;
        }
        
        // Getters y setters
        public Integer getIdProducto() { return this.idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
        
        public Integer getNuevoStock() { return this.nuevoStock; }
        public void setNuevoStock(Integer nuevoStock) { this.nuevoStock = nuevoStock; }
        
        public String getObservaciones() { return this.observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }
}
