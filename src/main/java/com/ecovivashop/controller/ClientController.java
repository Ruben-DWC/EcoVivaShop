package com.ecovivashop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.PedidoDetalle;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.CustomOAuth2User;
import com.ecovivashop.service.EmailService;
import com.ecovivashop.service.PedidoService;
import com.ecovivashop.service.ProductoService;
import com.ecovivashop.service.UsuarioService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final EmailService emailService;

    public ClientController(UsuarioService usuarioService, ProductoService productoService, PedidoService pedidoService, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.emailService = emailService;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }
        return "client/home";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model, Authentication authentication,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "12") int size) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }

        // Load products (temporarily without pagination to debug)
        List<Producto> productosList = this.productoService.obtenerProductosActivos();
        Page<Producto> productos = new PageImpl<>(productosList, PageRequest.of(page, size), productosList.size());

        // Load available categories
        List<String> categorias = this.productoService.obtenerCategoriasDisponibles();

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productos.getTotalPages());

        return "client/catalogo-completo";
    }

    @GetMapping("/producto-detalle/{id}")
    public String productoDetalle(@PathVariable Integer id, Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }

        // Obtener el producto por ID
        Producto producto = this.productoService.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/client/catalogo";
        }

        model.addAttribute("producto", producto);
        return "client/producto-detalle";
    }

    @GetMapping("/suscripcion")
    public String suscripcion(Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }
        return "client/suscripcion";
    }

    /**
     * @param model
     * @param authentication
     * @param session
     * @return
     */
    @GetMapping("/carrito")
    public String carrito(Model model, Authentication authentication, HttpSession session) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }

        // Obtener carrito de la sesión
        List<Map<String, Object>> carrito = getCarritoFromSession(session);
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        // Calcular resumen del pedido
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map<String, Object> item : carrito) {
            Object precio = item.get("precio");
            Object cantidad = item.get("cantidad");
            
            BigDecimal precioBD = BigDecimal.ZERO;
            if (precio instanceof BigDecimal bd) {
                precioBD = bd;
            } else if (precio instanceof Double d) {
                precioBD = BigDecimal.valueOf(d);
            } else if (precio instanceof String s) {
                precioBD = new BigDecimal(s);
            }
            
            int cantidadInt = 0;
            if (cantidad instanceof Integer i) {
                cantidadInt = i;
            } else if (cantidad instanceof String s) {
                cantidadInt = Integer.parseInt(s);
            }
            
            subtotal = subtotal.add(precioBD.multiply(BigDecimal.valueOf(cantidadInt)));
        }
        
        // Calcular descuento eco (5%)
        BigDecimal descuento = subtotal.multiply(BigDecimal.valueOf(0.05));
        
        // Envío gratis
        BigDecimal envio = BigDecimal.ZERO;
        
        // Calcular IGV (18%) sobre el subtotal después del descuento
        BigDecimal baseImponible = subtotal.subtract(descuento);
        BigDecimal igv = baseImponible.multiply(BigDecimal.valueOf(0.18));
        
        // Calcular total
        BigDecimal total = subtotal.subtract(descuento).add(envio).add(igv);

        model.addAttribute("carrito", carrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("descuento", descuento);
        model.addAttribute("envio", envio);
        model.addAttribute("igv", igv);
        model.addAttribute("total", total);
        return "client/carrito";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication, HttpSession session) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }

        // Verificar que el carrito no esté vacío
        List<Map<String, Object>> carrito = getCarritoFromSession(session);
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/client/catalogo";
        }

                // Calcular resumen del pedido (reutilizar lógica del carrito)
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map<String, Object> item : carrito) {
            Object precio = item.get("precio");
            Object cantidad = item.get("cantidad");
            
            BigDecimal precioBD = BigDecimal.ZERO;
            if (precio instanceof BigDecimal bd) {
                precioBD = bd;
            } else if (precio instanceof Double d) {
                precioBD = BigDecimal.valueOf(d);
            } else if (precio instanceof String s) {
                precioBD = new BigDecimal(s);
            }
            
            int cantidadInt = 0;
            if (cantidad instanceof Integer i) {
                cantidadInt = i;
            } else if (cantidad instanceof String s) {
                cantidadInt = Integer.parseInt(s);
            }
            
            subtotal = subtotal.add(precioBD.multiply(BigDecimal.valueOf(cantidadInt)));
        }
        
        // Calcular descuento eco (5%)
        BigDecimal descuento = subtotal.multiply(BigDecimal.valueOf(0.05));
        
        // Envío gratis
        BigDecimal envio = BigDecimal.ZERO;
        
        // Calcular IGV (18%) sobre el subtotal después del descuento
        BigDecimal baseImponible = subtotal.subtract(descuento);
        BigDecimal igv = baseImponible.multiply(BigDecimal.valueOf(0.18));
        
        // Calcular total
        BigDecimal total = subtotal.subtract(descuento).add(envio).add(igv);

        model.addAttribute("carrito", carrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("descuento", descuento);
        model.addAttribute("envio", envio);
        model.addAttribute("igv", igv);
        model.addAttribute("total", total);
        return "client/pago";
    }

    @PostMapping("/procesar-pago")
    @ResponseBody
    @SuppressWarnings("UseSpecificCatch")
    public Map<String, Object> procesarPago(@RequestParam("firstName") String firstName,
                              @RequestParam("lastName") String lastName,
                              @RequestParam("email") String email,
                              @RequestParam("phone") String phone,
                              @RequestParam("address") String address,
                              @RequestParam("city") String city,
                              @RequestParam("zipCode") String zipCode,
                              @RequestParam("paymentMethod") String paymentMethod,
                              @RequestParam(value = "acceptTerms", defaultValue = "false") boolean acceptTerms,
                              Authentication authentication, HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validar usuario autenticado
            if (authentication == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            Usuario usuario = getUsuarioFromPrincipal(authentication);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado en la base de datos");
                return response;
            }

            // Validar términos y condiciones
            if (!acceptTerms) {
                response.put("success", false);
                response.put("message", "Debes aceptar los términos y condiciones para continuar.");
                return response;
            }

            // Obtener carrito de la sesión
            List<Map<String, Object>> carrito = getCarritoFromSession(session);
            if (carrito == null || carrito.isEmpty()) {
                response.put("success", false);
                response.put("message", "El carrito está vacío.");
                return response;
            }

            // Calcular totales (reutilizar lógica del checkout)
            BigDecimal subtotal = BigDecimal.ZERO;
            for (Map<String, Object> item : carrito) {
                Object precio = item.get("precio");
                Object cantidad = item.get("cantidad");

                BigDecimal precioBD = BigDecimal.ZERO;
                if (precio instanceof BigDecimal bd) {
                    precioBD = bd;
                } else if (precio instanceof Double d) {
                    precioBD = BigDecimal.valueOf(d);
                } else if (precio instanceof String s) {
                    precioBD = new BigDecimal(s);
                }

                int cantidadInt = 0;
                if (cantidad instanceof Integer i) {
                    cantidadInt = i;
                } else if (cantidad instanceof String s) {
                    cantidadInt = Integer.parseInt(s);
                }

                subtotal = subtotal.add(precioBD.multiply(BigDecimal.valueOf(cantidadInt)));
            }

            // Calcular descuento eco (5%)
            BigDecimal descuento = subtotal.multiply(BigDecimal.valueOf(0.05));

            // Envío gratis
            BigDecimal envio = BigDecimal.ZERO;

            // Calcular IGV (18%) sobre el subtotal después del descuento
            BigDecimal baseImponible = subtotal.subtract(descuento);
            BigDecimal igv = baseImponible.multiply(BigDecimal.valueOf(0.18));

            // Calcular total
            BigDecimal total = subtotal.subtract(descuento).add(envio).add(igv);

            // Construir dirección de envío
            String direccionEnvio = firstName + " " + lastName + ", " + address + ", " + city + ", " + zipCode;

            // Generar número de pedido único
            String numeroPedido = "ECO-" + System.currentTimeMillis();

            // Crear pedido usando PedidoService
            Pedido pedido = this.pedidoService.crearPedidoDesdeCarro(
                usuario, carrito, numeroPedido, paymentMethod, direccionEnvio,
                subtotal, envio, descuento, igv, total
            );

            // Verificar que el pedido se creó correctamente
            if (pedido == null) {
                response.put("success", false);
                response.put("message", "Error al crear el pedido");
                return response;
            }

            // Enviar email de confirmación de pedido
            try {
                String nombreCompleto = firstName + " " + lastName;
                this.emailService.enviarCorreoConfirmacionPedido(email, nombreCompleto, numeroPedido, total.toString());
                System.out.println("✅ Email de confirmación enviado para pedido: " + numeroPedido);
            } catch (Exception e) {
                System.err.println("⚠️ Error enviando email de confirmación para pedido " + numeroPedido + ": " + e.getMessage());
                // No fallar el proceso de pago por error en email
            }

            // Limpiar carrito de la sesión
            session.removeAttribute("carrito");

            // Agregar número de pedido a la sesión para la página de confirmación
            session.setAttribute("ultimoNumeroPedido", numeroPedido);

            response.put("success", true);
            response.put("message", "Pago procesado correctamente");
            response.put("redirectUrl", "/client/confirmacion");

        } catch (IllegalArgumentException | NullPointerException e) {
            response.put("success", false);
            response.put("message", "Datos inválidos en el procesamiento del pago: " + e.getMessage());
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error en el procesamiento del pago: " + e.getMessage());
        } catch (Exception e) { // Catch genérico intencional para errores inesperados en procesamiento complejo
            response.put("success", false);
            response.put("message", "Error interno al procesar el pago: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/pago")
    public String pago(Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }
        return "client/pago";
    }

    @GetMapping("/confirmacion")
    public String confirmacion(Model model, Authentication authentication, HttpSession session) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }

        // Obtener el último número de pedido de la sesión
        String ultimoNumeroPedido = (String) session.getAttribute("ultimoNumeroPedido");
        if (ultimoNumeroPedido != null) {
            // Recargar el pedido con detalles desde la base de datos
            Pedido ultimoPedido = this.pedidoService.findByNumeroPedidoWithDetalles(ultimoNumeroPedido).orElse(null);
            if (ultimoPedido != null) {
                // Pasar datos individuales que el template espera
                model.addAttribute("numeroPedido", ultimoPedido.getNumeroPedido());
                model.addAttribute("fechaPedido", ultimoPedido.getFechaPedido());
                model.addAttribute("metodoPago", ultimoPedido.getMetodoPago());
                model.addAttribute("estado", ultimoPedido.getEstado());

                // Crear lista de productos comprados desde los detalles del pedido
                List<Map<String, Object>> productosComprados = new ArrayList<>();
                for (PedidoDetalle detalle : ultimoPedido.getDetalles()) {
                    Map<String, Object> producto = new HashMap<>();
                    producto.put("nombre", detalle.getProducto().getNombre());
                    producto.put("cantidad", detalle.getCantidad());
                    producto.put("precio", detalle.getPrecioUnitario());
                    producto.put("total", detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                    productosComprados.add(producto);
                }
                model.addAttribute("productosComprados", productosComprados);

                // Pasar los totales calculados
                model.addAttribute("subtotal", ultimoPedido.getSubtotal());
                model.addAttribute("descuento", ultimoPedido.getDescuento());
                model.addAttribute("envio", ultimoPedido.getCostoEnvio());
                model.addAttribute("igv", ultimoPedido.getImpuestos());
                model.addAttribute("total", ultimoPedido.getTotal());

                // También pasar el objeto pedido completo por si se necesita
                model.addAttribute("pedido", ultimoPedido);

                // Limpiar el número de pedido de la sesión después de mostrarlo
                session.removeAttribute("ultimoNumeroPedido");
            }
        }

        return "client/confirmacion";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }
        return "client/perfil";
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model, Authentication authentication) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
        }
        return "client/configuracion";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model, Authentication authentication, HttpSession session) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);

            // Obtener pedidos del usuario
            List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuario.getIdUsuario());
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("totalPedidos", pedidos.size());

            // Calcular métricas adicionales
            BigDecimal totalGastado = BigDecimal.ZERO;
            int pedidosEnviados = 0;
            int pedidosEntregados = 0;

            for (Pedido pedido : pedidos) {
                // Sumar al total gastado
                if (pedido.getTotal() != null) {
                    totalGastado = totalGastado.add(pedido.getTotal());
                }

                // Contar pedidos enviados (en camino)
                if ("ENVIADO".equals(pedido.getEstado())) {
                    pedidosEnviados++;
                }

                // Contar pedidos entregados
                if ("ENTREGADO".equals(pedido.getEstado())) {
                    pedidosEntregados++;
                }
            }

            model.addAttribute("totalGastado", totalGastado);
            model.addAttribute("pedidosEnviados", pedidosEnviados);
            model.addAttribute("pedidosEntregados", pedidosEntregados);
        }
        return "client/pedidos";
    }

    @GetMapping("/pedido-detalle/{numeroPedido}")
    @ResponseBody
    public Map<String, Object> pedidoDetalleAjax(@PathVariable String numeroPedido, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        // Verificar que el usuario esté autenticado
        if (authentication == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado");
            return response;
        }

        Usuario usuario = getUsuarioFromPrincipal(authentication);
        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return response;
        }

        try {
            // Cargar el pedido con detalles
            Pedido pedido = this.pedidoService.findByNumeroPedidoWithDetalles(numeroPedido).orElse(null);
            if (pedido == null) {
                response.put("success", false);
                response.put("message", "Pedido no encontrado");
                return response;
            }

            // Verificar que el pedido pertenece al usuario autenticado
            if (!pedido.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                response.put("success", false);
                response.put("message", "No tienes permisos para ver este pedido");
                return response;
            }

            // Construir respuesta JSON con los detalles del pedido
            response.put("success", true);
            response.put("numeroPedido", pedido.getNumeroPedido());
            response.put("fecha", pedido.getFechaPedido() != null ? 
                pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null);
            response.put("metodoPago", pedido.getMetodoPago());
            response.put("estado", pedido.getEstado());
            response.put("total", pedido.getTotal());

            // Lista de detalles
            List<Map<String, Object>> detalles = new ArrayList<>();
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                Map<String, Object> detalleMap = new HashMap<>();
                detalleMap.put("producto", detalle.getProducto().getNombre());
                detalleMap.put("cantidad", detalle.getCantidad());
                detalleMap.put("precio", detalle.getPrecioUnitario());
                detalleMap.put("subtotal", detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
                detalles.add(detalleMap);
            }
            response.put("detalles", detalles);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/configuracion")
    public String actualizarPerfil(@ModelAttribute("usuario") Usuario usuarioForm,
                                   @RequestParam(value = "fotoPerfilFile", required = false) MultipartFile fotoPerfilFile,
                                   Authentication authentication, Model model) {
        if (authentication != null) {
            Usuario usuario = getUsuarioFromPrincipal(authentication);
            // Actualizar campos editables
            usuario.setNombre(usuarioForm.getNombre());
            usuario.setApellido(usuarioForm.getApellido());
            usuario.setTelefono(usuarioForm.getTelefono());
            usuario.setDireccion(usuarioForm.getDireccion());
            usuario.setDni(usuarioForm.getDni());
            usuario.setFechaNacimiento(usuarioForm.getFechaNacimiento());
            // Guardar foto de perfil si se subió
            if (fotoPerfilFile != null && !fotoPerfilFile.isEmpty()) {
                try {
                    String uploadsDir = "uploads/";
                    Path uploadsPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", uploadsDir);
                    if (!Files.exists(uploadsPath)) {
                        Files.createDirectories(uploadsPath);
                    }
                    String filename = usuario.getIdUsuario() + "_" + fotoPerfilFile.getOriginalFilename();
                    Path filePath = uploadsPath.resolve(filename);
                    fotoPerfilFile.transferTo(filePath);
                    usuario.setFotoPerfil(filename);
                } catch (IOException | IllegalStateException e) {
                    model.addAttribute("error", "Error al subir la foto de perfil: " + e.getMessage());
                }
            }
            this.usuarioService.save(usuario);
            // Recargar usuario actualizado desde la base de datos
            usuario = getUsuarioFromPrincipal(authentication);
            model.addAttribute("usuario", usuario);
            model.addAttribute("mensaje", "Datos actualizados correctamente");
        }
        return "client/configuracion";
    }

    @PostMapping("/agregar-al-carrito")
    @ResponseBody
    public Map<String, Object> agregarAlCarritoLegacy(@RequestParam("productoId") Integer productoId,
                                                     @RequestParam("cantidad") int cantidad,
                                                     HttpSession session) {
        // Reutilizar la lógica del método agregarAlCarrito
        return agregarAlCarrito(productoId, cantidad, session);
    }

    @GetMapping("/carrito/count")
    @ResponseBody
    public int getCartCount(HttpSession session) {
        List<Map<String, Object>> carrito = getCarritoFromSession(session);
        if (carrito == null) {
            return 0;
        }
        return carrito.stream().mapToInt(item -> {
            Object cantidad = item.get("cantidad");
            if (cantidad instanceof Integer i) {
                return i;
            } else if (cantidad instanceof String s) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return 0;
                }
            } else {
                return 0;
            }
        }).sum();
    }

    @PostMapping("/carrito/agregar")
    @ResponseBody
    public Map<String, Object> agregarAlCarrito(@RequestParam("productoId") Integer productoId,
                                               @RequestParam("cantidad") int cantidad,
                                               HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener producto
            Producto producto = productoService.findById(productoId).orElse(null);
            if (producto == null) {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return response;
            }
            
            // Obtener carrito de la sesión
            List<Map<String, Object>> carrito = getCarritoFromSession(session);
            
            // Ensure carrito is not null
            if (carrito == null) {
                carrito = new ArrayList<>();
                session.setAttribute("carrito", carrito);
            }
            
            // Verificar si el producto ya está en el carrito
            boolean productoExistente = false;
            for (Map<String, Object> item : carrito) {
                Object idProducto = item.get("idProducto");
                if (idProducto != null && idProducto.equals(productoId)) {
                    // Actualizar cantidad
                    Object cantidadActual = item.get("cantidad");
                    int cantidadExistente = 0;
                    if (cantidadActual instanceof Integer cantidadInt) {
                        cantidadExistente = cantidadInt;
                    }
                    int nuevaCantidad = cantidad + cantidadExistente;
                    item.put("cantidad", nuevaCantidad);
                    productoExistente = true;
                    break;
                }
            }
            
            // Si no existe, agregar nuevo item
            if (!productoExistente) {
                Map<String, Object> item = new HashMap<>();
                item.put("idProducto", productoId);
                item.put("nombre", producto.getNombre());
                item.put("precio", producto.getPrecio());
                item.put("cantidad", cantidad);
                item.put("imagen", producto.getImagenUrl());
                carrito.add(item);
            }
            
            response.put("success", true);
            response.put("message", "Producto agregado al carrito");
            response.put("totalItems", getCartCount(session));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar producto al carrito: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/actualizar-carrito")
    @ResponseBody
    public Map<String, Object> actualizarCarrito(@RequestParam("productoId") Integer productoId,
                                                @RequestParam("cantidad") int cantidad,
                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener carrito de la sesión
            List<Map<String, Object>> carrito = getCarritoFromSession(session);
            if (carrito == null) {
                response.put("success", false);
                response.put("message", "Carrito no encontrado");
                return response;
            }
            
            // Buscar el producto en el carrito
            boolean productoEncontrado = false;
            for (Map<String, Object> item : carrito) {
                Object idProducto = item.get("idProducto");
                if (idProducto != null && idProducto.equals(productoId)) {
                    if (cantidad <= 0) {
                        // Si cantidad es 0 o menor, eliminar el producto
                        carrito.remove(item);
                    } else {
                        // Actualizar cantidad
                        item.put("cantidad", cantidad);
                    }
                    productoEncontrado = true;
                    break;
                }
            }
            
            if (!productoEncontrado) {
                response.put("success", false);
                response.put("message", "Producto no encontrado en el carrito");
                return response;
            }
            
            response.put("success", true);
            response.put("message", "Carrito actualizado correctamente");
            response.put("totalItems", getCartCount(session));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar el carrito: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/remover-del-carrito")
    @ResponseBody
    public Map<String, Object> removerDelCarrito(@RequestParam("productoId") Integer productoId,
                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Obtener carrito de la sesión
            List<Map<String, Object>> carrito = getCarritoFromSession(session);
            if (carrito == null) {
                response.put("success", false);
                response.put("message", "Carrito no encontrado");
                return response;
            }
            
            // Buscar y remover el producto del carrito
            boolean productoRemovido = carrito.removeIf(item -> {
                Object idProducto = item.get("idProducto");
                return idProducto != null && idProducto.equals(productoId);
            });
            
            if (!productoRemovido) {
                response.put("success", false);
                response.put("message", "Producto no encontrado en el carrito");
                return response;
            }
            
            response.put("success", true);
            response.put("message", "Producto eliminado del carrito");
            response.put("totalItems", getCartCount(session));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar producto del carrito: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/descargar-boleta")
    public void descargarBoleta(@RequestParam String numeroPedido, HttpServletResponse response) throws IOException, DocumentException {
        System.out.println("🔍 Buscando pedido con numeroPedido: " + numeroPedido);

        // Buscar el pedido por número
        Pedido pedido = pedidoService.findByNumeroPedidoWithDetalles(numeroPedido).orElse(null);
        if (pedido == null) {
            System.out.println("❌ Pedido no encontrado para numeroPedido: " + numeroPedido);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Pedido no encontrado");
            return;
        }

        System.out.println("✅ Pedido encontrado: ID=" + pedido.getIdPedido() + ", Usuario=" + pedido.getUsuario().getEmail());

        // Configurar respuesta para descarga de PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=boleta_" + numeroPedido + ".pdf");

        // Crear documento PDF
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Fuentes
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Título
        Paragraph title = new Paragraph("BOLETA DE VENTA", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Información de la empresa
        Paragraph empresaInfo = new Paragraph("EcoVivaShop - Tienda Ecológica", headerFont);
        empresaInfo.setAlignment(Element.ALIGN_CENTER);
        empresaInfo.setSpacingAfter(10);
        document.add(empresaInfo);

        Paragraph empresaDetalle = new Paragraph("RUC: 12345678901\nDirección: Av. Ecológica 123, Lima, Perú\nTeléfono: (01) 123-4567", normalFont);
        empresaDetalle.setAlignment(Element.ALIGN_CENTER);
        empresaDetalle.setSpacingAfter(20);
        document.add(empresaDetalle);

        // Número de pedido y fecha
        Paragraph pedidoInfo = new Paragraph("Número de Pedido: " + numeroPedido + "\nFecha: " + pedido.getFechaPedido().toString(), normalFont);
        pedidoInfo.setSpacingAfter(20);
        document.add(pedidoInfo);

        // Información del cliente
        Paragraph clienteTitle = new Paragraph("DATOS DEL CLIENTE", headerFont);
        clienteTitle.setSpacingAfter(10);
        document.add(clienteTitle);

        Usuario cliente = pedido.getUsuario();
        Paragraph clienteInfo = new Paragraph(
            "Nombre: " + cliente.getNombre() + " " + cliente.getApellido() + "\n" +
            "Email: " + cliente.getEmail() + "\n" +
            "Dirección: " + (cliente.getDireccion() != null ? cliente.getDireccion() : "No especificada"), normalFont);
        clienteInfo.setSpacingAfter(20);
        document.add(clienteInfo);

        // Detalles del pedido
        Paragraph productosTitle = new Paragraph("DETALLE DE PRODUCTOS", headerFont);
        productosTitle.setSpacingAfter(10);
        document.add(productosTitle);

        // Crear tabla de productos
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 2, 2, 2});

        // Encabezados de tabla
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("Producto", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Cantidad", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Precio Unit.", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Subtotal", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Productos
        BigDecimal total = BigDecimal.ZERO;
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            table.addCell(new Phrase(detalle.getProducto().getNombre(), normalFont));
            table.addCell(new Phrase(String.valueOf(detalle.getCantidad()), normalFont));
            table.addCell(new Phrase("S/ " + detalle.getPrecioUnitario().toString(), normalFont));
            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            table.addCell(new Phrase("S/ " + subtotal.toString(), normalFont));
            total = total.add(subtotal);
        }

        document.add(table);

        // Cálculos finales
        Paragraph totalesTitle = new Paragraph("\nRESUMEN DE COMPRA", headerFont);
        totalesTitle.setSpacingAfter(10);
        document.add(totalesTitle);

        BigDecimal descuento = total.multiply(BigDecimal.valueOf(0.05)); // 5% descuento
        BigDecimal subtotalConDescuento = total.subtract(descuento);
        BigDecimal igv = subtotalConDescuento.multiply(BigDecimal.valueOf(0.18)); // 18% IGV
        BigDecimal totalFinal = subtotalConDescuento.add(igv);

        Paragraph resumen = new Paragraph(
            "Subtotal: S/ " + total.toString() + "\n" +
            "Descuento (5%): S/ " + descuento.toString() + "\n" +
            "Subtotal con descuento: S/ " + subtotalConDescuento.toString() + "\n" +
            "IGV (18%): S/ " + igv.toString() + "\n" +
            "TOTAL A PAGAR: S/ " + totalFinal.toString(), normalFont);
        resumen.setSpacingAfter(20);
        document.add(resumen);

        // Mensaje de agradecimiento
        Paragraph gracias = new Paragraph("¡Gracias por tu compra en EcoVivaShop!\nContribuyendo a un planeta más sostenible.", normalFont);
        gracias.setAlignment(Element.ALIGN_CENTER);
        gracias.setSpacingAfter(10);
        document.add(gracias);

        // Fecha de emisión
        Paragraph fechaEmision = new Paragraph("Fecha de emisión: " + java.time.LocalDate.now().toString(), smallFont);
        fechaEmision.setAlignment(Element.ALIGN_CENTER);
        document.add(fechaEmision);

        document.close();
    }

    /**
     * Helper method to safely get the shopping cart from session
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getCarritoFromSession(HttpSession session) {
        // Ensure session is created
        session.setAttribute("session_test", "test");
        
        Object carritoObj = session.getAttribute("carrito");
        
        if (carritoObj instanceof List<?> carritoList) {
            // Pattern matching instanceof ensures type safety, no need for try-catch
            return (List<Map<String, Object>>) carritoList;
        }
        List<Map<String, Object>> newCarrito = new ArrayList<>();
        session.setAttribute("carrito", newCarrito);
        return newCarrito;
    }

    /**
     * Helper method to get Usuario from Authentication, handling both regular and OAuth2 users
     */
    private Usuario getUsuarioFromPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomOAuth2User oauthUser) {
            Integer userId = oauthUser.getUserId();
            return this.usuarioService.findById(userId).orElse(null);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            return this.usuarioService.findByEmail(email);
        } else if (principal instanceof String email) {
            return this.usuarioService.findByEmail(email);
        } else {
            return null;
        }
    }
}
