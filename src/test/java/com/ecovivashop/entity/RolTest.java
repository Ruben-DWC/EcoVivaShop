package com.ecovivashop.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class RolTest {

    @Test
    void testDefaultConstructor() {
        Rol nuevoRol = new Rol();

        assertNull(nuevoRol.getIdRol());
        assertNull(nuevoRol.getNombre());
        assertNull(nuevoRol.getDescripcion());
        assertTrue(nuevoRol.getEstado());
        assertNotNull(nuevoRol.getFechaCreacion());
    }

    @Test
    void testCustomConstructor() {
        Rol nuevoRol = new Rol("ROLE_ADMIN", "Rol de administrador");

        assertEquals("ROLE_ADMIN", nuevoRol.getNombre());
        assertEquals("Rol de administrador", nuevoRol.getDescripcion());
        assertTrue(nuevoRol.getEstado());
        assertNotNull(nuevoRol.getFechaCreacion());
    }

    @Test
    void testGettersAndSetters() {
        Rol rol = new Rol();
        LocalDateTime fechaCreacion = LocalDateTime.of(2023, 1, 1, 10, 0);

        rol.setIdRol(2);
        rol.setNombre("ROLE_VENDEDOR");
        rol.setDescripcion("Rol de vendedor");
        rol.setEstado(false);
        rol.setFechaCreacion(fechaCreacion);

        assertEquals(2, rol.getIdRol());
        assertEquals("ROLE_VENDEDOR", rol.getNombre());
        assertEquals("Rol de vendedor", rol.getDescripcion());
        assertFalse(rol.getEstado());
        assertEquals(fechaCreacion, rol.getFechaCreacion());
    }

    @Test
    void testEqualsAndHashCode() {
        Rol rol1 = new Rol();
        rol1.setIdRol(1);

        Rol rol2 = new Rol();
        rol2.setIdRol(1);

        Rol rol3 = new Rol();
        rol3.setIdRol(2);

        assertEquals(rol1, rol2);
        assertEquals(rol1.hashCode(), rol2.hashCode());
        assertNotEquals(rol1, rol3);
        assertNotEquals(rol1, null);
        assertEquals(rol1, rol1);
    }

    @Test
    void testPrePersist() {
        Rol nuevoRol = new Rol();
        nuevoRol.setFechaCreacion(null);
        nuevoRol.setEstado(null);

        nuevoRol.prePersist();

        assertNotNull(nuevoRol.getFechaCreacion());
        assertTrue(nuevoRol.getEstado());
    }

    @Test
    void testPrePersistExistingValues() {
        LocalDateTime existingFecha = LocalDateTime.of(2023, 1, 1, 10, 0);

        Rol nuevoRol = new Rol();
        nuevoRol.setFechaCreacion(existingFecha);
        nuevoRol.setEstado(false);

        nuevoRol.prePersist();

        assertEquals(existingFecha, nuevoRol.getFechaCreacion());
        assertFalse(nuevoRol.getEstado());
    }

    @Test
    void testUsuariosRelationship() {
        Rol rol = new Rol();
        // Test that usuarios collection can be set and retrieved
        rol.setUsuarios(null);
        assertNull(rol.getUsuarios());

        // Note: We don't test the actual relationship here as it would require
        // complex setup with Usuario entities and proper JPA context
    }
}