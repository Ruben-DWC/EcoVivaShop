package com.ecovivashop.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.UsuarioService;

/**
 * Clase base para todos los controladores de clientes
 * Proporciona funcionalidad común como acceso al usuario autenticado
 */
public abstract class BaseClientController {
    
    @Autowired
    protected UsuarioService usuarioService;
    
    /**
     * Método para hacer que el usuario esté disponible en todas las páginas de cliente
     * Se ejecuta antes de cada método del controlador
     */
    @ModelAttribute("usuario")
    public Usuario usuarioAutenticado(Principal principal) {
        if (principal != null) {
            try {
                return this.usuarioService.findByEmail(principal.getName());
            } catch (Exception e) {
                // Si hay error, retornar null para usar imagen por defecto
                return null;
            }
        }
        return null;
    }
}
