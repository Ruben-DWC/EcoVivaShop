package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;

public class CustomOAuth2UserService extends OidcUserService {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public CustomOAuth2UserService(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("🔍 CustomOAuth2UserService.loadUser() called for provider: " + userRequest.getClientRegistration().getRegistrationId());

        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String givenName = oidcUser.getAttribute("given_name");
        String familyName = oidcUser.getAttribute("family_name");
        String picture = oidcUser.getAttribute("picture");
        String providerId = oidcUser.getAttribute("sub"); // Para Google

        if (providerId == null && "google".equals(registrationId)) {
            providerId = oidcUser.getAttribute("id");
        }

        if (providerId == null && "facebook".equals(registrationId)) {
            providerId = oidcUser.getAttribute("id");
        }

        // Buscar usuario existente por email
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorEmail(email);
        System.out.println("🔍 Checking if user exists with email: " + email + " - Found: " + usuarioExistente.isPresent());

        Usuario usuario;

        if (usuarioExistente.isPresent()) {
            // Usuario ya existe, actualizar información OAuth2 si es necesario
            System.out.println("🔄 User already exists, updating OAuth2 info");
            usuario = usuarioExistente.get();

            // Si el usuario no tiene información OAuth2, agregarla
            if (!usuario.isOAuth2User()) {
                usuario.setProvider(registrationId);
                usuario.setProviderId(providerId);
                usuario.setProviderEmail(email);
                // Mantener la contraseña existente para usuarios que ya tenían cuenta
            } else if (!registrationId.equalsIgnoreCase(usuario.getProvider())) {
                // Usuario ya tiene OAuth2 con otro proveedor
                throw new OAuth2AuthenticationException("Cuenta ya registrada con otro proveedor");
            }

            // Actualizar foto de perfil si está disponible
            if (picture != null && !picture.isEmpty() && (usuario.getFotoPerfil() == null || usuario.getFotoPerfil().isEmpty())) {
                usuario.setFotoPerfil(picture);
            }

            usuario.setUltimoAcceso(LocalDateTime.now());
            usuario = usuarioService.save(usuario);
        } else {
            // Crear nuevo usuario OAuth2
            System.out.println("🆕 Creating new OAuth2 user");
            usuario = crearUsuarioOAuth2(registrationId, email, name, givenName, familyName, picture, providerId);
        }

        // Enviar email de bienvenida para OAuth2 (tanto nuevos como existentes)
        try {
            usuarioService.getEmailService().enviarCorreoBienvenidaOAuth2(
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                registrationId
            );
            System.out.println("📧 Email de bienvenida OAuth2 enviado a: " + usuario.getEmail());
        } catch (Exception e) {
            // Log error but don't fail login
            System.err.println("Error enviando email de bienvenida OAuth2: " + e.getMessage());
        }

        System.out.println("✅ Returning CustomOAuth2User with role: " + usuario.getRol().getNombre());
        return new CustomOAuth2User(oidcUser, usuario.getIdUsuario(), usuario.getRol().getNombre());
    }

    private Usuario crearUsuarioOAuth2(String provider, String email, String name, String givenName,
                                     String familyName, String picture, String providerId) {
        // Separar nombre y apellido
        String nombre = givenName != null ? givenName : (name != null ? name.split(" ")[0] : "Usuario");
        String apellido = familyName != null ? familyName : (name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "OAuth2");

        // Obtener rol cliente por defecto
        Rol rolCliente = rolService.buscarPorNombre("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEmail(email.toLowerCase());
        nuevoUsuario.setPassword(""); // No necesita contraseña para OAuth2
        nuevoUsuario.setProvider(provider);
        nuevoUsuario.setProviderId(providerId);
        nuevoUsuario.setProviderEmail(email);
        nuevoUsuario.setFotoPerfil(picture);
        nuevoUsuario.setRol(rolCliente);
        nuevoUsuario.setEstado(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());
        nuevoUsuario.setUltimoAcceso(LocalDateTime.now());

        Usuario usuarioGuardado = usuarioService.save(nuevoUsuario);

        return usuarioGuardado;
    }
}