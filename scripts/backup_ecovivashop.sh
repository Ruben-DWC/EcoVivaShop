#!/bin/bash
# backup_ecovivashop.sh - Script de backup completo para EcoVivaShop

# Configuración
BACKUP_ROOT="/var/backups/ecovivashop"
DB_NAME="ecovivashop_db"
DB_USER="ecovivashop_user"
APP_DIR="/opt/ecovivashop"
LOG_FILE="/var/log/ecovivashop/backup.log"

# Crear directorios si no existen
mkdir -p $BACKUP_ROOT/database
mkdir -p $BACKUP_ROOT/files
mkdir -p $BACKUP_ROOT/config
mkdir -p $(dirname $LOG_FILE)

# Función de logging
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a $LOG_FILE
}

# Función de envío de alertas (placeholder)
send_alert() {
    local subject="$1"
    local message="$2"
    # Implementar envío de email o notificación
    log "ALERTA: $subject - $message"
}

log "=== Iniciando Backup Completo de EcoVivaShop ==="

# 1. Backup de Base de Datos
log "Realizando backup de base de datos..."
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_ROOT/database/ecovivashop_db_$TIMESTAMP.sql"

if pg_dump -U $DB_USER -h localhost -Fc $DB_NAME > $BACKUP_FILE; then
    log "Backup de BD exitoso: $BACKUP_FILE"

    # Comprimir
    gzip $BACKUP_FILE
    log "Backup comprimido: $BACKUP_FILE.gz"

    # Verificar integridad
    if gunzip -c $BACKUP_FILE.gz | pg_restore -l > /dev/null 2>&1; then
        log "Integridad del backup verificada"
    else
        send_alert "Backup Corrupto" "El backup de BD $TIMESTAMP está corrupto"
    fi
else
    send_alert "Error en Backup de BD" "Falló el backup de base de datos"
    exit 1
fi

# 2. Backup de Archivos
log "Realizando backup de archivos..."
FILES_BACKUP="$BACKUP_ROOT/files/files_$TIMESTAMP.tar.gz"

if tar -czf $FILES_BACKUP -C /var/www/ecovivashop uploads/ 2>/dev/null; then
    log "Backup de archivos exitoso: $FILES_BACKUP"
else
    log "Advertencia: Error en backup de archivos (posiblemente directorio vacío)"
fi

# 3. Backup de Configuración
log "Realizando backup de configuración..."
CONFIG_BACKUP="$BACKUP_ROOT/config/config_$TIMESTAMP.tar.gz"

if tar -czf $CONFIG_BACKUP \
    /opt/ecovivashop/application.properties \
    /etc/systemd/system/ecovivashop.service \
    2>/dev/null; then
    log "Backup de configuración exitoso: $CONFIG_BACKUP"
else
    log "Advertencia: Algunos archivos de configuración no encontrados"
fi

# 4. Limpieza de backups antiguos (mantener 30 días)
log "Limpiando backups antiguos..."
find $BACKUP_ROOT -name "*.sql.gz" -mtime +30 -delete
find $BACKUP_ROOT -name "*.tar.gz" -mtime +30 -delete

# 5. Verificación de espacio en disco
DISK_USAGE=$(df $BACKUP_ROOT | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 90 ]; then
    send_alert "Espacio en Disco Bajo" "Uso de disco en $BACKUP_ROOT: $DISK_USAGE%"
fi

# 6. Generar reporte
TOTAL_BACKUPS=$(find $BACKUP_ROOT -name "*.gz" | wc -l)
BACKUP_SIZE=$(du -sh $BACKUP_ROOT | awk '{print $1}')

log "=== Backup Completado ==="
log "Total de backups: $TOTAL_BACKUPS"
log "Espacio utilizado: $BACKUP_SIZE"
log "Ubicación: $BACKUP_ROOT"

echo "Backup completado exitosamente - $(date)" >> $LOG_FILE