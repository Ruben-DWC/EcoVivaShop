// Admin Modern Interactions - EcoVivaShop
// Script para mejorar las interacciones de las vistas administrativas

document.addEventListener('DOMContentLoaded', function() {
    
    // Mejorar action cards con efectos de hover dinámicos
    enhanceActionCards();
    
    // Agregar efectos de animación a las métricas
    animateMetricCards();
    
    // Mejorar interacciones de botones
    enhanceButtonInteractions();
    
    // Agregar efectos de timeline
    enhanceTimeline();
    
    // Efectos de parallax suave en el fondo
    addParallaxEffects();
});

function enhanceActionCards() {
    const actionCards = document.querySelectorAll('.action-card');
    
    actionCards.forEach(card => {
        // Agregar evento de mouse enter
        card.addEventListener('mouseenter', function() {
            // Asegurar que los colores se apliquen correctamente
            this.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
            
            // Agregar efectos específicos según el tipo de botón
            if (this.classList.contains('btn-outline-info')) {
                this.style.background = 'linear-gradient(135deg, #17a2b8, #138496)';
                this.style.borderColor = '#17a2b8';
                this.style.color = 'white';
                this.style.boxShadow = '0 12px 35px rgba(23, 162, 184, 0.5)';
            } else if (this.classList.contains('btn-outline-purple')) {
                this.style.background = 'linear-gradient(135deg, #6f42c1, #563d7c)';
                this.style.borderColor = '#6f42c1';
                this.style.color = 'white';
                this.style.boxShadow = '0 12px 35px rgba(111, 66, 193, 0.5)';
            } else if (this.classList.contains('btn-outline-success')) {
                this.style.background = 'linear-gradient(135deg, #28a745, #20c997)';
                this.style.borderColor = '#28a745';
                this.style.color = 'white';
                this.style.boxShadow = '0 12px 35px rgba(40, 167, 69, 0.5)';
            } else if (this.classList.contains('btn-outline-primary')) {
                this.style.background = 'linear-gradient(135deg, #007bff, #0056b3)';
                this.style.borderColor = '#007bff';
                this.style.color = 'white';
                this.style.boxShadow = '0 12px 35px rgba(0, 123, 255, 0.5)';
            } else if (this.classList.contains('btn-outline-warning')) {
                this.style.background = 'linear-gradient(135deg, #ffc107, #e0a800)';
                this.style.borderColor = '#ffc107';
                this.style.color = '#212529';
                this.style.boxShadow = '0 12px 35px rgba(255, 193, 7, 0.5)';
            }
            
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });
        
        // Agregar evento de mouse leave
        card.addEventListener('mouseleave', function() {
            this.style.background = '';
            this.style.borderColor = '';
            this.style.color = '';
            this.style.boxShadow = '';
            this.style.transform = '';
        });
        
        // Efecto de click con ripple
        card.addEventListener('click', function(e) {
            createRippleEffect(e, this);
        });
    });
}

function animateMetricCards() {
    const metricCards = document.querySelectorAll('.metric-card, .metric-card-modern');
    
    // Observador de intersección para animaciones cuando entran en viewport
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, { threshold: 0.1 });
    
    metricCards.forEach((card, index) => {
        // Configurar estado inicial
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        card.style.transition = `all 0.6s ease ${index * 0.1}s`;
        
        observer.observe(card);
        
        // Efecto de hover mejorado
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
            this.style.boxShadow = '0 20px 40px rgba(0,0,0,0.2)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
            this.style.boxShadow = '';
        });
    });
}

function enhanceButtonInteractions() {
    const buttons = document.querySelectorAll('.btn-modern, .btn');
    
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            createRippleEffect(e, this);
        });
        
        // Efecto de hover suave
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });
        
        button.addEventListener('mouseleave', function() {
            if (!this.classList.contains('action-card')) {
                this.style.transform = '';
            }
        });
    });
}

function enhanceTimeline() {
    const timelineItems = document.querySelectorAll('.timeline-item, .timeline-item-modern');
    
    timelineItems.forEach((item, index) => {
        // Animación de entrada
        setTimeout(() => {
            item.style.opacity = '1';
            item.style.transform = 'translateX(0)';
        }, index * 200);
        
        // Configurar estado inicial
        item.style.opacity = '0';
        item.style.transform = 'translateX(-20px)';
        item.style.transition = 'all 0.5s ease';
        
        // Efecto de hover
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(10px) scale(1.02)';
            this.style.boxShadow = '0 10px 25px rgba(0,0,0,0.15)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0) scale(1)';
            this.style.boxShadow = '';
        });
    });
}

function createRippleEffect(event, element) {
    const ripple = document.createElement('div');
    const rect = element.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;
    
    ripple.style.cssText = `
        position: absolute;
        width: ${size}px;
        height: ${size}px;
        left: ${x}px;
        top: ${y}px;
        background: rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        transform: scale(0);
        animation: ripple-animation 0.6s ease-out;
        pointer-events: none;
        z-index: 1000;
    `;
    
    element.style.position = 'relative';
    element.style.overflow = 'hidden';
    element.appendChild(ripple);
    
    setTimeout(() => {
        ripple.remove();
    }, 600);
}

function addParallaxEffects() {
    window.addEventListener('scroll', function() {
        const scrolled = window.pageYOffset;
        const parallaxElements = document.querySelectorAll('.metric-card, .modern-card');
        
        parallaxElements.forEach((element, index) => {
            const speed = 0.5 + (index * 0.1);
            const yPos = -(scrolled * speed / 100);
            element.style.transform = `translate3d(0, ${yPos}px, 0)`;
        });
    });
}

// Agregar estilos de animación CSS dinámicamente
const rippleStyle = document.createElement('style');
rippleStyle.textContent = `
    @keyframes ripple-animation {
        to {
            transform: scale(2);
            opacity: 0;
        }
    }
    
    .admin-fade-in {
        animation: fadeInUp 0.8s ease-out forwards;
    }
    
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(40px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    .hover-glow {
        transition: all 0.3s ease;
    }
    
    .hover-glow:hover {
        filter: drop-shadow(0 0 10px rgba(40, 167, 69, 0.5));
    }
`;
document.head.appendChild(rippleStyle);

// Función para mejorar la accesibilidad
function enhanceAccessibility() {
    const interactiveElements = document.querySelectorAll('.action-card, .btn, .metric-card');
    
    interactiveElements.forEach(element => {
        // Agregar soporte para navegación por teclado
        element.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                this.click();
            }
        });
        
        // Asegurar que elementos sean focusables
        if (!element.hasAttribute('tabindex')) {
            element.setAttribute('tabindex', '0');
        }
    });
}

// Ejecutar mejoras de accesibilidad
setTimeout(enhanceAccessibility, 100);
