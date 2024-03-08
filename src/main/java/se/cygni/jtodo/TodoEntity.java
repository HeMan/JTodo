package se.cygni.jtodo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "todos")
class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String task;


    @CreationTimestamp
    private Instant added;

    @Column
    private Instant due;

    @Column(columnDefinition = "boolean default false")
    private Boolean done;


    public TodoEntity() {
        this.done = false;
    }

    public TodoEntity(Long id, String task, Instant due, Boolean done) {
        this.id = id;
        this.task = task;
        this.due = due;
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Instant getAdded() {
        return added;
    }

    public void setDue(Instant due) {
        this.due = due;
    }

    public Instant getDue() {
        return due;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getDone() {
        return done;
    }


}