# =====================================================================
# Script PowerShell: Generar Backup Completo de EcoVivaShop
# =====================================================================
# Descripción: Este script genera un backup completo de la base de datos
#              PostgreSQL ecovivashop_db usando pg_dump
# =====================================================================

# Configuración
$DB_NAME = "ecovivashop_db"
$DB_USER = "postgres"
$DB_HOST = "localhost"
$DB_PORT = "5432"
$BACKUP_DATE = Get-Date -Format "yyyyMMdd_HHmmss"
$BACKUP_FILE = "ecovivashop_db_complete_backup_$BACKUP_DATE.sql"
$SCRIPT_DIR = $PSScriptRoot

Write-Host "=====================================================================
" -ForegroundColor Cyan
Write-Host "  GENERADOR DE BACKUP - ECOVIVASHOP DATABASE" -ForegroundColor Cyan
Write-Host "=====================================================================
" -ForegroundColor Cyan

Write-Host "`nConfiguracion:"
Write-Host "  - Base de datos: $DB_NAME"
Write-Host "  - Usuario:       $DB_USER"
Write-Host "  - Host:          $DB_HOST"
Write-Host "  - Puerto:        $DB_PORT"
Write-Host "  - Archivo:       $BACKUP_FILE"
Write-Host ""

# Verificar si pg_dump está disponible
Write-Host "Verificando instalacion de PostgreSQL..." -ForegroundColor Yellow

try {
    $pgDumpPath = (Get-Command pg_dump -ErrorAction Stop).Source
    Write-Host "✓ pg_dump encontrado en: $pgDumpPath" -ForegroundColor Green
} catch {
    Write-Host "✗ ERROR: pg_dump no encontrado en el PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor, verifica que PostgreSQL este instalado y agregado al PATH del sistema." -ForegroundColor Yellow
    Write-Host "Ruta tipica: C:\Program Files\PostgreSQL\16\bin" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Presiona cualquier tecla para salir..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# Generar backup
Write-Host "`nGenerando backup..." -ForegroundColor Yellow
Write-Host "Esto puede tardar varios minutos dependiendo del tamaño de la base de datos.`n"

# Configurar variable de entorno para la contraseña (para evitar prompt interactivo)
$env:PGPASSWORD = "123456789"

try {
    # Ejecutar pg_dump con ruta entre comillas para manejar espacios
    $backupPath = Join-Path $SCRIPT_DIR $BACKUP_FILE
    
    $arguments = @(
        "-h", $DB_HOST,
        "-p", $DB_PORT,
        "-U", $DB_USER,
        "-d", $DB_NAME,
        "-F", "p",  # Formato plain SQL
        "-f", "`"$backupPath`"",
        "--no-owner",  # No incluir comandos de propietario
        "--no-privileges",  # No incluir comandos de privilegios
        "--encoding=UTF8"  # Codificación UTF-8
    )
    
    # Ejecutar pg_dump y capturar salida
    $pinfo = New-Object System.Diagnostics.ProcessStartInfo
    $pinfo.FileName = "pg_dump"
    $pinfo.RedirectStandardError = $true
    $pinfo.RedirectStandardOutput = $true
    $pinfo.UseShellExecute = $false
    $pinfo.Arguments = $arguments -join " "
    $p = New-Object System.Diagnostics.Process
    $p.StartInfo = $pinfo
    $p.Start() | Out-Null
    $stdout = $p.StandardOutput.ReadToEnd()
    $stderr = $p.StandardError.ReadToEnd()
    $p.WaitForExit()
    $process = $p
    
    if ($process.ExitCode -eq 0) {
        Write-Host "`n✓ Backup generado exitosamente!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Archivo:   $backupPath" -ForegroundColor Cyan
        
        # Obtener tamaño del archivo
        $fileInfo = Get-Item $backupPath
        $fileSizeMB = [math]::Round($fileInfo.Length / 1MB, 2)
        Write-Host "Tamaño:    $fileSizeMB MB" -ForegroundColor Cyan
        Write-Host "Fecha:     $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan
        
    } else {
        Write-Host "`n✗ ERROR: Fallo al generar el backup (Codigo de salida: $($process.ExitCode))" -ForegroundColor Red
        if ($stderr) {
            Write-Host "Error details: $stderr" -ForegroundColor Red
        }
        exit 1
    }
    
} catch {
    Write-Host "`n✗ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    # Limpiar variable de entorno de contraseña
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host "`n====================================================================="
Write-Host "  INSTRUCCIONES PARA RESTAURAR EN OTRO ORDENADOR" -ForegroundColor Cyan
Write-Host "=====================================================================" 
Write-Host "`n1. Instalar PostgreSQL 16 en el nuevo ordenador"
Write-Host "   Descarga: https://www.postgresql.org/download/"
Write-Host ""
Write-Host "2. Copiar el archivo de backup al nuevo ordenador:"
Write-Host "   $BACKUP_FILE" -ForegroundColor Yellow
Write-Host ""
Write-Host "3. Crear la base de datos (solo si no existe):"
Write-Host '   createdb -U postgres ecovivashop_db' -ForegroundColor Green
Write-Host ""
Write-Host "4. Restaurar el backup:"
Write-Host "   psql -U postgres -d ecovivashop_db -f $BACKUP_FILE" -ForegroundColor Green
Write-Host ""
Write-Host "5. Verificar la restauracion:"
Write-Host "   psql -U postgres -d ecovivashop_db -c '\dt'" -ForegroundColor Green
Write-Host ""
Write-Host "6. Configurar application.properties en el proyecto Spring Boot:"
Write-Host "   spring.datasource.url=jdbc:postgresql://localhost:5432/ecovivashop_db"
Write-Host "   spring.datasource.username=postgres"
Write-Host "   spring.datasource.password=TU_CONTRASEÑA" -ForegroundColor Yellow
Write-Host ""
Write-Host "=====================================================================" 
Write-Host ""

Write-Host "Presiona cualquier tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
