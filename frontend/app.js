const logEl = document.getElementById("log");
function log(m) {
  if (logEl) logEl.textContent += m + "\n";
}

async function api(path, opts) {
  const r = await fetch(path, {
    headers: { "Content-Type": "application/json" },
    ...opts,
  });
  const body = await r.json().catch(() => ({}));
  log(
    `${opts?.method || "GET"} ${path} -> ${r.status} ${JSON.stringify(body).slice(0, 200)}`,
  );
  if (!r.ok) throw new Error(body.error || r.statusText);
  return body;
}

async function refresh() {
  const users = await api("/api/users");
  document.getElementById("users").innerHTML =
    users
      .map((u) => `<li>${u.id} | <b>${u.name}</b> (${u.email})</li>`)
      .join("") || "<li><i>no users</i></li>";
  const tasks = await api("/api/tasks");
  document.getElementById("tasks").innerHTML =
    tasks
      .map((t) => {
        const title = t.taskTitle || t.title;
        const done = t.isCompleted ?? t.done;
        return `<li class="${done ? "done" : ""}">${t.id} ${title} [user ${t.user_id}] ${done ? "Completed" : "Pending"} <button onclick="toggleTask(${t.id},${!done})">toggle</button> <button onclick="delTask(${t.id})">del</button></li>`;
      })
      .join("") || "<li><i>no tasks</i></li>";
}
async function createUser() {
  await api("/api/users", {
    method: "POST",
    body: JSON.stringify({ name: uname.value, email: uemail.value }),
  });
  uname.value = "";
  uemail.value = "";
  refresh();
}
async function createTask() {
  await api("/api/tasks", {
    method: "POST",
    body: JSON.stringify({
      title: ttitle.value,
      user_id: Number(tuserid.value),
    }),
  });
  ttitle.value = "";
  refresh();
}
async function toggleTask(id, done) {
  await api("/api/tasks/" + id, {
    method: "PUT",
    body: JSON.stringify({ done: done }),
  });
  refresh();
}
async function delTask(id) {
  await api("/api/tasks/" + id, { method: "DELETE" });
  refresh();
}
refresh();
