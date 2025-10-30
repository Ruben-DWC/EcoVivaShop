/**
 * DROPDOWN USUARIO - VERSIÓN DEFINITIVA
 * Funcionalidad garantizada para el menú dropdown del perfil de usuario
 */

console.log('🚀 CARGANDO DROPDOWN USER SCRIPT v2.0');

document.addEventListener('DOMContentLoaded', function() {
    console.log('🟢 DOM CARGADO - INICIALIZANDO DROPDOWN');
    
    // Búsqueda de elementos
    const button = document.getElementById('userDropdownBtn');
    const menu = document.getElementById('userDropdownMenu');
    
    console.log('🔍 Elementos buscados:');
    console.log('- Button:', button);
    console.log('- Menu:', menu);
    
    // Verificación de existencia
    if (!button) {
        console.error('❌ ERROR: userDropdownBtn NO ENCONTRADO');
        console.log('🔍 Elementos con id que contienen "user":', 
            document.querySelectorAll('[id*="user"]'));
        return;
    }
    
    if (!menu) {
        console.error('❌ ERROR: userDropdownMenu NO ENCONTRADO');
        console.log('🔍 Elementos con id que contienen "dropdown":', 
            document.querySelectorAll('[id*="dropdown"]'));
        return;
    }
    
    console.log('✅ AMBOS ELEMENTOS ENCONTRADOS CORRECTAMENTE');
    
    // Estado inicial del menú
    console.log('📊 ESTADO INICIAL:');
    console.log('- Classes:', Array.from(menu.classList));
    console.log('- Display:', getComputedStyle(menu).display);
    console.log('- Visibility:', getComputedStyle(menu).visibility);
    console.log('- Z-Index:', getComputedStyle(menu).zIndex);
    console.log('- Position:', getComputedStyle(menu).position);
    
    // Función para mostrar menú
    function showMenu() {
        console.log('🟢 === EJECUTANDO SHOW MENU ===');
        
        // Agregar clase show
        menu.classList.add('show');
        console.log('✅ Clase "show" agregada');
        
        // Verificar estado después de aplicar
        setTimeout(() => {
            const computedStyle = getComputedStyle(menu);
            console.log('📊 ESTADO DESPUÉS DE MOSTRAR:');
            console.log('- Classes:', Array.from(menu.classList));
            console.log('- Display:', computedStyle.display);
            console.log('- Visibility:', computedStyle.visibility);
            console.log('- Z-Index:', computedStyle.zIndex);
            console.log('- Opacity:', computedStyle.opacity);
            
            // Verificar si realmente es visible
            const rect = menu.getBoundingClientRect();
            console.log('📐 Dimensiones del menú:', {
                width: rect.width,
                height: rect.height,
                top: rect.top,
                left: rect.left,
                right: rect.right,
                bottom: rect.bottom
            });
            
            if (rect.width === 0 || rect.height === 0) {
                console.warn('⚠️ ADVERTENCIA: El menú tiene dimensiones 0, puede no ser visible');
            } else {
                console.log('✅ Menú tiene dimensiones válidas - debería ser visible');
            }
        }, 100);
    }
    
    // Función para ocultar menú
    function hideMenu() {
        console.log('🔴 === EJECUTANDO HIDE MENU ===');
        menu.classList.remove('show');
        console.log('✅ Clase "show" removida');
    }
    
    // Event listener para el botón
    button.addEventListener('click', function(e) {
        console.log('👆 === CLICK EN BOTÓN DETECTADO ===');
        console.log('- Evento:', e);
        console.log('- Target:', e.target);
        console.log('- CurrentTarget:', e.currentTarget);
        
        // Prevenir comportamientos por defecto
        e.preventDefault();
        e.stopPropagation();
        
        // Verificar estado actual
        const isCurrentlyVisible = menu.classList.contains('show');
        console.log('- Estado actual (tiene clase show):', isCurrentlyVisible);
        
        // Toggle del menú
        if (isCurrentlyVisible) {
            console.log('➡️ Menú visible - procediendo a ocultar');
            hideMenu();
        } else {
            console.log('➡️ Menú oculto - procediendo a mostrar');
            showMenu();
        }
    });
    
    // Click fuera para cerrar
    document.addEventListener('click', function(e) {
        const isClickOnButton = button.contains(e.target);
        const isClickOnMenu = menu.contains(e.target);
        
        if (!isClickOnButton && !isClickOnMenu) {
            console.log('👆 Click fuera detectado - cerrando menú');
            hideMenu();
        }
    });
    
    // Tecla Escape para cerrar
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            console.log('⌨️ Tecla Escape presionada - cerrando menú');
            hideMenu();
        }
    });
    
    // Test de funcionalidad
    console.log('🧪 EJECUTANDO TESTS DE FUNCIONALIDAD...');
    
    // Test 1: Verificar que los elementos existen en el DOM
    console.log('Test 1 - Elementos en DOM:', {
        buttonInDOM: document.contains(button),
        menuInDOM: document.contains(menu)
    });
    
    // Test 2: Verificar clases CSS aplicadas
    console.log('Test 2 - CSS aplicado:', {
        buttonHasClasses: button.className,
        menuHasClasses: menu.className
    });
    
    console.log('🎉 ===== DROPDOWN INICIALIZADO COMPLETAMENTE =====');
    
    // Exponer funciones globalmente para debug
    window.debugDropdown = {
        showMenu: showMenu,
        hideMenu: hideMenu,
        button: button,
        menu: menu,
        toggle: () => {
            if (menu.classList.contains('show')) {
                hideMenu();
            } else {
                showMenu();
            }
        }
    };
    
    console.log('🛠️ Funciones debug disponibles en window.debugDropdown');
});
