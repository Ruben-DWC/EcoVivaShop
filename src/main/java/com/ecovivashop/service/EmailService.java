package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 📧 Servicio de Email para EcoVivaShop
 * 
 * FUNCIONA TANTO PARA EMAILS REALES COMO FALSOS:
 * ✅ Si tienes Gmail configurado correctamente -> Envía emails REALES
 * ✅ Si usas emails falsos o hay errores -> Simula el envío (NUNCA falla el registro)
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    // ========= MÉTODOS PÚBLICOS ==========

    // Correo de bienvenida a usuario
    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        try {
            if (this.puedeEnviarEmailReal()) {
                this.enviarEmailReal(destinatario, nombreUsuario);
                System.out.println("✅ [EMAIL REAL] Correo de bienvenida enviado a: " + destinatario);
            } else {
                this.simularEnvioEmail(destinatario, nombreUsuario);
            }
        } catch (MailException e) {
            System.err.println("⚠️ Error enviando email, simulando envío: " + e.getMessage());
            this.simularEnvioEmail(destinatario, nombreUsuario);
        } catch (RuntimeException e) {
            System.err.println("⚠️ Error inesperado enviando email, simulando envío: " + e.getMessage());
            this.simularEnvioEmail(destinatario, nombreUsuario);
        }
    }

    // Correo de bienvenida a admin
    public void enviarCorreoBienvenidaAdmin(String destinatario, String nombreAdmin, String rol) {
        try {
            if (this.puedeEnviarEmailReal()) {
                this.enviarEmailRealAdmin(destinatario, nombreAdmin, rol);
                System.out.println("✅ [EMAIL REAL] Correo admin enviado a: " + destinatario);
            } else {
                this.simularEnvioEmailAdmin(destinatario, nombreAdmin, rol);
            }
        } catch (MailException e) {
            System.err.println("⚠️ Error enviando email admin, simulando: " + e.getMessage());
            this.simularEnvioEmailAdmin(destinatario, nombreAdmin, rol);
        } catch (RuntimeException e) {
            System.err.println("⚠️ Error inesperado enviando email admin, simulando: " + e.getMessage());
            this.simularEnvioEmailAdmin(destinatario, nombreAdmin, rol);
        }
    }

    // Correo de bienvenida a admin (con credenciales temporales)
    public void enviarCorreoBienvenidaAdmin(String destinatario, String nombreAdmin, String rol, String credencialesTemp) {
        try {
            if (this.puedeEnviarEmailReal()) {
                this.enviarEmailRealAdmin(destinatario, nombreAdmin, rol, credencialesTemp);
                System.out.println("✅ [EMAIL REAL] Correo admin enviado a: " + destinatario);
            } else {
                this.simularEnvioEmailAdmin(destinatario, nombreAdmin, rol, credencialesTemp);
            }
        } catch (MailException e) {
            System.err.println("⚠️ Error enviando email admin, simulando: " + e.getMessage());
            this.simularEnvioEmailAdmin(destinatario, nombreAdmin, rol, credencialesTemp);
        } catch (RuntimeException e) {
            System.err.println("⚠️ Error inesperado enviando email admin, simulando: " + e.getMessage());
            this.simularEnvioEmailAdmin(destinatario, nombreAdmin, rol, credencialesTemp);
        }
    }

    // Correo de bienvenida a usuario OAuth2
    public void enviarCorreoBienvenidaOAuth2(String destinatario, String nombreUsuario, String provider) {
        try {
            if (this.puedeEnviarEmailReal()) {
                this.enviarEmailRealOAuth2(destinatario, nombreUsuario, provider);
                System.out.println("✅ [EMAIL REAL] Correo OAuth2 enviado a: " + destinatario);
            } else {
                this.simularEnvioEmailOAuth2(destinatario, nombreUsuario, provider);
            }
        } catch (MailException e) {
            System.err.println("⚠️ Error enviando email OAuth2, simulando: " + e.getMessage());
            this.simularEnvioEmailOAuth2(destinatario, nombreUsuario, provider);
        } catch (RuntimeException e) {
            System.err.println("⚠️ Error inesperado enviando email OAuth2, simulando: " + e.getMessage());
            this.simularEnvioEmailOAuth2(destinatario, nombreUsuario, provider);
        }
    }

    // Correo de confirmación de pedido
    public void enviarCorreoConfirmacionPedido(String destinatario, String nombreCliente, String numeroPedido, String totalPedido) {
        try {
            if (this.puedeEnviarEmailReal()) {
                this.enviarEmailRealPedido(destinatario, nombreCliente, numeroPedido, totalPedido);
                System.out.println("✅ [EMAIL REAL] Confirmación de pedido enviada a: " + destinatario);
            } else {
                this.simularEnvioEmailPedido(destinatario, nombreCliente, numeroPedido, totalPedido);
            }
        } catch (MailException e) {
            System.err.println("⚠️ Error enviando confirmación de pedido, simulando: " + e.getMessage());
            this.simularEnvioEmailPedido(destinatario, nombreCliente, numeroPedido, totalPedido);
        } catch (RuntimeException e) {
            System.err.println("⚠️ Error inesperado enviando confirmación de pedido, simulando: " + e.getMessage());
            this.simularEnvioEmailPedido(destinatario, nombreCliente, numeroPedido, totalPedido);
        }
    }

    // Correo de reseteo de contraseña
    public void enviarCorreoRestablecimientoPassword(String destinatario, String nombreUsuario, String tokenReset) {
        try {
            if (this.puedeEnviarEmailReal()) {
                this.enviarEmailRealReset(destinatario, nombreUsuario, tokenReset);
                System.out.println("✅ [EMAIL REAL] Reset password enviado a: " + destinatario);
            } else {
                this.simularEnvioEmailReset(destinatario, nombreUsuario, tokenReset);
            }
        } catch (MailException e) {
            System.err.println("⚠️ Error enviando reset password, simulando: " + e.getMessage());
            this.simularEnvioEmailReset(destinatario, nombreUsuario, tokenReset);
        } catch (RuntimeException e) {
            System.err.println("⚠️ Error inesperado enviando reset password, simulando: " + e.getMessage());
            this.simularEnvioEmailReset(destinatario, nombreUsuario, tokenReset);
        }
    }

    // ========= MÉTODOS PRIVADOS ==========

    // Verifica si puede enviar emails reales
    private boolean puedeEnviarEmailReal() {
        return this.mailSender != null &&
               this.fromEmail != null &&
               !this.fromEmail.isEmpty() &&
               !this.fromEmail.contains("PONDRA-TU-CONTRASEÑA-DE-APLICACION-AQUI");
    }

    // -------- Métodos para envío real --------
    private void enviarEmailReal(String destinatario, String nombreUsuario) throws MailException {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(this.fromEmail);
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido/a a EcoVivaShop! 🌱");
        mensaje.setText("Hola " + nombreUsuario + ",\n\n"
                + "¡Gracias por registrarte en EcoVivaShop!\n\n"
                + "Tu cuenta ha sido creada exitosamente y ya puedes comenzar a explorar nuestros productos ecológicos.\n\n"
                + "Saludos,\n"
                + "El equipo de EcoVivaShop 🌱");
        this.mailSender.send(mensaje);
    }

    private void enviarEmailRealAdmin(String destinatario, String nombreAdmin, String rol) throws MailException {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(this.fromEmail);
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido al equipo EcoVivaShop! 🔐");
        mensaje.setText("Hola " + nombreAdmin + ",\n\n"
                + "Tu cuenta de administrador ha sido creada exitosamente.\n\n"
                + "Rol asignado: " + rol + "\n\n"
                + "Ya puedes acceder al panel de administración.\n\n"
                + "Saludos,\n"
                + "El equipo de EcoVivaShop");
        this.mailSender.send(mensaje);
    }

    private void enviarEmailRealAdmin(String destinatario, String nombreAdmin, String rol, String credencialesTemp) throws MailException {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(this.fromEmail);
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido al equipo EcoVivaShop! 🔐");
        mensaje.setText("Hola " + nombreAdmin + ",\n\n"
                + "Tu cuenta de administrador ha sido creada exitosamente.\n\n"
                + "Rol asignado: " + rol + "\n"
                + "Credenciales temporales: " + credencialesTemp + "\n\n"
                + "Ya puedes acceder al panel de administración.\n\n"
                + "Saludos,\n"
                + "El equipo de EcoVivaShop");
        this.mailSender.send(mensaje);
    }

    private void enviarEmailRealPedido(String destinatario, String nombreCliente, String numeroPedido, String totalPedido) throws MailException {
        try {
            MimeMessage mensaje = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(this.fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("✅ Confirmación de tu pedido #" + numeroPedido + " - EcoVivaShop");

            // Preparar contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("nombreCliente", nombreCliente);
            context.setVariable("numeroPedido", numeroPedido);
            context.setVariable("emailCliente", destinatario);
            context.setVariable("totalPedido", totalPedido);

            // Procesar plantilla HTML
            String contenidoHtml = this.templateEngine.process("email/confirmacion-pedido", context);

            helper.setText(contenidoHtml, true); // true indica que es HTML

            this.mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new MailException("Error creando mensaje HTML", e) {};
        }
    }

    private void enviarEmailRealReset(String destinatario, String nombreUsuario, String tokenReset) throws MailException {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(this.fromEmail);
        mensaje.setTo(destinatario);
        mensaje.setSubject("🔐 Restablece tu contraseña - EcoVivaShop");
        mensaje.setText("Hola " + nombreUsuario + ",\n\n"
                + "Hemos recibido una solicitud para restablecer tu contraseña.\n\n"
                + "Tu token de restablecimiento es: " + tokenReset + "\n\n"
                + "Si no solicitaste este cambio, puedes ignorar este mensaje.\n\n"
                + "Saludos,\n"
                + "El equipo de EcoVivaShop");
        this.mailSender.send(mensaje);
    }

    private void enviarEmailRealOAuth2(String destinatario, String nombreUsuario, String provider) throws MailException {
        try {
            MimeMessage mensaje = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(this.fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("¡Bienvenido/a a EcoVivaShop con " + provider + "! 🌱");

            // Preparar contexto para Thymeleaf
            Context context = new Context();
            context.setVariable("nombreUsuario", nombreUsuario);
            context.setVariable("provider", provider);
            context.setVariable("emailUsuario", destinatario);
            context.setVariable("fechaRegistro", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            // Procesar plantilla HTML
            String contenidoHtml = this.templateEngine.process("email/bienvenida", context);

            helper.setText(contenidoHtml, true); // true indica que es HTML

            this.mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new MailException("Error creando email HTML OAuth2", e) {};
        }
    }

    // -------- Métodos para simulación (dev/universidad) --------
    private void simularEnvioEmail(String destinatario, String nombreUsuario) {
        this.logSimulacion("Bienvenida", destinatario, "Usuario: " + nombreUsuario, "¡Bienvenido/a a EcoVivaShop! 🌱");
    }

    private void simularEnvioEmailAdmin(String destinatario, String nombreAdmin, String rol) {
        this.logSimulacion("Bienvenida Admin", destinatario, "Admin: " + nombreAdmin + " Rol: " + rol, "¡Bienvenido al equipo EcoVivaShop! 🔐");
    }

    private void simularEnvioEmailAdmin(String destinatario, String nombreAdmin, String rol, String credencialesTemp) {
        this.logSimulacion("Bienvenida Admin", destinatario, "Admin: " + nombreAdmin + " Rol: " + rol + " Credenciales: " + credencialesTemp, "¡Bienvenido al equipo EcoVivaShop! 🔐");
    }

    private void simularEnvioEmailOAuth2(String destinatario, String nombreUsuario, String provider) {
        this.logSimulacion("Bienvenida OAuth2", destinatario, "Usuario: " + nombreUsuario + " Provider: " + provider, "¡Bienvenido/a a EcoVivaShop con " + provider + "! 🌱");
    }

    private void simularEnvioEmailPedido(String destinatario, String nombreCliente, String numeroPedido, String totalPedido) {
        this.logSimulacion("Confirmación de Pedido (HTML)", destinatario, "Cliente: " + nombreCliente + " Pedido: #" + numeroPedido + " Total: $" + totalPedido, "✅ Confirmación de tu pedido #" + numeroPedido + " - EcoVivaShop");
    }

    private void simularEnvioEmailReset(String destinatario, String nombreUsuario, String tokenReset) {
        this.logSimulacion("Reset de Contraseña", destinatario, "Usuario: " + nombreUsuario + " Token: " + tokenReset, "🔐 Restablece tu contraseña - EcoVivaShop");
    }

    // Método genérico para imprimir simulación de email
    private void logSimulacion(String tipo, String destinatario, String infoExtra, String asunto) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("📧 ============== SIMULACIÓN DE EMAIL ==============");
        System.out.println("🕒 Hora: " + timestamp);
        System.out.println("📩 Tipo: " + tipo);
        System.out.println("👤 Destinatario: " + destinatario);
        System.out.println("🔎 Info extra: " + infoExtra);
        System.out.println("📝 Asunto: " + asunto);
        System.out.println("💡 NOTA: Email simulado (funciona con emails reales y falsos)");
        System.out.println("📧 =============================================");
    }

    // Estado del servicio de email (útil para debug)
    public String getEmailServiceStatus() {
        if (this.puedeEnviarEmailReal()) {
            return "✅ EMAILS REALES ACTIVOS - Configurado con: " + this.fromEmail;
        } else {
            return "🔧 MODO SIMULACIÓN - Acepta emails reales y falsos";
        }
    }
}
