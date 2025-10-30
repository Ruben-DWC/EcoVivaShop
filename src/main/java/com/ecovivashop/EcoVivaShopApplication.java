package com.ecovivashop;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecovivashop.config.AppProperties;
import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.RolRepository;
import com.ecovivashop.repository.UsuarioRepository;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class EcoVivaShopApplication {

	public static void main(String[] args) {
		// Verificar si se debe generar plantilla Excel
		if (args.length > 0 && "generate-excel".equals(args[0])) {
			generateExcelTemplate();
			return;
		}
		SpringApplication.run(EcoVivaShopApplication.class, args);
	}

	// Método para generar plantilla Excel
        @SuppressWarnings("CallToPrintStackTrace")
	public static void generateExcelTemplate() {
		System.out.println("Generando plantilla Excel para productos...");

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Productos");

			// Headers del servicio ProductoBulkService
			String[] headers = {
				"nombre", "descripcion", "precio", "categoria", "marca", "modelo",
				"color", "peso", "dimensiones", "material", "garantia_meses",
				"eficiencia_energetica", "impacto_ambiental", "puntuacion_eco",
				"imagen_url", "stock_inicial", "stock_minimo", "stock_maximo"
			};

			// Crear fila de encabezados
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}

			// Crear fila de ejemplo
			Row ejemploRow = sheet.createRow(1);
			String[] ejemplo = {
				"Laptop Eco Friendly",
				"Laptop ecológica con bajo consumo energético",
				"2500.00",
				"Electrónicos",
				"EcoTech",
				"ET-2024",
				"Gris",
				"1.5",
				"35x25x2 cm",
				"Aluminio reciclado",
				"24",
				"A+++",
				"Bajo",
				"8.5",
				"https://ejemplo.com/imagen.jpg",
				"50",
				"5",
				"100"
			};

			for (int i = 0; i < ejemplo.length; i++) {
				ejemploRow.createCell(i).setCellValue(ejemplo[i]);
			}

			// Ajustar ancho de columnas
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			// Guardar archivo
			try (FileOutputStream fos = new FileOutputStream("productos_plantilla.xlsx")) {
				workbook.write(fos);
			}

			System.out.println("✅ Plantilla Excel generada exitosamente: productos_plantilla.xlsx");
			System.out.println("📊 Columnas incluidas: " + headers.length);

		} catch (Exception e) {
			System.err.println("❌ Error al generar plantilla: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Bean
	@SuppressWarnings("unused")
	CommandLineRunner initData(RolRepository rolRepository, UsuarioRepository usuarioRepository, 
							   PasswordEncoder passwordEncoder) {
		return args -> {
			// Crear roles si no existen
			if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
				Rol rolAdmin = new Rol();
				rolAdmin.setNombre("ROLE_ADMIN");
				rolAdmin.setDescripcion("Administrador del sistema");
				rolAdmin.setEstado(true);
				rolAdmin.setFechaCreacion(java.time.LocalDateTime.now());
				rolRepository.save(rolAdmin);
				System.out.println("Rol ROLE_ADMIN creado");
			}
			
			if (rolRepository.findByNombre("ROLE_CLIENTE").isEmpty()) {
				Rol rolCliente = new Rol();
				rolCliente.setNombre("ROLE_CLIENTE");
				rolCliente.setDescripcion("Cliente del sistema");
				rolCliente.setEstado(true);
				rolCliente.setFechaCreacion(java.time.LocalDateTime.now());
				rolRepository.save(rolCliente);
				System.out.println("Rol ROLE_CLIENTE creado");
			}
			
			// Crear usuario de prueba si no existe
			if (usuarioRepository.findByEmailIgnoreCase("test@test.com").isEmpty()) {
				Usuario usuario = new Usuario();
				usuario.setNombre("Usuario");
				usuario.setApellido("de Prueba");
				usuario.setEmail("test@test.com");
				usuario.setPassword(passwordEncoder.encode("123456"));
				usuario.setTelefono("123456789");
				usuario.setEstado(true);
				usuario.setRol(rolRepository.findByNombre("ROLE_CLIENTE").orElse(null));
				usuario.setFechaRegistro(java.time.LocalDateTime.now());
				usuarioRepository.save(usuario);
				System.out.println("Usuario de prueba creado: test@test.com / 123456");
			}
		};
	}
}
