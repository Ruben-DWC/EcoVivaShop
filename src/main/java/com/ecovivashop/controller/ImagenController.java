package com.ecovivashop.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ecovivashop.entity.ImagenPerfil;
import com.ecovivashop.entity.ImagenPerfil.TipoUsuario;
import com.ecovivashop.entity.ImagenProducto;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.ImagenService;
import com.ecovivashop.service.UsuarioService;

@Controller
@RequestMapping("/api/imagenes")
public class ImagenController {
    
    @Autowired
    private ImagenService imagenService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Value("${app.upload.path:uploads}")
    private String uploadPath;
    
    /**
     * Sube una imagen de perfil para admin
     */
    @PostMapping("/perfil/admin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> subirImagenPerfilAdmin(
            @RequestParam("imagen") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Guardar imagen
            ImagenPerfil imagen = imagenService.guardarImagenPerfil(usuario.getIdUsuario().longValue(), TipoUsuario.ADMIN, file);
            
            // Construir URL de respuesta
            String imageUrl = String.format("/uploads/profiles/admin/%s", imagen.getNombreArchivo());
            
            response.put("success", true);
            response.put("message", "Imagen subida exitosamente");
            response.put("imageUrl", imageUrl);
            response.put("imagenId", imagen.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Archivo no válido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error del sistema: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Sube una imagen de perfil para cliente
     */
    @PostMapping("/perfil/cliente")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> subirImagenPerfilCliente(
            @RequestParam("imagen") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Guardar imagen
            ImagenPerfil imagen = imagenService.guardarImagenPerfil(usuario.getIdUsuario().longValue(), TipoUsuario.CLIENTE, file);
            
            // Construir URL de respuesta
            String imageUrl = String.format("/uploads/profiles/cliente/%s", imagen.getNombreArchivo());
            
            response.put("success", true);
            response.put("message", "Imagen subida exitosamente");
            response.put("imageUrl", imageUrl);
            response.put("imagenId", imagen.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Archivo no válido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error del sistema: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Sube una imagen de producto
     */
    @PostMapping("/producto/{productoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> subirImagenProducto(
            @PathVariable Long productoId,
            @RequestParam("imagen") MultipartFile file,
            @RequestParam(required = false, defaultValue = "0") Integer orden,
            @RequestParam(required = false, defaultValue = "false") Boolean esPrincipal,
            @RequestParam(required = false) String altText) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Guardar imagen
            ImagenProducto imagen = imagenService.guardarImagenProducto(productoId, file, orden, esPrincipal, altText);
            
            // Construir URL de respuesta
            String imageUrl = String.format("/uploads/products/%s/%s", productoId, imagen.getNombreArchivo());
            
            response.put("success", true);
            response.put("message", "Imagen subida exitosamente");
            response.put("imageUrl", imageUrl);
            response.put("imagenId", imagen.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Archivo no válido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error del sistema: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Elimina una imagen de perfil
     */
    @DeleteMapping("/perfil/{tipoUsuario}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarImagenPerfil(@PathVariable String tipoUsuario) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = usuarioService.findByEmail(auth.getName());
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario.toUpperCase());
            imagenService.eliminarImagenPerfil(usuario.getIdUsuario().longValue(), tipo);
            
            response.put("success", true);
            response.put("message", "Imagen eliminada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Tipo de usuario no válido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Elimina una imagen de producto
     */
    @DeleteMapping("/producto/{imagenId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarImagenProducto(@PathVariable Long imagenId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            imagenService.eliminarImagenProducto(imagenId);
            
            response.put("success", true);
            response.put("message", "Imagen eliminada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Imagen no encontrada: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Obtiene las imágenes de un producto
     */
    @GetMapping("/producto/{productoId}")
    @ResponseBody
    public ResponseEntity<List<ImagenProducto>> obtenerImagenesProducto(@PathVariable Long productoId) {
        try {
            List<ImagenProducto> imagenes = imagenService.obtenerImagenesProducto(productoId);
            return ResponseEntity.ok(imagenes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Sirve la imagen de perfil de un usuario específico
     */
    @GetMapping("/perfil/{usuarioId}")
    public ResponseEntity<Resource> servirImagenPerfil(@PathVariable Long usuarioId) {
        try {
            // Buscar la imagen de perfil del usuario (primero admin, luego cliente)
            Optional<ImagenPerfil> imagenPerfilOpt = imagenService.obtenerImagenPerfil(usuarioId, TipoUsuario.ADMIN);
            TipoUsuario tipoUsuario = TipoUsuario.ADMIN;
            
            if (!imagenPerfilOpt.isPresent()) {
                // Si no es admin, buscar como cliente
                imagenPerfilOpt = imagenService.obtenerImagenPerfil(usuarioId, TipoUsuario.CLIENTE);
                tipoUsuario = TipoUsuario.CLIENTE;
            }
            
            if (!imagenPerfilOpt.isPresent()) {
                // Si no tiene imagen, devolver la imagen por defecto
                Path defaultPath = Paths.get("src/main/resources/static/img/default-profile.svg");
                Resource defaultResource = new FileSystemResource(defaultPath.toFile());
                
                if (defaultResource.exists()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.valueOf("image/svg+xml"))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"default-profile.svg\"")
                            .body(defaultResource);
                }
                
                return ResponseEntity.notFound().build();
            }
            
            ImagenPerfil imagenPerfil = imagenPerfilOpt.get();
            
            // Construir la ruta del archivo según el tipo de usuario
            String directorioTipo = tipoUsuario == TipoUsuario.ADMIN ? "admin" : "cliente";
            Path filePath = Paths.get(uploadPath, "profiles", directorioTipo, imagenPerfil.getNombreArchivo());
            Resource resource = new FileSystemResource(filePath.toFile());
            
            if (!resource.exists()) {
                // Si el archivo no existe, devolver la imagen por defecto
                Path defaultPath = Paths.get("src/main/resources/static/img/default-profile.svg");
                Resource defaultResource = new FileSystemResource(defaultPath.toFile());
                
                if (defaultResource.exists()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.valueOf("image/svg+xml"))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"default-profile.svg\"")
                            .body(defaultResource);
                }
                
                return ResponseEntity.notFound().build();
            }
            
            // Determinar el tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imagenPerfil.getNombreArchivo() + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Sirve archivos de imágenes
     */
    @GetMapping("/uploads/{tipo}/{subtipo}/{filename:.+}")
    public ResponseEntity<Resource> servirImagen(
            @PathVariable String tipo,
            @PathVariable String subtipo,
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadPath, tipo, subtipo, filename);
            Resource resource = new FileSystemResource(filePath.toFile());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determinar el tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Sirve archivos de imágenes de productos (con ID de producto)
     */
    @GetMapping("/uploads/products/{productoId}/{filename:.+}")
    public ResponseEntity<Resource> servirImagenProducto(
            @PathVariable Long productoId,
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(uploadPath, "products", productoId.toString(), filename);
            Resource resource = new FileSystemResource(filePath.toFile());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determinar el tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
