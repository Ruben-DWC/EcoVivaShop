# 📧 CONFIGURACIÓN DE EMAIL GMAIL - ECOVIVASHOP

## ✅ GUÍA PASO A PASO PARA ENVÍO DE EMAILS

**Fecha**: 12 de Junio, 2025  
**Estado**: 🔄 **PENDIENTE DE CONFIGURACIÓN**

---

## 🚨 PROBLEMA IDENTIFICADO

``
⚠️ Error enviando correo de bienvenida: Authentication failed
✅ Cliente registrado exitosamente: subiendovideos903@gmail.com
``

**Causa**: Las credenciales de email no están configuradas correctamente.

---

## 🛠️ SOLUCIÓN: CONFIGURAR GMAIL

### **PASO 1: Generar Contraseña de Aplicación en Gmail**

1. **Ir a tu cuenta de Gmail**: `https://myaccount.google.com/`

2. **Activar Verificación en 2 pasos**:
   - Ve a "Seguridad"
   - Busca "Verificación en 2 pasos"
   - Actívala si no está activada

3. **Generar Contraseña de Aplicación**:
   - En "Seguridad", busca "Contraseñas de aplicación"
   - Clic en "Contraseñas de aplicación"
   - Selecciona "Aplicación personalizada"
   - Escribe: "EcoVivaShop"
   - Clic en "Generar"
   - **COPIAR la contraseña de 16 caracteres** (ejemplo: `abcd efgh ijkl mnop`)

### **PASO 2: Actualizar application.properties**

Editar el archivo: `src/main/resources/application.properties`

**Cambiar estas líneas**:

```properties
spring.mail.username=subiendovideos903@gmail.com
spring.mail.password=your-gmail-app-password-here
```

**Por**:

```properties
spring.mail.username=subiendovideos903@gmail.com
spring.mail.password=abcd efgh ijkl mnop
```

*(Usar la contraseña real que generaste)

### **PASO 3: Reiniciar la Aplicación**

1. **Detener** el servidor actual (Ctrl+C en la terminal)
2. **Reiniciar** con: `mvn spring-boot:run`

### **PASO 4: Probar el Envío**

1. **Ir a**: `http://localhost:8081/test/email`
2. **Enviar email de prueba** con tu email
3. **Verificar** que llegue a tu bandeja

---

## 🔧 CONFIGURACIÓN COMPLETA

### **application.properties CORRECTO**

```properties
# EMAIL CONFIGURATION - PRODUCCIÓN
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=subiendovideos903@gmail.com
spring.mail.password=tu-contraseña-de-aplicacion-aqui
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=3000
spring.mail.properties.mail.smtp.writetimeout=5000

# EMAIL TEMPLATES
spring.mail.default-encoding=UTF-8
spring.mail.properties.mail.smtp.starttls.required=true
```

---

## 🧪 PRUEBAS A REALIZAR

### **1. Prueba Manual de Email**

``
URL: http://localhost:8081/test/email
Email: subiendovideos903@gmail.com
Nombre: Prueba Usuario
``

### **2. Prueba de Registro**

``
URL: http://localhost:8081/auth/registro
Llenar formulario con email real
Verificar email de bienvenida
``

### **3. Verificar Logs**

Buscar en consola:
✅ Correo de bienvenida enviado a: [email]
❌ Error al enviar correo: [error]
``

---

## 🔐 SEGURIDAD

### **⚠️ IMPORTANTES**

- **NUNCA** subir la contraseña real a Git
- Usar **contraseñas de aplicación**, no tu contraseña normal
- La contraseña tiene formato: `xxxx xxxx xxxx xxxx`
- **Guardar** la contraseña en lugar seguro

### **🛡️ Para Producción**

- Usar variables de entorno
- Configurar en servidor de producción
- No incluir en código fuente

---

## 🚀 ALTERNATIVA: MODO DESARROLLO

Si no quieres configurar Gmail ahora, puedes usar **modo simulado**:

```properties
# DESARROLLO - SIN EMAILS REALES
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

Esto hará que los emails se "envíen" pero sin llegar realmente.

---

## 📋 CHECKLIST DE CONFIGURACIÓN

### **Antes de la configuración**

- [ ] ❌ Authentication failed
- [ ] ❌ Emails no llegan
- [ ] ✅ Registro funciona
- [ ] ✅ Login funciona

### **Después de la configuración**

- [ ] ✅ Authentication exitosa
- [ ] ✅ Emails llegan a Gmail
- [ ] ✅ Template profesional
- [ ] ✅ Registro + Email funcional

---

## 🎯 PASOS INMEDIATOS

### **Para arreglar AHORA**

1. **Abrir**: `https://myaccount.google.com/security`
2. **Activar**: Verificación en 2 pasos
3. **Generar**: Contraseña de aplicación "EcoVivaShop"
4. **Copiar**: La contraseña de 16 caracteres
5. **Editar**: `application.properties`
6. **Reemplazar**: `your-gmail-app-password-here` con la contraseña real
7. **Reiniciar**: La aplicación
8. **Probar**: `/test/email`

---

## 🔍 DIAGNÓSTICO RÁPIDO

### **Si sigue fallando**

1. **Verificar credenciales** en Gmail
2. **Revisar configuración** 2FA
3. **Comprobar contraseña** de aplicación
4. **Verificar firewall** / antivirus
5. **Probar con otro email** (Outlook, Yahoo)

### **Logs útiles**

``
✅ ÉXITO: "Correo de bienvenida enviado exitosamente"
❌ ERROR: "Authentication failed"
❌ ERROR: "Connection refused"
❌ ERROR: "Invalid credentials"
``

---

## ✅ RESULTADO ESPERADO

**Una vez configurado correctamente**:

- ✅ Registro de usuario funcional
- ✅ Email de bienvenida enviado automáticamente
- ✅ Template profesional recibido en Gmail
- ✅ Sistema completo operativo

**🎉 ¡Tu sistema enviará emails reales a usuarios!**

---

**📝 Notas**:

- La configuración solo se hace UNA vez
- Guardar la contraseña de aplicación
- Funciona con cualquier proveedor SMTP
- El template de bienvenida es profesional

**🎯 Estado**: ⏳ **PENDIENTE DE CONFIGURACIÓN GMAIL**
