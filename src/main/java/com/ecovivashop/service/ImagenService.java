package com.ecovivashop.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecovivashop.entity.ImagenPerfil;
import com.ecovivashop.entity.ImagenPerfil.TipoUsuario;
import com.ecovivashop.entity.ImagenProducto;
import com.ecovivashop.repository.ImagenPerfilRepository;
import com.ecovivashop.repository.ImagenProductoRepository;

@Service
public class ImagenService {
    
    @Value("${app.upload.path:uploads}")
    private String uploadPath;
    
    @Autowired
    private ImagenPerfilRepository imagenPerfilRepository;
    
    @Autowired
    private ImagenProductoRepository imagenProductoRepository;
    
    // Tamaños para redimensionamiento
    private static final int PROFILE_IMAGE_SIZE = 300;
    private static final int PRODUCT_IMAGE_WIDTH = 400;
    private static final int PRODUCT_IMAGE_HEIGHT = 400;
    
    /**
     * Guarda una imagen de perfil
     */
    public ImagenPerfil guardarImagenPerfil(Long usuarioId, TipoUsuario tipoUsuario, MultipartFile file) throws IOException {
        // Validar archivo
        validarArchivo(file);
        
        // Desactivar imagen anterior si existe
        imagenPerfilRepository.deactivateByUsuarioIdAndTipoUsuario(usuarioId, tipoUsuario);
        
        // Crear directorio si no existe
        String directorioTipo = tipoUsuario == TipoUsuario.ADMIN ? "admin" : "cliente";
        Path directorio = Paths.get(uploadPath, "profiles", directorioTipo);
        Files.createDirectories(directorio);
        
        // Generar nombre único para el archivo
        String extension = getFileExtension(file.getOriginalFilename());
        String nombreArchivo = generateUniqueFileName(usuarioId, tipoUsuario.name(), extension);
        Path rutaCompleta = directorio.resolve(nombreArchivo);
        
        // Redimensionar y guardar imagen
        BufferedImage imagenRedimensionada = redimensionarImagen(file, PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
        
        if (imagenRedimensionada != null) {
            // Imagen redimensionada exitosamente
            guardarImagen(imagenRedimensionada, rutaCompleta.toFile(), extension);
        } else {
            // Archivo que no se puede redimensionar (ej: SVG), copiar directamente
            Files.copy(file.getInputStream(), rutaCompleta);
        }
        
        // Crear y guardar registro en BD
        ImagenPerfil imagenPerfil = new ImagenPerfil(
            usuarioId,
            tipoUsuario,
            nombreArchivo,
            file.getOriginalFilename(),
            rutaCompleta.toString(),
            file.getContentType(),
            file.getSize()
        );
        
        return imagenPerfilRepository.save(imagenPerfil);
    }
    
    /**
     * Guarda una imagen de producto
     */
    public ImagenProducto guardarImagenProducto(Long productoId, MultipartFile file, 
                                               Integer orden, Boolean esPrincipal, String altText) throws IOException {
        // Validar archivo
        validarArchivo(file);
        
        // Si es principal, quitar la marca de principal de otras imágenes
        if (esPrincipal != null && esPrincipal) {
            imagenProductoRepository.clearPrincipalByProductoId(productoId);
        }
        
        // Crear directorio si no existe
        Path directorio = Paths.get(uploadPath, "products", productoId.toString());
        Files.createDirectories(directorio);
        
        // Generar nombre único para el archivo
        String extension = getFileExtension(file.getOriginalFilename());
        String nombreArchivo = generateUniqueFileName(productoId, "PRODUCT", extension);
        Path rutaCompleta = directorio.resolve(nombreArchivo);
        
        // Redimensionar y guardar imagen
        BufferedImage imagenRedimensionada = redimensionarImagen(file, PRODUCT_IMAGE_WIDTH, PRODUCT_IMAGE_HEIGHT);
        
        if (imagenRedimensionada != null) {
            // Imagen redimensionada exitosamente
            guardarImagen(imagenRedimensionada, rutaCompleta.toFile(), extension);
        } else {
            // Archivo que no se puede redimensionar (ej: SVG), copiar directamente
            Files.copy(file.getInputStream(), rutaCompleta);
        }
        
        // Crear y guardar registro en BD
        ImagenProducto imagenProducto = new ImagenProducto(
            productoId,
            nombreArchivo,
            file.getOriginalFilename(),
            rutaCompleta.toString(),
            file.getContentType(),
            file.getSize(),
            orden != null ? orden : 0,
            esPrincipal != null ? esPrincipal : false,
            altText
        );
        
        return imagenProductoRepository.save(imagenProducto);
    }
    
    /**
     * Obtiene la imagen de perfil activa de un usuario
     */
    public Optional<ImagenPerfil> obtenerImagenPerfil(Long usuarioId, TipoUsuario tipoUsuario) {
        return imagenPerfilRepository.findByUsuarioIdAndTipoUsuarioAndActivoTrue(usuarioId, tipoUsuario);
    }
    
    /**
     * Obtiene todas las imágenes de un producto
     */
    public List<ImagenProducto> obtenerImagenesProducto(Long productoId) {
        return imagenProductoRepository.findByProductoIdAndActivoTrueOrderByOrden(productoId);
    }
    
    /**
     * Obtiene la imagen principal de un producto
     */
    public Optional<ImagenProducto> obtenerImagenPrincipalProducto(Long productoId) {
        return imagenProductoRepository.findPrincipalByProductoId(productoId);
    }
    
    /**
     * Elimina una imagen de perfil
     */
    public void eliminarImagenPerfil(Long usuarioId, TipoUsuario tipoUsuario) {
        Optional<ImagenPerfil> imagen = imagenPerfilRepository.findByUsuarioIdAndTipoUsuarioAndActivoTrue(usuarioId, tipoUsuario);
        if (imagen.isPresent()) {
            ImagenPerfil img = imagen.get();
            img.setActivo(false);
            imagenPerfilRepository.save(img);
            
            // Eliminar archivo físico
            try {
                Files.deleteIfExists(Paths.get(img.getRutaArchivo()));
            } catch (IOException e) {
                // Log error but don't throw exception
                System.err.println("Error al eliminar archivo: " + e.getMessage());
            }
        }
    }
    
    /**
     * Elimina una imagen de producto
     */
    public void eliminarImagenProducto(Long imagenId) {
        Optional<ImagenProducto> imagen = imagenProductoRepository.findById(imagenId);
        if (imagen.isPresent()) {
            ImagenProducto img = imagen.get();
            img.setActivo(false);
            imagenProductoRepository.save(img);
            
            // Eliminar archivo físico
            try {
                Files.deleteIfExists(Paths.get(img.getRutaArchivo()));
            } catch (IOException e) {
                // Log error but don't throw exception
                System.err.println("Error al eliminar archivo: " + e.getMessage());
            }
        }
    }
    
    /**
     * Valida que el archivo sea válido
     */
    private void validarArchivo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("El archivo no puede estar vacío");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IOException("Nombre de archivo inválido");
        }
        
        // Permitir cualquier tipo y tamaño de archivo
        // Solo validamos que tenga un nombre válido
    }
    
    /**
     * Redimensiona una imagen manteniendo la proporción
     */
    private BufferedImage redimensionarImagen(MultipartFile file, int maxWidth, int maxHeight) throws IOException {
        try {
            String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
            
            // Para archivos que no son imágenes típicas, no redimensionar
            if (extension.equals(".svg") || extension.equals(".pdf") || extension.equals(".gif")) {
                return null;
            }
            
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            
            // Si no se puede leer como imagen, devolver null para guardar tal como está
            if (originalImage == null) {
                return null;
            }
            
            // Calcular nuevas dimensiones manteniendo proporción
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);
            
            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);
            
            // Si la imagen ya es más pequeña que el tamaño máximo, no redimensionar
            if (newWidth >= originalWidth && newHeight >= originalHeight) {
                return originalImage;
            }
            
            // Determinar el tipo de imagen para crear la nueva imagen
            int imageType = originalImage.getType();
            if (imageType == BufferedImage.TYPE_CUSTOM) {
                imageType = BufferedImage.TYPE_INT_RGB;
            }
            
            // Crear nueva imagen redimensionada
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);
            Graphics2D g2d = resizedImage.createGraphics();
            
            // Mejorar calidad de redimensionamiento
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();
            
            return resizedImage;
        } catch (IOException | IllegalArgumentException e) {
            // Si hay cualquier error, devolver null para guardar el archivo original
            return null;
        }
    }
    
    /**
     * Guarda una imagen en disco
     */
    private void guardarImagen(BufferedImage imagen, File archivo, String extension) throws IOException {
        String formatName = extension.substring(1).toLowerCase(); // Remover el punto
        
        // Mapear extensiones a formatos compatibles con ImageIO
        formatName = switch (formatName) {
            case "jpg", "jpeg", "jfif", "pjpeg", "pjp" -> "jpeg";
            case "tif" -> "tiff";
            case "svg" -> throw new IOException("SVG no requiere redimensionamiento, usar copia directa");
            default -> {
                // Para otros formatos, usar PNG como fallback si no es compatible
                String[] supportedFormats = ImageIO.getWriterFormatNames();
                boolean supported = false;
                for (String format : supportedFormats) {
                    if (format.equalsIgnoreCase(formatName)) {
                        supported = true;
                        break;
                    }
                }
                yield supported ? formatName : "png"; // Fallback a PNG
            }
        };
        
        // Crear archivo padre si no existe
        File parentDir = archivo.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // Verificar que el formato sea soportado
        if (!ImageIO.write(imagen, formatName, archivo)) {
            throw new IOException("No se pudo guardar la imagen en formato: " + formatName);
        }
    }
    
    /**
     * Genera un nombre único para el archivo
     */
    private String generateUniqueFileName(Long entityId, String type, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s_%s_%s%s", type, entityId, timestamp, uuid, extension);
    }
    
    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
    
    /**
     * Obtiene la URL pública de una imagen de perfil
     */
    public String obtenerUrlImagenPerfil(Long usuarioId, TipoUsuario tipoUsuario) {
        Optional<ImagenPerfil> imagen = obtenerImagenPerfil(usuarioId, tipoUsuario);
        if (imagen.isPresent()) {
            String tipoUsuarioStr = tipoUsuario == TipoUsuario.ADMIN ? "admin" : "cliente";
            return String.format("/uploads/profiles/%s/%s", tipoUsuarioStr, imagen.get().getNombreArchivo());
        }
        return "/img/default-profile.svg"; // Imagen por defecto SVG
    }
    
    /**
     * Obtiene la URL pública de la imagen principal de un producto
     */
    public String obtenerUrlImagenPrincipalProducto(Long productoId) {
        Optional<ImagenProducto> imagen = obtenerImagenPrincipalProducto(productoId);
        if (imagen.isPresent()) {
            return String.format("/uploads/products/%s/%s", productoId, imagen.get().getNombreArchivo());
        }
        return "/img/default-product.svg"; // Imagen por defecto SVG
    }
}
