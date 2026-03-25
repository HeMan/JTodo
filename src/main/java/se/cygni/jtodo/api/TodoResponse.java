package se.cygni.jtodo.api;

import se.cygni.jtodo.domain.TodoEntity;

import java.time.Instant;

public record TodoResponse(Long id, String task, Instant added, Instant due, Boolean done) {

    public static TodoResponse from(TodoEntity entity) {
        return new TodoResponse(
                entity.getId(),
                entity.getTask(),
                entity.getAdded(),
                entity.getDue(),
                entity.getDone()
        );
    }
}
