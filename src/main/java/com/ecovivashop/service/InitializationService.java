package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.InventarioRepository;
import com.ecovivashop.repository.ProductoRepository;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;

@Service
@Transactional
public class InitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(InitializationService.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor manual
    public InitializationService(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                               ProductoRepository productoRepository, InventarioRepository inventarioRepository,
                               PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        this.initializeData();
    }

    public void initializeData() {
        try {
            System.out.println("🚀 Iniciando configuración de datos por defecto...");
            
            // 1. Crear roles por defecto
            this.crearRolesPorDefecto();
            
            // 2. Crear usuario administrador por defecto
            this.crearUsuarioAdminPorDefecto();
              // 3. Crear productos de muestra
            this.crearProductosDeMuestra();
            
            System.out.println("✅ Configuración de datos completada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error durante la inicialización: " + e.getMessage());
            InitializationService.logger.error("Error durante la inicialización", e);
        }
    }

    private void crearRolesPorDefecto() {
        System.out.println("📋 Creando roles por defecto...");
        
        if (this.rolRepository.count() == 0) {
            // Super Administrador
            Rol superAdmin = new Rol();
            superAdmin.setNombre("ROLE_SUPER_ADMIN");
            superAdmin.setDescripcion("Super Administrador con acceso completo al sistema");
            superAdmin.setEstado(true);
            superAdmin.setFechaCreacion(LocalDateTime.now());
            this.rolRepository.save(superAdmin);

            // Administrador
            Rol admin = new Rol();
            admin.setNombre("ROLE_ADMIN");
            admin.setDescripcion("Administrador con permisos de gestión");
            admin.setEstado(true);
            admin.setFechaCreacion(LocalDateTime.now());
            this.rolRepository.save(admin);

            // Cliente
            Rol cliente = new Rol();
            cliente.setNombre("ROLE_CLIENTE");
            cliente.setDescripcion("Cliente con acceso a compras y gestión de cuenta");
            cliente.setEstado(true);
            cliente.setFechaCreacion(LocalDateTime.now());
            this.rolRepository.save(cliente);

            // Vendedor
            Rol vendedor = new Rol();
            vendedor.setNombre("ROLE_VENDEDOR");
            vendedor.setDescripcion("Vendedor con acceso a gestión de productos y pedidos");
            vendedor.setEstado(true);
            vendedor.setFechaCreacion(LocalDateTime.now());
            this.rolRepository.save(vendedor);

            System.out.println("✅ Roles creados: SUPER_ADMIN, ADMIN, CLIENTE, VENDEDOR");
        } else {
            System.out.println("ℹ️ Los roles ya existen, omitiendo creación");
        }
    }

    private void crearUsuarioAdminPorDefecto() {
        System.out.println("👤 Creando usuario administrador por defecto...");
          if (!this.usuarioRepository.existsByEmail("admin@ecovivashop.com")) {
            Rol rolAdmin = this.rolRepository.findByNombre("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));

            Usuario admin = new Usuario();            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@ecovivashop.com");
            admin.setPassword(this.passwordEncoder.encode("admin123"));
            admin.setTelefono("+51 987 654 321");
            admin.setDireccion("Sede Principal EcoVivaShop, Lima - Perú");
            admin.setRol(rolAdmin);
            admin.setEstado(true);
            admin.setFechaRegistro(LocalDateTime.now());
            
            this.usuarioRepository.save(admin);
            
            System.out.println("✅ Usuario administrador creado:");
            System.out.println("   📧 Email: admin@ecovivashop.com");
            System.out.println("   🔑 Password: admin123");
        } else {
            System.out.println("ℹ️ El usuario administrador ya existe, omitiendo creación");
        }
          // Crear usuario cliente de prueba
        if (!this.usuarioRepository.existsByEmail("cliente@test.com")) {
            Rol rolCliente = this.rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));

            Usuario cliente = new Usuario();
            cliente.setNombre("Juan Carlos");
            cliente.setApellido("Pérez García");            cliente.setEmail("cliente@test.com");
            cliente.setPassword(this.passwordEncoder.encode("cliente123"));
            cliente.setTelefono("+51 999 888 777");
            cliente.setDireccion("Av. Javier Prado 456, San Isidro - Lima");
            cliente.setRol(rolCliente);
            cliente.setEstado(true);
            cliente.setFechaRegistro(LocalDateTime.now());
            
            this.usuarioRepository.save(cliente);
            
            System.out.println("✅ Usuario cliente de prueba creado:");
            System.out.println("   📧 Email: cliente@test.com");
            System.out.println("   🔑 Password: cliente123");
        }
    }

    private void crearProductosDeMuestra() {
        System.out.println("🛍️ Creando productos de muestra...");
        
        if (this.productoRepository.count() == 0) {
            
            // Producto 1: Refrigerador Eco
            Producto refrigerador = new Producto();
            refrigerador.setNombre("Refrigerador EcoMax 300L");
            refrigerador.setDescripcion("Refrigerador de alta eficiencia energética con tecnología inverter y compartimentos organizadores.");
            refrigerador.setPrecio(new BigDecimal("1899000"));
            refrigerador.setCategoria("Electrodomésticos");
            refrigerador.setMarca("EcoMax");
            refrigerador.setModelo("ECO-300");
            refrigerador.setColor("Acero Inoxidable");
            refrigerador.setPeso(new BigDecimal("65.5"));
            refrigerador.setDimensiones("180cm x 60cm x 65cm");
            refrigerador.setMaterial("Acero inoxidable y plástico ABS");
            refrigerador.setGarantiaMeses(24);
            refrigerador.setEficienciaEnergetica("A+++");
            refrigerador.setImpactoAmbiental("Bajo");
            refrigerador.setPuntuacionEco(new BigDecimal("9.2"));
            refrigerador.setImagenUrl("/img/productos/refrigerador-eco.jpg");
            refrigerador.setEstado(true);
            refrigerador.setFechaCreacion(LocalDateTime.now());
            
            Producto refrigeradorGuardado = this.productoRepository.save(refrigerador);
            this.crearInventarioParaProducto(refrigeradorGuardado, 15, 3, 50, "Almacén A-1");

            // Producto 2: Lavadora Sostenible
            Producto lavadora = new Producto();
            lavadora.setNombre("Lavadora Sostenible 12Kg");
            lavadora.setDescripcion("Lavadora con tecnología de ahorro de agua y energía, ideal para familias eco-conscientes.");
            lavadora.setPrecio(new BigDecimal("1299000"));
            lavadora.setCategoria("Electrodomésticos");
            lavadora.setMarca("GreenTech");
            lavadora.setModelo("GT-12ECO");
            lavadora.setColor("Blanco");
            lavadora.setPeso(new BigDecimal("55.0"));
            lavadora.setDimensiones("85cm x 60cm x 60cm");
            lavadora.setMaterial("Acero esmaltado");
            lavadora.setGarantiaMeses(18);
            lavadora.setEficienciaEnergetica("A++");
            lavadora.setImpactoAmbiental("Medio");
            lavadora.setPuntuacionEco(new BigDecimal("8.5"));
            lavadora.setImagenUrl("/img/productos/lavadora-sostenible.jpg");
            lavadora.setEstado(true);
            lavadora.setFechaCreacion(LocalDateTime.now());
            
            Producto lavadoraGuardada = this.productoRepository.save(lavadora);
            this.crearInventarioParaProducto(lavadoraGuardada, 8, 2, 25, "Almacén A-2");

            // Producto 3: Panel Solar Portátil
            Producto panelSolar = new Producto();
            panelSolar.setNombre("Panel Solar Portátil 100W");
            panelSolar.setDescripcion("Panel solar plegable perfecto para camping y emergencias, con alta eficiencia de conversión.");
            panelSolar.setPrecio(new BigDecimal("459000"));
            panelSolar.setCategoria("Energía Renovable");
            panelSolar.setMarca("SolarMax");
            panelSolar.setModelo("SM-100P");
            panelSolar.setColor("Negro");
            panelSolar.setPeso(new BigDecimal("4.2"));
            panelSolar.setDimensiones("120cm x 60cm x 3cm");
            panelSolar.setMaterial("Silicio monocristalino");
            panelSolar.setGarantiaMeses(60);
            panelSolar.setEficienciaEnergetica("A+++");
            panelSolar.setImpactoAmbiental("Muy Bajo");
            panelSolar.setPuntuacionEco(new BigDecimal("9.8"));
            panelSolar.setImagenUrl("/img/productos/panel-solar.jpg");
            panelSolar.setEstado(true);
            panelSolar.setFechaCreacion(LocalDateTime.now());
            
            Producto panelGuardado = this.productoRepository.save(panelSolar);
            this.crearInventarioParaProducto(panelGuardado, 25, 5, 100, "Almacén B-1");

            // Producto 4: Termo Biodegradable
            Producto termo = new Producto();
            termo.setNombre("Termo Biodegradable 500ml");
            termo.setDescripcion("Termo fabricado con materiales 100% biodegradables, perfecto para bebidas calientes y frías.");
            termo.setPrecio(new BigDecimal("89000"));
            termo.setCategoria("Accesorios Eco");
            termo.setMarca("EcoLife");
            termo.setModelo("EL-TERMO-500");
            termo.setColor("Verde Natural");
            termo.setPeso(new BigDecimal("0.3"));
            termo.setDimensiones("20cm x 8cm x 8cm");
            termo.setMaterial("Fibra de bambú y bioplástico");
            termo.setGarantiaMeses(12);
            termo.setEficienciaEnergetica("N/A");
            termo.setImpactoAmbiental("Muy Bajo");
            termo.setPuntuacionEco(new BigDecimal("9.5"));
            termo.setImagenUrl("/img/productos/termo-biodegradable.jpg");
            termo.setEstado(true);
            termo.setFechaCreacion(LocalDateTime.now());
            
            Producto termoGuardado = this.productoRepository.save(termo);
            this.crearInventarioParaProducto(termoGuardado, 100, 20, 500, "Almacén C-1");

            // Producto 5: Purificador de Agua
            Producto purificador = new Producto();
            purificador.setNombre("Purificador de Agua UV");
            purificador.setDescripcion("Sistema de purificación por luz ultravioleta, elimina 99.9% de bacterias y virus sin químicos.");
            purificador.setPrecio(new BigDecimal("599000"));
            purificador.setCategoria("Hogar Saludable");
            purificador.setMarca("AquaPure");
            purificador.setModelo("AP-UV-2000");
            purificador.setColor("Azul Transparente");
            purificador.setPeso(new BigDecimal("2.1"));
            purificador.setDimensiones("35cm x 15cm x 15cm");
            purificador.setMaterial("Plástico libre de BPA");
            purificador.setGarantiaMeses(36);
            purificador.setEficienciaEnergetica("A+");
            purificador.setImpactoAmbiental("Bajo");
            purificador.setPuntuacionEco(new BigDecimal("8.8"));
            purificador.setImagenUrl("/img/productos/purificador-agua.jpg");
            purificador.setEstado(true);
            purificador.setFechaCreacion(LocalDateTime.now());
            
            Producto purificadorGuardado = this.productoRepository.save(purificador);
            this.crearInventarioParaProducto(purificadorGuardado, 30, 5, 100, "Almacén B-2");

            System.out.println("✅ Productos de muestra creados:");
            System.out.println("   🔹 Refrigerador EcoMax 300L - $1,899,000");
            System.out.println("   🔹 Lavadora Sostenible 12Kg - $1,299,000");
            System.out.println("   🔹 Panel Solar Portátil 100W - $459,000");
            System.out.println("   🔹 Termo Biodegradable 500ml - $89,000");
            System.out.println("   🔹 Purificador de Agua UV - $599,000");
            
        } else {
            System.out.println("ℹ️ Los productos ya existen, omitiendo creación");
        }
    }

    private void crearInventarioParaProducto(Producto producto, int stock, int stockMinimo, int stockMaximo, String ubicacion) {
        if (this.inventarioRepository.findByProducto(producto).isEmpty()) {
            Inventario inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setStock(stock);
            inventario.setStockMinimo(stockMinimo);
            inventario.setStockMaximo(stockMaximo);
            inventario.setUbicacion(ubicacion);
            inventario.setEstado(true);
            inventario.setFechaActualizacion(LocalDateTime.now());
            inventario.setUsuarioActualizacion("SISTEMA");
            
            this.inventarioRepository.save(inventario);
        }
    }
}
