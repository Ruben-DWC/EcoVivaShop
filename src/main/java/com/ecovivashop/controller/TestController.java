package com.ecovivashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/registro")
    public String testRegistro(Model model) {
        model.addAttribute("message", "La ruta /auth/registro está funcionando correctamente");
        return "test/test-page";
    }
    
    @GetMapping("/rutas")
    public String testRutas(Model model) {
        model.addAttribute("message", "Sistema de rutas funcionando");
        return "test/test-page";
    }
}
