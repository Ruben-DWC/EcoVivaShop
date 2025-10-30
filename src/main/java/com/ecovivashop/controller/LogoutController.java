package com.ecovivashop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {

    @GetMapping("/auth/logout")
    public String showLogoutPage(@RequestParam(value = "userName", required = false) String userNameParam,
                                 @RequestParam(value = "userRole", required = false) String userRoleParam,
                                 Model model, HttpServletRequest request) {
        // Obtener información del usuario antes del logout
        String userName = "Usuario";
        String userRole = "GUEST";
        
        // Check request parameters first
        if (userNameParam != null && !userNameParam.isEmpty()) {
            userName = userNameParam;
            userRole = userRoleParam != null ? userRoleParam : "GUEST";
        } else {
            // Fallback to current authentication (might be anonymous)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                userName = auth.getName();
                // Determinar el rol del usuario
                if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    userRole = "ADMIN";
                } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                    userRole = "CLIENTE";
                }
            }
        }
        
        model.addAttribute("userName", userName);
        model.addAttribute("userRole", userRole);
        
        return "auth/logout";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Obtener la autenticación actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Si hay un usuario autenticado, cerrar la sesión completamente
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        // Limpiar cookies adicionales
        clearAllCookies(request, response);
        
        // Redirigir a la página de logout personalizada
        return "redirect:/auth/logout";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpServletRequest request, HttpServletResponse response) {
        // Spring Security maneja automáticamente el POST logout
        clearAllCookies(request, response);
        return "redirect:/auth/logout";
    }
    
    /**
     * Método para limpiar todas las cookies de la sesión
     */
    private void clearAllCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
