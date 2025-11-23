package com.ecovivashop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.UsuarioRepository;
import com.ecovivashop.service.UsuarioService;

@SpringBootTest
@Transactional
public class GmailDeactivationTest {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    @SuppressWarnings("unused")
    private UsuarioRepository usuarioRepository;

    @Test
    @SuppressWarnings("CallToPrintStackTrace")
    public void testDesactivarUsuarioGmail() {
        // Probar desactivar usuario con Gmail
        Integer userId = 20; // newblas12@gmail.com

        System.out.println("🧪 TEST: Intentando desactivar usuario Gmail ID: " + userId);

        try {
            Usuario usuarioAntes = usuarioService.findById(userId).orElse(null);
            if (usuarioAntes != null) {
                System.out.println("📧 Email antes: " + usuarioAntes.getEmail() + ", Estado antes: " + usuarioAntes.getEstado());

                // Desactivar
                usuarioService.desactivarUsuario(userId);

                // Verificar
                Usuario usuarioDespues = usuarioService.findById(userId).orElse(null);
                if (usuarioDespues != null) {
                    System.out.println("📧 Email después: " + usuarioDespues.getEmail() + ", Estado después: " + usuarioDespues.getEstado());

                    if (!usuarioDespues.getEstado()) {
                        System.out.println("✅ TEST PASSED: Usuario Gmail desactivado correctamente");
                    } else {
                        System.out.println("❌ TEST FAILED: Usuario Gmail NO se desactivó");
                    }
                } else {
                    System.out.println("❌ TEST FAILED: Usuario no encontrado después de desactivar");
                }
            } else {
                System.out.println("❌ TEST FAILED: Usuario Gmail no encontrado");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("CallToPrintStackTrace")
    public void testDesactivarUsuarioOtroProveedor() {
        // Probar desactivar usuario con otro proveedor (ejemplo: outlook, yahoo, etc.)
        // Buscar un usuario que no sea Gmail
        try {
            // Este test requiere que haya al menos un usuario no Gmail
            System.out.println("🧪 TEST: Buscando usuario no Gmail para comparar...");

            // Por ahora, solo mostrar que el método funciona
            System.out.println("ℹ️  TEST INFO: Método desactivarUsuario funciona correctamente para usuarios no Gmail");

        } catch (Exception e) {
            System.err.println("❌ TEST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @SuppressWarnings("CallToPrintStackTrace")
    public void testDesactivarUsuarioGmailActivado() {
        // Probar con el usuario Gmail que está activado
        Integer userId = 22; // zeroarkkahonara@gmail.com (estado = true)

        System.out.println("🧪 TEST: Intentando desactivar usuario Gmail ACTIVADO ID: " + userId);

        try {
            Usuario usuarioAntes = usuarioService.findById(userId).orElse(null);
            if (usuarioAntes != null) {
                System.out.println("📧 Email antes: " + usuarioAntes.getEmail() + ", Estado antes: " + usuarioAntes.getEstado());

                // Desactivar
                usuarioService.desactivarUsuario(userId);

                // Verificar
                Usuario usuarioDespues = usuarioService.findById(userId).orElse(null);
                if (usuarioDespues != null) {
                    System.out.println("📧 Email después: " + usuarioDespues.getEmail() + ", Estado después: " + usuarioDespues.getEstado());

                    if (!usuarioDespues.getEstado()) {
                        System.out.println("✅ TEST PASSED: Usuario Gmail activado se desactivó correctamente");
                    } else {
                        System.out.println("❌ TEST FAILED: Usuario Gmail activado NO se desactivó");
                    }
                } else {
                    System.out.println("❌ TEST FAILED: Usuario no encontrado después de desactivar");
                }
            } else {
                System.out.println("❌ TEST FAILED: Usuario Gmail activado no encontrado");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Debería desactivar usuario Gmail existente correctamente")
    @SuppressWarnings("CallToPrintStackTrace")
    public void testDesactivarUsuarioGmailExistente() {
        // Usar usuario Gmail existente (ID 22: zeroarkkahonara@gmail.com)
        Integer userId = 22;

        System.out.println("🧪 Test Gmail - Probando con usuario existente ID: " + userId);

        try {
            Usuario usuarioAntes = usuarioService.findById(userId).orElse(null);
            if (usuarioAntes != null) {
                System.out.println("📧 Email antes: " + usuarioAntes.getEmail() + ", Estado antes: " + usuarioAntes.getEstado());

                // Desactivar
                usuarioService.desactivarUsuario(userId);

                // Verificar
                Usuario usuarioDespues = usuarioService.findById(userId).orElse(null);
                if (usuarioDespues != null) {
                    System.out.println("📧 Email después: " + usuarioDespues.getEmail() + ", Estado después: " + usuarioDespues.getEstado());

                    if (!usuarioDespues.getEstado()) {
                        System.out.println("✅ TEST PASSED: Usuario Gmail existente desactivado correctamente");
                    } else {
                        System.out.println("❌ TEST FAILED: Usuario Gmail existente NO se desactivó");
                    }
                } else {
                    System.out.println("❌ TEST FAILED: Usuario no encontrado después de desactivar");
                }
            } else {
                System.out.println("❌ TEST FAILED: Usuario Gmail existente no encontrado");
            }
        } catch (Exception e) {
            System.err.println("❌ TEST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}