document.addEventListener('DOMContentLoaded', () => {
  'use strict';

  // Submit spinner + disable button on submit
  const form = document.getElementById('loginForm');
  if (form) {
    form.addEventListener('submit', (e) => {
      // If invalid, highlight validation but do not show spinner
      if (!form.checkValidity()) {
        return;
      }
      const submitBtn = form.querySelector('button[type=submit]');
      if (!submitBtn) return;
      // Prevent double submit visual
      submitBtn.disabled = true;
      // Add spinner
      let spinner = submitBtn.querySelector('.btn-spinner');
      if (!spinner) {
        spinner = document.createElement('span');
        spinner.classList.add('btn-spinner');
        submitBtn.prepend(spinner);
      } else {
        spinner.classList.remove('hidden');
      }
    });
  }

  // Toggle password accessibility
  const togglePass = document.getElementById('togglePass');
  const passInput = document.getElementById('password');
  if (togglePass && passInput) {
    togglePass.addEventListener('click', () => {
      const isPwd = passInput.type === 'password';
      passInput.type = isPwd ? 'text' : 'password';
      togglePass.setAttribute('aria-pressed', isPwd ? 'true' : 'false');
      const icon = togglePass.querySelector('i');
      if (icon) icon.classList.toggle('bi-eye');
      if (icon) icon.classList.toggle('bi-eye-slash');
    });
  }

  // If there's an error alert, animate it (shake), then focus the first input
  const errorAlert = document.querySelector('.alert.alert-danger');
  if (errorAlert) {
    // Add a brief shake on the card
    const card = errorAlert.closest('.card');
    if (card) {
      card.classList.add('shake');
      setTimeout(() => { card.classList.remove('shake'); }, 600);
    }
    const firstInput = form ? form.querySelector('input[name="username"]') : null;
    if (firstInput) firstInput.focus();
  }

  // Floating label: ensure empty placeholder to allow css :placeholder-shown to work
  const floatingEls = document.querySelectorAll('.floating-label input');
  floatingEls.forEach((el) => {
    if (!el.hasAttribute('placeholder') || el.getAttribute('placeholder') === '') {
      el.setAttribute('placeholder', ' ');
    }
  });

  // Respect prefers-reduced-motion
  const prefersReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (prefersReduced) {
    const styleSheets = document.querySelectorAll('link[rel=stylesheet], style');
    // Optional: do something to reduce animation; we keep simple CSS checks
  }

});
