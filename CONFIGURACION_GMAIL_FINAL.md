# 🔧 INSTRUCCIONES FINALES PARA CONFIGURAR GMAIL

## 🚨 PASO CRÍTICO: Configurar tu contraseña de aplicación de Gmail

**Archivo a editar:** `src/main/resources/application.properties`

**Línea a modificar:**

```properties
spring.mail.password=PONDRA-TU-CONTRASEÑA-DE-APLICACION-AQUI
```

**Cambiar por tu contraseña real de aplicación Gmail:**

```properties
spring.mail.password=tu-contraseña-de-aplicacion-de-16-caracteres
```

---

## 📋 PASOS PARA OBTENER LA CONTRASEÑA DE APLICACIÓN

### 1. **Ir a tu cuenta de Google:**

- Ve a: <https://myaccount.google.com/>

### 2. **Activar verificación en 2 pasos:**

- Ir a "Seguridad"
- Buscar "Verificación en 2 pasos"
- Activarla si no está activada

### 3. **Generar contraseña de aplicación:**

- En "Seguridad", buscar "Contraseñas de aplicación"
- Clic en "Contraseñas de aplicación"
- Seleccionar "Aplicación personalizada"
- Escribir: "EcoMaxTienda"
- Clic en "Generar"
- **COPIAR** la contraseña de 16 caracteres

### 4. **Actualizar application.properties:**

- Reemplazar `PONDRA-TU-CONTRASEÑA-DE-APLICACION-AQUI`
- Con la contraseña real que copiaste

---

## 🎯 DESPUÉS DE CONFIGURAR

1. **Reiniciar la aplicación:**

   ```bash
   mvn spring-boot:run
   ```

2. **Probar registro:**
   - Ir a: <http://localhost:8081/auth/registro>
   - Registrarse con tu email real: <subiendovideos903@gmail.com>
   - Verificar que llegue el correo de bienvenida

3. **Verificar en consola:**
   - Buscar: `✅ [REAL EMAIL] Correo de bienvenida enviado a: subiendovideos903@gmail.com`
   - O: `✅ [SIMPLE EMAIL] Correo de bienvenida enviado a: subiendovideos903@gmail.com`

---

## ✅ SISTEMA MEJORADO

- **Never-fail:** El registro nunca falla por errores de email
- **HTML Templates:** Si están disponibles, usa templates profesionales
- **Simple Email:** Si hay problemas, usa emails simples
- **Simulación:** Como último recurso, simula el envío
- **Logging claro:** Mensajes detallados para debugging

**¡El sistema está listo para pruebas con email real!** 🚀
