package com.ecovivashop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void testDefaultConstructor() {
        Usuario nuevoUsuario = new Usuario();

        assertNull(nuevoUsuario.getIdUsuario());
        assertNull(nuevoUsuario.getNombre());
        assertEquals("", nuevoUsuario.getApellido());
        assertEquals("", nuevoUsuario.getEmail());
        assertTrue(nuevoUsuario.getEstado());
        assertNotNull(nuevoUsuario.getFechaRegistro());
    }

    @Test
    void testCustomConstructor() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_CLIENTE");

        Usuario nuevoUsuario = new Usuario("María", "García", "maria@test.com", "pass123", "987654321", "Av. Principal", rol);

        assertEquals("María", nuevoUsuario.getNombre());
        assertEquals("García", nuevoUsuario.getApellido());
        assertEquals("maria@test.com", nuevoUsuario.getEmail());
        assertEquals("pass123", nuevoUsuario.getPassword());
        assertEquals("987654321", nuevoUsuario.getTelefono());
        assertEquals("Av. Principal", nuevoUsuario.getDireccion());
        assertEquals(rol, nuevoUsuario.getRol());
        assertTrue(nuevoUsuario.getEstado());
        assertNotNull(nuevoUsuario.getFechaRegistro());
    }

    @Test
    void testGettersAndSetters() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@test.com");
        usuario.setPassword("password123");
        usuario.setTelefono("123456789");
        usuario.setDireccion("Calle 123");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setEstado(true);
        usuario.setFechaRegistro(LocalDateTime.of(2023, 1, 1, 10, 0));
        usuario.setUltimoAcceso(LocalDateTime.of(2023, 12, 1, 15, 30));
        usuario.setFotoPerfil("foto.jpg");
        usuario.setDni("12345678");

        LocalDate fechaNac = LocalDate.of(1985, 5, 15);
        LocalDateTime fechaReg = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime ultimoAcc = LocalDateTime.of(2023, 12, 1, 15, 30);

        usuario.setFechaNacimiento(fechaNac);
        usuario.setFechaRegistro(fechaReg);
        usuario.setUltimoAcceso(ultimoAcc);
        usuario.setProvider("google");
        usuario.setProviderId("google123");
        usuario.setProviderEmail("google@test.com");

        assertEquals(1, usuario.getIdUsuario());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Pérez", usuario.getApellido());
        assertEquals("juan.perez@test.com", usuario.getEmail());
        assertEquals("password123", usuario.getPassword());
        assertEquals("123456789", usuario.getTelefono());
        assertEquals("Calle 123", usuario.getDireccion());
        assertEquals(fechaNac, usuario.getFechaNacimiento());
        assertTrue(usuario.getEstado());
        assertEquals(fechaReg, usuario.getFechaRegistro());
        assertEquals(ultimoAcc, usuario.getUltimoAcceso());
        assertEquals("foto.jpg", usuario.getFotoPerfil());
        assertEquals("12345678", usuario.getDni());
        assertEquals("google", usuario.getProvider());
        assertEquals("google123", usuario.getProviderId());
        assertEquals("google@test.com", usuario.getProviderEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        Usuario usuario1 = new Usuario();
        usuario1.setIdUsuario(1);

        Usuario usuario2 = new Usuario();
        usuario2.setIdUsuario(1);

        Usuario usuario3 = new Usuario();
        usuario3.setIdUsuario(2);

        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
        assertNotEquals(usuario1, usuario3);
        assertNotEquals(usuario1, null);
        assertEquals(usuario1, usuario1);
    }

    @Test
    void testGetNombreCompleto() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");

        assertEquals("Juan Pérez", usuario.getNombreCompleto());
    }

    @Test
    void testIsAdmin() {
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ROLE_ADMIN");

        Usuario adminUser = new Usuario();
        adminUser.setRol(rolAdmin);

        Usuario regularUser = new Usuario();

        assertTrue(adminUser.isAdmin());
        assertFalse(regularUser.isAdmin());
    }

    @Test
    void testIsCliente() {
        Rol rolCliente = new Rol();
        rolCliente.setNombre("ROLE_CLIENTE");

        Usuario clienteUser = new Usuario();
        clienteUser.setRol(rolCliente);

        assertTrue(clienteUser.isCliente());

        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ROLE_ADMIN");
        Usuario adminUser = new Usuario();
        adminUser.setRol(rolAdmin);

        assertFalse(adminUser.isCliente());
    }

    @Test
    void testOAuth2Methods() {
        Usuario oauthUser = new Usuario();
        oauthUser.setProvider("google");

        Usuario regularUser = new Usuario();

        assertTrue(oauthUser.isOAuth2User());
        assertFalse(regularUser.isOAuth2User());

        assertTrue(oauthUser.isGoogleUser());
        assertFalse(oauthUser.isFacebookUser());

        Usuario facebookUser = new Usuario();
        facebookUser.setProvider("facebook");
        assertTrue(facebookUser.isFacebookUser());
        assertFalse(facebookUser.isGoogleUser());
    }

    @Test
    void testPrePersist() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setFechaRegistro(null);
        nuevoUsuario.setEstado(null);

        nuevoUsuario.prePersist();

        assertNotNull(nuevoUsuario.getFechaRegistro());
        assertTrue(nuevoUsuario.getEstado());
    }

    @Test
    void testPrePersistExistingValues() {
        LocalDateTime existingFecha = LocalDateTime.of(2023, 1, 1, 10, 0);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setFechaRegistro(existingFecha);
        nuevoUsuario.setEstado(false);

        nuevoUsuario.prePersist();

        assertEquals(existingFecha, nuevoUsuario.getFechaRegistro());
        assertFalse(nuevoUsuario.getEstado());
    }
}