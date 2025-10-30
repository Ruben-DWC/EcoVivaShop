package com.ecovivashop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.ImagenPerfil;
import com.ecovivashop.entity.ImagenPerfil.TipoUsuario;

@Repository
public interface ImagenPerfilRepository extends JpaRepository<ImagenPerfil, Long> {
    
    @Query("SELECT i FROM ImagenPerfil i WHERE i.usuarioId = :usuarioId AND i.tipoUsuario = :tipoUsuario AND i.activo = true")
    Optional<ImagenPerfil> findByUsuarioIdAndTipoUsuarioAndActivoTrue(@Param("usuarioId") Long usuarioId, 
                                                                     @Param("tipoUsuario") TipoUsuario tipoUsuario);
    
    @Query("SELECT i FROM ImagenPerfil i WHERE i.usuarioId = :usuarioId AND i.tipoUsuario = :tipoUsuario")
    List<ImagenPerfil> findByUsuarioIdAndTipoUsuario(@Param("usuarioId") Long usuarioId, 
                                                     @Param("tipoUsuario") TipoUsuario tipoUsuario);
    
    @Query("SELECT i FROM ImagenPerfil i WHERE i.activo = true")
    List<ImagenPerfil> findAllActive();
    
    @Query("SELECT i FROM ImagenPerfil i WHERE i.tipoUsuario = :tipoUsuario AND i.activo = true")
    List<ImagenPerfil> findByTipoUsuarioAndActivoTrue(@Param("tipoUsuario") TipoUsuario tipoUsuario);
    
    @Modifying
    @Transactional
    @Query("UPDATE ImagenPerfil i SET i.activo = false WHERE i.usuarioId = :usuarioId AND i.tipoUsuario = :tipoUsuario")
    void deactivateByUsuarioIdAndTipoUsuario(@Param("usuarioId") Long usuarioId, 
                                           @Param("tipoUsuario") TipoUsuario tipoUsuario);
}
