package com.ecovivashop.controller;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;
import com.ecovivashop.service.ImagenService;
import com.ecovivashop.service.UsuarioService;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private UsuarioService usuarioService;

    @MockBean
    @SuppressWarnings({"removal", "unused"})
    private ImagenService imagenService;

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testPortal_WithPrincipal() throws Exception {
        // Given
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Admin");
        usuario.setEmail("admin@example.com");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("admin@example.com");
        when(usuarioService.findByEmail("admin@example.com")).thenReturn(usuario);

        // When & Then
        mockMvc.perform(get("/admin/portal").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("usuario"));

        verify(usuarioService, times(2)).findByEmail("admin@example.com");
    }

    @Test
    void testPortal_WithoutPrincipal() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/portal"))
                .andExpect(status().is3xxRedirection()); // Redirect to login

        verify(usuarioService, never()).findByEmail(anyString());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testMostrarFormularioRegistroAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/registro-admin"))
                .andExpect(model().attributeExists("usuario"));
    }

    @TestConfiguration
    @SuppressWarnings("unused")
    static class TestConfig {
        @Bean
        public RolRepository rolRepository() {
            return mock(RolRepository.class);
        }

        @Bean
        public UsuarioRepository usuarioRepository() {
            return mock(UsuarioRepository.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }
    }
}