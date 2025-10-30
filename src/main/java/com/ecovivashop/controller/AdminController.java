package com.ecovivashop.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ecovivashop.entity.ImagenPerfil.TipoUsuario;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.ImagenService;
import com.ecovivashop.service.UsuarioService;

@Controller
public class AdminController extends BaseAdminController {
    private final UsuarioService usuarioService;
    private final ImagenService imagenService;

    // Constructor manual
    public AdminController(UsuarioService usuarioService, ImagenService imagenService) {
        this.usuarioService = usuarioService;
        this.imagenService = imagenService;
    }// ========== RUTAS PRINCIPALES ==========
    
    // Dashboard empresarial principal 
    @GetMapping("/admin/portal")
    public String portal(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = this.usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        return "admin/dashboard";
    }
    
    @GetMapping("/admin/registro")
    public String mostrarFormularioRegistroAdmin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/registro-admin";
    }

    @PostMapping("/admin/registro")
    public String registrarAdmin(@ModelAttribute Usuario usuario, Model model) {
        try {
            this.usuarioService.crearUsuario(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getPassword(),
                "ADMIN"
            );
            return "redirect:/admin/portal_administrador";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar administrador: " + e.getMessage());
            return "admin/registro";
        }
    }

    @GetMapping("/admin/portal_administrador")
    public String portalAdministrador(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = this.usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        return "admin/portal_administrador";
    }

    @GetMapping("/admin/control_dashboard")
    public String controlDashboard() {
        return "admin/control_dashboard";
    }

    // ========== NUEVAS RUTAS ==========
    
    // Dashboard empresarial
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = this.usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        return "admin/dashboard";
    }

    // Perfil de administrador
    @GetMapping("/admin/perfil")
    public String perfilAdmin(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = this.usuarioService.findByEmail(principal.getName());
            if (usuario != null) {
                model.addAttribute("admin", usuario);
                
                // Agregar URL de imagen de perfil
                String imagenPerfilUrl = imagenService.obtenerUrlImagenPerfil(usuario.getIdUsuario().longValue(), TipoUsuario.ADMIN);
                model.addAttribute("imagenPerfilUrl", imagenPerfilUrl);
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        return "admin/perfil-admin";
    }
    
    // Ruta temporal para testing
    @GetMapping("/admin/perfil-test")
    public String perfilAdminTest(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = this.usuarioService.findByEmail(principal.getName());
            if (usuario != null) {
                model.addAttribute("admin", usuario);
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        return "admin/perfil-admin-test";
    }
    
    // Ruta simple para testing
    @GetMapping("/admin/perfil-simple")
    public String perfilAdminSimple(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = this.usuarioService.findByEmail(principal.getName());
            if (usuario != null) {
                model.addAttribute("admin", usuario);
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        return "admin/perfil-admin-simple";
    }

    // Actualizar perfil
    @PostMapping("/admin/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm, Principal principal, Model model) {
        try {
            if (principal != null) {
                Usuario usuarioActual = this.usuarioService.findByEmail(principal.getName());
                usuarioActual.setNombre(usuarioForm.getNombre());
                usuarioActual.setApellido(usuarioForm.getApellido());
                usuarioActual.setEmail(usuarioForm.getEmail());
                usuarioActual.setTelefono(usuarioForm.getTelefono());
                usuarioActual.setDireccion(usuarioForm.getDireccion());
                
                this.usuarioService.actualizarUsuario(usuarioActual);
                model.addAttribute("mensaje", "Perfil actualizado exitosamente");
                model.addAttribute("admin", usuarioActual);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }
        return "redirect:/admin/perfil";
    }

    // Subir foto de perfil
    @PostMapping("/admin/perfil/foto")
    public String subirFotoPerfil(@RequestParam("foto") MultipartFile foto, Principal principal, Model model) {
        try {
            if (principal != null && !foto.isEmpty()) {
                Usuario usuario = this.usuarioService.findByEmail(principal.getName());
                if (usuario != null) {
                    imagenService.guardarImagenPerfil(usuario.getIdUsuario().longValue(), TipoUsuario.ADMIN, foto);
                    model.addAttribute("success", "Foto de perfil actualizada exitosamente");
                }
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error al subir foto: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Archivo no válido: " + e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error del sistema: " + e.getMessage());
        }
        return "redirect:/admin/perfil";
    }

    // Eliminar foto de perfil
    @DeleteMapping("/admin/perfil/foto/eliminar")
    @ResponseBody
    public String eliminarFotoPerfil(Principal principal) {
        try {
            if (principal != null) {
                Usuario usuario = this.usuarioService.findByEmail(principal.getName());
                if (usuario != null) {
                    imagenService.eliminarImagenPerfil(usuario.getIdUsuario().longValue(), TipoUsuario.ADMIN);
                    return "{\"success\": true, \"message\": \"Foto eliminada exitosamente\"}";
                }
            }
            return "{\"success\": false, \"message\": \"Usuario no autenticado\"}";
        } catch (IllegalArgumentException e) {
            return "{\"success\": false, \"message\": \"Archivo no válido: " + e.getMessage() + "\"}";
        } catch (RuntimeException e) {
            return "{\"success\": false, \"message\": \"Error del sistema: " + e.getMessage() + "\"}";
        }
    }

    // Cambiar contraseña
    @PostMapping("/admin/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual, 
                                 @RequestParam String nuevaPassword, 
                                 Principal principal, Model model) {
        try {
            if (principal != null) {
                this.usuarioService.cambiarPassword(principal.getName(), passwordActual, nuevaPassword);
                model.addAttribute("mensaje", "Contraseña cambiada exitosamente");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
        }
        return "redirect:/admin/perfil";
    }

    // Gestión de administradores
    @GetMapping("/admin/administradores")
    public String gestionAdministradores(Model model) {
        List<Usuario> administradores = this.usuarioService.obtenerAdministradores();
        model.addAttribute("administradores", administradores);
        return "admin/administradores";
    }    // Management routes with different paths to avoid conflicts
    @GetMapping("/admin/manage/clientes")
    public String gestionClientes(Model model) {
        return "admin/clientes";
    }

    @GetMapping("/admin/manage/pedidos")
    public String gestionPedidos(Model model) {
        return "admin/pedidos";
    }

}
