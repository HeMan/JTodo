async function getTodos() {
    const response = await fetch("api/todos")
    const todos = await response.json()
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

window.onload = getTodos()