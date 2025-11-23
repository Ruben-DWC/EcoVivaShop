package com.ecovivashop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecovivashop.service.EmailService;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final EmailService emailService;

    public DebugController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email-status")
    public String getEmailStatus() {
        return emailService.getEmailServiceStatus();
    }

    @GetMapping("/test-reset-password-email")
    public ResponseEntity<String> testResetPasswordEmail() {
        try {
            // Generar un token de prueba
            String testToken = "test-token-" + System.currentTimeMillis();
            emailService.enviarCorreoRestablecimientoPassword("subiendovideos903@gmail.com", "Usuario de Prueba", testToken);
            return ResponseEntity.ok("✅ Email de reset de contraseña enviado exitosamente\n🔗 Enlace de prueba: http://localhost:8081/auth/reset-password?token=" + testToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error enviando email de reset: " + e.getMessage());
        }
    }

    @GetMapping("/test-smtp-connection")
    public ResponseEntity<String> testSmtpConnection() {
        try {
            // Intentar conectar directamente al servidor SMTP
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("subiendovideos903@gmail.com", "bomxylaeypmunyex");
                }
            });

            // Intentar conectar
            try (Transport transport = session.getTransport("smtp")) {
                transport.connect("smtp.gmail.com", "subiendovideos903@gmail.com", "bomxylaeypmunyex");
            }

            return ResponseEntity.ok("✅ Conexión SMTP exitosa");
        } catch (jakarta.mail.MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error de conexión SMTP: " + e.getMessage());
        }
    }

    // Endpoint temporal público para probar envío de email sin autenticación
    @GetMapping("/public-test-email")
    public ResponseEntity<String> publicTestEmail() {
        try {
            String testToken = "public-test-token-" + System.currentTimeMillis();
            emailService.enviarCorreoRestablecimientoPassword("subiendovideos903@gmail.com", "Usuario de Prueba", testToken);
            return ResponseEntity.ok("✅ Email enviado - Revisa logs para detalles de configuración JavaMailSender");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error: " + e.getMessage());
        }
    }
}