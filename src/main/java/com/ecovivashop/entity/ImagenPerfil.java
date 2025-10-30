package com.ecovivashop.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "imagenes_perfil")
public class ImagenPerfil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "tipo_usuario", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;
    
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;
    
    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;
    
    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;
    
    @Column(name = "tipo_mime", nullable = false)
    private String tipoMime;
    
    @Column(name = "tamaño")
    private Long tamaño;
    
    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    public enum TipoUsuario {
        ADMIN, CLIENTE
    }
    
    // Constructores
    public ImagenPerfil() {
        this.fechaSubida = LocalDateTime.now();
    }
    
    public ImagenPerfil(Long usuarioId, TipoUsuario tipoUsuario, String nombreArchivo, 
                       String nombreOriginal, String rutaArchivo, String tipoMime, Long tamaño) {
        this();
        this.usuarioId = usuarioId;
        this.tipoUsuario = tipoUsuario;
        this.nombreArchivo = nombreArchivo;
        this.nombreOriginal = nombreOriginal;
        this.rutaArchivo = rutaArchivo;
        this.tipoMime = tipoMime;
        this.tamaño = tamaño;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
    
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public String getNombreOriginal() {
        return nombreOriginal;
    }
    
    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    public String getTipoMime() {
        return tipoMime;
    }
    
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }
    
    public Long getTamaño() {
        return tamaño;
    }
    
    public void setTamaño(Long tamaño) {
        this.tamaño = tamaño;
    }
    
    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }
    
    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
