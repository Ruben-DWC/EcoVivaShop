package com.ecovivashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.PasswordResetService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Muestra el formulario para solicitar reset de contraseña
     */
    @GetMapping("/auth/forgot-password")
    public String mostrarForgotPassword() {
        return "auth/forgot-password";
    }

    /**
     * Procesa la solicitud de reset de contraseña
     */
    @PostMapping("/auth/forgot-password")
    public String procesarForgotPassword(@RequestParam @Email @NotBlank String email,
                                       RedirectAttributes redirectAttributes) {
        try {
            PasswordResetService.ResultadoResetPassword resultado = passwordResetService.solicitarResetPassword(email);

            if (resultado.isExitoso()) {
                redirectAttributes.addFlashAttribute("successMessage", resultado.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", resultado.getMensaje());
            }

            return "redirect:/auth/forgot-password";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al procesar la solicitud.");
            return "redirect:/auth/forgot-password";
        }
    }

    /**
     * Muestra el formulario para resetear contraseña con token
     */
    @GetMapping("/auth/reset-password")
    public String mostrarResetPassword(@RequestParam(required = false) String token,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (token == null || token.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Token de restablecimiento no válido.");
            return "redirect:/auth/forgot-password";
        }

        // Validar token
        var usuarioOpt = passwordResetService.validarTokenReset(token);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El enlace de restablecimiento no es válido o ha expirado.");
            return "redirect:/auth/forgot-password";
        }

        Usuario usuario = usuarioOpt.get();
        model.addAttribute("token", token);
        model.addAttribute("email", usuario.getEmail());
        model.addAttribute("nombre", usuario.getNombre());

        return "auth/reset-password";
    }

    /**
     * Procesa el cambio de contraseña con token
     */
    @PostMapping("/auth/reset-password")
    public String procesarResetPassword(@RequestParam String token,
                                      @RequestParam @NotBlank String password,
                                      @RequestParam @NotBlank String confirmPassword,
                                      RedirectAttributes redirectAttributes) {
        try {
            PasswordResetService.ResultadoResetPassword resultado =
                passwordResetService.actualizarPasswordConToken(token, password, confirmPassword);

            if (resultado.isExitoso()) {
                redirectAttributes.addFlashAttribute("successMessage", resultado.getMensaje());
                return "redirect:/auth/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", resultado.getMensaje());
                // Mantener el token en la URL para reintentar
                return "redirect:/auth/reset-password?token=" + token;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al actualizar la contraseña.");
            return "redirect:/auth/reset-password?token=" + token;
        }
    }

    /**
     * Endpoint para verificar estado del token (útil para AJAX)
     */
    @PostMapping("/auth/verify-token")
    public String verificarToken(@RequestParam String token,
                               RedirectAttributes redirectAttributes) {
        var usuarioOpt = passwordResetService.validarTokenReset(token);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Token inválido o expirado.");
            return "redirect:/auth/forgot-password";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Token válido. Puedes cambiar tu contraseña.");
        return "redirect:/auth/reset-password?token=" + token;
    }
}