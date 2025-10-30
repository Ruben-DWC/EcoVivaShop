package com.ecovivashop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Rol;
import com.ecovivashop.repository.RolRepository;



@Service
@Transactional
public class RolService {
    private final RolRepository rolRepository;

    // Constructor manual
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    // Métodos CRUD básicos
    public List<Rol> findAll() {
        return this.rolRepository.findAll();
    }
    
    public Optional<Rol> findById(Integer id) {
        return this.rolRepository.findById(id);
    }
    
    public Rol save(Rol rol) {
        return this.rolRepository.save(rol);
    }
    
    public void deleteById(Integer id) {
        this.rolRepository.deleteById(id);
    }
    
    // Métodos de negocio
    public Optional<Rol> buscarPorNombre(String nombre) {
        return this.rolRepository.findByNombre(nombre);
    }
    
    public List<Rol> findRolesActivos() {
        return this.rolRepository.findByEstadoTrue();
    }
    
    public boolean existeRolConNombre(String nombre) {
        return this.rolRepository.existsByNombre(nombre);
    }
    
    public Rol crearRol(String nombre, String descripcion) {
        if (this.existeRolConNombre(nombre)) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nombre);
        }
        
        Rol rol = new Rol();
        rol.setNombre(nombre.toUpperCase());
        rol.setDescripcion(descripcion);
        rol.setEstado(true);
        rol.setFechaCreacion(LocalDateTime.now());
        
        return this.rolRepository.save(rol);
    }
    
    public Rol actualizarRol(Integer id, String nombre, String descripcion) {
        Optional<Rol> rolExistente = this.rolRepository.findById(id);
        if (rolExistente.isEmpty()) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        
        Rol rol = rolExistente.get();
        
        // Verificar si el nuevo nombre ya existe (diferente al actual)
        if (!rol.getNombre().equals(nombre.toUpperCase()) && this.existeRolConNombre(nombre)) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nombre);
        }
        
        rol.setNombre(nombre.toUpperCase());
        rol.setDescripcion(descripcion);
        
        return this.rolRepository.save(rol);
    }
    
    public void desactivarRol(Integer id) {
        Optional<Rol> rolExistente = this.rolRepository.findById(id);
        if (rolExistente.isEmpty()) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        
        Rol rol = rolExistente.get();
        rol.setEstado(false);
        this.rolRepository.save(rol);
    }
    
    public void activarRol(Integer id) {
        Optional<Rol> rolExistente = this.rolRepository.findById(id);
        if (rolExistente.isEmpty()) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        
        Rol rol = rolExistente.get();
        rol.setEstado(true);
        this.rolRepository.save(rol);
    }
    
    public List<Rol> buscarRoles(String texto) {
        return this.rolRepository.buscarPorTexto(texto);
    }
    
    public Long contarRolesActivos() {
        return this.rolRepository.contarRolesActivos();
    }
    
    public List<Rol> obtenerRolesMasUtilizados() {
        return this.rolRepository.obtenerRolesMasUtilizados();
    }
    
    // Métodos para inicializar roles por defecto
    public void inicializarRolesPorDefecto() {
        if (this.rolRepository.count() == 0) {
            this.crearRol("SUPER_ADMIN", "Super Administrador con acceso completo al sistema");
            this.crearRol("ADMIN", "Administrador con permisos de gestión");
            this.crearRol("CLIENTE", "Cliente con acceso a compras y gestión de cuenta");
            this.crearRol("VENDEDOR", "Vendedor con acceso a gestión de productos y pedidos");
        }
    }
    
    // Validaciones
    public boolean puedeEliminarRol(Integer id) {
        Optional<Rol> rol = this.rolRepository.findById(id);
        if (rol.isEmpty()) {
            return false;
        }
        
        // No se puede eliminar si tiene usuarios asignados
        return rol.get().getUsuarios() == null || rol.get().getUsuarios().isEmpty();
    }
    
    public boolean esRolSistema(String nombre) {
        return nombre.equals("SUPER_ADMIN") || nombre.equals("ADMIN") || nombre.equals("CLIENTE");
    }
}
