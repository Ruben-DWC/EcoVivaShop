package com.ecovivashop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.ecovivashop.service.CustomOAuth2UserService;
import com.ecovivashop.service.CustomUserDetailsService;
import com.ecovivashop.service.RolService;
import com.ecovivashop.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    
    // Constructor manual en lugar de @RequiredArgsConstructor
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @SuppressWarnings("deprecation")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CustomOAuth2UserService customOAuth2UserService(UsuarioService usuarioService, RolService rolService) {
        return new CustomOAuth2UserService(usuarioService, rolService);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/imagenes/**")
                .ignoringRequestMatchers("/api/data/**")
                .ignoringRequestMatchers("/admin/upload-profile-image")
                .ignoringRequestMatchers("/client/upload-profile-image")
                .ignoringRequestMatchers("/admin/productos/bulk-upload")
                .ignoringRequestMatchers("/admin/productos/cargar-masivo")
                .ignoringRequestMatchers("/admin/pedidos/api-login") // Login API sin CSRF
                .ignoringRequestMatchers("/client/carrito/**", "/client/agregar-al-carrito", "/client/actualizar-carrito", "/client/remover-del-carrito", "/client/procesar-pago") // Carrito y pago sin CSRF
            )
            .authenticationProvider(this.authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/css/**", "/js/**", "/img/**", "/fonts/**", "/").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                // Test endpoints - Permitir acceso público para pruebas
                .requestMatchers("/test/**").permitAll()
                // Demo endpoints - Permitir acceso público para demostrar librerías externas
                .requestMatchers("/api/demo/**").permitAll()
                // Catálogo público - TODOS LOS ENDPOINTS DE CATÁLOGO
                .requestMatchers("/client/catalogo/**", "/client/catalogo", "/client/catalogo-*", "/client/catalogo-final", "/client/catalogo-integrado", "/client/catalogo-simple-test", "/client/producto-detalle/**", "/client/test-catalogo").permitAll()
                .requestMatchers("/client/home").permitAll() // Home público
                .requestMatchers("/client/agregar-al-carrito", "/client/actualizar-carrito", "/client/remover-del-carrito", "/client/carrito/**", "/client/procesar-pago").permitAll() // Carrito y pago público
                .requestMatchers("/api/imagenes/**").permitAll() // Imágenes públicas
                .requestMatchers("/images/**").permitAll() // Imágenes de productos públicas
                // ENDPOINTS DE DIAGNÓSTICO TEMPORALMENTE PÚBLICOS
                .requestMatchers("/admin/pedidos/auth-diagnostic", "/admin/pedidos/test-simple", "/admin/pedidos/roles-diagnostic", "/admin/pedidos/password-diagnostic", "/admin/pedidos/userdetails-diagnostic", "/admin/pedidos/auth-test", "/admin/pedidos/api-login").permitAll()
                .requestMatchers("/api/data/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                // Nota: removimos .requestMatchers("/client/**").hasRole("CLIENTE") para evitar conflictos
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(this.myAuthenticationSuccessHandler())
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOAuth2UserService)
                )
                .successHandler(this.myAuthenticationSuccessHandler())
                .failureUrl("/auth/login?error=oauth2")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(this.myLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            System.out.println("🎯 AuthenticationSuccessHandler called for user: " + authentication.getName());
            System.out.println("🎯 User authorities: " + authorities.stream().map(a -> a.getAuthority()).toList());

            String redirectUrl = "/";
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/admin/portal_administrador";
                System.out.println("🎯 Redirecting ADMIN to: " + redirectUrl);
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                redirectUrl = "/client/home";
                System.out.println("🎯 Redirecting CLIENTE to: " + redirectUrl);
            } else {
                System.out.println("🎯 No matching role found, redirecting to: " + redirectUrl);
            }
            response.sendRedirect(redirectUrl);
        };
    }
    
    @Bean
    public LogoutSuccessHandler myLogoutSuccessHandler() {
        return new SimpleUrlLogoutSuccessHandler() {
            @Override
            protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                // Store user info in flash attributes before logout
                if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
                    String userName = authentication.getName();
                    String userRole = "GUEST";
                    if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        userRole = "ADMIN";
                    } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                        userRole = "CLIENTE";
                    }
                    // Redirect with parameters
                    return "/auth/logout?userName=" + userName + "&userRole=" + userRole;
                }
                return "/auth/logout";
            }
        };
    }
}
