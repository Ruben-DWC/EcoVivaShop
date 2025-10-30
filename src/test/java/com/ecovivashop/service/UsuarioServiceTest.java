package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testFindAll() {
        // Given
        List<Usuario> usuarios = Arrays.asList(
            crearUsuario(1, "Juan", "Pérez", "juan@test.com"),
            crearUsuario(2, "María", "García", "maria@test.com")
        );
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<Usuario> result = usuarioService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getNombre());
        assertEquals("María", result.get(1).getNombre());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testFindById_UsuarioExiste() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        // When
        Optional<Usuario> result = usuarioService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getNombre());
        verify(usuarioRepository).findById(1);
    }

    @Test
    void testFindById_UsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Usuario> result = usuarioService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(usuarioRepository).findById(999);
    }

    @Test
    void testSave() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        Usuario result = usuarioService.save(usuario);

        // Then
        assertEquals("Juan", result.getNombre());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testDeleteById() {
        // When
        usuarioService.deleteById(1);

        // Then
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void testBuscarPorEmail_UsuarioExiste() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));

        // When
        Optional<Usuario> result = usuarioService.buscarPorEmail("juan@test.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("juan@test.com", result.get().getEmail());
        verify(usuarioRepository).findByEmail("juan@test.com");
    }

    @Test
    void testBuscarPorEmail_UsuarioNoExiste() {
        // Given
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // When
        Optional<Usuario> result = usuarioService.buscarPorEmail("noexiste@test.com");

        // Then
        assertFalse(result.isPresent());
        verify(usuarioRepository).findByEmail("noexiste@test.com");
    }

    @Test
    void testFindByEmail() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));

        // When
        Usuario result = usuarioService.findByEmail("juan@test.com");

        // Then
        assertEquals("juan@test.com", result.getEmail());
        verify(usuarioRepository).findByEmail("juan@test.com");
    }

    @Test
    void testBuscarPorEmailConRol() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        Rol rol = new Rol();
        rol.setNombre("CLIENTE");
        usuario.setRol(rol);
        when(usuarioRepository.findByEmailIgnoreCaseWithRole("juan@test.com")).thenReturn(Optional.of(usuario));

        // When
        Optional<Usuario> result = usuarioService.buscarPorEmailConRol("juan@test.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("CLIENTE", result.get().getRol().getNombre());
        verify(usuarioRepository).findByEmailIgnoreCaseWithRole("juan@test.com");
    }

    @Test
    void testExisteEmail_Existe() {
        // Given
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(true);

        // When
        boolean result = usuarioService.existeEmail("juan@test.com");

        // Then
        assertTrue(result);
        verify(usuarioRepository).existsByEmail("juan@test.com");
    }

    @Test
    void testExisteEmail_NoExiste() {
        // Given
        when(usuarioRepository.existsByEmail("noexiste@test.com")).thenReturn(false);

        // When
        boolean result = usuarioService.existeEmail("noexiste@test.com");

        // Then
        assertFalse(result);
        verify(usuarioRepository).existsByEmail("noexiste@test.com");
    }

    @Test
    void testCrearUsuario() {
        // Given
        Rol rol = new Rol();
        rol.setNombre("ROLE_CLIENTE");
        when(rolRepository.findByNombre("ROLE_CLIENTE")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.crearUsuario("Juan", "Pérez", "juan@test.com", "password123", "ROLE_CLIENTE");

        // Then
        assertEquals("Juan", result.getNombre());
        assertEquals("Pérez", result.getApellido());
        assertEquals("juan@test.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(rol, result.getRol());
        assertTrue(result.getEstado());
        verify(rolRepository).findByNombre("ROLE_CLIENTE");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCrearCliente() {
        // Given
        Rol rol = new Rol();
        rol.setNombre("ROLE_CLIENTE");
        when(rolRepository.findByNombre("ROLE_CLIENTE")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.crearCliente("María", "García", "maria@test.com", "password123");

        // Then
        assertEquals("María", result.getNombre());
        assertEquals("García", result.getApellido());
        assertEquals("maria@test.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(rol, result.getRol());
        verify(rolRepository).findByNombre("ROLE_CLIENTE");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCrearAdmin() {
        // Given
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMIN");
        when(rolRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdminPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.crearAdmin("Admin", "Sistema", "admin@test.com", "admin123", "123456789", "Dirección Admin");

        // Then
        assertEquals("Admin", result.getNombre());
        assertEquals("Sistema", result.getApellido());
        assertEquals("admin@test.com", result.getEmail());
        assertEquals("123456789", result.getTelefono());
        assertEquals("Dirección Admin", result.getDireccion());
        assertEquals("encodedAdminPassword", result.getPassword());
        assertEquals(rol, result.getRol());
        verify(rolRepository).findByNombre("ROLE_ADMIN");
        verify(passwordEncoder).encode("admin123");
        verify(usuarioRepository, times(2)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario() {
        // Given
        Usuario usuarioExistente = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        usuarioExistente.setTelefono("987654321");
        usuarioExistente.setDireccion("Dirección Vieja");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Usuario result = usuarioService.actualizarUsuario(1, "Juan Carlos", "Pérez López", "123456789", "Nueva Dirección");

        // Then
        assertEquals("Juan Carlos", result.getNombre());
        assertEquals("Pérez López", result.getApellido());
        assertEquals("123456789", result.getTelefono());
        assertEquals("Nueva Dirección", result.getDireccion());
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCambiarPassword_PasswordCorrecto() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        usuario.setPassword("encodedOldPassword");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        usuarioService.cambiarPassword(1, "oldPassword", "newPassword");

        // Then
        verify(passwordEncoder).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCambiarPassword_PasswordIncorrecto() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        usuario.setPassword("encodedOldPassword");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            usuarioService.cambiarPassword(1, "wrongPassword", "newPassword"));
        assertEquals("La contraseña actual es incorrecta", exception.getMessage());
        verify(passwordEncoder).matches("wrongPassword", "encodedOldPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testDesactivarUsuario() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        usuario.setEstado(true);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        usuarioService.desactivarUsuario(1);

        // Then
        assertFalse(usuario.getEstado());
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testActivarUsuario() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");
        usuario.setEstado(false);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        usuarioService.activarUsuario(1);

        // Then
        assertTrue(usuario.getEstado());
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testActualizarUltimoAcceso() {
        // Given
        Usuario usuario = crearUsuario(1, "Juan", "Pérez", "juan@test.com");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        usuarioService.actualizarUltimoAcceso(1);

        // Then
        assertNotNull(usuario.getUltimoAcceso());
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testObtenerUsuariosActivos() {
        // Given
        List<Usuario> usuariosActivos = Arrays.asList(
            crearUsuario(1, "Juan", "Pérez", "juan@test.com"),
            crearUsuario(2, "María", "García", "maria@test.com")
        );
        when(usuarioRepository.findByEstadoTrue()).thenReturn(usuariosActivos);

        // When
        List<Usuario> result = usuarioService.obtenerUsuariosActivos();

        // Then
        assertEquals(2, result.size());
        verify(usuarioRepository).findByEstadoTrue();
    }

    @Test
    void testObtenerAdministradores() {
        // Given
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ADMIN");

        Usuario admin1 = crearUsuario(1, "Admin1", "Sistema", "admin1@test.com");
        admin1.setRol(rolAdmin);
        Usuario admin2 = crearUsuario(2, "Admin2", "Sistema", "admin2@test.com");
        admin2.setRol(rolAdmin);

        when(usuarioRepository.findAdministradores()).thenReturn(Arrays.asList(admin1, admin2));

        // When
        List<Usuario> result = usuarioService.obtenerAdministradores();

        // Then
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getRol().getNombre());
        verify(usuarioRepository).findAdministradores();
    }

    @Test
    void testObtenerClientes() {
        // Given
        Rol rolCliente = new Rol();
        rolCliente.setNombre("CLIENTE");

        Usuario cliente1 = crearUsuario(1, "Cliente1", "Apellido1", "cliente1@test.com");
        cliente1.setRol(rolCliente);
        Usuario cliente2 = crearUsuario(2, "Cliente2", "Apellido2", "cliente2@test.com");
        cliente2.setRol(rolCliente);

        when(usuarioRepository.findClientes()).thenReturn(Arrays.asList(cliente1, cliente2));

        // When
        List<Usuario> result = usuarioService.obtenerClientes();

        // Then
        assertEquals(2, result.size());
        assertEquals("CLIENTE", result.get(0).getRol().getNombre());
        verify(usuarioRepository).findClientes();
    }

    @Test
    void testBuscarUsuarios() {
        // Given
        List<Usuario> usuariosEncontrados = Arrays.asList(
            crearUsuario(1, "Juan", "Pérez", "juan@test.com"),
            crearUsuario(2, "Juan Carlos", "García", "juancarlos@test.com")
        );
        when(usuarioRepository.buscarUsuarios("Juan")).thenReturn(usuariosEncontrados);

        // When
        List<Usuario> result = usuarioService.buscarUsuarios("Juan");

        // Then
        assertEquals(2, result.size());
        verify(usuarioRepository).buscarUsuarios("Juan");
    }

    @Test
    void testCrearUsuario_ValidacionesConCommonsLang3_NombreVacio() {
        // When & Then - Usando Apache Commons Lang3 para validaciones
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("", "Perez", "juan@example.com", "password", "ROLE_CLIENTE");
        });
        assertEquals("El nombre no puede estar vacío (Apache Commons Lang3)", exception.getMessage());
    }

    @Test
    void testCrearUsuario_ValidacionesConCommonsLang3_ApellidoVacio() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("Juan", "", "juan@example.com", "password", "ROLE_CLIENTE");
        });
        assertEquals("El apellido no puede estar vacío (Apache Commons Lang3)", exception.getMessage());
    }

    @Test
    void testCrearUsuario_ValidacionesConCommonsLang3_EmailVacio() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("Juan", "Perez", "", "password", "ROLE_CLIENTE");
        });
        assertEquals("El email no puede estar vacío (Apache Commons Lang3)", exception.getMessage());
    }

    @Test
    void testCrearUsuario_ValidacionesConCommonsLang3_PasswordVacio() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("Juan", "Perez", "juan@example.com", "", "ROLE_CLIENTE");
        });
        assertEquals("La contraseña no puede estar vacía (Apache Commons Lang3)", exception.getMessage());
    }

    // Método helper para crear usuarios de prueba
    private Usuario crearUsuario(Integer idUsuario, String nombre, String apellido, String email) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword("password123");
        usuario.setEstado(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuario;
    }
}