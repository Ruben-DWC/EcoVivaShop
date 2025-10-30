package com.ecovivashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class WebController extends BaseAdminController {
  
    @GetMapping("/registro-admin")
    public String registroAdmin(Model model) {
        model.addAttribute("pageTitle", "Registro de Administrador");
        return "admin/registro-admin";
    }

    @GetMapping("/inventario")
    public String inventario(Model model) {
        model.addAttribute("pageTitle", "Gestión de Inventario");
        return "admin/inventario";
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        model.addAttribute("pageTitle", "Configuración del Sistema");
        return "admin/configuracion";
    }
}
