package com.ecovivashop.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecovivashop.entity.Usuario;

@Controller
@RequestMapping("/admin")
public class ClienteAdminController extends BaseAdminController {
    
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
        
        // Estadísticas
        long totalClientes = usuarioService.contarClientes();
        long clientesActivos = usuarioService.contarClientesActivos();
        long clientesInactivos = totalClientes - clientesActivos;
        
        // Agregar al modelo
        model.addAttribute("clientes", clientes);
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
    public String guardarCliente(@ModelAttribute Usuario cliente,
                                RedirectAttributes redirectAttributes) {
        try {
            // Si es nuevo cliente, asignar rol de cliente
            if (cliente.getIdUsuario() == null) {
                usuarioService.registrarCliente(cliente);
                redirectAttributes.addFlashAttribute("success", 
                    "Cliente registrado exitosamente");
            } else {
                usuarioService.actualizarCliente(cliente);
                redirectAttributes.addFlashAttribute("success", 
                    "Cliente actualizado exitosamente");
            }
                
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        
        // Obtener estadísticas del cliente
        // Implementar contadores de pedidos, compras, etc.
        
        model.addAttribute("cliente", cliente);
        
        return "admin/cliente-detalle";
    }
    
    /**
     * Eliminar cliente (cambiar estado a inactivo)
     */
    @PostMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable Integer id, 
                                 RedirectAttributes redirectAttributes) {
        try {
            // Verificar que el cliente existe antes de desactivar
            usuarioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            
            // En lugar de eliminar físicamente, desactivamos el cliente
            usuarioService.desactivarUsuario(id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Cliente desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al desactivar cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * Activar cliente
     */
    @PostMapping("/clientes/activar/{id}")
    public String activarCliente(@PathVariable Integer id, 
                                RedirectAttributes redirectAttributes) {
        try {
            usuarioService.activarUsuario(id);
            redirectAttributes.addFlashAttribute("success", 
                "Cliente activado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al activar cliente: " + e.getMessage());
        }
        
        return "redirect:/admin/clientes";
    }
    
    /**
     * Eliminar cliente completamente (solo si no tiene pedidos)
     */
    @PostMapping("/clientes/eliminar-completo/{id}")
    public String eliminarClienteCompleto(@PathVariable Integer id, 
                                         RedirectAttributes redirectAttributes) {
        try {
            // Verificar si se puede eliminar
            if (!usuarioService.puedeEliminarUsuario(id)) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el cliente porque tiene pedidos asociados");
                return "redirect:/admin/clientes";
            }
            
            // Eliminar completamente
            usuarioService.deleteById(id);
            
            redirectAttributes.addFlashAttribute("success", 
                "Cliente eliminado completamente del sistema");
        } catch (Exception e) {
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
