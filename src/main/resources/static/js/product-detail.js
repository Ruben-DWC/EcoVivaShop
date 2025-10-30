/**
 * EcoVivaShop - Script para página de detalle de producto
 * Funcionalidades: Selector de cantidad, validaciones y interacciones
 */

document.addEventListener('DOMContentLoaded', function() {
    initializeQuantitySelector();
    initializeProductActions();
    initializeProductImageEffects();
});

/**
 * Inicializar selector de cantidad
 */
function initializeQuantitySelector() {
    const quantityInput = document.querySelector('.quantity-input');
    const decreaseBtn = document.querySelector('.quantity-btn[data-action="decrease"]');
    const increaseBtn = document.querySelector('.quantity-btn[data-action="increase"]');
    
    if (!quantityInput || !decreaseBtn || !increaseBtn) {
        console.log('Elementos del selector de cantidad no encontrados');
        return;
    }

    // Función para actualizar cantidad
    function updateQuantity(newValue) {
        const min = parseInt(quantityInput.min) || 1;
        const max = parseInt(quantityInput.max) || 99;
        
        newValue = Math.max(min, Math.min(max, newValue));
        quantityInput.value = newValue;
        
        // Actualizar estado de botones
        decreaseBtn.disabled = newValue <= min;
        increaseBtn.disabled = newValue >= max;
        
        // Añadir clase visual para botones deshabilitados
        decreaseBtn.classList.toggle('disabled', newValue <= min);
        increaseBtn.classList.toggle('disabled', newValue >= max);
        
        // Trigger evento personalizado para otros scripts
        quantityInput.dispatchEvent(new CustomEvent('quantityChange', { 
            detail: { quantity: newValue }
        }));
    }

    // Event listeners para botones
    decreaseBtn.addEventListener('click', function(e) {
        e.preventDefault();
        const currentValue = parseInt(quantityInput.value) || 1;
        updateQuantity(currentValue - 1);
    });

    increaseBtn.addEventListener('click', function(e) {
        e.preventDefault();
        const currentValue = parseInt(quantityInput.value) || 1;
        updateQuantity(currentValue + 1);
    });

    // Event listener para input directo
    quantityInput.addEventListener('input', function() {
        const newValue = parseInt(this.value) || 1;
        updateQuantity(newValue);
    });

    // Event listener para teclas
    quantityInput.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowUp') {
            e.preventDefault();
            const currentValue = parseInt(this.value) || 1;
            updateQuantity(currentValue + 1);
        } else if (e.key === 'ArrowDown') {
            e.preventDefault();
            const currentValue = parseInt(this.value) || 1;
            updateQuantity(currentValue - 1);
        }
    });

    // Inicializar estado
    updateQuantity(parseInt(quantityInput.value) || 1);
}

/**
 * Inicializar acciones del producto
 */
function initializeProductActions() {
    const addToCartBtn = document.querySelector('.add-to-cart-btn');
    const buyNowBtn = document.querySelector('.buy-now-btn');
    const quantityInput = document.querySelector('.quantity-input');

    // Agregar al carrito
    if (addToCartBtn) {
        addToCartBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            const productId = this.getAttribute('data-product-id');
            const quantity = parseInt(quantityInput?.value) || 1;
            
            if (!productId) {
                showNotification('Error: ID de producto no encontrado', 'error');
                return;
            }

            // Deshabilitar botón temporalmente
            this.disabled = true;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Agregando...';

            // Realizar petición al servidor
            addToCart(productId, quantity)
                .then(response => {
                    if (response.success) {
                        showNotification('Producto agregado al carrito exitosamente', 'success');
                        updateCartCounter(response.cartCount);
                        
                        // Efecto visual en el botón
                        this.innerHTML = '<i class="fas fa-check"></i> ¡Agregado!';
                        this.classList.add('success-state');
                        
                        setTimeout(() => {
                            this.innerHTML = '<i class="fas fa-cart-plus"></i> Agregar al Carrito';
                            this.classList.remove('success-state');
                            this.disabled = false;
                        }, 2000);
                        
                    } else {
                        throw new Error(response.message || 'Error al agregar producto');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification(error.message, 'error');
                    this.innerHTML = '<i class="fas fa-cart-plus"></i> Agregar al Carrito';
                    this.disabled = false;
                });
        });
    }

    // Comprar ahora
    if (buyNowBtn) {
        buyNowBtn.addEventListener('click', function(e) {
            const productId = this.getAttribute('data-product-id');
            const quantity = parseInt(quantityInput?.value) || 1;
            
            if (!productId) {
                e.preventDefault();
                showNotification('Error: ID de producto no encontrado', 'error');
                return;
            }
            
            // Agregar cantidad a la URL como parámetro
            const url = new URL(this.href, window.location.origin);
            url.searchParams.set('quantity', quantity);
            this.href = url.toString();
        });
    }
}

/**
 * Inicializar efectos de imagen del producto
 */
function initializeProductImageEffects() {
    const productImage = document.querySelector('.product-detail-image');
    
    if (productImage) {
        // Efecto de zoom con mouse
        productImage.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.05)';
        });
        
        productImage.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
        
        // Click para ver imagen completa
        productImage.addEventListener('click', function() {
            openImageModal(this.src, this.alt);
        });
    }
}

/**
 * Función para agregar producto al carrito
 */
async function addToCart(productId, quantity) {
    try {
        const response = await fetch('/client/carrito/agregar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify({
                productoId: productId,
                cantidad: quantity
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
        
    } catch (error) {
        console.error('Error en addToCart:', error);
        throw error;
    }
}

/**
 * Mostrar notificación
 */
function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
            <span>${message}</span>
        </div>
    `;
    
    // Añadir estilos CSS inline si no existen
    if (!document.getElementById('notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            .notification {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                padding: 1rem 1.5rem;
                border-radius: 8px;
                box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
                animation: slideInRight 0.3s ease;
            }
            .notification-success {
                background: rgba(16, 185, 129, 0.9);
                color: white;
                border: 2px solid #10b981;
            }
            .notification-error {
                background: rgba(239, 68, 68, 0.9);
                color: white;
                border: 2px solid #ef4444;
            }
            .notification-info {
                background: rgba(59, 130, 246, 0.9);
                color: white;
                border: 2px solid #3b82f6;
            }
            .notification-content {
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }
            @keyframes slideInRight {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(notification);
    
    // Remover después de 4 segundos
    setTimeout(() => {
        notification.style.animation = 'slideInRight 0.3s ease reverse';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 4000);
}

/**
 * Actualizar contador del carrito
 */
function updateCartCounter(count) {
    // Buscar el contador específico del carrito
    const cartCounter = document.querySelector('.carrito-count, .cart-counter, .badge');
    if (cartCounter) {
        cartCounter.textContent = count;
        cartCounter.setAttribute('data-count', count);
        
        // Añadir clase para animación
        cartCounter.classList.add('updated');
        
        // Añadir o quitar clase has-items según el count
        if (count > 0) {
            cartCounter.classList.add('has-items');
            cartCounter.style.display = 'flex'; // Asegurar que se muestre
        } else {
            cartCounter.classList.remove('has-items');
            cartCounter.style.display = 'none'; // Ocultar si es 0
        }
        
        setTimeout(() => {
            cartCounter.classList.remove('updated');
        }, 500);
    }
}

/**
 * Abrir modal de imagen (función placeholder)
 */
function openImageModal(src, alt) {
    // Esta función se puede expandir para mostrar un modal de imagen completa
    console.log('Abrir modal de imagen:', src, alt);
}

// Añadir estilos CSS para estados de botones
const additionalStyles = document.createElement('style');
additionalStyles.textContent = `
    .quantity-btn.disabled {
        opacity: 0.5;
        cursor: not-allowed;
        transform: none !important;
    }
    
    .add-to-cart-btn.success-state {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
    }
    
    .cart-counter.updated {
        animation: pulse 0.5s ease;
    }
    
    @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.2); }
        100% { transform: scale(1); }
    }
`;
document.head.appendChild(additionalStyles);
