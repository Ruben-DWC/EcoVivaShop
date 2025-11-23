// auth-validation.js - Validación de formularios de autenticación
document.addEventListener('DOMContentLoaded', function() {
    'use strict';

    // Función para validar email
    function validarEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    // Función para mostrar feedback de validación
    function mostrarFeedback(input, esValido, mensaje) {
        const feedback = input.parentNode.querySelector('.invalid-feedback');
        if (feedback) {
            if (!esValido) {
                input.classList.add('is-invalid');
                input.classList.remove('is-valid');
                feedback.textContent = mensaje;
            } else {
                input.classList.remove('is-invalid');
                input.classList.add('is-valid');
            }
        }
    }

    // Validación del formulario de forgot password
    const forgotPasswordForm = document.querySelector('form[action*="forgot-password"]');
    if (forgotPasswordForm) {
        const emailInput = forgotPasswordForm.querySelector('input[name="email"]');
        const submitBtn = forgotPasswordForm.querySelector('#submitBtn');

        if (emailInput) {
            emailInput.addEventListener('blur', function() {
                const email = this.value.trim();
                if (email) {
                    const esValido = validarEmail(email);
                    mostrarFeedback(this, esValido, 'Por favor ingresa un correo electrónico válido.');
                }
            });

            emailInput.addEventListener('input', function() {
                if (this.classList.contains('is-invalid')) {
                    const email = this.value.trim();
                    if (email && validarEmail(email)) {
                        this.classList.remove('is-invalid');
                        this.classList.add('is-valid');
                    }
                }
            });
        }

        // Validación al enviar el formulario
        forgotPasswordForm.addEventListener('submit', function(e) {
            const email = emailInput.value.trim();

            if (!email) {
                e.preventDefault();
                mostrarFeedback(emailInput, false, 'El correo electrónico es obligatorio.');
                emailInput.focus();
                return false;
            }

            if (!validarEmail(email)) {
                e.preventDefault();
                mostrarFeedback(emailInput, false, 'Por favor ingresa un correo electrónico válido.');
                emailInput.focus();
                return false;
            }

            // Deshabilitar botón para evitar envíos múltiples
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Enviando...';
            }
        });
    }

    // Validación del formulario de login
    const loginForm = document.querySelector('form[action*="login"]');
    if (loginForm) {
        const emailInput = loginForm.querySelector('input[name="username"]');
        const passwordInput = loginForm.querySelector('input[name="password"]');
        const submitBtn = loginForm.querySelector('button[type="submit"]');

        if (emailInput) {
            emailInput.addEventListener('blur', function() {
                const email = this.value.trim();
                if (email) {
                    const esValido = validarEmail(email);
                    mostrarFeedback(this, esValido, 'Por favor ingresa un correo electrónico válido.');
                }
            });
        }

        loginForm.addEventListener('submit', function(e) {
            const email = emailInput ? emailInput.value.trim() : '';
            const password = passwordInput ? passwordInput.value.trim() : '';

            let hayErrores = false;

            if (!email) {
                e.preventDefault();
                mostrarFeedback(emailInput, false, 'El correo electrónico es obligatorio.');
                if (!hayErrores) emailInput.focus();
                hayErrores = true;
            } else if (!validarEmail(email)) {
                e.preventDefault();
                mostrarFeedback(emailInput, false, 'Por favor ingresa un correo electrónico válido.');
                if (!hayErrores) emailInput.focus();
                hayErrores = true;
            }

            if (!password) {
                e.preventDefault();
                mostrarFeedback(passwordInput, false, 'La contraseña es obligatoria.');
                if (!hayErrores) passwordInput.focus();
                hayErrores = true;
            }

            if (!hayErrores && submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Iniciando sesión...';
            }
        });
    }

    // Validación del formulario de registro
    const registroForm = document.querySelector('form[action*="registro"]');
    if (registroForm) {
        const camposRequeridos = registroForm.querySelectorAll('input[required]');
        const submitBtn = registroForm.querySelector('button[type="submit"]');

        camposRequeridos.forEach(function(input) {
            input.addEventListener('blur', function() {
                const valor = this.value.trim();
                const tipo = this.type;

                if (!valor) {
                    mostrarFeedback(this, false, 'Este campo es obligatorio.');
                    return;
                }

                if (tipo === 'email' && !validarEmail(valor)) {
                    mostrarFeedback(this, false, 'Por favor ingresa un correo electrónico válido.');
                    return;
                }

                if (tipo === 'password' && valor.length < 6) {
                    mostrarFeedback(this, false, 'La contraseña debe tener al menos 6 caracteres.');
                    return;
                }

                mostrarFeedback(this, true, '');
            });
        });

        registroForm.addEventListener('submit', function(e) {
            let hayErrores = false;
            let primerCampoError = null;

            camposRequeridos.forEach(function(input) {
                const valor = input.value.trim();
                const tipo = input.type;

                if (!valor) {
                    mostrarFeedback(input, false, 'Este campo es obligatorio.');
                    if (!primerCampoError) primerCampoError = input;
                    hayErrores = true;
                    return;
                }

                if (tipo === 'email' && !validarEmail(valor)) {
                    mostrarFeedback(input, false, 'Por favor ingresa un correo electrónico válido.');
                    if (!primerCampoError) primerCampoError = input;
                    hayErrores = true;
                    return;
                }

                if (tipo === 'password' && valor.length < 6) {
                    mostrarFeedback(input, false, 'La contraseña debe tener al menos 6 caracteres.');
                    if (!primerCampoError) primerCampoError = input;
                    hayErrores = true;
                    return;
                }
            });

            if (hayErrores) {
                e.preventDefault();
                if (primerCampoError) primerCampoError.focus();
                return false;
            }

            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Creando cuenta...';
            }
        });
    }

    console.log('✅ [AUTH-VALIDATION] Validación de formularios de autenticación inicializada');
});