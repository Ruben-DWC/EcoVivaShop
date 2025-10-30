package com.ecovivashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {    @GetMapping({"/", "/index", "/index.html"})
    public String index() {
        System.out.println("✅ GET / - Mostrando página principal");
        return "index";
    }
}
