package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.PedidoDetalle;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.PedidoDetalleRepository;
import com.ecovivashop.repository.PedidoRepository;
import com.ecovivashop.repository.ProductoRepository;
import com.ecovivashop.repository.UsuarioRepository;

@Service
@Transactional
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;
    private final EmailService emailService;

    // Constructor manual
    public PedidoService(PedidoRepository pedidoRepository, PedidoDetalleRepository pedidoDetalleRepository,
                        ProductoRepository productoRepository, UsuarioRepository usuarioRepository,
                        InventarioService inventarioService, EmailService emailService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioService = inventarioService;
        this.emailService = emailService;
    }

    // Métodos CRUD básicos
    public List<Pedido> findAll() {
        return this.pedidoRepository.findAll();
    }
    
    public Optional<Pedido> findById(Integer id) {
        return this.pedidoRepository.findById(id);
    }
    
    public Optional<Pedido> findByNumeroPedido(String numeroPedido) {
        return this.pedidoRepository.findByNumeroPedido(numeroPedido);
    }
    
    public Optional<Pedido> findByNumeroPedidoWithDetalles(String numeroPedido) {
        return this.pedidoRepository.findByNumeroPedidoWithDetalles(numeroPedido);
    }
    
    public Pedido save(Pedido pedido) {
        return this.pedidoRepository.save(pedido);
    }
    
    // Métodos de negocio
    public List<Pedido> obtenerPedidosPorUsuario(Integer idUsuario) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
        return this.pedidoRepository.findByUsuario(usuario.get());
    }
    
    public Page<Pedido> obtenerPedidosPorUsuarioPaginado(Integer idUsuario, Pageable pageable) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
        return this.pedidoRepository.findByUsuario(usuario.get(), pageable);
    }
    
    public List<Pedido> obtenerPorEstado(String estado) {
        return this.pedidoRepository.findByEstado(estado);
    }
    
    public Page<Pedido> obtenerPorEstadoPaginado(String estado, Pageable pageable) {
        return this.pedidoRepository.findByEstado(estado, pageable);
    }
    
    public List<Pedido> obtenerPedidosRecientes(int cantidad) {
        return this.pedidoRepository.findPedidosRecientes(Pageable.ofSize(cantidad));
    }
    
    public List<Pedido> obtenerPedidosPendientes() {
        return this.pedidoRepository.findByEstadoOrderByFechaPedidoDesc("PENDIENTE");
    }
    
    public List<Pedido> buscarPedidos(String busqueda) {
        return this.pedidoRepository.buscarPedidos(busqueda);
    }
    
    // Crear pedido
    public Pedido crearPedido(Integer idUsuario, String direccionEnvio, String telefonoContacto, 
                             String metodoPago, String notas, List<ItemPedido> items) {
        
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        // Validar stock de productos
        for (ItemPedido item : items) {
            if (!this.inventarioService.verificarDisponibilidad(item.getIdProducto(), item.getCantidad())) {
                Optional<Producto> producto = this.productoRepository.findById(item.getIdProducto());
                String nombreProducto = producto.map(Producto::getNombre).orElse("Producto " + item.getIdProducto());
                throw new RuntimeException("Stock insuficiente para el producto: " + nombreProducto);
            }
        }
        
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario.get());
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setTelefonoContacto(telefonoContacto);
        pedido.setMetodoPago(metodoPago);
        pedido.setNotas(notas);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaPedido(LocalDateTime.now());
        
        // Calcular fecha estimada de entrega (7 días por defecto)
        pedido.setFechaEstimadaEntrega(LocalDateTime.now().plusDays(7));
        
        Pedido pedidoGuardado = this.pedidoRepository.save(pedido);
        
        // Crear detalles del pedido
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemPedido item : items) {
            PedidoDetalle detalle = this.crearDetallePedido(pedidoGuardado, item);
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        
        // Actualizar totales del pedido
        pedidoGuardado.setSubtotal(subtotal);
        pedidoGuardado.calcularTotales();
        
        pedidoGuardado = this.pedidoRepository.save(pedidoGuardado);
        
        // Reducir stock de inventario
        for (ItemPedido item : items) {
            this.inventarioService.reducirStock(item.getIdProducto(), item.getCantidad(), "Venta - Pedido " + pedidoGuardado.getNumeroPedido());
        }
          // Enviar email de confirmación
        try {
            this.emailService.enviarCorreoConfirmacionPedido(usuario.get().getEmail(), usuario.get().getNombreCompleto(), 
                                                pedidoGuardado.getNumeroPedido(), pedidoGuardado.getTotal().toString());
        } catch (Exception e) {
            System.err.println("Error enviando confirmación de pedido: " + e.getMessage());
        }
        
        return pedidoGuardado;
    }
    
    private PedidoDetalle crearDetallePedido(Pedido pedido, ItemPedido item) {
        Optional<Producto> producto = this.productoRepository.findById(item.getIdProducto());
        if (producto.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + item.getIdProducto());
        }
        
        PedidoDetalle detalle = new PedidoDetalle();
        detalle.setPedido(pedido);
        detalle.setProducto(producto.get());
        detalle.setCantidad(item.getCantidad());
        detalle.setPrecioUnitario(producto.get().getPrecio());
        detalle.setDescuentoUnitario(item.getDescuentoUnitario() != null ? item.getDescuentoUnitario() : BigDecimal.ZERO);
        detalle.calcularSubtotal();
        
        return this.pedidoDetalleRepository.save(detalle);
    }
    
    // Gestión de estados del pedido
    public void confirmarPedido(Integer idPedido) {
        Optional<Pedido> pedidoExistente = this.pedidoRepository.findById(idPedido);
        if (pedidoExistente.isEmpty()) {
            throw new RuntimeException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoExistente.get();
        pedido.confirmar();
        this.pedidoRepository.save(pedido);
        
        // Enviar email de confirmación al cliente
        try {
            String emailCliente = pedido.getUsuario().getEmail();
            String nombreCliente = pedido.getUsuario().getNombre();
            String numeroPedido = pedido.getNumeroPedido();
            String totalPedido = pedido.getTotal().toString();
            
            this.emailService.enviarCorreoConfirmacionPedido(emailCliente, nombreCliente, numeroPedido, totalPedido);
            System.out.println("? Email de confirmación enviado para pedido: " + numeroPedido);
        } catch (Exception e) {
            System.err.println("⚠️ Error enviando email de confirmación para pedido " + idPedido + ": " + e.getMessage());
            // No fallar la confirmación del pedido por error de email
        }
    }
    
    public void enviarPedido(Integer idPedido, String numeroSeguimiento, String transportadora) {
        Optional<Pedido> pedidoExistente = this.pedidoRepository.findById(idPedido);
        if (pedidoExistente.isEmpty()) {
            throw new RuntimeException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoExistente.get();
        pedido.enviar(numeroSeguimiento, transportadora);
        this.pedidoRepository.save(pedido);
    }
    
    public void entregarPedido(Integer idPedido) {
        Optional<Pedido> pedidoExistente = this.pedidoRepository.findById(idPedido);
        if (pedidoExistente.isEmpty()) {
            throw new RuntimeException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoExistente.get();
        pedido.entregar();
        this.pedidoRepository.save(pedido);
    }
    
    public void cancelarPedido(Integer idPedido) {
        Optional<Pedido> pedidoExistente = this.pedidoRepository.findById(idPedido);
        if (pedidoExistente.isEmpty()) {
            throw new RuntimeException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoExistente.get();
        
        if (!pedido.puedeSerCancelado()) {
            throw new RuntimeException("El pedido no puede ser cancelado en su estado actual: " + pedido.getEstado());
        }
        
        pedido.cancelar();
        
        // Restaurar stock
        List<PedidoDetalle> detalles = this.pedidoDetalleRepository.findByPedido(pedido);
        for (PedidoDetalle detalle : detalles) {
            this.inventarioService.aumentarStock(detalle.getProducto().getIdProducto(), detalle.getCantidad(), 
                                          "Cancelación - Pedido " + pedido.getNumeroPedido());
        }
        
        this.pedidoRepository.save(pedido);
    }
    
    // Estadísticas
    public Long contarPorEstado(String estado) {
        return this.pedidoRepository.contarPorEstado(estado);
    }
    
    public BigDecimal calcularVentasEnPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return this.pedidoRepository.calcularVentasEnPeriodo(inicio, fin);
    }
    
    public BigDecimal obtenerTicketPromedio() {
        return this.pedidoRepository.obtenerTicketPromedio();
    }
    
    public BigDecimal calcularIngresosTotales() {
        return this.pedidoRepository.calcularIngresosTotales();
    }
    
    public List<String> obtenerEstados() {
        return this.pedidoRepository.findEstados();
    }
    
    public List<String> obtenerMetodosPago() {
        return this.pedidoRepository.findMetodosPago();
    }
    
    public List<Pedido> obtenerPedidosQueNecesitanAtencion() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(3); // Pendientes por más de 3 días
        return this.pedidoRepository.findPedidosQueNecesitanAtencion(fechaLimite);
    }
    
    public List<Pedido> obtenerPedidosConRetraso() {
        return this.pedidoRepository.findPedidosConRetraso(LocalDateTime.now());
    }
    
    // ===== MÉTODOS PARA ADMINISTRACIÓN DE PEDIDOS =====
    
    /**
     * Obtener pedidos paginados
     */
    public Page<Pedido> obtenerPedidosPaginados(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }
    
    /**
     * Obtener pedidos por estado paginado
     */
    public Page<Pedido> obtenerPedidosPorEstadoPaginado(String estado, Pageable pageable) {
        return pedidoRepository.findByEstado(estado, pageable);
    }
    
    /**
     * Buscar pedidos paginado
     */
    public Page<Pedido> buscarPedidosPaginado(String busqueda, Pageable pageable) {
        return pedidoRepository.buscarPedidos(busqueda, pageable);
    }
    
    /**
     * Contar total de pedidos
     */
    public long contarPedidos() {
        return pedidoRepository.count();
    }
    
    /**
     * Contar pedidos por estado
     */
    public long contarPedidosPorEstado(String estado) {
        return pedidoRepository.countByEstado(estado);
    }
    
    /**
     * Contar pedidos por usuario
     */
    public long contarPedidosPorUsuario(Integer idUsuario) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
        return this.pedidoRepository.countByUsuario(usuario.get());
    }
    
    /**
     * Obtener pedidos entre fechas
     */
    public List<Pedido> obtenerPedidosEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByFechaPedidoBetween(inicio, fin);
    }
    
    /**
     * Obtener pedidos por estado
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }
    
    /**
     * Eliminar pedido (admin only)
     */
    public void eliminarPedido(Integer idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoOpt.get();
        
        // Solo permitir eliminar pedidos cancelados o muy antiguos
        if (!"CANCELADO".equals(pedido.getEstado())) {
            throw new IllegalStateException("Solo se pueden eliminar pedidos cancelados");
        }
        
        pedidoRepository.delete(pedido);
    }
    
    /**
     * Cambiar estado del pedido
     */
    public void cambiarEstadoPedido(Integer idPedido, String nuevoEstado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoOpt.get();
        String estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);
        // pedido.setFechaActualizacion(LocalDateTime.now()); // Comentado hasta que se agregue el campo
        
        // Si se cambia a ENTREGADO, actualizar fecha de entrega
        if ("ENTREGADO".equals(nuevoEstado)) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }
        
        // Si se cambia a CANCELADO, restaurar inventario
        if ("CANCELADO".equals(nuevoEstado) && !"CANCELADO".equals(estadoAnterior)) {
            restaurarInventarioPedido(pedido);
        }
        
        pedidoRepository.save(pedido);
        
        // Enviar email de notificación al cliente
        try {
            enviarNotificacionCambioEstado(pedido, estadoAnterior, nuevoEstado);
        } catch (Exception e) {
            // Log del error pero no detener el proceso
            System.err.println("Error enviando email de notificación: " + e.getMessage());
        }
    }
    
    /**
     * Cancelar pedido con motivo
     */
    public void cancelarPedido(Integer idPedido, String motivo) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado con ID: " + idPedido);
        }
        
        Pedido pedido = pedidoOpt.get();
        
        if ("CANCELADO".equals(pedido.getEstado())) {
            throw new IllegalStateException("El pedido ya está cancelado");
        }
        
        if ("ENTREGADO".equals(pedido.getEstado())) {
            throw new IllegalStateException("No se puede cancelar un pedido ya entregado");
        }
        
        pedido.setEstado("CANCELADO");
        // pedido.setFechaActualizacion(LocalDateTime.now()); // Comentado hasta que se agregue el campo
        // Podríamos agregar un campo observaciones/motivo en el futuro
        
        // Restaurar inventario
        restaurarInventarioPedido(pedido);
        
        pedidoRepository.save(pedido);
        
        // Enviar email de notificación
        try {
            enviarNotificacionCancelacion(pedido, motivo);
        } catch (Exception e) {
            System.err.println("Error enviando email de cancelación: " + e.getMessage());
        }
    }
    
    /**
     * Restaurar inventario cuando se cancela un pedido
     */
    private void restaurarInventarioPedido(Pedido pedido) {
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            try {
                // Usar el método correcto de InventarioService
                inventarioService.aumentarStock(detalle.getProducto().getIdProducto(), detalle.getCantidad(), "Restauración por cancelación de pedido");
            } catch (Exception e) {
                System.err.println("Error restaurando stock del producto " + 
                    detalle.getProducto().getIdProducto() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Enviar notificación de cambio de estado
     */
    private void enviarNotificacionCambioEstado(Pedido pedido, String estadoAnterior, String nuevoEstado) {
        try {
            // Usar uno de los métodos existentes de EmailService
            // Por ahora vamos a comentar esto hasta crear un método genérico
            System.out.println("📧 Notificación: Pedido #" + pedido.getIdPedido() + 
                " cambió de " + estadoAnterior + " a " + nuevoEstado + 
                " para " + pedido.getUsuario().getEmail());
        } catch (Exception e) {
            System.err.println("Error en notificación: " + e.getMessage());
        }
    }
    
    /**
     * Enviar notificación de cancelación
     */
    private void enviarNotificacionCancelacion(Pedido pedido, String motivo) {
        try {
            // Por ahora vamos a comentar esto hasta crear un método genérico
            System.out.println("📧 Cancelación: Pedido #" + pedido.getIdPedido() + 
                " cancelado para " + pedido.getUsuario().getEmail() + 
                (motivo != null ? " - Motivo: " + motivo : ""));
        } catch (Exception e) {
            System.err.println("Error en notificación de cancelación: " + e.getMessage());
        }
    }
    
    // Clase auxiliar para items del pedido
    public static class ItemPedido {
        private Integer idProducto;
        private Integer cantidad;
        private BigDecimal descuentoUnitario;
        
        // Constructores
        public ItemPedido() {}
        
        public ItemPedido(Integer idProducto, Integer cantidad) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
            this.descuentoUnitario = BigDecimal.ZERO;
        }
        
        public ItemPedido(Integer idProducto, Integer cantidad, BigDecimal descuentoUnitario) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
            this.descuentoUnitario = descuentoUnitario;
        }
        
        // Getters y setters
        public Integer getIdProducto() { return this.idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
        
        public Integer getCantidad() { return this.cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        
        public BigDecimal getDescuentoUnitario() { return this.descuentoUnitario; }
        public void setDescuentoUnitario(BigDecimal descuentoUnitario) { this.descuentoUnitario = descuentoUnitario; }
    }

    /**
     * Crear pedido desde carrito de compras
     */
    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public Pedido crearPedidoDesdeCarro(Usuario usuario, java.util.List<java.util.Map<String, Object>> carrito, 
                                        String numeroPedido, String metodoPago, String direccionEnvio, 
                                        BigDecimal subtotal, BigDecimal costoEnvio, BigDecimal descuento, 
                                        BigDecimal impuestos, BigDecimal total) {
        try {
            // Crear pedido principal
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setNumeroPedido(numeroPedido);
            pedido.setFechaPedido(LocalDateTime.now());
            pedido.setEstado("PENDIENTE");
            pedido.setMetodoPago(metodoPago);
            pedido.setDireccionEnvio(direccionEnvio);
            pedido.setSubtotal(subtotal);
            pedido.setCostoEnvio(costoEnvio);
            pedido.setDescuento(descuento);
            pedido.setImpuestos(impuestos);
            pedido.setTotal(total);
            
            // Guardar pedido
            Pedido pedidoGuardado = pedidoRepository.save(pedido);
            System.out.println("✅ Pedido guardado con ID: " + pedidoGuardado.getIdPedido());

            // Crear detalles del pedido
            for (java.util.Map<String, Object> item : carrito) {
                try {
                    // Validaciones más robustas - buscar tanto "idProducto" como "productoId"
                    Object idProductoObj = item.get("idProducto");
                    if (idProductoObj == null) {
                        idProductoObj = item.get("productoId"); // Fallback para compatibilidad
                    }
                    Object cantidadObj = item.get("cantidad");
                    Object precioObj = item.get("precio");
                    
                    System.out.println("🔍 Procesando item del carrito:");
                    System.out.println("  - idProducto: " + idProductoObj + " (tipo: " + (idProductoObj != null ? idProductoObj.getClass().getSimpleName() : "null") + ")");
                    System.out.println("  - cantidad: " + cantidadObj + " (tipo: " + (cantidadObj != null ? cantidadObj.getClass().getSimpleName() : "null") + ")");
                    System.out.println("  - precio: " + precioObj + " (tipo: " + (precioObj != null ? precioObj.getClass().getSimpleName() : "null") + ")");
                    System.out.println("  - Claves disponibles en item: " + item.keySet());
                    
                    if (idProductoObj == null) {
                        System.err.println("❌ ID del producto es null, saltando item");
                        continue;
                    }
                    
                    if (cantidadObj == null) {
                        System.err.println("❌ Cantidad es null, saltando item");
                        continue;
                    }
                    
                    if (precioObj == null) {
                        System.err.println("❌ Precio es null, saltando item");
                        continue;
                    }
                    
                    Integer idProducto;
                    Integer cantidad;
                    Double precio;
                    
                    // Conversiones seguras
                    try {
                        idProducto = (Integer) idProductoObj;
                    } catch (ClassCastException e) {
                        System.err.println("❌ Error al convertir idProducto: " + e.getMessage());
                        continue;
                    }
                    
                    try {
                        cantidad = (Integer) cantidadObj;
                    } catch (ClassCastException e) {
                        System.err.println("❌ Error al convertir cantidad: " + e.getMessage());
                        continue;
                    }
                    
                    try {
                        precio = ((Number) precioObj).doubleValue();
                    } catch (ClassCastException e) {
                        System.err.println("❌ Error al convertir precio: " + e.getMessage());
                        continue;
                    }
                    
                    // Validar valores
                    if (idProducto <= 0) {
                        System.err.println("❌ ID del producto inválido: " + idProducto);
                        continue;
                    }
                    
                    if (cantidad <= 0) {
                        System.err.println("❌ Cantidad inválida: " + cantidad);
                        continue;
                    }
                    
                    if (precio <= 0) {
                        System.err.println("❌ Precio inválido: " + precio);
                        continue;
                    }

                    // Buscar el producto
                    Optional<Producto> productoOpt = productoRepository.findById(idProducto);
                    if (productoOpt.isPresent()) {
                        Producto producto = productoOpt.get();

                        // Crear detalle del pedido
                        PedidoDetalle detalle = new PedidoDetalle();
                        detalle.setPedido(pedidoGuardado);
                        detalle.setProducto(producto);
                        detalle.setCantidad(cantidad);
                        detalle.setPrecioUnitario(BigDecimal.valueOf(precio));
                        detalle.setDescuentoUnitario(BigDecimal.ZERO);
                        detalle.setSubtotal(BigDecimal.valueOf(precio * cantidad));

                        // Guardar detalle
                        pedidoDetalleRepository.save(detalle);
                        System.out.println("✅ Detalle guardado para producto: " + producto.getNombre());

                        // Actualizar inventario
                        try {
                            String usuarioActualizacion = usuario.getEmail() + " - Pedido " + numeroPedido;
                            inventarioService.reducirStock(idProducto, cantidad, usuarioActualizacion);
                            System.out.println("✅ Stock actualizado para producto: " + producto.getNombre());
                        } catch (Exception e) {
                            System.err.println("⚠️ Error al actualizar stock para producto " + producto.getNombre() + ": " + e.getMessage());
                            // No lanzamos excepción aquí para no romper todo el pedido
                        }
                    } else {
                        System.err.println("❌ Producto no encontrado con ID: " + idProducto);
                        // Consideramos este un error crítico que debe fallar la transacción
                        throw new RuntimeException("Producto no encontrado con ID: " + idProducto);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error al procesar item del carrito: " + e.getMessage());
                    System.err.println("❌ Tipo de error: " + e.getClass().getSimpleName());
                    // Si es un error crítico, lanzamos la excepción para fallar la transacción
                    if (e instanceof RuntimeException && e.getMessage().contains("Producto no encontrado")) {
                        throw e;
                    }
                    // Para otros errores, continuamos pero los registramos
                }
            }

            System.out.println("✅ Pedido completo creado exitosamente: " + numeroPedido);
            return pedidoGuardado;

        } catch (Exception e) {
            System.err.println("❌ Error al crear pedido desde carrito: " + e.getMessage());
            System.err.println("❌ Tipo de error: " + e.getClass().getSimpleName());
            throw new RuntimeException("Error al crear pedido", e);
        }
    }

    /**
     * Calcular ventas del mes actual (todos los pedidos no cancelados)
     */
    public BigDecimal calcularVentasMesActual() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMes = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime finMes = inicioMes.plusMonths(1).minusSeconds(1);

        System.out.println("🔍 PedidoService.calcularVentasMesActual():");
        System.out.println("  - Fecha actual: " + ahora);
        System.out.println("  - Inicio del mes: " + inicioMes);
        System.out.println("  - Fin del mes: " + finMes);

        // Calcular ventas de todos los pedidos del mes que no estén cancelados
        List<Pedido> pedidosMes = this.pedidoRepository.findByFechaPedidoBetween(inicioMes, finMes);
        System.out.println("  - Total de pedidos encontrados en el rango: " + pedidosMes.size());

        for (Pedido p : pedidosMes) {
            System.out.println("    - Pedido ID " + p.getIdPedido() + ": estado='" + p.getEstado() + "', total=" + p.getTotal() + ", fecha=" + p.getFechaPedido());
        }

        BigDecimal ventasTotales = pedidosMes.stream()
            .filter(p -> !Arrays.asList("CANCELADO").contains(p.getEstado())) // Excluir solo cancelados
            .peek(p -> System.out.println("    - Incluyendo pedido " + p.getIdPedido() + " con total " + p.getTotal()))
            .map(Pedido::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("  - Ventas totales calculadas: " + ventasTotales);
        return ventasTotales;
    }

    /**
     * Calcular total gastado por un usuario (solo pedidos entregados)
     */
    public BigDecimal calcularTotalGastadoPorUsuario(Integer idUsuario) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }

        List<Pedido> pedidos = this.pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario.get());
        return pedidos.stream()
            .filter(p -> "ENTREGADO".equals(p.getEstado()))
            .map(Pedido::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcular promedio de pedido por usuario (solo pedidos entregados)
     */
    public BigDecimal calcularPromedioPedidoPorUsuario(Integer idUsuario) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }

        List<Pedido> pedidos = this.pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario.get());
        List<Pedido> pedidosEntregados = pedidos.stream()
            .filter(p -> "ENTREGADO".equals(p.getEstado()))
            .toList();

        if (pedidosEntregados.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = pedidosEntregados.stream()
            .map(Pedido::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(pedidosEntregados.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Obtener fecha del último pedido de un usuario
     */
    public java.time.LocalDateTime obtenerFechaUltimoPedido(Integer idUsuario) {
        Optional<Usuario> usuario = this.usuarioRepository.findById(idUsuario);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }

        List<Pedido> pedidos = this.pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario.get());
        return pedidos.stream()
            .findFirst()
            .map(Pedido::getFechaPedido)
            .orElse(null);
    }
}
