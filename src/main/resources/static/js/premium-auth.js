document.addEventListener('DOMContentLoaded', () => {
    // Check if GSAP is loaded
    if (typeof gsap === 'undefined') {
        console.warn('GSAP not loaded. Animations disabled.');
        return;
    }

    // Animation Configuration
    const animConfig = {
        duration: 0.8,
        ease: "power3.out",
        y: 30,
        opacity: 0
    };

    // Animate Brand Side
    const brandSide = document.querySelector('.brand-side');
    if (brandSide) {
        // Ensure visibility before animating
        brandSide.style.visibility = 'visible';
        
        gsap.from(brandSide, {
            duration: 1,
            x: -50,
            opacity: 0,
            ease: "power2.out",
            clearProps: "all" // Clear inline styles after animation to prevent issues
        });

        const brandContent = document.querySelectorAll('.brand-content > *');
        if (brandContent.length > 0) {
            gsap.from(brandContent, {
                ...animConfig,
                stagger: 0.2,
                delay: 0.3,
                clearProps: "all"
            });
        }
    }

    // Animate Form Side
    const formWrapper = document.querySelector('.form-wrapper');
    if (formWrapper) {
        formWrapper.style.visibility = 'visible';
        
        gsap.from(formWrapper, {
            duration: 1,
            y: 50,
            opacity: 0,
            ease: "back.out(1.7)",
            delay: 0.2,
            clearProps: "all"
        });

        const formElements = document.querySelectorAll('.form-wrapper > *');
        if (formElements.length > 0) {
            gsap.from(formElements, {
                ...animConfig,
                stagger: 0.1,
                delay: 0.6,
                clearProps: "all"
            });
        }
    }

    // Animate Registration Card
    const regCard = document.querySelector('.eco-registro-card');
    if (regCard) {
        regCard.style.visibility = 'visible';
        
        gsap.from(regCard, {
            duration: 0.8,
            scale: 0.9,
            opacity: 0,
            ease: "back.out(1.2)",
            clearProps: "all"
        });
        
        const regElements = document.querySelectorAll('.eco-registro-card .form-control, .eco-registro-card .btn');
        if (regElements.length > 0) {
            gsap.from(regElements, {
                y: 20,
                opacity: 0,
                duration: 0.5,
                stagger: 0.05,
                delay: 0.4,
                ease: "power2.out",
                clearProps: "all"
            });
        }
    }

    // Input Focus Effects
    const inputs = document.querySelectorAll('.form-control');
    inputs.forEach(input => {
        input.addEventListener('focus', () => {
            if (input.parentElement) {
                gsap.to(input.parentElement, {
                    scale: 1.02,
                    duration: 0.3,
                    ease: "power1.out"
                });
            }
        });

        input.addEventListener('blur', () => {
            if (input.parentElement) {
                gsap.to(input.parentElement, {
                    scale: 1,
                    duration: 0.3,
                    ease: "power1.out"
                });
            }
        });
    });

    // Password Toggle
    const toggleBtns = document.querySelectorAll('.toggle-password');
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.previousElementSibling;
            const icon = this.querySelector('i');
            
            if (input && icon) {
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.classList.remove('bi-eye-slash');
                    icon.classList.add('bi-eye');
                    
                    // Micro-animation for icon
                    gsap.fromTo(icon, {scale: 0.5}, {scale: 1, duration: 0.3, ease: "elastic.out(1, 0.3)"});
                } else {
                    input.type = 'password';
                    icon.classList.remove('bi-eye');
                    icon.classList.add('bi-eye-slash');
                }
            }
        });
    });
});
