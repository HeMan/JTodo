package se.cygni.jtodo;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todos", description = "Endpoints for managing todos")
public class TodoController {
    private final TodoRepository repo;

    TodoController(TodoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @Operation(summary = "All todos", description = "List all todos")
    List<TodoEntity> getAll() {
        return repo.findAll();
    }

    @PostMapping
    @Operation(summary = "Add new todo", description = "Add a new todo to the database", responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoEntity.class))))
    TodoEntity add(@RequestBody CreateTodoRequest request) {
        var todo = new TodoEntity();
        todo.setTask(request.task());
        if (request.done() != null) {
            todo.setDone(request.done());
        }
        return repo.save(todo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update todo", description = "Update todo. Could be any of the properties of the todo.", responses = {@ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TodoEntity.class))), @ApiResponse(responseCode = "404", description = "Not found")})
    TodoEntity update(@PathVariable @Parameter(description = "id of todo") Long id, @RequestBody UpdateTodoRequest request) {
        var todo = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        todo.setTask(request.task());
        return repo.save(todo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete todo", responses = {@ApiResponse(responseCode = "204", description = "The todo was deleted successfully"), @ApiResponse(responseCode = "404", description = "Not found")})
    void delete(@PathVariable Long id) {
        var todo = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        repo.delete(todo);
    }
}
