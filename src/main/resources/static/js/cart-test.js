/* 
 * PRUEBA TEMPORAL DEL CONTADOR DEL CARRITO 
 * Este archivo es solo para probar visualmente el contador
 * Se debe eliminar después de confirmar que funciona correctamente
 */

// Simular que hay 3 items en el carrito para prueba visual
document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
        const cartBadge = document.querySelector('.carrito-count');
        if (cartBadge) {
            cartBadge.textContent = '3';
            cartBadge.setAttribute('data-count', '3');
            cartBadge.style.display = 'flex';
            cartBadge.classList.add('has-items');
            console.log('🧪 PRUEBA: Contador del carrito configurado en 3 para prueba visual');
        }
    }, 1000);
});
