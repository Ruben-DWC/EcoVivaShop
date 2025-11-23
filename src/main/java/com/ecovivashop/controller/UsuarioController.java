package com.ecovivashop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ecovivashop.dto.UsuarioRegistroDTO;
import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;
import com.ecovivashop.service.EmailService;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
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
    }

    @GetMapping("/auth/registro")
    public String mostrarFormularioRegistro(Model model) {
        System.out.println("✅ GET /auth/registro - Mostrando formulario de registro");
        try {
            model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
            System.out.println("✅ DTO agregado al modelo exitosamente");
            return "auth/registro";
        } catch (Exception e) {
            logger.error("Error en GET /auth/registro: {}", e.getMessage(), e);
            throw e; // Re-lanzar para que se maneje apropiadamente
        }
    }

    @PostMapping("/auth/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioRegistro") UsuarioRegistroDTO usuarioDTO,
                                  BindingResult bindingResult, Model model) {
        System.out.println("✅ POST /auth/registro - Procesando registro para: " + usuarioDTO.getEmail());

        // Verificar errores de validación
        if (bindingResult.hasErrors()) {
            System.err.println("❌ Errores de validación en el formulario de registro");
            bindingResult.getAllErrors().forEach(error ->
                System.err.println("❌ " + error.getDefaultMessage()));
            return "auth/registro";
        }

        System.out.println("✅ Validación del formulario exitosa");

        try {
            System.out.println("🔍 Buscando rol ROLE_CLIENTE...");
            // Buscar rol ROLE_CLIENTE (con ROLE_ prefix para Spring Security)
            Rol rolCliente = this.rolRepository.findByNombre("ROLE_CLIENTE").orElse(null);

            if (rolCliente == null) {
                System.err.println("❌ Error: Rol ROLE_CLIENTE no encontrado en la base de datos");
                model.addAttribute("error", "Error del sistema: Rol ROLE_CLIENTE no encontrado");
                return "auth/registro";
            }

            System.out.println("✅ Rol ROLE_CLIENTE encontrado: " + rolCliente.getIdRol());

            // Convertir DTO a Usuario
            System.out.println("🔄 Convirtiendo DTO a Usuario...");
            Usuario usuario = usuarioDTO.toUsuario();
            usuario.setRol(rolCliente);
            usuario.setPassword(this.passwordEncoder.encode(usuario.getPassword()));
            usuario.setFechaRegistro(java.time.LocalDateTime.now());
            usuario.setEstado(true);

            System.out.println("💾 Guardando usuario en la base de datos...");
            Usuario usuarioGuardado = this.usuarioRepository.save(usuario);
            System.out.println("✅ Usuario guardado exitosamente con ID: " + usuarioGuardado.getIdUsuario());

            // Enviar email de bienvenida
            try {
                System.out.println("📧 Enviando correo de bienvenida...");
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
            logger.error("Error en registro de usuario: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "auth/registro";
        }
    }
}
