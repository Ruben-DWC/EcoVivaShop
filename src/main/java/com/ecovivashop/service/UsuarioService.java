package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.TransaccionPago;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.TransaccionPagoRepository;
import com.ecovivashop.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TransaccionPagoRepository transaccionPagoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Constructor manual
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                         TransaccionPagoRepository transaccionPagoRepository,
                         PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.transaccionPagoRepository = transaccionPagoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Métodos CRUD básicos
    public List<Usuario> findAll() {
        return this.usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Integer id) {
        return this.usuarioRepository.findById(id);
    }
    
    public Usuario save(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }
    
    public void deleteById(Integer id) {
        this.usuarioRepository.deleteById(id);
    }
    
    // Métodos de búsqueda
    public Optional<Usuario> buscarPorEmail(String email) {
        return this.usuarioRepository.findByEmail(email);
    }
    
    // Método de compatibilidad para ClientController
    public Usuario findByEmail(String email) {
        return this.usuarioRepository.findByEmail(email).orElse(null);
    }
    
    public Optional<Usuario> buscarPorEmailConRol(String email) {
        return this.usuarioRepository.findByEmailIgnoreCaseWithRole(email);
    }
    
    public boolean existeEmail(String email) {
        return this.usuarioRepository.existsByEmail(email);
    }
    
    // Métodos de negocio
    public Usuario crearUsuario(String nombre, String apellido, String email, String password, String rolNombre) {
        // Validaciones usando Apache Commons Lang3
        if (StringUtils.isBlank(nombre)) {
            throw new IllegalArgumentException("El nombre no puede estar vacío (Apache Commons Lang3)");
        }
        if (StringUtils.isBlank(apellido)) {
            throw new IllegalArgumentException("El apellido no puede estar vacío (Apache Commons Lang3)");
        }
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("El email no puede estar vacío (Apache Commons Lang3)");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía (Apache Commons Lang3)");
        }
        
        if (this.existeEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con el correo electrónico: " + email);
        }
        
        Optional<Rol> rol = this.rolRepository.findByNombre(rolNombre);
        if (rol.isEmpty()) {
            throw new RuntimeException("Rol no encontrado: " + rolNombre);
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email.toLowerCase());
        usuario.setPassword(this.passwordEncoder.encode(password));
        usuario.setRol(rol.get());
        usuario.setEstado(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        
        Usuario usuarioGuardado = this.usuarioRepository.save(usuario);
          // Enviar email de bienvenida
        try {
            if ("ROLE_CLIENTE".equals(rolNombre)) {
                this.emailService.enviarCorreoBienvenida(usuarioGuardado.getEmail(), usuarioGuardado.getNombreCompleto());
            } else if ("ROLE_ADMIN".equals(rolNombre) || "ROLE_SUPER_ADMIN".equals(rolNombre)) {
                // Generar credenciales temporales para admin
                String credencialesTemp = "Admin" + System.currentTimeMillis() + "!";
                this.emailService.enviarCorreoBienvenidaAdmin(usuarioGuardado.getEmail(), usuarioGuardado.getNombreCompleto(), rolNombre, credencialesTemp);
            }
        } catch (Exception e) {
            // Log error but don't fail user creation
            System.err.println("Error enviando correo electrónico de bienvenida: " + e.getMessage());
        }
        
        return usuarioGuardado;
    }
    
    public Usuario crearCliente(String nombre, String apellido, String email, String password) {
        return this.crearUsuario(nombre, apellido, email, password, "ROLE_CLIENTE");
    }
    
    public Usuario crearAdmin(String nombre, String apellido, String email, String password, String telefono, String direccion) {
        Usuario usuario = this.crearUsuario(nombre, apellido, email, password, "ROLE_ADMIN");
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        return this.usuarioRepository.save(usuario);
    }
    
    public Usuario crearAdminCompleto(String nombre, String apellido, String email, String password, 
                                     String telefono, String dni, String departamento, String observaciones, 
                                     String[] permisos) {
        // Validaciones adicionales
        if (StringUtils.isBlank(dni)) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        if (StringUtils.isBlank(departamento)) {
            throw new IllegalArgumentException("El departamento no puede estar vacío");
        }
        
        Usuario usuario = this.crearUsuario(nombre, apellido, email, password, "ROLE_ADMIN");
        usuario.setTelefono(telefono);
        usuario.setDni(dni);
        usuario.setDepartamento(departamento);
        usuario.setObservaciones(observaciones);
        
        // Convertir permisos a JSON string
        if (permisos != null && permisos.length > 0) {
            try {
                // Usar Jackson para convertir array a JSON
                ObjectMapper objectMapper = new ObjectMapper();
                String permisosJson = objectMapper.writeValueAsString(permisos);
                usuario.setPermisos(permisosJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error al procesar permisos: " + e.getMessage());
            }
        }
        
        return this.usuarioRepository.save(usuario);
    }
    
    public Usuario actualizarUsuario(Integer id, String nombre, String apellido, String telefono, String direccion) {
        Optional<Usuario> usuarioExistente = this.usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        Usuario usuario = usuarioExistente.get();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        
        return this.usuarioRepository.save(usuario);
    }
    
    public void cambiarPassword(Integer id, String passwordActual, String nuevaPassword) {
        Optional<Usuario> usuarioExistente = this.usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        Usuario usuario = usuarioExistente.get();
        
        if (!this.passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        usuario.setPassword(this.passwordEncoder.encode(nuevaPassword));
        this.usuarioRepository.save(usuario);
    }
    
    public void desactivarUsuario(Integer id) {
        Optional<Usuario> usuarioExistente = this.usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        Usuario usuario = usuarioExistente.get();
        usuario.setEstado(false);
        this.usuarioRepository.save(usuario);
    }
    
    public void activarUsuario(Integer id) {
        Optional<Usuario> usuarioExistente = this.usuarioRepository.findById(id);
        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        Usuario usuario = usuarioExistente.get();
        usuario.setEstado(true);
        this.usuarioRepository.save(usuario);
    }
    
    public void actualizarUltimoAcceso(Integer id) {
        Optional<Usuario> usuarioExistente = this.usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setUltimoAcceso(LocalDateTime.now());
            this.usuarioRepository.save(usuario);
        }
    }
    
    // Consultas específicas
    public List<Usuario> obtenerUsuariosActivos() {
        return this.usuarioRepository.findByEstadoTrue();
    }
    
    public List<Usuario> obtenerAdministradores() {
        return this.usuarioRepository.findAdministradores();
    }
    
    public List<Usuario> obtenerClientes() {
        return this.usuarioRepository.findClientes();
    }
    
    public List<Usuario> buscarUsuarios(String busqueda) {
        return this.usuarioRepository.buscarUsuarios(busqueda);
    }
    
    public Page<Usuario> obtenerUsuariosPaginados(Pageable pageable) {
        return this.usuarioRepository.findByEstadoTrue(pageable);
    }
    
    // Estadísticas
    public Long contarUsuariosActivos() {
        return this.usuarioRepository.contarUsuariosActivos();
    }
    
    public Long contarClientes() {
        return this.usuarioRepository.contarPorRol("ROLE_CLIENTE");
    }
    
    public Long contarClientesActivos() {
        return this.usuarioRepository.contarClientesActivos();
    }
    
    public Long contarAdministradores() {
        return this.usuarioRepository.contarPorRol("ROLE_ADMIN") + this.usuarioRepository.contarPorRol("ROLE_SUPER_ADMIN");
    }
    
    public Long contarUsuariosRegistradosHoy() {
        LocalDateTime inicioHoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return this.usuarioRepository.contarUsuariosRegistradosDespueDe(inicioHoy);
    }
    
    public List<Usuario> obtenerUltimosRegistrados(int cantidad) {
        return this.usuarioRepository.findUltimosRegistrados(Pageable.ofSize(cantidad));
    }
    
    // Validaciones
    public boolean puedeEliminarUsuario(Integer id) {
        System.out.println("🔍 === VERIFICANDO SI SE PUEDE ELIMINAR USUARIO ===");
        System.out.println("🔍 ID del usuario: " + id);

        Optional<Usuario> usuario = this.usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            System.out.println("❌ Usuario no encontrado con ID: " + id);
            return false;
        }

        Usuario u = usuario.get();
        System.out.println("🔍 Usuario encontrado:");
        System.out.println("🔍   Email: " + u.getEmail());
        System.out.println("🔍   Es Gmail: " + u.getEmail().toLowerCase().contains("@gmail.com"));

        // No se puede eliminar si tiene pedidos
        boolean tienePedidos = u.getPedidos() != null && !u.getPedidos().isEmpty();
        System.out.println("🔍 ¿Tiene pedidos? " + tienePedidos);
        if (tienePedidos) {
            System.out.println("🔍   Número de pedidos: " + u.getPedidos().size());
            u.getPedidos().forEach(pedido -> {
                System.out.println("🔍     Pedido ID: " + pedido.getIdPedido() +
                                 ", Estado: " + pedido.getEstado() +
                                 ", Fecha: " + pedido.getFechaPedido());
            });
        }

        if (tienePedidos) {
            System.out.println("❌ No se puede eliminar - tiene pedidos asociados");
            return false;
        }

        // No se puede eliminar si tiene transacciones de pago
        System.out.println("🔍 Verificando transacciones de pago...");
        List<TransaccionPago> transacciones = transaccionPagoRepository.findByUsuarioOrderByFechaCreacionDesc(u);
        boolean tieneTransacciones = transacciones != null && !transacciones.isEmpty();
        System.out.println("🔍 ¿Tiene transacciones de pago? " + tieneTransacciones);

        if (transacciones != null && !transacciones.isEmpty()) {
            System.out.println("🔍   Número de transacciones: " + transacciones.size());
            transacciones.stream()
                .filter(transaccion -> transaccion != null)
                .forEach(transaccion -> {
                    System.out.println("🔍     Transacción ID: " + (transaccion.getIdTransaccion() != null ? transaccion.getIdTransaccion() : "null") +
                                     ", Monto: " + (transaccion.getMonto() != null ? transaccion.getMonto() : "null") +
                                     ", Estado: " + (transaccion.getEstadoTransaccion() != null ? transaccion.getEstadoTransaccion() : "null") +
                                     ", Fecha: " + (transaccion.getFechaCreacion() != null ? transaccion.getFechaCreacion() : "null"));
                });
        }

        boolean puedeEliminar = !tieneTransacciones;
        System.out.println("🔍 ¿Puede eliminar usuario? " + puedeEliminar);
        System.out.println("🔍 === FIN VERIFICACIÓN ELIMINACIÓN USUARIO ===");

        return puedeEliminar;
    }
    
    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && usuario.isAdmin();
    }
    
    public boolean esCliente(Usuario usuario) {
        return usuario != null && usuario.isCliente();
    }
    
    // Métodos adicionales para gestión de administradores
    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }
        return this.usuarioRepository.save(usuario);
    }
    
    public void cambiarPassword(String email, String passwordActual, String passwordNueva) {
        Usuario usuario = this.buscarPorEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (usuario.getPassword() == null) {
            throw new RuntimeException("El usuario no tiene contraseña configurada");
        }
        
        if (!this.passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        if (passwordNueva.length() < 8) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        
        usuario.setPassword(this.passwordEncoder.encode(passwordNueva));
        this.usuarioRepository.save(usuario);
    }
    
    // ========== MÉTODOS PARA ADMINISTRACIÓN DE CLIENTES ==========
    
    /**
     * Obtener clientes paginados
     */
    public Page<Usuario> obtenerClientesPaginados(Pageable pageable) {
        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));
        return usuarioRepository.findByRol(rolCliente, pageable);
    }
    
    /**
     * Buscar clientes por texto (incluyendo ID, nombre, email, teléfono, DNI)
     */
    public Page<Usuario> buscarClientesPaginado(String busqueda, Pageable pageable) {
        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));
        return usuarioRepository.buscarClientesCompleto(rolCliente, busqueda, pageable);
    }
    
    /**
     * Obtener clientes por estado
     */
    public Page<Usuario> obtenerClientesPorEstadoPaginado(boolean estado, Pageable pageable) {
        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));
        return usuarioRepository.findByRolAndEstado(rolCliente, estado, pageable);
    }
    
    /**
     * Registrar nuevo cliente desde admin
     */
    public Usuario registrarCliente(Usuario cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no puede ser null");
        }
        
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del cliente es requerido");
        }
        
        if (cliente.getPassword() == null || cliente.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña del cliente es requerida");
        }
        
        // Validar email único
        if (usuarioRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Asignar rol de cliente
        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));
        
        cliente.setRol(rolCliente);
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        cliente.setFechaRegistro(LocalDateTime.now());
        cliente.setEstado(true);
        
        return usuarioRepository.save(cliente);
    }
    
    /**
     * Actualizar cliente existente
     */
    public Usuario actualizarCliente(Usuario cliente) {
        System.out.println("🔍 === INICIO ACTUALIZAR CLIENTE EN SERVICE ===");
        System.out.println("🔍 Cliente recibido - ID: " + (cliente != null ? cliente.getIdUsuario() : "null"));
        System.out.println("🔍 Cliente recibido - Email: " + (cliente != null ? cliente.getEmail() : "null"));
        System.out.println("🔍 Cliente recibido - Nombre: " + (cliente != null ? cliente.getNombre() : "null"));
        
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no puede ser null");
        }
        
        if (cliente.getIdUsuario() == null) {
            throw new IllegalArgumentException("ID del cliente es requerido para actualización");
        }
        
        Usuario clienteExistente = usuarioRepository.findById(cliente.getIdUsuario())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        System.out.println("🔍 Cliente existente encontrado - ID: " + clienteExistente.getIdUsuario());
        System.out.println("🔍 Cliente existente - Email: " + clienteExistente.getEmail());
        
        // Validar email único (excluyendo el cliente actual)
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            // Extraer el email como string para evitar conflictos con el objeto managed
            String emailToValidate = cliente.getEmail().trim();
            Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmailIgnoreCase(emailToValidate);
            if (usuarioConEmail.isPresent() && !usuarioConEmail.get().getIdUsuario().equals(cliente.getIdUsuario())) {
                throw new RuntimeException("El email ya está registrado por otro usuario");
            }
        }
        
        // Actualizar campos
        if (cliente.getNombre() != null) {
            clienteExistente.setNombre(cliente.getNombre());
        }
        if (cliente.getApellido() != null) {
            clienteExistente.setApellido(cliente.getApellido());
        }
        if (cliente.getEmail() != null) {
            clienteExistente.setEmail(cliente.getEmail());
        }
        if (cliente.getTelefono() != null) {
            clienteExistente.setTelefono(cliente.getTelefono());
        }
        if (cliente.getDireccion() != null) {
            clienteExistente.setDireccion(cliente.getDireccion());
        }
        if (cliente.getDni() != null) {
            clienteExistente.setDni(cliente.getDni());
        }
        if (cliente.getFechaNacimiento() != null) {
            clienteExistente.setFechaNacimiento(cliente.getFechaNacimiento());
        }
        
        // Solo cambiar contraseña si se proporciona una nueva
        if (cliente.getPassword() != null && !cliente.getPassword().isEmpty()) {
            clienteExistente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        }
        
        if (cliente.getEstado() != null) {
            clienteExistente.setEstado(cliente.getEstado());
        }
        
        System.out.println("🔍 Guardando cliente actualizado...");
        Usuario resultado = usuarioRepository.save(clienteExistente);
        System.out.println("🔍 Cliente guardado exitosamente - ID: " + resultado.getIdUsuario());
        System.out.println("🔍 === FIN ACTUALIZAR CLIENTE EN SERVICE ===");
        
        return resultado;
    }
    
    /**
     * Cambiar estado del cliente (activar/desactivar)
     */
    public void cambiarEstadoCliente(Integer id) {
        Usuario cliente = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        cliente.setEstado(!cliente.getEstado());
        usuarioRepository.save(cliente);
    }
    
    /**
     * Validar email único
     */
    public boolean validarEmailUnico(String email, Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isEmpty()) {
            return true; // Email disponible
        }
        
        // Si es para edición, verificar que sea el mismo usuario
        return idUsuario != null && usuario.get().getIdUsuario().equals(idUsuario);
    }
    
    // ===== MÉTODOS PARA REPORTES =====
    
    /**
     * Obtener clientes por rango de fechas
     */
    public List<Usuario> obtenerClientesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return usuarioRepository.findClientesPorFecha(fechaInicio, fechaFin);
    }
    
    /**
     * Obtener todos los clientes (solo con rol CLIENTE)
     */
    public List<Usuario> obtenerTodosLosClientes() {
        return usuarioRepository.findByRolNombre("ROLE_CLIENTE");
    }
    
    // ===== GETTERS PARA DEPENDENCIAS =====
    
    /**
     * Obtener EmailService
     */
    public EmailService getEmailService() {
        return this.emailService;
    }
}
