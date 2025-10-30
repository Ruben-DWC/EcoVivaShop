// Utilidades para avatares de administradores
function generarAvatar(nombre, apellido) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    
    canvas.width = 120;
    canvas.height = 120;
    
    // Colores de fondo aleatorios pero consistentes
    const colors = [
        '#198754', '#0d6efd', '#ffc107', '#dc3545', '#6f42c1', 
        '#20c997', '#fd7e14', '#e83e8c', '#6c757d', '#17a2b8'
    ];
    
    const name = (nombre + apellido).toLowerCase();
    const colorIndex = name.charCodeAt(0) % colors.length;
    
    // Fondo circular
    ctx.fillStyle = colors[colorIndex];
    ctx.beginPath();
    ctx.arc(60, 60, 60, 0, 2 * Math.PI);
    ctx.fill();
    
    // Iniciales
    ctx.fillStyle = '#ffffff';
    ctx.font = 'bold 36px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    
    const iniciales = (nombre.charAt(0) + apellido.charAt(0)).toUpperCase();
    ctx.fillText(iniciales, 60, 60);
    
    return canvas.toDataURL();
}

function actualizarAvatar(nombre, apellido, elementoImg) {
    if (elementoImg) {
        const avatarUrl = generarAvatar(nombre || 'A', apellido || 'D');
        elementoImg.src = avatarUrl;
    }
}

// Generar avatar por defecto al cargar la página
document.addEventListener('DOMContentLoaded', function() {
    const avatares = document.querySelectorAll('.profile-image, .admin-avatar');
    avatares.forEach(img => {
        if (img.src.includes('admin-avatar.jpg')) {
            actualizarAvatar('Admin', 'User', img);
        }
    });
});

// Función para previsualizar imagen subida
function previsualizarImagen(input, previewElement) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            previewElement.src = e.target.result;
        };
        
        reader.readAsDataURL(input.files[0]);
    }
}

// Validar tipo y tamaño de archivo
function validarArchivoImagen(file) {
    const tiposPermitidos = ['image/jpeg', 'image/png', 'image/gif'];
    const tamañoMaximo = 2 * 1024 * 1024; // 2MB
    
    if (!tiposPermitidos.includes(file.type)) {
        throw new Error('Solo se permiten archivos JPG, PNG o GIF');
    }
    
    if (file.size > tamañoMaximo) {
        throw new Error('El archivo no puede superar los 2MB');
    }
    
    return true;
}

// Exportar funciones para uso global
window.AdminAvatarUtils = {
    generarAvatar,
    actualizarAvatar,
    previsualizarImagen,
    validarArchivoImagen
};
