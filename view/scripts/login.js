const API_URL = "http://localhost:8080";

function showAlert(message, type = "error") {
    const el = document.getElementById("alert");
    el.textContent = message;
    el.className = `alert alert-${type} show`;
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loginForm").addEventListener("submit", async (event) => {
        event.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value;

        try {
            const response = await fetch(`${API_URL}/user/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                const err = await response.json().catch(() => null);
                showAlert(err?.message || "Usuário ou senha inválidos.");
                return;
            }

            const user = await response.json();
            localStorage.setItem("userId", user.id);
            window.location = "index.html";
        } catch (e) {
            showAlert("Não foi possível conectar à API. Ela está rodando em localhost:8080?");
        }
    });
});
