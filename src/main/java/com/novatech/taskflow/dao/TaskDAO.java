package com.novatech.taskflow.dao;

import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object interface for Task operations
 */
public interface TaskDAO {

    /**
     * Create a new task in the database
     * @param task The task to create
     * @return The created task with its generated ID
     */
    Task create(Task task);

    /**
     * Get a task by its ID
     * @param id The task ID
     * @return The task or null if not found
     */
    Task getById(Long id);

    /**
     * Get all tasks
     * @return List of all tasks
     */
    List<Task> getAll();

    /**
     * Update an existing task
     * @param task The task to update
     * @return The updated task
     */
    Task update(Task task);

    /**
     * Delete a task by its ID
     * @param id The task ID to delete
     * @return true if deleted, false if not found
     */
    boolean delete(Long id);

    /**
     * Get tasks by status
     * @param status The task status to filter by
     * @return List of tasks with the specified status
     */
    List<Task> getByStatus(TaskStatus status);

    /**
     * Get tasks due before a specific date
     * @param date The due date to filter by
     * @return List of tasks due before the specified date
     */
    List<Task> getByDueDateBefore(Date date);

    /**
     * Get tasks sorted by due date (ascending)
     * @return List of tasks sorted by due date
     */
    List<Task> getAllSortedByDueDate();
}