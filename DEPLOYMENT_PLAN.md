# Plan de Despliegue - EcoVivaShop

## 📋 Información General

- **Aplicación:** EcoVivaShop v1.0.0
- **Framework:** Spring Boot 3.2.0
- **Java Version:** 17
- **Base de Datos:** PostgreSQL 12+
- **Servidor Web:** Tomcat Embedded
- **Build Tool:** Maven 3.9.9

## 🏗️ Requisitos del Sistema

### Servidor de Producción

- **OS:** Ubuntu 20.04 LTS / CentOS 7+ / Windows Server 2019+
- **CPU:** 2 cores mínimo, 4 cores recomendado
- **RAM:** 4GB mínimo, 8GB recomendado
- **Disco:** 20GB mínimo, 50GB recomendado
- **Java:** OpenJDK 17 o superior
- **PostgreSQL:** Versión 12 o superior

### Dependencias

- PostgreSQL Server
- Java Runtime Environment (JRE) 17+
- Maven 3.6+ (para build)
- Git (para deployment)

## 🚀 Estrategias de Despliegue

### Opción 1: Despliegue Manual (Recomendado para Desarrollo)

```bash
# 1. Clonar repositorio
git clone https://github.com/ecovivashop/ecovivashop.git
cd ecovivashop

# 2. Configurar base de datos
sudo -u postgres psql
CREATE DATABASE ecovivashop_db;
CREATE USER ecovivashop_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE ecovivashop_db TO ecovivashop_user;
\q

# 3. Configurar aplicación
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Editar application.properties con configuración de producción

# 4. Build de la aplicación
mvn clean package -DskipTests

# 5. Ejecutar aplicación
java -jar target/ecovivashop-1.0.0.jar --spring.profiles.active=prod
```

### Opción 2: Despliegue con Docker

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/ecovivashop-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

```bash
# Build y ejecución
docker build -t ecovivashop:latest .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod ecovivashop:latest
```

### Opción 3: Despliegue en Servidor de Aplicaciones

```bash
# Para Tomcat/WildFly
mvn clean package -DskipTests
cp target/ecovivashop-1.0.0.war /opt/tomcat/webapps/
```

## ⚙️ Configuración de Producción

### application-prod.properties

```properties
# Base de datos de producción
spring.datasource.url=jdbc:postgresql://localhost:5432/ecovivashop_db
spring.datasource.username=ecovivashop_user
spring.datasource.password=SECURE_PASSWORD_HERE

# Configuración de producción
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
server.port=8080

# Logging de producción
logging.level.com.ecovivashop=INFO
logging.level.org.springframework=INFO
logging.file.name=/var/log/ecovivashop/ecovivashop.log

# Email de producción
spring.mail.username=produccion@ecovivashop.com
spring.mail.password=SECURE_APP_PASSWORD

# Configuración de archivos
app.upload.path=/var/www/ecovivashop/uploads
```

## 🔒 Configuración de Seguridad

### Firewall (UFW - Ubuntu)

```bash
sudo ufw enable
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
```

### SSL/TLS con Let's Encrypt

```bash
sudo apt install certbot
sudo certbot --nginx -d ecovivashop.com
```

### Configuración Nginx (Reverse Proxy)

```nginx
server {
    listen 80;
    server_name ecovivashop.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 📊 Monitoreo y Health Checks

### Endpoints de Monitoreo

- **Health Check:** `http://localhost:8080/actuator/health`
- **Métricas:** `http://localhost:8080/actuator/metrics`
- **Info:** `http://localhost:8080/actuator/info`

### Configuración de Monitoreo

```properties
# Actuator en producción
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.endpoint.metrics.enabled=true
```

## 🔄 Estrategias de Backup

### Backup Automático de Base de Datos

```bash
#!/bin/bash
# backup_db.sh
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/var/backups/ecovivashop"
DB_NAME="ecovivashop_db"
DB_USER="ecovivashop_user"

mkdir -p $BACKUP_DIR

pg_dump -U $DB_USER -h localhost $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# Comprimir
gzip $BACKUP_DIR/db_backup_$DATE.sql

# Mantener solo últimos 30 backups
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +30 -delete

echo "Backup completado: $BACKUP_DIR/db_backup_$DATE.sql.gz"
```

### Backup de Archivos

```bash
#!/bin/bash
# backup_files.sh
DATE=$(date +%Y%m%d_%H%M%S)
SOURCE_DIR="/var/www/ecovivashop/uploads"
BACKUP_DIR="/var/backups/ecovivashop/files"

rsync -av --delete $SOURCE_DIR $BACKUP_DIR/backup_$DATE/

# Comprimir
tar -czf $BACKUP_DIR/backup_$DATE.tar.gz -C $BACKUP_DIR backup_$DATE
rm -rf $BACKUP_DIR/backup_$DATE

# Mantener solo últimos 30 backups
find $BACKUP_DIR -name "backup_*.tar.gz" -mtime +30 -delete
```

## ⏰ Tareas Programadas (Cron Jobs)

### Configuración de Cron

```bash
# Editar crontab
crontab -e

# Backup diario de BD a las 2:00 AM
0 2 * * * /path/to/backup_db.sh

# Backup semanal de archivos los domingos a las 3:00 AM
0 3 * * 0 /path/to/backup_files.sh

# Reinicio preventivo mensual (primer día del mes a las 4:00 AM)
0 4 1 * * /path/to/restart_app.sh

# Limpieza de logs antiguos (diario a las 5:00 AM)
0 5 * * * /path/to/clean_logs.sh
```

## 📈 Escalabilidad

### Configuración Horizontal

- **Load Balancer:** Nginx upstream o AWS ALB
- **Sesiones:** Configurar Redis para sesiones distribuidas
- **Base de Datos:** Read replicas para consultas de lectura

### Configuración Vertical

- **JVM Tuning:**

```bash
java -Xms2g -Xmx4g -XX:+UseG1GC -jar ecovivashop.jar
```

## 🚨 Plan de Recuperación de Desastres

### Estrategia de Backup

1. **Backups Diarios:** Base de datos completa
2. **Backups Semanales:** Archivos y configuración
3. **Backups Mensuales:** Entorno completo

### Procedimiento de Restauración

```bash
# 1. Detener aplicación
sudo systemctl stop ecovivashop

# 2. Restaurar base de datos
gunzip latest_backup.sql.gz
psql -U ecovivashop_user -d ecovivashop_db < latest_backup.sql

# 3. Restaurar archivos
tar -xzf latest_files_backup.tar.gz -C /var/www/ecovivashop/

# 4. Reiniciar aplicación
sudo systemctl start ecovivashop
```

## ✅ Checklist de Despliegue

### Pre-Despliegue

- [ ] Repositorio clonado y actualizado
- [ ] Base de datos creada y configurada
- [ ] Variables de entorno configuradas
- [ ] Certificados SSL obtenidos
- [ ] Firewall configurado

### Despliegue

- [ ] Build exitoso (`mvn clean package`)
- [ ] Aplicación iniciada correctamente
- [ ] Endpoints de health responding
- [ ] Base de datos conectada
- [ ] Email funcionando

### Post-Despliegue

- [ ] Logs verificados (sin errores)
- [ ] Funcionalidades básicas probadas
- [ ] Backups automáticos configurados
- [ ] Monitoreo activo
- [ ] Documentación actualizada

## 📞 Contactos de Emergencia

- **Administrador de Sistema:** <admin@ecovivashop.com>
- **Desarrollador Principal:** <dev@ecovivashop.com>
- **Soporte Técnico:** <support@ecovivashop.com>

---

**Fecha de Creación:** Noviembre 2025
**Versión del Documento:** 1.0
**Responsable:** Equipo de Desarrollo EcoVivaShop
