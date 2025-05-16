package com.novatech.taskflow.service;

import com.novatech.taskflow.dao.TaskDAO;
import com.novatech.taskflow.dao.TaskDAOImpl;
import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;
import com.novatech.taskflow.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the TaskService interface
 */
public class TaskServiceImpl implements TaskService {

    private final TaskDAO taskDAO;

    /**
     * Constructor with default DAO implementation
     */
    public TaskServiceImpl() {
        this.taskDAO = new TaskDAOImpl();
    }

    /**
     * Constructor with custom DAO implementation (for testing)
     */
    public TaskServiceImpl(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /**
     * Create a new task
     */
    @Override
    public Task createTask(Task task) {
        if (!validateTask(task)) {
            throw new IllegalArgumentException("Invalid task data");
        }

        // Ensure created and updated dates are set
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(new Date());
        }
        if (task.getUpdatedAt() == null) {
            task.setUpdatedAt(new Date());
        }

        // Set default status if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }

        return taskDAO.create(task);
    }

    /**
     * Get a task by its ID
     */
    @Override
    public Task getTaskById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid task ID");
        }

        return taskDAO.getById(id);
    }

    /**
     * Get all tasks
     */
    @Override
    public List<Task> getAllTasks() {
        return taskDAO.getAll();
    }

    /**
     * Update an existing task
     */
    @Override
    public Task updateTask(Task task) {
        if (task.getId() == null || task.getId() <= 0) {
            throw new IllegalArgumentException("Task ID is required for update");
        }

        if (!validateTask(task)) {
            throw new IllegalArgumentException("Invalid task data");
        }

        // Update the updated_at timestamp
        task.setUpdatedAt(new Date());

        return taskDAO.update(task);
    }

    /**
     * Mark a task as completed
     */
    @Override
    public Task markTaskAsCompleted(Long id) {
        Task task = getTaskById(id);

        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setUpdatedAt(new Date());

        return taskDAO.update(task);
    }

    /**
     * Delete a task
     */
    @Override
    public boolean deleteTask(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid task ID");
        }

        return taskDAO.delete(id);
    }

    /**
     * Get tasks by status
     */
    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        return taskDAO.getByStatus(status);
    }

    /**
     * Get tasks due today
     */
    @Override
    public List<Task> getTasksDueToday() {
        // Get all tasks
        List<Task> allTasks = taskDAO.getAll();

        // Filter tasks due today
        Date today = DateUtil.stripTime(new Date());
        Date tomorrow = DateUtil.addDays(today, 1);

        return allTasks.stream()
                .filter(task -> {
                    Date dueDate = DateUtil.stripTime(task.getDueDate());
                    return dueDate.equals(today) && task.getStatus() != TaskStatus.COMPLETED;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get overdue tasks
     */
    @Override
    public List<Task> getOverdueTasks() {
        // Get current date without time component
        Date today = DateUtil.stripTime(new Date());

        // Get all tasks
        List<Task> allTasks = taskDAO.getAll();

        // Filter overdue tasks (due date before today and not completed)
        return allTasks.stream()
                .filter(task -> {
                    Date dueDate = DateUtil.stripTime(task.getDueDate());
                    return dueDate.before(today) && task.getStatus() != TaskStatus.COMPLETED;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get tasks sorted by due date
     */
    @Override
    public List<Task> getTasksSortedByDueDate() {
        return taskDAO.getAllSortedByDueDate();
    }

    /**
     * Validate task data
     */
    @Override
    public boolean validateTask(Task task) {
        // Title is required and must be between 1 and 100 characters
        if (task.getTitle() == null || task.getTitle().trim().isEmpty() || task.getTitle().length() > 100) {
            return false;
        }

        // Description can be null but if provided must be less than 500 characters
        if (task.getDescription() != null && task.getDescription().length() > 500) {
            return false;
        }

        // Due date is required and must not be in the past when creating a new task
        if (task.getDueDate() == null) {
            return false;
        }

        // For new tasks (without ID), due date should not be in the past
        if (task.getId() == null) {
            Date today = DateUtil.stripTime(new Date());
            Date dueDate = DateUtil.stripTime(task.getDueDate());
            if (dueDate.before(today)) {
                return false;
            }
        }

        return true;
    }
}