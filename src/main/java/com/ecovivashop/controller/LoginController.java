package com.ecovivashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/auth/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    // Si en el futuro se quiere un login para admin:
    // @GetMapping("/admin/login")
    // public String mostrarLoginAdmin() {
    //     return "admin/login";
    // }
}
