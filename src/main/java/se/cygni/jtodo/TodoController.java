package se.cygni.jtodo;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {
    private final TodoRepository repo;

    TodoController(TodoRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/todos")
    List<TodoEntity> getAll() {
        return repo.findAll();
    }
}
