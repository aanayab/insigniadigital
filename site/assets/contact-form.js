(() => {
    "use strict";

    const form = document.getElementById("wpforms-form-71");
    if (!form || form.dataset.contactApiReady === "true") {
        return;
    }
    form.dataset.contactApiReady = "true";

    const submitButton = document.getElementById("wpforms-submit-71");
    const status = document.createElement("p");
    status.className = "insignia-contact-status";
    status.setAttribute("role", "status");
    status.setAttribute("aria-live", "polite");
    status.hidden = true;
    form.querySelector(".wpforms-submit-container")?.append(status);

    let submitting = false;

    form.addEventListener("submit", async event => {
        event.preventDefault();
        event.stopImmediatePropagation();

        if (submitting || !form.reportValidity()) {
            return;
        }

        submitting = true;
        submitButton.disabled = true;
        submitButton.textContent = "Enviando...";
        showStatus("Enviando tu información…", "pending");

        const payload = {
            firstName: value("wpforms-71-field_1"),
            lastName: value("wpforms-71-field_1-last"),
            email: value("wpforms-71-field_2"),
            message: value("wpforms-71-field_3"),
            website: value("wpforms-71-field_4")
        };

        try {
            const response = await fetch("/api/contact", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
            const body = await response.json().catch(() => ({}));

            if (response.ok && body.status === "sent") {
                form.reset();
                showStatus("¡Gracias! Recibimos tus datos. Nos pondremos en contacto contigo pronto.", "success");
                return;
            }
            if (response.status === 429) {
                showStatus("Has realizado varios intentos. Espera unos minutos antes de volver a enviar.", "error");
                return;
            }
            if (response.status === 400) {
                showStatus("Revisa los campos del formulario e inténtalo nuevamente.", "error");
                return;
            }
            throw new Error("contact_api_unavailable");
        } catch (error) {
            showStatus("No fue posible enviar tu información. Inténtalo nuevamente o contáctanos por WhatsApp.", "error");
        } finally {
            submitting = false;
            submitButton.disabled = false;
            submitButton.textContent = "Enviar";
        }
    }, true);

    function value(id) {
        return String(document.getElementById(id)?.value || "").trim();
    }

    function showStatus(message, state) {
        status.textContent = message;
        status.dataset.state = state;
        status.hidden = false;
    }
})();
