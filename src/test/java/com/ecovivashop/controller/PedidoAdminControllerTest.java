package com.ecovivashop.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.ExportService;
import com.ecovivashop.service.JasperExportService;
import com.ecovivashop.service.PedidoService;

@WebMvcTest(value = PedidoAdminController.class, excludeAutoConfiguration = {ThymeleafAutoConfiguration.class})
@SuppressWarnings({"removal","unused"})
class PedidoAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @SuppressWarnings("unused")
    @MockBean
    private ExportService exportService;

    @MockBean
    private JasperExportService jasperExportService;
    
    @SuppressWarnings("unused")
    @MockBean
    private com.ecovivashop.service.UsuarioService usuarioService;

    @SuppressWarnings("unused")
    @MockBean
    private com.ecovivashop.repository.RolRepository rolRepository;

    @SuppressWarnings("unused")
    @MockBean
    private com.ecovivashop.repository.UsuarioRepository usuarioRepository;

    @SuppressWarnings("unused")
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    void testImprimirPedidoJasperPDF() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setNumeroPedido("ORD-12345");
        pedido.setTotal(new BigDecimal("100.00"));
        pedido.setFechaPedido(LocalDateTime.now());
        Usuario u = new Usuario();
        u.setNombre("Juan");
        u.setApellido("Perez");
        pedido.setUsuario(u);

        when(pedidoService.findById(1)).thenReturn(Optional.of(pedido));
        when(jasperExportService.exportarPedidoJasperPDF(pedido)).thenReturn(new byte[]{1,2,3});

        mockMvc.perform(get("/admin/pedidos/exportar/jasper/1/pdf"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pedido_jasper_ORD-12345_" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf\""));
    }
}
