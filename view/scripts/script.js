const API_URL = "http://localhost:8080";

function showAlert(message, type = "error") {
    const el = document.getElementById("alert");
    el.textContent = message;
    el.className = `alert alert-${type} show`;
}

function clearAlert() {
    document.getElementById("alert").className = "alert";
}

function renderTasks(tasks) {
    const list = document.getElementById("taskList");

    if (!tasks.length) {
        list.innerHTML = `<li class="empty-state">Nenhuma tarefa ainda. Adicione a primeira acima!</li>`;
        return;
    }

    list.innerHTML = tasks
        .map(
            (task) => `
        <li class="task-item">
            <span class="desc">${task.description}</span>
            <button class="btn-delete" data-id="${task.id}">Excluir</button>
        </li>`
        )
        .join("");

    list.querySelectorAll(".btn-delete").forEach((btn) => {
        btn.addEventListener("click", () => deleteTask(Number(btn.dataset.id)));
    });
}

async function getTasks() {
    const userId = localStorage.getItem("userId");
    const loader = document.getElementById("loader");
    loader.style.display = "block";

    try {
        const response = await fetch(`${API_URL}/task/user/${userId}`);
        const data = await response.json();
        renderTasks(data);
    } catch (e) {
        showAlert("Não foi possível carregar as tarefas. A API está rodando?");
    } finally {
        loader.style.display = "none";
    }
}

async function addTask(description) {
    const userId = localStorage.getItem("userId");

    const response = await fetch(`${API_URL}/task`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ user: { id: Number(userId) }, description }),
    });

    if (!response.ok) {
        const err = await response.json().catch(() => null);
        showAlert((err && err.message) || "Não foi possível criar a tarefa.");
        return false;
    }

    clearAlert();
    return true;
}

async function deleteTask(id) {
    if (!confirm("Tem certeza que deseja excluir essa tarefa?")) return;

    try {
        const response = await fetch(`${API_URL}/task/${id}`, { method: "DELETE" });

        if (!response.ok) {
            const err = await response.json().catch(() => null);
            showAlert((err && err.message) || "Não foi possível excluir a tarefa.");
            return;
        }

        clearAlert();
        getTasks();
    } catch (e) {
        showAlert("Erro de conexão ao excluir a tarefa (veja o Console - F12).");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    if (!localStorage.getItem("userId")) {
        window.location = "login.html";
        return;
    }

    document.getElementById("logoutBtn").addEventListener("click", () => {
        localStorage.removeItem("userId");
        window.location = "login.html";
    });

    document.getElementById("newTaskForm").addEventListener("submit", async (event) => {
        event.preventDefault();
        const input = document.getElementById("newTaskDescription");
        const description = input.value.trim();
        if (!description) return;

        const ok = await addTask(description);
        if (ok) {
            input.value = "";
            getTasks();
        }
    });

    getTasks();
});
