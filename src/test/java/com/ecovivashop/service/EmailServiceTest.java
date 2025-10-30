package com.ecovivashop.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testEnviarCorreoBienvenida_EmailRealDisponible() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "usuario@test.com";
        String nombreUsuario = "Juan Pérez";

        // When
        emailService.enviarCorreoBienvenida(destinatario, nombreUsuario);

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEnviarCorreoBienvenida_EmailRealNoDisponible() {
        // Given
        ReflectionTestUtils.setField(emailService, "mailSender", null);
        String destinatario = "usuario@test.com";
        String nombreUsuario = "Juan Pérez";

        // When
        emailService.enviarCorreoBienvenida(destinatario, nombreUsuario);

        // Then
        verifyNoInteractions(mailSender);
        // El método simularEnvioEmail se ejecuta pero no podemos verificar System.out.println
    }

    @Test
    void testEnviarCorreoBienvenida_MailException() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "usuario@test.com";
        String nombreUsuario = "Juan Pérez";
        doThrow(new MailException("Error de envío") {}).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.enviarCorreoBienvenida(destinatario, nombreUsuario);

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
        // Debería simular el envío como fallback
    }

    @Test
    void testEnviarCorreoBienvenidaAdmin_SinCredenciales() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "admin@test.com";
        String nombreAdmin = "María García";
        String rol = "ADMIN";

        // When
        emailService.enviarCorreoBienvenidaAdmin(destinatario, nombreAdmin, rol);

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEnviarCorreoBienvenidaAdmin_ConCredenciales() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "admin@test.com";
        String nombreAdmin = "María García";
        String rol = "ADMIN";
        String credencialesTemp = "temp123";

        // When
        emailService.enviarCorreoBienvenidaAdmin(destinatario, nombreAdmin, rol, credencialesTemp);

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEnviarCorreoBienvenidaOAuth2() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "usuario@google.com";
        String nombreUsuario = "Ana López";
        String provider = "Google";
        when(templateEngine.process(eq("email/bienvenida"), any(Context.class)))
            .thenReturn("<html>Bienvenido</html>");
        when(mailSender.createMimeMessage()).thenReturn(mock(jakarta.mail.internet.MimeMessage.class));

        // When
        emailService.enviarCorreoBienvenidaOAuth2(destinatario, nombreUsuario, provider);

        // Then
        verify(templateEngine).process(eq("email/bienvenida"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarCorreoConfirmacionPedido() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "cliente@test.com";
        String nombreCliente = "Carlos Rodríguez";
        String numeroPedido = "PED-001";
        String totalPedido = "150.00";
        when(templateEngine.process(eq("email/confirmacion-pedido"), any(Context.class)))
            .thenReturn("<html>Confirmación de pedido</html>");
        when(mailSender.createMimeMessage()).thenReturn(mock(jakarta.mail.internet.MimeMessage.class));

        // When
        emailService.enviarCorreoConfirmacionPedido(destinatario, nombreCliente, numeroPedido, totalPedido);

        // Then
        verify(templateEngine).process(eq("email/confirmacion-pedido"), any(Context.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarCorreoRestablecimientoPassword() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");
        String destinatario = "usuario@test.com";
        String nombreUsuario = "Pedro Sánchez";
        String tokenReset = "reset-token-123";

        // When
        emailService.enviarCorreoRestablecimientoPassword(destinatario, nombreUsuario, tokenReset);

        // Then
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testGetEmailServiceStatus_EmailRealDisponible() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@ecovivashop.com");

        // When
        String status = emailService.getEmailServiceStatus();

        // Then
        assertTrue(status.contains("EMAILS REALES ACTIVOS"));
        assertTrue(status.contains("test@ecovivashop.com"));
    }

    @Test
    void testGetEmailServiceStatus_ModoSimulacion_MailSenderNull() {
        // Given
        ReflectionTestUtils.setField(emailService, "mailSender", null);

        // When
        String status = emailService.getEmailServiceStatus();

        // Then
        assertTrue(status.contains("MODO SIMULACIÓN"));
    }

    @Test
    void testGetEmailServiceStatus_ModoSimulacion_FromEmailInvalido() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "PONDRA-TU-CONTRASEÑA-DE-APLICACION-AQUI");

        // When
        String status = emailService.getEmailServiceStatus();

        // Then
        assertTrue(status.contains("MODO SIMULACIÓN"));
    }

    @Test
    void testGetEmailServiceStatus_ModoSimulacion_FromEmailVacio() {
        // Given
        ReflectionTestUtils.setField(emailService, "fromEmail", "");

        // When
        String status = emailService.getEmailServiceStatus();

        // Then
        assertTrue(status.contains("MODO SIMULACIÓN"));
    }
}