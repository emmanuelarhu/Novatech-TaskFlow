package com.novatech.taskflow.model;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a task in the TaskFlow system
 */
public class Task {
    private Long id;
    private String title;
    private String description;
    private Date dueDate;
    private TaskStatus status;
    private Date createdAt;
    private Date updatedAt;

    // Default constructor
    public Task() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = TaskStatus.PENDING;
    }

    // Constructor with parameters
    public Task(String title, String description, Date dueDate) {
        this();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    // Full constructor
    public Task(Long id, String title, String description, Date dueDate, TaskStatus status) {
        this(title, description, dueDate);
        this.id = id;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(dueDate, task.dueDate) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, dueDate, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}