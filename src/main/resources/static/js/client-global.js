/**
 * EcoVivaShop - Script global para funcionalidades del cliente
 * Funcionalidades: Dropdown usuario, paginación, navegación general
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('🔧 Cliente Global JS cargado');
    
    // Esperar un poco más para asegurar que Bootstrap esté completamente cargado
    setTimeout(function() {
        initializeUserDropdown();
        initializePagination();
        initializeNavigation();
        initializeGlobalEffects();
        loadCartCounter(); // Cargar contador del carrito
    }, 200);
});

/**
 * Inicializar dropdown del usuario - Versión ultra simplificada
 */
function initializeUserDropdown() {
    console.log('🔧 Inicializando dropdown del usuario...');
    
    // Solo manejar el evento de logout especialmente, dejar el resto a Bootstrap
    setTimeout(function() {
        const logoutLink = document.querySelector('a[href="/logout"]');
        if (logoutLink) {
            console.log('✅ Link de logout encontrado, configurando...');
            
            logoutLink.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                console.log('🚪 Procesando logout...');
                
                // Crear form para logout
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/logout';
                
                // Agregar token CSRF si está disponible
                const csrfToken = document.querySelector('meta[name="_csrf"]');
                if (csrfToken) {
                    const csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    csrfInput.value = csrfToken.getAttribute('content');
                    form.appendChild(csrfInput);
                }
                
                document.body.appendChild(form);
                form.submit();
            });
        }
        
        // Log para debug
        const dropdownItems = document.querySelectorAll('.dropdown-item');
        console.log(`✅ ${dropdownItems.length} dropdown items encontrados`);
        
        dropdownItems.forEach((item, index) => {
            const href = item.getAttribute('href');
            console.log(`   ${index + 1}. ${item.textContent.trim()} -> ${href}`);
        });
        
    }, 300);
}



/**
 * Inicializar paginación mejorada
 */
function initializePagination() {
    const paginationContainer = document.querySelector('.pagination');
    const pageLinks = document.querySelectorAll('.page-link');

    if (!paginationContainer) {
        console.log('Paginación no encontrada');
        return;
    }

    pageLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            
            // Prevenir navegación a URLs con hash
            if (href && href.includes('#') && !href.startsWith('#')) {
                e.preventDefault();
                
                // Extraer el número de página de la URL
                const urlParts = href.split('#');
                const cleanUrl = urlParts[0];
                
                // Navegar a la URL limpia
                window.location.href = cleanUrl;
            }
        });
    });

    // Mejorar accesibilidad
    const activeLink = paginationContainer.querySelector('.page-item.active .page-link');
    if (activeLink) {
        activeLink.setAttribute('aria-current', 'page');
    }
}

/**
 * Inicializar navegación general
 */
function initializeNavigation() {
    // Mejorar enlaces de navegación
    const navLinks = document.querySelectorAll('.nav-link, .navbar-nav .nav-link');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Añadir efecto visual
            this.style.transform = 'translateY(-2px)';
            setTimeout(() => {
                this.style.transform = '';
            }, 200);
        });
    });

    // Marcar link activo
    const currentPath = window.location.pathname;
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.includes(href) && href !== '/') {
            link.classList.add('active');
        }
    });
}

/**
 * Inicializar efectos globales
 */
function initializeGlobalEffects() {
    // Efecto smooth scroll
    const links = document.querySelectorAll('a[href^="#"]');
    links.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href === '#' || href === '') return;
            
            const target = document.querySelector(href);
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Efecto de loading en formularios
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitButton = form.querySelector('button[type="submit"], input[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
                const originalText = submitButton.textContent || submitButton.value;
                
                if (submitButton.tagName === 'BUTTON') {
                    submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
                } else {
                    submitButton.value = 'Procesando...';
                }
                
                // Restaurar después de 10 segundos como fallback
                setTimeout(() => {
                    submitButton.disabled = false;
                    if (submitButton.tagName === 'BUTTON') {
                        submitButton.textContent = originalText;
                    } else {
                        submitButton.value = originalText;
                    }
                }, 10000);
            }
        });
    });

    // Mejorar botones con efectos hover
    const buttons = document.querySelectorAll('.btn, .client-btn-primary, .client-btn-secondary');
    buttons.forEach(button => {
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });
        
        button.addEventListener('mouseleave', function() {
            this.style.transform = '';
        });
        
        button.addEventListener('mousedown', function() {
            this.style.transform = 'translateY(0px)';
        });
        
        button.addEventListener('mouseup', function() {
            this.style.transform = 'translateY(-2px)';
        });
    });

    // Lazy loading para imágenes
    const images = document.querySelectorAll('img[data-src]');
    if (images.length > 0 && 'IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });

        images.forEach(img => imageObserver.observe(img));
    }
}

/**
 * Función de utilidad para mostrar alerts personalizados
 */
function showAlert(message, type = 'info', duration = 4000) {
    const alertContainer = document.createElement('div');
    alertContainer.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertContainer.style.cssText = `
        top: 20px;
        right: 20px;
        z-index: 9999;
        max-width: 400px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.1);
    `;
    
    alertContainer.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertContainer);
    
    // Auto-remove después del duration especificado
    setTimeout(() => {
        if (alertContainer.parentNode) {
            alertContainer.classList.remove('show');
            setTimeout(() => {
                if (alertContainer.parentNode) {
                    alertContainer.parentNode.removeChild(alertContainer);
                }
            }, 150);
        }
    }, duration);
    
    return alertContainer;
}

/**
 * Función para validar formularios
 */
function validateForm(form) {
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

// Exponer funciones globalmente para uso en otras partes
window.EcoVivaShop = {
    showAlert,
    validateForm,
    initializeUserDropdown,
    initializePagination
};

// Añadir estilos CSS adicionales para mejorar la UX
const globalStyles = document.createElement('style');
globalStyles.textContent = `
    /* Mejoras para dropdown */
    .dropdown-menu.show {
        display: block !important;
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
        transition: all 0.2s ease;
    }
    
    .dropdown-menu {
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.2s ease;
    }
    
    /* Mejoras para botones */
    .btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
    
    /* Loading state para formularios */
    .form-loading {
        position: relative;
        pointer-events: none;
        opacity: 0.7;
    }
    
    .form-loading::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        width: 20px;
        height: 20px;
        margin: -10px 0 0 -10px;
        border: 2px solid #27c9b5;
        border-radius: 50%;
        border-top-color: transparent;
        animation: spin 1s linear infinite;
    }
    
    @keyframes spin {
        to { transform: rotate(360deg); }
    }
    
    /* Mejoras visuales */
    .nav-link.active {
        background: rgba(255, 255, 255, 0.2) !important;
        border-radius: 6px;
    }
    
    .lazy {
        opacity: 0;
        transition: opacity 0.3s;
    }
    
    .lazy:not([data-src]) {
        opacity: 1;
    }
`;
document.head.appendChild(globalStyles);

/**
 * Utilidad para realizar peticiones AJAX con token CSRF automático
 * @param {string} url - URL del endpoint
 * @param {object} options - Opciones de la petición (method, body, etc.)
 * @returns {Promise} - Promesa de la petición fetch
 */
window.fetchWithCSRF = function(url, options = {}) {
    // Configurar headers por defecto
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        ...options.headers
    };
    
    // Agregar token CSRF automáticamente
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
        
        if (csrfToken && csrfHeader) {
            headers[csrfHeader.getAttribute('content')] = csrfToken.getAttribute('content');
        }
    } catch (error) {
        console.warn('No se pudo obtener token CSRF:', error);
    }
    
    // Realizar petición con headers actualizados
    return fetch(url, {
        ...options,
        headers: headers
    });
};

/**
 * Funciones de utilidad para obtener tokens CSRF
 */
window.getCsrfToken = function() {
    const token = document.querySelector('meta[name="_csrf"]');
    return token ? token.getAttribute('content') : null;
};

window.getCsrfHeader = function() {
    const header = document.querySelector('meta[name="_csrf_header"]');
    return header ? header.getAttribute('content') : 'X-CSRF-TOKEN';
};

/**
 * Cargar contador del carrito desde el servidor
 */
function loadCartCounter() {
    console.log('� Cargando contador del carrito...');
    
    fetch('/client/carrito/count', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            [getCsrfHeader()]: getCsrfToken()
        },
        credentials: 'same-origin'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al obtener contador del carrito');
        }
        return response.json();
    })
    .then(data => {
        console.log('🛒 Contador del carrito obtenido:', data.count);
        if (typeof updateCartCounter === 'function') {
            updateCartCounter(data.count);
        } else if (typeof window.updateCartCounter === 'function') {
            window.updateCartCounter(data.count);
        } else {
            // Actualizar directamente si las funciones no están disponibles
            const cartBadge = document.querySelector('.carrito-count');
            if (cartBadge) {
                cartBadge.textContent = data.count;
                cartBadge.setAttribute('data-count', data.count);
                
                if (data.count > 0) {
                    cartBadge.style.display = 'flex';
                    cartBadge.classList.add('has-items');
                } else {
                    cartBadge.style.display = 'none';
                    cartBadge.classList.remove('has-items');
                }
            }
        }
    })
    .catch(error => {
        console.error('❌ Error cargando contador del carrito:', error);
    });
}

// Hacer disponible globalmente
window.loadCartCounter = loadCartCounter;

console.log('�🔧 EcoVivaShop Client Global - Funciones CSRF y carrito disponibles globalmente');
