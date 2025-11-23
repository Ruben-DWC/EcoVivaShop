package com.ecovivashop.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpHeaders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;
import com.ecovivashop.service.ExportService;
import com.ecovivashop.service.ProductoBulkService;
import com.ecovivashop.service.ProductoService;
import com.ecovivashop.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(value = ProductoController.class, excludeAutoConfiguration = {ThymeleafAutoConfiguration.class})
@SuppressWarnings({"unused", "removal"})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @MockBean
    private ProductoService productoService;

    @SuppressWarnings("unused")
    @MockBean
    private ProductoBulkService productoBulkService;

    @SuppressWarnings("unused")
    @MockBean
    private ExportService exportService;

    @SuppressWarnings("unused")
    @MockBean
    private com.ecovivashop.service.JasperExportService jasperExportService;

    @SuppressWarnings("unused")
    @MockBean
    private UsuarioService usuarioService;

    @SuppressWarnings("unused")
    @MockBean
    private UsuarioRepository usuarioRepository;

    @SuppressWarnings("unused")
    @MockBean
    private RolRepository rolRepository;

    @SuppressWarnings("unused")
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListarProductos() throws Exception {
        // Given
        List<Producto> productos = Arrays.asList(
            crearProducto(1, "Producto 1", new BigDecimal("10.00")),
            crearProducto(2, "Producto 2", new BigDecimal("20.00"))
        );
        Page<Producto> page = new PageImpl<>(productos, PageRequest.of(0, 10), 2);
        when(productoService.obtenerProductosPaginados(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/productos/gestion"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"));

        verify(productoService).obtenerProductosPaginados(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testMostrarFormularioCrearProducto() throws Exception {
        // Given
        when(productoService.obtenerCategoriasDisponibles()).thenReturn(Arrays.asList("Categoria1", "Categoria2"));

        // When & Then
        mockMvc.perform(get("/admin/productos/agregar"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/productos/formulario"))
                .andExpect(model().attributeExists("producto"))
                .andExpect(model().attributeExists("categorias"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testBuscarProductos() throws Exception {
        // Given
        List<Producto> productos = Arrays.asList(
            crearProducto(1, "Producto Test", new BigDecimal("15.00"))
        );
        Page<Producto> page = new PageImpl<>(productos, PageRequest.of(0, 10), 1);
        when(productoService.buscarProductosPaginado(org.mockito.ArgumentMatchers.eq("test"), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/admin/productos").param("busqueda", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/productos/gestion"))
                .andExpect(model().attributeExists("productos"));

        verify(productoService).buscarProductosPaginado(org.mockito.ArgumentMatchers.eq("test"), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testExportarProductosJasperPDF() throws Exception {
        // Given
        List<Producto> productos = Arrays.asList(
            crearProducto(1, "Producto 1", new BigDecimal("10.00"))
        );
        when(productoService.findAll()).thenReturn(productos);
        when(jasperExportService.exportarProductosJasperPDF(productos)).thenReturn(new byte[]{1,2,3});

        // When & Then
        mockMvc.perform(get("/admin/productos/exportar/jasper/pdf"))
            .andExpect(status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType("application/pdf"))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_productos_jasper.pdf\""));
    }

    // Método helper para crear productos de prueba
    private Producto crearProducto(Integer id, String nombre, BigDecimal precio) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setEstado(true);
        return producto;
    }
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ViewResolver viewResolver() {
            return (viewName, locale) -> new View() {
                @Override
                public String getContentType() {
                    return "text/html";
                }

                @Override
                public void render(java.util.Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
                        throws Exception {
                    // No-op: avoid rendering Thymeleaf templates during unit tests to speed up and prevent template-related errors
                }
            };
        }
    }
}