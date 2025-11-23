package com.ecovivashop.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.InventarioHistorial;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.InventarioHistorialRepository;
import com.ecovivashop.service.InventarioService;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    @Mock
    private InventarioService inventarioService;

    @Mock
    private InventarioHistorialRepository inventarioHistorialRepository;

    @InjectMocks
    private InventarioController inventarioController;

    private MockMvc mockMvc;
    private Inventario inventario;
    private Producto producto;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();

        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Producto Test");

        inventario = new Inventario();
        inventario.setIdInventario(1);
        inventario.setProducto(producto);
        inventario.setStock(10);
    }

    @Test
    void testListarInventario() throws Exception {
        List<Inventario> inventariosList = Arrays.asList(inventario);
        Page<Inventario> inventariosPage = new PageImpl<>(inventariosList, PageRequest.of(0, 15), inventariosList.size());
        when(inventarioService.obtenerInventariosActivos(any(Pageable.class))).thenReturn(inventariosPage);

        mockMvc.perform(get("/admin/inventario"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inventario/gestion"))
                .andExpect(model().attributeExists("inventarios"));

        verify(inventarioService, times(1)).obtenerInventariosActivos(any(Pageable.class));
    }

    @Test
    void testAjustarStock() throws Exception {
        when(inventarioService.findByProductoId(1)).thenReturn(Optional.of(inventario));
        doNothing().when(inventarioService).ajustarStock(eq(1), eq(-5), eq("test"), eq("SISTEMA"));
        when(inventarioHistorialRepository.save(any(InventarioHistorial.class))).thenReturn(null);

        mockMvc.perform(post("/admin/inventario/ajustar-stock")
                        .param("idProducto", "1")
                        .param("cantidadAjuste", "-5")
                        .param("motivo", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/inventario"));

        verify(inventarioService, times(1)).ajustarStock(1, -5, "test", "SISTEMA");
    }

    @Test
    void testAumentarStockApi() throws Exception {
        when(inventarioService.findByProductoId(1)).thenReturn(Optional.of(inventario));
        doNothing().when(inventarioService).ajustarStock(eq(1), eq(10), eq("Aumento manual de stock"), eq("SISTEMA"));
        when(inventarioHistorialRepository.save(any(InventarioHistorial.class))).thenReturn(null);

        mockMvc.perform(post("/admin/inventario/api/aumentar-stock")
                        .param("idProducto", "1")
                        .param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(inventarioService, times(1)).ajustarStock(1, 10, "Aumento manual de stock", "SISTEMA");
    }

    @Test
    void testReducirStockApi() throws Exception {
        when(inventarioService.findByProductoId(1)).thenReturn(Optional.of(inventario));
        doNothing().when(inventarioService).ajustarStock(eq(1), eq(-3), eq("Disminución manual de stock"), eq("SISTEMA"));
        when(inventarioHistorialRepository.save(any(InventarioHistorial.class))).thenReturn(null);

        mockMvc.perform(post("/admin/inventario/api/disminuir-stock")
                        .param("idProducto", "1")
                        .param("cantidad", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(inventarioService, times(1)).ajustarStock(1, -3, "Disminución manual de stock", "SISTEMA");
    }
}