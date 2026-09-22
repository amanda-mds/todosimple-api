const API_URL = "http://localhost:8080";

function showAlert(message, type = "error") {
    const el = document.getElementById("alert");
    el.textContent = message;
    el.className = `alert alert-${type} show`;
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("signupForm").addEventListener("submit", async (event) => {
        event.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        try {
            const response = await fetch(`${API_URL}/user`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                const err = await response.json().catch(() => null);
                if (err && err.errors && err.errors.length) {
                    showAlert(err.errors.map((f) => f.message).join(" | "));
                } else {
                    showAlert((err && err.message) || "Não foi possível cadastrar.");
                }
                return;
            }

            showAlert("Cadastro realizado! Redirecionando para o login...", "success");
            setTimeout(() => {
                window.location = "login.html";
            }, 1200);
        } catch (e) {
            showAlert("Não foi possível conectar à API. Ela está rodando em localhost:8080?");
        }
    });
});
