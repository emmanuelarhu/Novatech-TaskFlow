package com.novatech.taskflow.service;

import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;

import java.util.Date;
import java.util.List;

/**
 * Service interface for task operations
 */
public interface TaskService {

    /**
     * Create a new task
     * @param task The task to create
     * @return The created task with ID assigned
     */
    Task createTask(Task task);

    /**
     * Get a task by its ID
     * @param id The task ID
     * @return The task or null if not found
     */
    Task getTaskById(Long id);

    /**
     * Get all tasks
     * @return List of all tasks
     */
    List<Task> getAllTasks();

    /**
     * Update an existing task
     * @param task The task to update
     * @return The updated task
     */
    Task updateTask(Task task);

    /**
     * Mark a task as completed
     * @param id The task ID
     * @return The updated task
     */
    Task markTaskAsCompleted(Long id);

    /**
     * Delete a task
     * @param id The task ID
     * @return true if deleted, false if not found
     */
    boolean deleteTask(Long id);

    /**
     * Get tasks by status
     * @param status The status to filter by
     * @return List of tasks with the specified status
     */
    List<Task> getTasksByStatus(TaskStatus status);

    /**
     * Get tasks due today
     * @return List of tasks due today
     */
    List<Task> getTasksDueToday();

    /**
     * Get overdue tasks (due date in the past and not completed)
     * @return List of overdue tasks
     */
    List<Task> getOverdueTasks();

    /**
     * Get tasks sorted by due date
     * @return List of tasks sorted by due date
     */
    List<Task> getTasksSortedByDueDate();

    /**
     * Validate if a task is valid
     * @param task The task to validate
     * @return true if valid, false otherwise
     */
    boolean validateTask(Task task);
}