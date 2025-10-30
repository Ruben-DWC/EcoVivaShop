// Script para limpiar completamente el caché del carrito
// Ejecutar en la consola del navegador (F12 > Console) si hay problemas

console.log('🧹 Iniciando limpieza completa del carrito...');

// Limpiar localStorage
localStorage.removeItem('cart');
localStorage.removeItem('carrito');
localStorage.removeItem('cartItems');

// Limpiar sessionStorage
sessionStorage.removeItem('cart');
sessionStorage.removeItem('carrito');
sessionStorage.removeItem('cartItems');

// Limpiar cookies relacionadas con el carrito
document.cookie = 'cart=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
document.cookie = 'carrito=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

// Mostrar resultado
console.log('✅ Limpieza completada');
console.log('📊 Estado del localStorage:', localStorage.getItem('cart'));
console.log('🔄 Recarga la página para aplicar los cambios');

// Opcional: recargar automáticamente
if (confirm('¿Quieres recargar la página automáticamente?')) {
    location.reload();
}
