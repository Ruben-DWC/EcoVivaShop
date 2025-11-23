# Plan de Monitoreo y Mantenimiento - EcoVivaShop

## 📊 Sistema de Monitoreo

### 1. Monitoreo de Aplicación (Spring Boot Actuator)

#### Endpoints Configurados

```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics,env,configprops
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true
management.endpoint.env.enabled=true
```

#### Health Checks Disponibles

- **Health General:** `/actuator/health`
- **Health Base de Datos:** `/actuator/health/db`
- **Health Disco:** `/actuator/health/diskSpace`
- **Métricas JVM:** `/actuator/metrics/jvm.memory.used`
- **Métricas HTTP:** `/actuator/metrics/http.server.requests`

### 2. Monitoreo de Logs

#### Configuración de Logging

```properties
# Logs principales
logging.level.com.ecovivashop=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=INFO

# Logs de seguridad
logging.level.org.springframework.security=DEBUG

# Archivo de logs
logging.file.name=logs/ecovivashop.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
```

#### Tipos de Logs a Monitorear

- **Errores de Aplicación:** Excepciones no manejadas
- **Errores de Base de Datos:** Conexiones fallidas, deadlocks
- **Errores de Seguridad:** Intentos de login fallidos, accesos no autorizados
- **Errores de Performance:** Consultas lentas, memoria alta

### 3. Monitoreo de Base de Datos

#### Métricas PostgreSQL

```sql
-- Conexiones activas
SELECT count(*) FROM pg_stat_activity WHERE state = 'active';

-- Tamaño de la base de datos
SELECT pg_size_pretty(pg_database_size('ecovivashop_db'));

-- Consultas lentas (requiere configuración)
SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;

-- Locks activos
SELECT * FROM pg_locks WHERE NOT granted;
```

#### Alertas de Base de Datos

- **Conexiones > 80% del límite**
- **Espacio en disco < 10% disponible**
- **Consultas que tardan > 5 segundos**
- **Deadlocks detectados**

### 4. Monitoreo de Sistema Operativo

#### Métricas del SO

- **CPU Usage:** > 80% por más de 5 minutos
- **Memory Usage:** > 85% de RAM utilizada
- **Disk Usage:** > 90% de espacio utilizado
- **Network I/O:** Picos inusuales de tráfico

#### Comandos de Monitoreo

```bash
# CPU y Memoria
top -b -n1 | head -20

# Espacio en disco
df -h

# Conexiones de red
netstat -tlnp | grep :8080

# Procesos Java
ps aux | grep java
```

## 🔧 Plan de Mantenimiento

### 1. Mantenimiento Preventivo

#### Tareas Diarias

```bash
#!/bin/bash
# daily_maintenance.sh

echo "=== Mantenimiento Diario - $(date) ==="

# 1. Verificar estado de la aplicación
curl -f http://localhost:8080/actuator/health || echo "Aplicación NO saludable"

# 2. Verificar conexiones a BD
psql -h localhost -U ecovivashop_user -d ecovivashop_db -c "SELECT 1;" || echo "Base de datos NO accesible"

# 3. Verificar espacio en disco
DISK_USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 90 ]; then
    echo "ALERTA: Espacio en disco > 90%"
fi

# 4. Limpiar logs antiguos (más de 7 días)
find /var/log/ecovivashop -name "*.log" -mtime +7 -delete

# 5. Verificar procesos
JAVA_PROCESSES=$(pgrep -f "ecovivashop")
if [ -z "$JAVA_PROCESSES" ]; then
    echo "ALERTA: No hay procesos Java ejecutándose"
fi

echo "=== Fin Mantenimiento Diario ==="
```

#### Tareas Semanales

```bash
#!/bin/bash
# weekly_maintenance.sh

echo "=== Mantenimiento Semanal - $(date) ==="

# 1. Backup completo de base de datos
pg_dump -U ecovivashop_user -h localhost ecovivashop_db > /var/backups/ecovivashop/db_weekly_$(date +%Y%m%d).sql

# 2. Análisis de tabla de PostgreSQL
psql -U ecovivashop_user -d ecovivashop_db -c "VACUUM ANALYZE;"

# 3. Verificar integridad de archivos
find /var/www/ecovivashop/uploads -type f -exec file {} \; | grep -v "image\|PDF" || echo "Archivos sospechosos encontrados"

# 4. Actualizar estadísticas de BD
psql -U ecovivashop_user -d ecovivashop_db -c "ANALYZE;"

echo "=== Fin Mantenimiento Semanal ==="
```

#### Tareas Mensuales

```bash
#!/bin/bash
# monthly_maintenance.sh

echo "=== Mantenimiento Mensual - $(date) ==="

# 1. Reinicio preventivo de aplicación
systemctl restart ecovivashop

# 2. Reindexación de tablas críticas
psql -U ecovivashop_user -d ecovivashop_db -c "REINDEX DATABASE ecovivashop_db;"

# 3. Limpieza de archivos temporales
find /tmp -name "ecovivashop_*" -mtime +30 -delete

# 4. Verificación de permisos
chown -R ecovivashop:ecovivashop /var/www/ecovivashop
chmod -R 755 /var/www/ecovivashop/uploads

# 5. Actualización de dependencias (si aplica)
# mvn versions:display-dependency-updates

echo "=== Fin Mantenimiento Mensual ==="
```

### 2. Estrategias de Backup

#### Backup de Base de Datos

```bash
#!/bin/bash
# backup_database.sh

BACKUP_DIR="/var/backups/ecovivashop/database"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="ecovivashop_db"
DB_USER="ecovivashop_user"
RETENTION_DAYS=30

# Crear directorio si no existe
mkdir -p $BACKUP_DIR

# Realizar backup
pg_dump -U $DB_USER -h localhost -Fc $DB_NAME > $BACKUP_DIR/${DB_NAME}_backup_$DATE.dump

# Comprimir
gzip $BACKUP_DIR/${DB_NAME}_backup_$DATE.dump

# Verificar integridad del backup
gunzip -c $BACKUP_DIR/${DB_NAME}_backup_$DATE.dump.gz | pg_restore -l > /dev/null
if [ $? -eq 0 ]; then
    echo "Backup exitoso: ${DB_NAME}_backup_$DATE.dump.gz"
else
    echo "ERROR: Backup corrupto"
    rm $BACKUP_DIR/${DB_NAME}_backup_$DATE.dump.gz
    exit 1
fi

# Limpiar backups antiguos
find $BACKUP_DIR -name "*.gz" -mtime +$RETENTION_DAYS -delete

# Mantener solo último backup diario
find $BACKUP_DIR -name "*_backup_$(date +%Y%m%d)*.gz" -not -name "${DB_NAME}_backup_$DATE.dump.gz" -delete
```

#### Backup de Archivos

```bash
#!/bin/bash
# backup_files.sh

SOURCE_DIR="/var/www/ecovivashop/uploads"
BACKUP_DIR="/var/backups/ecovivashop/files"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=90

# Crear directorio si no existe
mkdir -p $BACKUP_DIR

# Realizar backup incremental con rsync
rsync -av --delete --link-dest=$BACKUP_DIR/latest $SOURCE_DIR $BACKUP_DIR/backup_$DATE/

# Crear enlace simbólico al último backup
rm -f $BACKUP_DIR/latest
ln -s backup_$DATE $BACKUP_DIR/latest

# Comprimir backup semanal (solo domingos)
if [ $(date +%w) -eq 0 ]; then
    tar -czf $BACKUP_DIR/backup_$DATE.tar.gz -C $BACKUP_DIR backup_$DATE
    echo "Backup semanal comprimido creado"
fi

# Limpiar backups antiguos
find $BACKUP_DIR -maxdepth 1 -name "backup_*" -type d -mtime +$RETENTION_DAYS -exec rm -rf {} \;

echo "Backup de archivos completado: backup_$DATE"
```

#### Backup de Configuración

```bash
#!/bin/bash
# backup_config.sh

CONFIG_DIR="/etc/ecovivashop"
BACKUP_DIR="/var/backups/ecovivashop/config"
DATE=$(date +%Y%m%d_%H%M%S)

# Crear directorio si no existe
mkdir -p $BACKUP_DIR

# Backup de archivos de configuración
tar -czf $BACKUP_DIR/config_backup_$DATE.tar.gz \
    /etc/ecovivashop/ \
    /opt/ecovivashop/application.properties \
    /etc/nginx/sites-available/ecovivashop \
    /etc/systemd/system/ecovivashop.service

echo "Backup de configuración completado: config_backup_$DATE.tar.gz"
```

### 3. Plan de Recuperación de Desastres

#### Escenario 1: Fallo de Aplicación

```bash
#!/bin/bash
# recover_application.sh

echo "=== Recuperación de Aplicación ==="

# 1. Verificar estado
if pgrep -f "ecovivashop" > /dev/null; then
    echo "Aplicación ya está ejecutándose"
    exit 0
fi

# 2. Reiniciar servicio
systemctl restart ecovivashop

# 3. Verificar health check
sleep 30
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status')
if [ "$HEALTH" = "UP" ]; then
    echo "Aplicación recuperada exitosamente"
else
    echo "ERROR: Aplicación no saludable después del reinicio"
    exit 1
fi
```

#### Escenario 2: Fallo de Base de Datos

```bash
#!/bin/bash
# recover_database.sh

echo "=== Recuperación de Base de Datos ==="

BACKUP_DIR="/var/backups/ecovivashop/database"
LATEST_BACKUP=$(ls -t $BACKUP_DIR/*.gz | head -1)

# 1. Detener aplicación
systemctl stop ecovivashop

# 2. Crear backup del estado actual (si existe)
if psql -U ecovivashop_user -d ecovivashop_db -c "SELECT 1;" 2>/dev/null; then
    pg_dump -U ecovivashop_user ecovivashop_db > /tmp/pre_recovery_backup.sql
fi

# 3. Restaurar desde backup
dropdb -U ecovivashop_user ecovivashop_db
createdb -U ecovivashop_user ecovivashop_db
gunzip -c $LATEST_BACKUP | pg_restore -U ecovivashop_user -d ecovivashop_db

# 4. Reiniciar aplicación
systemctl start ecovivashop

# 5. Verificar
sleep 30
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status')
if [ "$HEALTH" = "UP" ]; then
    echo "Base de datos recuperada exitosamente"
else
    echo "ERROR: Problemas después de la recuperación"
    exit 1
fi
```

### 4. Configuración de Alertas

#### Alertas por Email

```bash
#!/bin/bash
# alert_system.sh

SMTP_SERVER="smtp.gmail.com"
SMTP_PORT="587"
FROM_EMAIL="alerts@ecovivashop.com"
TO_EMAIL="admin@ecovivashop.com"

send_alert() {
    local subject="$1"
    local message="$2"

    echo "Enviando alerta: $subject"
    echo "$message" | mail -s "$subject" -S smtp="$SMTP_SERVER:$SMTP_PORT" -S from="$FROM_EMAIL" "$TO_EMAIL"
}

# Verificar aplicación
HEALTH=$(curl -s http://localhost:8080/actuator/health | jq -r '.status' 2>/dev/null)
if [ "$HEALTH" != "UP" ]; then
    send_alert "EcoVivaShop - ALERTA: Aplicación No Saludable" "La aplicación EcoVivaShop no está respondiendo correctamente. Status: $HEALTH"
fi

# Verificar base de datos
if ! psql -U ecovivashop_user -d ecovivashop_db -c "SELECT 1;" 2>/dev/null; then
    send_alert "EcoVivaShop - ALERTA: Base de Datos No Accesible" "No se puede conectar a la base de datos PostgreSQL"
fi

# Verificar espacio en disco
DISK_USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 90 ]; then
    send_alert "EcoVivaShop - ALERTA: Espacio en Disco Bajo" "Uso de disco: $DISK_USAGE%"
fi
```

## 📋 Checklist de Monitoreo

### Métricas Diarias

- [ ] Health checks de aplicación (actuator)
- [ ] Conexiones activas a base de datos
- [ ] Uso de CPU y memoria
- [ ] Espacio disponible en disco
- [ ] Logs de errores (revisar logs/ecovivashop.log)

### Métricas Semanales

- [ ] Rendimiento de consultas SQL
- [ ] Tamaño de base de datos
- [ ] Número de usuarios activos
- [ ] Tasa de conversión de pedidos

### Métricas Mensuales

- [ ] Análisis de tendencias de uso
- [ ] Revisión de backups
- [ ] Actualización de dependencias
- [ ] Optimización de índices de BD

## 📞 Contactos y Escalamiento

### Niveles de Alerta

1. **INFO:** Información general, no requiere acción
2. **WARNING:** Situación que requiere atención pero no es crítica
3. **ERROR:** Problema que afecta funcionalidad, requiere acción inmediata
4. **CRITICAL:** Sistema caído, requiere intervención inmediata

### Equipo de Respuesta

- **Nivel 1 (Desarrollador):** Respuesta en 1 hora
- **Nivel 2 (SysAdmin):** Respuesta en 30 minutos para críticos
- **Nivel 3 (Gerencia):** Notificación inmediata para outages

---

**Fecha de Creación:** Noviembre 2025
**Versión del Documento:** 1.0
**Próxima Revisión:** Diciembre 2025
**Responsable:** Equipo de Operaciones EcoVivaShop
