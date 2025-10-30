package com.ecovivashop.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.InventarioService;
import com.ecovivashop.service.PedidoService;
import com.ecovivashop.service.ProductoService;
import com.ecovivashop.service.RolService;
import com.ecovivashop.service.UsuarioService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {
    
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final InventarioService inventarioService;
    private final RolService rolService;

    // Constructor manual
    public ApiController(UsuarioService usuarioService, ProductoService productoService,
                        PedidoService pedidoService, InventarioService inventarioService,
                        RolService rolService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.inventarioService = inventarioService;
        this.rolService = rolService;
    }

    // ========== DASHBOARD STATS ==========
    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasDashboard() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Estadísticas de usuarios
            stats.put("totalUsuarios", this.usuarioService.contarUsuariosActivos());
            stats.put("totalClientes", this.usuarioService.contarClientes());
            stats.put("totalAdmins", this.usuarioService.contarAdministradores());
            stats.put("usuariosHoy", this.usuarioService.contarUsuariosRegistradosHoy());
            
            // Estadísticas de productos
            stats.put("totalProductos", this.productoService.contarProductosActivos());
            stats.put("productosStock", this.inventarioService.contarConStock());
            stats.put("productosAgotados", this.inventarioService.contarAgotados());
            stats.put("stockBajo", this.inventarioService.contarConStockBajo());
            
            // Estadísticas de pedidos
            stats.put("pedidosPendientes", this.pedidoService.contarPorEstado("PENDIENTE"));
            stats.put("pedidosEnviados", this.pedidoService.contarPorEstado("ENVIADO"));
            stats.put("pedidosEntregados", this.pedidoService.contarPorEstado("ENTREGADO"));
              // Ventas del mes
            LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            BigDecimal ventasMes = this.pedidoService.calcularVentasEnPeriodo(inicioMes, LocalDateTime.now());
            stats.put("ventasMes", ventasMes != null ? ventasMes : BigDecimal.ZERO);
              return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ========== GESTIÓN DE USUARIOS ==========
    @GetMapping("/admin/usuarios")
    public ResponseEntity<Map<String, Object>> obtenerUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String busqueda) {
          try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaRegistro").descending());
            Page<Usuario> usuarios = this.usuarioService.obtenerUsuariosPaginados(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("usuarios", usuarios.getContent());
            response.put("totalPages", usuarios.getTotalPages());
            response.put("totalElements", usuarios.getTotalElements());            response.put("currentPage", usuarios.getNumber());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @PostMapping("/admin/usuarios")
    public ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody Map<String, String> request) {
        try {
            Usuario usuario = this.usuarioService.crearUsuario(
                request.get("nombre"),
                request.get("apellido"),
                request.get("email"),
                request.get("password"),
                request.get("rol")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario creado exitosamente");
            response.put("usuario", usuario);
            
            return ResponseEntity.ok(response);
        } catch (NumberFormatException | NullPointerException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error de formato o dato nulo: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/admin/usuarios/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoUsuario(@PathVariable Integer id, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean estado = request.get("estado");
            if (estado) {
                this.usuarioService.activarUsuario(id);
            } else {
                this.usuarioService.desactivarUsuario(id);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error inesperado: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }    // ========== GESTIÓN DE PRODUCTOS ==========
    @GetMapping("/api/admin/productos")
    public ResponseEntity<List<Producto>> obtenerProductos() {
        try {
            List<Producto> productos = this.productoService.obtenerProductosActivos();
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/api/admin/productos")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Map<String, Object> request) {
        try {
            Producto producto = this.productoService.crearProducto(
                (String) request.get("nombre"),
                (String) request.get("descripcion"),
                new BigDecimal(request.get("precio").toString()),
                (String) request.get("categoria"),
                (String) request.get("marca"),
                (String) request.get("modelo"),
                (String) request.get("color"),
                request.get("peso") != null ? new BigDecimal(request.get("peso").toString()) : null,
                (String) request.get("dimensiones"),
                (String) request.get("material"),
                request.get("garantiaMeses") != null ? Integer.valueOf(request.get("garantiaMeses").toString()) : null,
                (String) request.get("eficienciaEnergetica"),
                (String) request.get("impactoAmbiental"),
                request.get("puntuacionEco") != null ? new BigDecimal(request.get("puntuacionEco").toString()) : null,
                (String) request.get("imagenUrl"),
                request.get("stockInicial") != null ? Integer.valueOf(request.get("stockInicial").toString()) : 0
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado exitosamente");
            response.put("producto", producto);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error inesperado: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }    // ========== GESTIÓN DE PEDIDOS API ==========
    @GetMapping("/api/admin/pedidos")
    public ResponseEntity<Map<String, Object>> obtenerPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String estado) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaPedido").descending());
            Page<Pedido> pedidos;
            
            if (estado != null && !estado.trim().isEmpty()) {
                pedidos = this.pedidoService.obtenerPorEstadoPaginado(estado, pageable);
            } else {
                // Obtener todos los pedidos (necesitaríamos implementar este método)
                pedidos = this.pedidoService.obtenerPorEstadoPaginado("PENDIENTE", pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("pedidos", pedidos.getContent());
            response.put("totalPages", pedidos.getTotalPages());
            response.put("totalElements", pedidos.getTotalElements());
            response.put("currentPage", pedidos.getNumber());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
      @PutMapping("/api/admin/pedidos/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstadoPedido(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {            String accion = (String) request.get("accion");
            
            switch (accion) {
                case "confirmar" -> this.pedidoService.confirmarPedido(id);
                case "enviar" -> {
                    String numeroSeguimiento = (String) request.get("numeroSeguimiento");
                    String transportadora = (String) request.get("transportadora");
                    this.pedidoService.enviarPedido(id, numeroSeguimiento, transportadora);
                }
                case "entregar" -> this.pedidoService.entregarPedido(id);
                case "cancelar" -> this.pedidoService.cancelarPedido(id);
                default -> throw new RuntimeException("Acción no válida: " + accion);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado del pedido actualizado exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error inesperado: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ========== GESTIÓN DE INVENTARIO ==========
    @GetMapping("/admin/inventario")
    public ResponseEntity<List<Inventario>> obtenerInventario() {
        try {
            List<Inventario> inventarios = this.inventarioService.obtenerInventariosActivos();
            return ResponseEntity.ok(inventarios);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/admin/inventario/{id}/stock")
    public ResponseEntity<Map<String, Object>> actualizarStock(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            Integer nuevoStock = Integer.valueOf(request.get("stock").toString());
            String usuario = (String) request.getOrDefault("usuario", "ADMIN");
            
            // Buscar por ID de producto, no de inventario
            this.inventarioService.actualizarStock(id, nuevoStock, usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock actualizado exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ========== ALERTAS Y NOTIFICACIONES ==========
    @GetMapping("/admin/alertas")
    public ResponseEntity<Map<String, Object>> obtenerAlertas() {
        try {
            Map<String, Object> alertas = new HashMap<>();
            
            // Alertas de inventario
            alertas.put("inventarioBajo", this.inventarioService.obtenerAlertasInventario());
            
            // Pedidos que necesitan atención
            alertas.put("pedidosAtencion", this.pedidoService.obtenerPedidosQueNecesitanAtencion());
            
            // Pedidos con retraso
            alertas.put("pedidosRetraso", this.pedidoService.obtenerPedidosConRetraso());
            
            return ResponseEntity.ok(alertas);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== BÚSQUEDAS ==========
    @GetMapping("/admin/buscar/usuarios")
    public ResponseEntity<List<Usuario>> buscarUsuarios(@RequestParam String q) {
        try {
            List<Usuario> usuarios = this.usuarioService.buscarUsuarios(q);
            return ResponseEntity.ok(usuarios);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/admin/buscar/productos")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String q) {
        try {
            List<Producto> productos = this.productoService.buscarProductos(q);
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/admin/buscar/pedidos")
    public ResponseEntity<List<Pedido>> buscarPedidos(@RequestParam String q) {
        try {
            List<Pedido> pedidos = this.pedidoService.buscarPedidos(q);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== DATOS MAESTROS ==========
    @GetMapping("/admin/roles")
    public ResponseEntity<List<Rol>> obtenerRoles() {
        try {
            List<Rol> roles = this.rolService.findRolesActivos();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/admin/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        try {
            List<String> categorias = this.productoService.obtenerCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/admin/marcas")
    public ResponseEntity<List<String>> obtenerMarcas() {
        try {
            List<String> marcas = this.productoService.obtenerMarcas();
            return ResponseEntity.ok(marcas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
