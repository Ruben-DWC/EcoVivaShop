package com.ecovivashop.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecovivashop.entity.Suscripcion;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.CustomOAuth2User;
import com.ecovivashop.service.SuscripcionService;
import com.ecovivashop.service.UsuarioService;

@Controller
public class SuscripcionController {

    private final SuscripcionService suscripcionService;
    private final UsuarioService usuarioService;

    public SuscripcionController(SuscripcionService suscripcionService, UsuarioService usuarioService) {
        this.suscripcionService = suscripcionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/client/suscripciones")
    public String misSuscripciones(Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            if (usuario != null) {
                List<Suscripcion> suscripciones = suscripcionService.getSuscripcionesUsuario(usuario);
                model.addAttribute("suscripciones", suscripciones);
                model.addAttribute("usuario", usuario);
            }
        }
        return "client/suscripciones";
    }

    @PostMapping("/client/suscripcion/crear")
    @ResponseBody
    public Map<String, Object> crearSuscripcion(@RequestParam String tipoSuscripcion,
                                              @RequestParam BigDecimal precioMensual,
                                              @RequestParam String metodoPago,
                                              Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            Usuario usuario = getUsuarioFromPrincipal(authentication);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return response;
            }

            // Verificar si ya tiene una suscripción activa
            if (suscripcionService.tieneSuscripcionActiva(usuario)) {
                response.put("success", false);
                response.put("message", "Ya tienes una suscripción activa");
                return response;
            }

            // Crear la suscripción directamente (simplificado para este ejemplo)
            Suscripcion suscripcion = new Suscripcion();
            suscripcion.setUsuario(usuario);
            suscripcion.setTipoSuscripcion(tipoSuscripcion);
            suscripcion.setPrecioMensual(precioMensual);
            suscripcion.setEstado(true);
            suscripcion.setAutoRenovacion(true);

            // Configurar beneficios según el tipo
            switch (tipoSuscripcion.toUpperCase()) {
                case "BASIC" -> {
                    suscripcion.setBeneficios("Envío gratis, 5% descuento en productos");
                }
                case "PREMIUM" -> {
                    suscripcion.setBeneficios("Envío gratis, 15% descuento, productos exclusivos, entrega prioritaria");
                }
                case "VIP" -> {
                    suscripcion.setBeneficios("Envío gratis, 25% descuento, productos exclusivos, entrega prioritaria, asesoría personal");
                }
                default -> {
                    suscripcion.setBeneficios("Envío gratis, descuento en productos");
                }
            }

            // Simular procesamiento de pago
            boolean pagoExitoso = simularPago(metodoPago, precioMensual);

            if (!pagoExitoso) {
                response.put("success", false);
                response.put("message", "Error en el procesamiento del pago");
                return response;
            }

            // Guardar suscripción usando el repository directamente
            suscripcion = suscripcionService.save(suscripcion);

            response.put("success", true);
            response.put("message", "Suscripción creada exitosamente");
            response.put("suscripcionId", suscripcion.getIdSuscripcion());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear la suscripción: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/client/suscripcion/cancelar")
    @ResponseBody
    public Map<String, Object> cancelarSuscripcion(@RequestParam Integer suscripcionId,
                                                 @RequestParam String motivo,
                                                 Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            Usuario usuario = getUsuarioFromPrincipal(authentication);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return response;
            }

            Suscripcion suscripcion = suscripcionService.findById(suscripcionId).orElse(null);
            if (suscripcion == null) {
                response.put("success", false);
                response.put("message", "Suscripción no encontrada");
                return response;
            }

            // Verificar que la suscripción pertenece al usuario
            if (!suscripcion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                response.put("success", false);
                response.put("message", "No tienes permisos para cancelar esta suscripción");
                return response;
            }

            // Cancelar suscripción
            suscripcion.cancelar(motivo);
            suscripcionService.save(suscripcion);

            response.put("success", true);
            response.put("message", "Suscripción cancelada exitosamente");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cancelar la suscripción: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/client/suscripcion/renovar")
    @ResponseBody
    public Map<String, Object> renovarSuscripcion(@RequestParam Integer suscripcionId,
                                                @RequestParam String metodoPago,
                                                Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authentication == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            Usuario usuario = getUsuarioFromPrincipal(authentication);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return response;
            }

            Suscripcion suscripcion = suscripcionService.findById(suscripcionId).orElse(null);
            if (suscripcion == null) {
                response.put("success", false);
                response.put("message", "Suscripción no encontrada");
                return response;
            }

            // Verificar que la suscripción pertenece al usuario
            if (!suscripcion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                response.put("success", false);
                response.put("message", "No tienes permisos para renovar esta suscripción");
                return response;
            }

            // Simular procesamiento de pago
            boolean pagoExitoso = simularPago(metodoPago, suscripcion.getPrecioMensual());

            if (!pagoExitoso) {
                response.put("success", false);
                response.put("message", "Error en el procesamiento del pago");
                return response;
            }

            // Renovar por 1 mes
            suscripcion.renovar(1);
            suscripcionService.save(suscripcion);

            response.put("success", true);
            response.put("message", "Suscripción renovada exitosamente");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al renovar la suscripción: " + e.getMessage());
        }

        return response;
    }

    private boolean simularPago(String metodoPago, BigDecimal monto) {
        // Simulación de procesamiento de pago
        // En un entorno real, aquí se integraría con una pasarela de pagos
        System.out.println("Procesando pago de " + monto + " con método: " + metodoPago);
        try {
            Thread.sleep(2000); // Simular tiempo de procesamiento
            return true; // Simular pago exitoso
        } catch (InterruptedException e) {
            return false;
        }
    }

    private Usuario getUsuarioFromPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomOAuth2User oauthUser) {
            Integer userId = oauthUser.getUserId();
            return this.usuarioService.findById(userId).orElse(null);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            return this.usuarioService.findByEmail(email);
        } else if (principal instanceof String email) {
            return this.usuarioService.findByEmail(email);
        } else {
            return null;
        }
    }
}