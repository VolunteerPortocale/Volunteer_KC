initDropdown(document.getElementById('langSelect'));
handleInputs();

function initDropdown(dropdown) {
    // Skip bellow if no language dropdown present
    if (!dropdown) return
    const modal = document.createElement('div');
    modal.classList.add('vol-dropdown-modal');
    // close dropdown with click on modal and remove this
    modal.onclick = () => {
        dropdown.classList.remove('vol-dropdown-active');
        dropdown.removeChild(modal);
    };

    // handle toggle dropdown
    dropdown.querySelector('.vol-dropdown-value').onclick = () => {
        dropdown.classList.toggle('vol-dropdown-active');
        dropdown[dropdown.classList.contains('vol-dropdown-active') ? 'append' : 'removeChild'](modal);
    }
}

function handleInputs() {
    const removeServerErrors = (e) => {
        const parent = e.target.parentElement.parentElement.parentElement;

        parent.classList.remove('vol-field-with-error');
        e.target.removeEventListener('input', removeServerErrors);

        // remove server error texts
        parent.querySelectorAll('.vol-field-error').forEach((err) => {
            if (!err.classList.contains('vol-local-error')) parent.querySelector('.vol-field-errors').removeChild(err);
        });
    };

    document.querySelectorAll('input').forEach((inp) => {
        // disable browser tooltips on invalid inputs
        inp.addEventListener('invalid', (event) => event.preventDefault());

        // handle server errors and remove on update
        inp.addEventListener('input', removeServerErrors);
    });
}
function initPasswordToggle() {
  const toggle = document.getElementById("vol-password-toggle");
  const input = document.getElementById("password");
  if (!toggle || !input) return;

  toggle.addEventListener("click", () => {
    const show = input.type === "password";
    input.type = show ? "text" : "password";
    toggle.setAttribute("aria-pressed", String(show));
    toggle.setAttribute("aria-label", show ? toggle.dataset.labelHide : toggle.dataset.labelShow);
    input.focus();
  });
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initPasswordToggle);
} else {
  initPasswordToggle();
}
