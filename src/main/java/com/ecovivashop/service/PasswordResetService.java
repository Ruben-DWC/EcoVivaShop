package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.UsuarioRepository;

@Service
@Transactional
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UsuarioRepository usuarioRepository,
                               EmailService emailService,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inicia el proceso de reset de contraseña para un email
     */
    public ResultadoResetPassword solicitarResetPassword(String email) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email);

            if (usuarioOpt.isEmpty()) {
                // Por seguridad, no revelamos si el email existe o no
                return new ResultadoResetPassword(true, "Si el email existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña.");
            }

            Usuario usuario = usuarioOpt.get();

            // Verificar que no sea un usuario OAuth2 (no tienen contraseña local)
            if (usuario.isOAuth2User()) {
                return new ResultadoResetPassword(false, "Los usuarios registrados con Google o Facebook deben restablecer su contraseña directamente en el proveedor correspondiente.");
            }

            // Generar token de reset
            usuario.generarResetToken();
            usuarioRepository.save(usuario);

            // Enviar email con instrucciones
            emailService.enviarCorreoRestablecimientoPassword(
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getResetToken()
            );

            return new ResultadoResetPassword(true, "Se han enviado las instrucciones a tu email.");

        } catch (Exception e) {
            return new ResultadoResetPassword(false, "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    /**
     * Valida un token de reset y retorna el usuario si es válido
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> validarTokenReset(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetTokenAndValid(token, LocalDateTime.now());
        return usuarioOpt;
    }

    /**
     * Actualiza la contraseña usando un token válido
     */
    public ResultadoResetPassword actualizarPasswordConToken(String token, String nuevaPassword, String confirmarPassword) {
        try {
            // Validar entrada
            if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
                return new ResultadoResetPassword(false, "La nueva contraseña es obligatoria.");
            }

            if (nuevaPassword.length() < 6) {
                return new ResultadoResetPassword(false, "La contraseña debe tener al menos 6 caracteres.");
            }

            if (!nuevaPassword.equals(confirmarPassword)) {
                return new ResultadoResetPassword(false, "Las contraseñas no coinciden.");
            }

            // Validar token
            Optional<Usuario> usuarioOpt = validarTokenReset(token);
            if (usuarioOpt.isEmpty()) {
                return new ResultadoResetPassword(false, "El enlace de restablecimiento no es válido o ha expirado.");
            }

            Usuario usuario = usuarioOpt.get();

            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));

            // Limpiar token de reset
            usuario.limpiarResetToken();

            // Actualizar último acceso
            usuario.setUltimoAcceso(LocalDateTime.now());

            usuarioRepository.save(usuario);

            return new ResultadoResetPassword(true, "Tu contraseña ha sido actualizada exitosamente.");

        } catch (Exception e) {
            return new ResultadoResetPassword(false, "Error al actualizar la contraseña: " + e.getMessage());
        }
    }

    /**
     * Limpia tokens expirados (método de mantenimiento)
     */
    public void limpiarTokensExpirados() {
        // Nota: Este método requeriría una consulta personalizada en el repository
        // Por simplicidad, se puede implementar como un job programado
        System.out.println("Limpiando tokens de reset expirados...");
    }

    /**
     * Clase para resultados de operaciones
     */
    public static class ResultadoResetPassword {
        private final boolean exitoso;
        private final String mensaje;

        public ResultadoResetPassword(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}