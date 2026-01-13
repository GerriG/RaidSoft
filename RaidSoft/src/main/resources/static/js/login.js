// ============================================================
// ARCHIVO: static/js/login.js
// FUNCIÓN GLOBAL PARA ABRIR MODAL
// ============================================================

// 1. FUNCIÓN GLOBAL: Debe estar al inicio del archivo para ser visible por el onclick en el HTML.
function openResetModal(e) {
    // Usamos 'e.preventDefault()' solo si el evento viene del tag <a> (para evitar saltos de página)
    if (e) {
        e.preventDefault(); 
    }
    
    const modal = document.getElementById('resetModal');
    const formReset = document.getElementById('formReset');

    if (modal) {
        modal.classList.add('active');
        document.getElementById('modalSuccess').style.display = 'none';
        document.getElementById('modalError').style.display = 'none';
        
        if (formReset) {
            formReset.reset(); // Limpia el formulario al abrir
        }
    }
}

// 2. LÓGICA DE EVENTOS (Se ejecuta SOLO cuando el HTML está completamente cargado)
document.addEventListener('DOMContentLoaded', function() {
    
    // --- VARIABLES DE LOGIN ---
    const formLogin = document.getElementById('formLogin');
    const btnLogin = document.getElementById('btnLogin');
    const alertError = document.getElementById('alertError');
    const alertDisabled = document.getElementById('alertDisabled');
    
    // --- Lógica del Modal (Cerrar) ---
    const modal = document.getElementById('resetModal');
    const closeModalButton = document.getElementById('closeModal');
    
    if (closeModalButton) {
        closeModalButton.addEventListener('click', () => modal.classList.remove('active'));
    }
    
    if (modal) {
        modal.addEventListener('click', (e) => {
            const modalCard = document.querySelector('.modal-card');
            if (!modalCard.contains(e.target)) {
                modal.classList.remove('active');
            }
        });
    }

    // --- 3. LÓGICA DE LOGIN (AJAX) ---
    if (formLogin && btnLogin) {
        formLogin.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const params = new URLSearchParams(formData); 
            const originalText = btnLogin.innerText;

            btnLogin.innerText = 'Verificando...';
            btnLogin.disabled = true;

            alertError.style.display = 'none';
            alertDisabled.style.display = 'none';

            fetch('/login', { method: 'POST', body: params })
            .then(r => {
                 if (!r.ok) throw r;
                 return r.json();
            })
            .then(data => {
                if (data.status === 'success') {
                    window.location.href = data.redirectUrl; 
                }
            })
            .catch(r => {
                r.json().then(errorData => {
                    btnLogin.innerText = originalText;
                    btnLogin.disabled = false;
                    
                    if (errorData.message && errorData.message.includes('deshabilitada'))
                         document.getElementById('alertDisabled').style.display = 'flex';
                    else
                         document.getElementById('alertError').style.display = 'flex';
                }).catch(() => {
                    btnLogin.innerText = originalText;
                    btnLogin.disabled = false;
                    document.getElementById('alertError').style.display = 'flex';
                });
            });
        });
    }

    // --- 4. LÓGICA DE RESTABLECIMIENTO (AJAX) ---
    const formReset = document.getElementById('formReset');

    if (formReset) {
        formReset.addEventListener('submit', function(e) {
            e.preventDefault();

            const params = new URLSearchParams(new FormData(this));
            const btn = document.getElementById('btnReset');
            const original = btn.innerText;

            btn.innerText = 'Procesando...';
            btn.disabled = true;

            document.getElementById('modalSuccess').style.display = 'none';
            document.getElementById('modalError').style.display = 'none';

            fetch('/api/reset-password', { method: 'POST', body: params })
            .then(r => r.json())
            .then(data => {
                btn.innerText = original;
                btn.disabled = false;
                if (data.status === 'success') {
                    document.getElementById('modalSuccess').style.display = 'flex';
                    setTimeout(() => modal.classList.remove('active'), 2000);
                } else {
                    document.getElementById('modalError').style.display = 'flex';
                }
            })
            .catch(() => {
                btn.innerText = original;
                btn.disabled = false;
                document.getElementById('modalError').style.display = 'flex';
            });
        });
    }
});