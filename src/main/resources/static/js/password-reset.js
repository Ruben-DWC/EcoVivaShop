// Password Reset JavaScript - EcoVivaShop
document.addEventListener('DOMContentLoaded', function() {
    const resetForm = document.getElementById('resetForm');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const togglePasswordBtn = document.getElementById('togglePassword');
    const toggleConfirmPasswordBtn = document.getElementById('toggleConfirmPassword');
    const submitBtn = document.getElementById('submitBtn');
    const passwordStrength = document.getElementById('passwordStrength');
    const strengthBar = document.getElementById('strengthBar');
    const strengthText = document.getElementById('strengthText');

    // Toggle password visibility
    function togglePasswordVisibility(input, button) {
        const type = input.type === 'password' ? 'text' : 'password';
        input.type = type;

        const icon = button.querySelector('i');
        icon.className = type === 'password' ? 'bi bi-eye' : 'bi bi-eye-slash';
    }

    togglePasswordBtn?.addEventListener('click', () => {
        togglePasswordVisibility(passwordInput, togglePasswordBtn);
    });

    toggleConfirmPasswordBtn?.addEventListener('click', () => {
        togglePasswordVisibility(confirmPasswordInput, toggleConfirmPasswordBtn);
    });

    // Password strength checker
    function checkPasswordStrength(password) {
        let strength = 0;
        let feedback = [];

        if (password.length >= 8) strength += 25;
        else feedback.push('Al menos 8 caracteres');

        if (/[a-z]/.test(password)) strength += 25;
        else feedback.push('Letra minúscula');

        if (/[A-Z]/.test(password)) strength += 25;
        else feedback.push('Letra mayúscula');

        if (/[0-9]/.test(password)) strength += 15;
        else feedback.push('Número');

        if (/[^A-Za-z0-9]/.test(password)) strength += 10;
        else feedback.push('Símbolo especial');

        return { strength, feedback };
    }

    function updatePasswordStrength() {
        const password = passwordInput.value;

        if (password.length === 0) {
            passwordStrength.style.display = 'none';
            return;
        }

        passwordStrength.style.display = 'block';
        const { strength, feedback } = checkPasswordStrength(password);

        // Update progress bar
        strengthBar.style.width = strength + '%';

        // Set color based on strength
        if (strength < 25) {
            strengthBar.className = 'progress-bar bg-danger';
            strengthText.textContent = 'Muy débil';
            strengthText.className = 'text-danger';
        } else if (strength < 50) {
            strengthBar.className = 'progress-bar bg-warning';
            strengthText.textContent = 'Débil';
            strengthText.className = 'text-warning';
        } else if (strength < 75) {
            strengthBar.className = 'progress-bar bg-info';
            strengthText.textContent = 'Buena';
            strengthText.className = 'text-info';
        } else {
            strengthBar.className = 'progress-bar bg-success';
            strengthText.textContent = 'Excelente';
            strengthText.className = 'text-success';
        }

        // Show feedback if not strong enough
        if (strength < 75 && feedback.length > 0) {
            strengthText.textContent += ' - Sugerencias: ' + feedback.slice(0, 2).join(', ');
        }
    }

    passwordInput?.addEventListener('input', updatePasswordStrength);

    // Form validation
    resetForm?.addEventListener('submit', function(e) {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // Check password match
        if (password !== confirmPassword) {
            e.preventDefault();
            confirmPasswordInput.setCustomValidity('Las contraseñas no coinciden');
            confirmPasswordInput.reportValidity();
            return;
        } else {
            confirmPasswordInput.setCustomValidity('');
        }

        // Check password strength
        const { strength } = checkPasswordStrength(password);
        if (strength < 25) {
            e.preventDefault();
            alert('La contraseña es muy débil. Por favor, elige una contraseña más segura.');
            return;
        }

        // Disable submit button to prevent double submission
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Actualizando...';
    });

    // Real-time password confirmation validation
    confirmPasswordInput?.addEventListener('input', function() {
        const password = passwordInput.value;
        const confirmPassword = this.value;

        if (password !== confirmPassword) {
            this.setCustomValidity('Las contraseñas no coinciden');
        } else {
            this.setCustomValidity('');
        }
    });

    // Initialize password strength on page load if password has value
    if (passwordInput?.value) {
        updatePasswordStrength();
    }
});