window.onload = () => {
    const timerStart = parseInt(document.getElementById("code-validity").value, 10);
    const timerSpan = document.getElementById("timer");
    const codeExpiredSpan = document.getElementById("code-expired");
    const submitBtn = document.getElementById("two-factory-submit");
    let remainingSeconds = isNaN(timerStart) ? 0 : timerStart;

    const timerDisplay = document.getElementById("timer-countdown");

    function updateTimerDisplay() {
        const minutes = Math.floor(remainingSeconds / 60);
        const seconds = remainingSeconds % 60;

        timerDisplay.textContent =
            String(minutes) + 'min: ' +
            String(seconds).padStart(2, '0') + 'sec';
    }

    updateTimerDisplay();

    const interval = setInterval(() => {
        if (remainingSeconds > 0) {
            remainingSeconds--;
            submitBtn.disabled = false;
            updateTimerDisplay();
        } else {
            clearInterval(interval);
            submitBtn.disabled = true;
            timerSpan.hidden = true;
            codeExpiredSpan.hidden = false;
            codeExpiredSpan.classList.add("vol-field-error");
        }
    }, 1000);
}