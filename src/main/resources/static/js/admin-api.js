// Script para conectar el dashboard con las APIs del backend
class DashboardAPI {
    constructor() {
        this.baseURL = '/api';
        this.init();
    }

    async init() {
        await this.loadDashboardStats();
        await this.loadRecentActivity();
        this.setupRealTimeUpdates();
    }

    async loadDashboardStats() {
        try {
            const response = await fetch(`${this.baseURL}/admin/dashboard/stats`);
            const stats = await response.json();
            this.updateStatsCards(stats);
        } catch (error) {
            console.error('Error cargando estadísticas:', error);
            this.showError('Error al cargar las estadísticas del dashboard');
        }
    }

    updateStatsCards(stats) {
        // Actualizar tarjetas de estadísticas
        this.updateElement('total-usuarios', stats.totalUsuarios || 0);
        this.updateElement('total-clientes', stats.totalClientes || 0);
        this.updateElement('total-admins', stats.totalAdmins || 0);
        this.updateElement('usuarios-hoy', stats.usuariosHoy || 0);

        this.updateElement('total-productos', stats.totalProductos || 0);
        this.updateElement('productos-stock', stats.productosStock || 0);
        this.updateElement('productos-agotados', stats.productosAgotados || 0);
        this.updateElement('stock-bajo', stats.stockBajo || 0);

        this.updateElement('pedidos-pendientes', stats.pedidosPendientes || 0);
        this.updateElement('pedidos-enviados', stats.pedidosEnviados || 0);
        this.updateElement('pedidos-entregados', stats.pedidosEntregados || 0);

        // Formatear ventas
        const ventasMes = new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP'
        }).format(stats.ventasMes || 0);
        this.updateElement('ventas-mes', ventasMes);
    }

    updateElement(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    }

    async loadRecentActivity() {
        try {
            const response = await fetch(`${this.baseURL}/admin/dashboard/recent-activity`);
            const activity = await response.json();
            this.updateActivityTimeline(activity);
        } catch (error) {
            console.error('Error cargando actividad reciente:', error);
        }
    }

    updateActivityTimeline(activity) {
        // Actualizar timeline de actividad reciente
        const timeline = document.getElementById('activity-timeline');
        if (!timeline) return;

        let timelineHTML = '';

        // Últimos usuarios
        if (activity.ultimosUsuarios && activity.ultimosUsuarios.length > 0) {
            activity.ultimosUsuarios.forEach(usuario => {
                timelineHTML += `
                    <div class="timeline-item">
                        <div class="timeline-marker bg-success"></div>
                        <div class="timeline-content">
                            <h6 class="mb-0">Nuevo usuario registrado</h6>
                            <p class="text-muted mb-0">${usuario.nombreCompleto || usuario.nombre + ' ' + usuario.apellido}</p>
                            <small class="text-muted">${this.formatDate(usuario.fechaRegistro)}</small>
                        </div>
                    </div>
                `;
            });
        }

        // Pedidos recientes
        if (activity.pedidosRecientes && activity.pedidosRecientes.length > 0) {
            activity.pedidosRecientes.slice(0, 5).forEach(pedido => {
                const statusColor = this.getStatusColor(pedido.estado);
                timelineHTML += `
                    <div class="timeline-item">
                        <div class="timeline-marker bg-${statusColor}"></div>
                        <div class="timeline-content">
                            <h6 class="mb-0">Pedido ${pedido.numeroPedido}</h6>
                            <p class="text-muted mb-0">Estado: ${pedido.estado}</p>
                            <small class="text-muted">${this.formatDate(pedido.fechaPedido)}</small>
                        </div>
                    </div>
                `;
            });
        }

        timeline.innerHTML = timelineHTML;
    }

    getStatusColor(status) {
        const colors = {
            'PENDIENTE': 'warning',
            'CONFIRMADO': 'info',
            'ENVIADO': 'primary',
            'ENTREGADO': 'success',
            'CANCELADO': 'danger'
        };
        return colors[status] || 'secondary';
    }

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('es-CO', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    setupRealTimeUpdates() {
        // Actualizar estadísticas cada 5 minutos
        setInterval(() => {
            this.loadDashboardStats();
        }, 5 * 60 * 1000);

        // Actualizar actividad cada 2 minutos
        setInterval(() => {
            this.loadRecentActivity();
        }, 2 * 60 * 1000);
    }

    showError(message) {
        // Mostrar notificación de error
        const alert = document.createElement('div');
        alert.className = 'alert alert-danger alert-dismissible fade show position-fixed';
        alert.style.top = '20px';
        alert.style.right = '20px';
        alert.style.zIndex = '9999';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alert);

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (alert.parentNode) {
                alert.parentNode.removeChild(alert);
            }
        }, 5000);
    }
}

// API para gestión de usuarios
class UsuariosAPI {
    constructor() {
        this.baseURL = '/api/admin/usuarios';
    }

    async obtenerUsuarios(page = 0, size = 10, busqueda = '') {
        try {
            const params = new URLSearchParams({ page, size });
            if (busqueda) params.append('busqueda', busqueda);

            const response = await fetch(`${this.baseURL}?${params}`);
            return await response.json();
        } catch (error) {
            console.error('Error obteniendo usuarios:', error);
            throw error;
        }
    }

    async crearUsuario(userData) {
        try {
            const response = await fetch(this.baseURL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });
            return await response.json();
        } catch (error) {
            console.error('Error creando usuario:', error);
            throw error;
        }
    }

    async cambiarEstadoUsuario(id, estado) {
        try {
            const response = await fetch(`${this.baseURL}/${id}/estado`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ estado })
            });
            return await response.json();
        } catch (error) {
            console.error('Error cambiando estado del usuario:', error);
            throw error;
        }
    }
}

// API para gestión de productos
class ProductosAPI {
    constructor() {
        this.baseURL = '/api/admin/productos';
    }

    async obtenerProductos() {
        try {
            const response = await fetch(this.baseURL);
            return await response.json();
        } catch (error) {
            console.error('Error obteniendo productos:', error);
            throw error;
        }
    }

    async crearProducto(productData) {
        try {
            const response = await fetch(this.baseURL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(productData)
            });
            return await response.json();
        } catch (error) {
            console.error('Error creando producto:', error);
            throw error;
        }
    }
}

// API para gestión de pedidos
class PedidosAPI {
    constructor() {
        this.baseURL = '/api/admin/pedidos';
    }

    async obtenerPedidos(page = 0, size = 10, estado = '') {
        try {
            const params = new URLSearchParams({ page, size });
            if (estado) params.append('estado', estado);

            const response = await fetch(`${this.baseURL}?${params}`);
            return await response.json();
        } catch (error) {
            console.error('Error obteniendo pedidos:', error);
            throw error;
        }
    }

    async actualizarEstadoPedido(id, accion, data = {}) {
        try {
            const response = await fetch(`${this.baseURL}/${id}/estado`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ accion, ...data })
            });
            return await response.json();
        } catch (error) {
            console.error('Error actualizando estado del pedido:', error);
            throw error;
        }
    }
}

// Utilidades para mostrar notificaciones
class NotificationUtils {
    static showSuccess(message) {
        this.showNotification(message, 'success');
    }

    static showError(message) {
        this.showNotification(message, 'danger');
    }

    static showWarning(message) {
        this.showNotification(message, 'warning');
    }

    static showInfo(message) {
        this.showNotification(message, 'info');
    }

    static showNotification(message, type) {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alert.style.top = '20px';
        alert.style.right = '20px';
        alert.style.zIndex = '9999';
        alert.style.minWidth = '300px';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alert);

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (alert.parentNode) {
                alert.parentNode.removeChild(alert);
            }
        }, 5000);
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Solo inicializar el dashboard si estamos en la página correcta
    if (document.getElementById('dashboard-container')) {
        window.dashboardAPI = new DashboardAPI();
    }

    // Hacer las APIs disponibles globalmente
    window.usuariosAPI = new UsuariosAPI();
    window.productosAPI = new ProductosAPI();
    window.pedidosAPI = new PedidosAPI();
    window.NotificationUtils = NotificationUtils;
});
