package se.cygni.jtodo;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final TodoRepository repo;

    TodoController(TodoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    List<TodoEntity> getAll() {
        return repo.findAll();
    }

    @PostMapping
    TodoEntity add(@RequestBody TodoEntity todo)
    {
        return repo.save(todo);
    }

    @PutMapping("/{id}")
    TodoEntity update(@PathVariable Long id, @RequestBody TodoEntity todoUpdate)
    {
        Optional<TodoEntity> u = repo.findById(id);
        if (u.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        u.get().setTask(todoUpdate.getTask());
        return repo.save(u.get());
    }
}