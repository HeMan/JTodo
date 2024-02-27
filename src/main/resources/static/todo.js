async function getTodos() {
    const response = await fetch("api/todos")
    const todos = await response.json()
    document.getElementById("todolist").innerHTML = []
    todos.forEach((todo) => {
            let newA = document.createElement("a")
            newA.classList.add("list-group-item")
            newA.classList.add("list-group-item-action")
            newA.href = todo.id
            console.log(newA)
            newA.appendChild(document.createTextNode(todo.task))
            document.getElementById("todolist").appendChild(newA)
        }
    )
}

async function clicked(e) {
    e.preventDefault()
    let task = document.getElementById("task")
    const response = await fetch("api/todos", {
        method: "POST", headers: {
            "Content-Type": "application/json",

        },
        body: JSON.stringify({"task": task.value})
    })
    const todo = await response.json()
    console.log(todo)
    await getTodos()
    task.value = ""
}

async function loaded() {
    await getTodos()
    let taskForm = document.getElementById("taskForm")
    console.log(taskForm)
    taskForm.addEventListener("submit", clicked)
}

addEventListener("DOMContentLoaded", loaded);