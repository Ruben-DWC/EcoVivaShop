package com.ecovivashop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_usuario")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido = "";

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email = "";

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "foto_perfil", length = 500)
    private String fotoPerfil;

    @Column(name = "dni", length = 20)
    private String dni;

    // Campos para OAuth2
    @Column(name = "provider", length = 50)
    private String provider; // "google", "facebook", etc.

    @Column(name = "provider_id", length = 100)
    private String providerId; // ID único del proveedor

    @Column(name = "provider_email", length = 150)
    private String providerEmail; // Email del proveedor (puede ser diferente del email local)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Suscripcion> suscripciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Pedido> pedidos;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = true;
        }
    }

    // Constructor personalizado para casos de uso específicos
    public Usuario(String nombre, String apellido, String email, String password, String telefono, String direccion, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rol = rol;
        this.estado = true;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    public boolean isAdmin() {
        return this.rol != null && ("ROLE_ADMIN".equals(this.rol.getNombre()) || "ROLE_SUPER_ADMIN".equals(this.rol.getNombre()));
    }

    public boolean isCliente() {
        return this.rol != null && "ROLE_CLIENTE".equals(this.rol.getNombre());
    }

    // Métodos para OAuth2
    public boolean isOAuth2User() {
        return this.provider != null && !this.provider.isEmpty();
    }

    public boolean isGoogleUser() {
        return "google".equalsIgnoreCase(this.provider);
    }

    public boolean isFacebookUser() {
        return "facebook".equalsIgnoreCase(this.provider);
    }
}
