package com.ecovivashop.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;
import com.ecovivashop.service.EmailService;

@Controller
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Constructor manual
    public UsuarioController(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                           PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }@GetMapping("/auth/registro")
    public String mostrarFormularioRegistro(Model model) {
        System.out.println("✅ GET /auth/registro - Mostrando formulario de registro");
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }    @PostMapping("/auth/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            // Buscar rol ROLE_CLIENTE (con ROLE_ prefix para Spring Security)
            Rol rolCliente = this.rolRepository.findByNombre("ROLE_CLIENTE").orElse(null);
            
            if (rolCliente == null) {
                model.addAttribute("error", "Error del sistema: Rol ROLE_CLIENTE no encontrado");
                return "auth/registro";
            }
            
            usuario.setRol(rolCliente);
            usuario.setPassword(this.passwordEncoder.encode(usuario.getPassword()));
            usuario.setFechaRegistro(java.time.LocalDateTime.now());
            usuario.setEstado(true);
            Usuario usuarioGuardado = this.usuarioRepository.save(usuario);
            
            // Enviar email de bienvenida
            try {
                this.emailService.enviarCorreoBienvenida(
                    usuarioGuardado.getEmail(), 
                    usuarioGuardado.getNombre() + " " + usuarioGuardado.getApellido()
                );
                System.out.println("✅ Correo de bienvenida enviado a: " + usuarioGuardado.getEmail());
            } catch (Exception emailError) {
                System.err.println("⚠️ Error enviando correo de bienvenida: " + emailError.getMessage());
                // No fallar el registro por errores de email
            }
            
            System.out.println("✅ Cliente registrado exitosamente: " + usuario.getEmail());
            return "auth/registro_exitoso";
        } catch (Exception e) {
            System.err.println("❌ Error en registro: " + e.getMessage());
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "auth/registro";
        }
    }
}
