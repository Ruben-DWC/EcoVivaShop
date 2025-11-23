package com.ecovivashop.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecovivashop.entity.TransaccionPago;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.TransaccionPagoRepository;
import com.ecovivashop.service.PedidoService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class ClienteAdminController extends BaseAdminController {
    
    @Autowired
    private TransaccionPagoRepository transaccionPagoRepository;
    
    @Autowired
    private PedidoService pedidoService;
    
    public ClienteAdminController() {
        // Constructor sin parámetros - usa el usuarioService heredado
    }
    
    /**
     * Página principal de gestión de clientes
     */
    @GetMapping("/clientes")
    public String gestionClientes(Model model, Principal principal,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "fechaRegistro") String sortBy,
                                 @RequestParam(defaultValue = "desc") String sortDir,
                                 @RequestParam(required = false) String busqueda,
                                 @RequestParam(required = false) String estado) {
        
        // Usuario actual para el header
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        // Configurar paginación y ordenamiento
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Obtener clientes según filtros
        Page<Usuario> clientes;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            clientes = usuarioService.buscarClientesPaginado(busqueda, pageable);
        } else if (estado != null && !estado.trim().isEmpty()) {
            boolean estadoBool = "activo".equals(estado);
            clientes = usuarioService.obtenerClientesPorEstadoPaginado(estadoBool, pageable);
        } else {
            clientes = usuarioService.obtenerClientesPaginados(pageable);
        }
        
        // Calcular conteo de pedidos por cliente
        Map<Integer, Long> conteoPedidosPorCliente = new HashMap<>();
        for (Usuario cliente : clientes.getContent()) {
            try {
                long conteoPedidos = pedidoService.contarPedidosPorUsuario(cliente.getIdUsuario());
                conteoPedidosPorCliente.put(cliente.getIdUsuario(), conteoPedidos);
            } catch (RuntimeException e) {
                // Si hay error al contar pedidos, usar 0
                conteoPedidosPorCliente.put(cliente.getIdUsuario(), 0L);
            }
        }
        
        // Estadísticas
        long totalClientes = usuarioService.contarClientes();
        long clientesActivos = usuarioService.contarClientesActivos();
        long clientesInactivos = totalClientes - clientesActivos;
        
        // Agregar al modelo
        model.addAttribute("clientes", clientes);
        model.addAttribute("conteoPedidosPorCliente", conteoPedidosPorCliente);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientes.getTotalPages());
        model.addAttribute("totalItems", clientes.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estado", estado);
        
        // Estadísticas
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("clientesActivos", clientesActivos);
        model.addAttribute("clientesInactivos", clientesInactivos);
        
        return "admin/clientes";
    }
    
    /**
     * Formulario para crear nuevo cliente
     */
    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        model.addAttribute("cliente", new Usuario());
        model.addAttribute("esNuevo", true);
        
        return "admin/cliente-formulario";
    }
    
    /**
     * Formulario para editar cliente existente
     */
    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(@PathVariable Integer id, Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        Usuario cliente = usuarioService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        model.addAttribute("cliente", cliente);
        model.addAttribute("esNuevo", false);
        
        return "admin/cliente-formulario";
    }
    
    /**
     * Guardar cliente (nuevo o editado)
     */
    @PostMapping("/clientes/guardar")
    @SuppressWarnings("CallToPrintStackTrace")
    public String guardarCliente(HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 === INICIO GUARDAR CLIENTE ===");
            
            // Obtener parámetros del request
            String idClienteStr = request.getParameter("idUsuario");
            Integer idCliente = idClienteStr != null && !idClienteStr.trim().isEmpty() ? Integer.valueOf(idClienteStr.trim()) : null;
            
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String email = request.getParameter("email");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");
            String dni = request.getParameter("dni");
            String fechaNacimientoStr = request.getParameter("fechaNacimiento");
            String password = request.getParameter("password");
            String estadoStr = request.getParameter("estado");
            
            System.out.println("🔍 ID del cliente recibido: " + idCliente);
            System.out.println("🔍 Email: " + email);
            System.out.println("🔍 Nombre: " + nombre);
            System.out.println("🔍 Apellido: " + apellido);
            System.out.println("🔍 Estado: " + estadoStr);
            
            // Crear objeto Usuario con los datos del formulario
            Usuario cliente = new Usuario();
            cliente.setIdUsuario(idCliente);
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setDni(dni);
            
            if (fechaNacimientoStr != null && !fechaNacimientoStr.trim().isEmpty()) {
                try {
                    cliente.setFechaNacimiento(java.sql.Date.valueOf(fechaNacimientoStr.trim()).toLocalDate());
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ Error parseando fecha de nacimiento: " + e.getMessage());
                }
            }
            
            if (password != null && !password.trim().isEmpty()) {
                cliente.setPassword(password.trim());
            }
            
            if (estadoStr != null && !estadoStr.trim().isEmpty()) {
                cliente.setEstado("on".equals(estadoStr.trim()) || Boolean.parseBoolean(estadoStr.trim()));
            }
            
            // Si es nuevo cliente, asignar rol de cliente
            if (cliente.getIdUsuario() == null) {
                System.out.println("🔍 Creando nuevo cliente...");
                usuarioService.registrarCliente(cliente);
                redirectAttributes.addFlashAttribute("success", 
                    "Cliente registrado exitosamente");
            } else {
                System.out.println("🔍 Actualizando cliente existente con ID: " + cliente.getIdUsuario());
                usuarioService.actualizarCliente(cliente);
                redirectAttributes.addFlashAttribute("success", 
                    "Cliente actualizado exitosamente");
            }
                
        } catch (RuntimeException e) {
            System.err.println("❌ === ERROR AL GUARDAR CLIENTE ===");
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * Cambiar estado del cliente (activar/desactivar)
     */
    @PostMapping("/clientes/cambiar-estado/{id}")
    public String cambiarEstadoCliente(@PathVariable Integer id, 
                                      RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarEstadoCliente(id);
            redirectAttributes.addFlashAttribute("success", 
                "Estado del cliente actualizado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cambiar estado del cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * Ver detalles del cliente
     */
    @GetMapping("/clientes/detalle/{id}")
    public String detalleCliente(@PathVariable Integer id, Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        Usuario cliente = usuarioService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        // Calcular estadísticas del cliente
        long totalPedidos = pedidoService.contarPedidosPorUsuario(id);
        BigDecimal totalGastado = pedidoService.calcularTotalGastadoPorUsuario(id);
        BigDecimal promedioPedido = pedidoService.calcularPromedioPedidoPorUsuario(id);
        java.time.LocalDateTime fechaUltimoPedido = pedidoService.obtenerFechaUltimoPedido(id);
        
        // Obtener historial de pedidos (últimos 10 pedidos)
        List<com.ecovivashop.entity.Pedido> historialPedidos = pedidoService.obtenerPedidosPorUsuario(id);
        // Limitar a los últimos 10 pedidos más recientes
        if (historialPedidos.size() > 10) {
            historialPedidos = historialPedidos.subList(0, 10);
        }
        
        // Agregar datos al modelo
        model.addAttribute("cliente", cliente);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("totalGastado", totalGastado);
        model.addAttribute("promedioPedido", promedioPedido);
        model.addAttribute("fechaUltimoPedido", fechaUltimoPedido);
        model.addAttribute("historialPedidos", historialPedidos);
        
        return "admin/cliente-detalle";
    }
    
    /**
     * Eliminar cliente (cambiar estado a inactivo)
     */
    @PostMapping("/clientes/eliminar/{id}")
    @SuppressWarnings("CallToPrintStackTrace")
    public String eliminarCliente(@PathVariable Integer id, 
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {
        try {
            // Log detallado de la solicitud
            System.out.println("🔍 === INICIO DESACTIVACIÓN CLIENTE ===");
            System.out.println("🔍 ID recibido: " + id);
            System.out.println("🔍 Método HTTP: " + request.getMethod());
            System.out.println("🔍 URL: " + request.getRequestURL());
            System.out.println("🔍 Headers importantes:");
            System.out.println("🔍   Content-Type: " + request.getContentType());
            System.out.println("🔍   Content-Length: " + request.getContentLength());
            System.out.println("🔍   User-Agent: " + request.getHeader("User-Agent"));
            System.out.println("🔍   Referer: " + request.getHeader("Referer"));
            
            // Verificar que el cliente existe antes de desactivar
            Usuario cliente = usuarioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
            
            // Log del cliente encontrado
            System.out.println("🔍 Cliente encontrado:");
            System.out.println("🔍   ID: " + cliente.getIdUsuario());
            System.out.println("🔍   Email: " + cliente.getEmail());
            System.out.println("🔍   Estado actual: " + cliente.getEstado());
            System.out.println("🔍   Es Gmail: " + cliente.getEmail().toLowerCase().contains("@gmail.com"));
            
            // En lugar de eliminar físicamente, desactivamos el cliente
            System.out.println("🔍 Llamando a usuarioService.desactivarUsuario(" + id + ")");
            usuarioService.desactivarUsuario(id);
            
            // Verificar que se desactivó correctamente
            Usuario clienteDesactivado = usuarioService.findById(id).orElse(null);
            if (clienteDesactivado != null) {
                System.out.println("✅ Cliente desactivado exitosamente:");
                System.out.println("✅   Nuevo estado: " + clienteDesactivado.getEstado());
                System.out.println("✅   Confirmación: " + (clienteDesactivado.getEstado() == false));
            } else {
                System.out.println("❌ ERROR: No se pudo verificar el cliente después de desactivar");
            }
            
            System.out.println("🔍 === FIN DESACTIVACIÓN CLIENTE ===");
            redirectAttributes.addFlashAttribute("success", 
                "Cliente desactivado exitosamente");
        } catch (RuntimeException e) {
            System.err.println("❌ === ERROR EN DESACTIVACIÓN CLIENTE ===");
            System.err.println("❌ ID: " + id);
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("❌ === FIN ERROR DESACTIVACIÓN ===");
            redirectAttributes.addFlashAttribute("error", 
                "Error al desactivar cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * Activar cliente
     */
    @PostMapping("/clientes/activar/{id}")
    @SuppressWarnings("CallToPrintStackTrace")
    public String activarCliente(@PathVariable Integer id, 
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario cliente = usuarioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
            
            // Log para debugging
            System.out.println("🔍 Intentando activar cliente ID: " + id + ", Email: " + cliente.getEmail() + ", Estado actual: " + cliente.getEstado());
            
            usuarioService.activarUsuario(id);
            
            // Verificar que se activó correctamente
            Usuario clienteActivado = usuarioService.findById(id).orElse(null);
            if (clienteActivado != null) {
                System.out.println("✅ Cliente activado exitosamente. Nuevo estado: " + clienteActivado.getEstado());
            }
            
            redirectAttributes.addFlashAttribute("success", 
                "Cliente activado exitosamente");
        } catch (RuntimeException e) {
            System.err.println("❌ Error al activar cliente ID: " + id + " - " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error al activar cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * Eliminar cliente completamente (solo si no tiene pedidos ni transacciones de pago)
     */
    @PostMapping("/clientes/eliminar-completo/{id}")
    @SuppressWarnings("CallToPrintStackTrace")
    public String eliminarClienteCompleto(@PathVariable Integer id, 
                                         RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 === INICIO ELIMINACIÓN COMPLETA CLIENTE ===");
            System.out.println("🔍 ID recibido: " + id);
            
            // Verificar si se puede eliminar
            System.out.println("🔍 Verificando si se puede eliminar usuario ID: " + id);
            boolean puedeEliminar = usuarioService.puedeEliminarUsuario(id);
            System.out.println("🔍 ¿Puede eliminar? " + puedeEliminar);
            
            if (!puedeEliminar) {
                System.out.println("❌ No se puede eliminar - tiene pedidos o transacciones asociadas");
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el cliente porque tiene pedidos o transacciones de pago asociados");
                return "redirect:/admin/clientes";
            }
            
            // Obtener el usuario antes de eliminar
            Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            
            System.out.println("🔍 Usuario encontrado:");
            System.out.println("🔍   ID: " + usuario.getIdUsuario());
            System.out.println("🔍   Email: " + usuario.getEmail());
            System.out.println("🔍   Es Gmail: " + usuario.getEmail().toLowerCase().contains("@gmail.com"));
            
            // Eliminar transacciones de pago asociadas
            System.out.println("🔍 Buscando transacciones de pago...");
            List<TransaccionPago> transacciones = transaccionPagoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
            System.out.println("🔍 Transacciones encontradas: " + (transacciones != null ? transacciones.size() : 0));
            
            if (transacciones != null && !transacciones.isEmpty()) {
                System.out.println("🔍 Eliminando " + transacciones.size() + " transacciones...");
                transaccionPagoRepository.deleteAll(transacciones);
                System.out.println("✅ Transacciones eliminadas");
            }
            
            // Finalmente eliminar el usuario
            System.out.println("🔍 Eliminando usuario de la base de datos...");
            usuarioService.deleteById(id);
            System.out.println("✅ Usuario eliminado completamente");
            
            System.out.println("🔍 === FIN ELIMINACIÓN COMPLETA CLIENTE ===");
            redirectAttributes.addFlashAttribute("success", 
                "Cliente eliminado completamente del sistema");
        } catch (RuntimeException e) {
            System.err.println("❌ === ERROR EN ELIMINACIÓN COMPLETA CLIENTE ===");
            System.err.println("❌ ID: " + id);
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("❌ === FIN ERROR ELIMINACIÓN COMPLETA ===");
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * API: Obtener datos de cliente para formularios dinámicos
     */
    @GetMapping("/clientes/api/{id}")
    @ResponseBody
    public Usuario obtenerClienteApi(@PathVariable Integer id) {
        return usuarioService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }
    
    /**
     * API: Validar email único
     */
    @GetMapping("/clientes/api/validar-email")
    @ResponseBody
    public boolean validarEmailUnico(@RequestParam String email, 
                                    @RequestParam(required = false) Integer idUsuario) {
        return usuarioService.validarEmailUnico(email, idUsuario);
    }
}
