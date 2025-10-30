package com.ecovivashop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecovivashop.service.EmailService;

@Controller
@RequestMapping("/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    /**
     * Endpoint para probar el envío de correo de bienvenida
     * GET /test/email/bienvenida?email=test@example.com&nombre=Juan
     */
    @GetMapping("/email/bienvenida")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testEmailBienvenida(
            @RequestParam String email,
            @RequestParam(defaultValue = "Usuario de Prueba") String nombre) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            this.emailService.enviarCorreoBienvenida(email, nombre);
            response.put("status", "success");
            response.put("message", "✅ Correo de bienvenida enviado exitosamente a: " + email);
            response.put("destinatario", email);
            response.put("nombre", nombre);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ Error al enviar correo: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para probar el envío de correo de bienvenida admin
     * GET /test/email/bienvenida-admin?email=admin@example.com&nombre=Admin&rol=Super Administrador
     */
    @GetMapping("/email/bienvenida-admin")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testEmailBienvenidaAdmin(
            @RequestParam String email,
            @RequestParam(defaultValue = "Administrador de Prueba") String nombre,
            @RequestParam(defaultValue = "Super Administrador") String rol) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            String credencialesTemp = "TempAdmin123!";
            this.emailService.enviarCorreoBienvenidaAdmin(email, nombre, rol, credencialesTemp);
            
            response.put("status", "success");
            response.put("message", "✅ Correo de bienvenida admin enviado exitosamente a: " + email);
            response.put("destinatario", email);
            response.put("nombre", nombre);
            response.put("rol", rol);
            response.put("credenciales", credencialesTemp);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ Error al enviar correo admin: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para probar el envío de correo de confirmación de pedido
     * GET /test/email/confirmacion-pedido?email=cliente@example.com&nombre=Cliente&pedido=ECO-2024-1001&total=89.99
     */
    @GetMapping("/email/confirmacion-pedido")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testEmailConfirmacionPedido(
            @RequestParam String email,
            @RequestParam(defaultValue = "Cliente de Prueba") String nombre,
            @RequestParam(defaultValue = "ECO-2024-TEST") String pedido,
            @RequestParam(defaultValue = "99.99") String total) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            this.emailService.enviarCorreoConfirmacionPedido(email, nombre, pedido, "$" + total);
            
            response.put("status", "success");
            response.put("message", "✅ Correo de confirmación de pedido enviado exitosamente a: " + email);
            response.put("destinatario", email);
            response.put("cliente", nombre);
            response.put("pedido", pedido);
            response.put("total", "$" + total);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ Error al enviar correo de confirmación: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Página de prueba con formularios para enviar correos de prueba
     */
    @GetMapping("/email")
    public String testEmailPage() {
        return "test/email-test";
    }

    /**
     * Información sobre la configuración de correo
     */
    @GetMapping("/email/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> emailInfo() {
        Map<String, Object> info = new HashMap<>();
          info.put("status", "Servicio de correo activo");
        info.put("templates_disponibles", new String[]{
            "email/bienvenida - Correo de bienvenida para nuevos usuarios",
            "email/bienvenida - Correo de bienvenida OAuth2 HTML con Thymeleaf",
            "email/bienvenida-admin - Correo de bienvenida para nuevos administradores",
            "email/confirmacion-pedido - Confirmación de pedidos",
            "email/restablecer-password - Restablecimiento de contraseña"
        });
        
        info.put("endpoints_prueba", new String[]{
            "GET /test/email/bienvenida?email=test@example.com&nombre=Juan",
            "GET /test/email/bienvenida-oauth2?email=test@example.com&nombre=Juan&provider=Google",
            "GET /test/email/bienvenida-admin?email=admin@example.com&nombre=Admin&rol=Super Admin",
            "GET /test/email/confirmacion-pedido?email=cliente@example.com&nombre=Cliente&pedido=ECO-001&total=89.99"
        });
        
        info.put("configuracion_requerida", new String[]{
            "spring.mail.username - Configurar en application.properties",
            "spring.mail.password - Usar app password de Gmail",
            "Habilitar 'Aplicaciones menos seguras' o usar 'Contraseñas de aplicación'"
        });
        
        return ResponseEntity.ok(info);
    }

    /**
     * Endpoint para probar el envío de correo de bienvenida OAuth2 (HTML)
     * GET /test/email/bienvenida-oauth2?email=test@example.com&nombre=Juan&provider=Google
     */
    @GetMapping("/email/bienvenida-oauth2")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testEmailBienvenidaOAuth2(
            @RequestParam String email,
            @RequestParam(defaultValue = "Usuario OAuth2") String nombre,
            @RequestParam(defaultValue = "Google") String provider) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            this.emailService.enviarCorreoBienvenidaOAuth2(email, nombre, provider);
            
            response.put("status", "success");
            response.put("message", "✅ Correo de bienvenida OAuth2 HTML enviado exitosamente a: " + email);
            response.put("destinatario", email);
            response.put("nombre", nombre);
            response.put("provider", provider);
            response.put("tipo", "HTML con Thymeleaf");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ Error al enviar correo OAuth2 HTML: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
