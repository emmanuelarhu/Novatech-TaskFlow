package com.novatech.taskflow.dao;

import com.novatech.taskflow.config.DatabaseConfig;
import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of TaskDAO interface using JDBC
 */
public class TaskDAOImpl implements TaskDAO {

    // SQL Queries
    private static final String INSERT_TASK =
            "INSERT INTO tasks (title, description, due_date, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_TASK_BY_ID =
            "SELECT * FROM tasks WHERE id = ?";
    private static final String SELECT_ALL_TASKS =
            "SELECT * FROM tasks";
    private static final String UPDATE_TASK =
            "UPDATE tasks SET title = ?, description = ?, due_date = ?, status = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE_TASK =
            "DELETE FROM tasks WHERE id = ?";
    private static final String SELECT_TASKS_BY_STATUS =
            "SELECT * FROM tasks WHERE status = ?";
    private static final String SELECT_TASKS_BY_DUE_DATE_BEFORE =
            "SELECT * FROM tasks WHERE due_date < ?";
    private static final String SELECT_ALL_TASKS_SORTED_BY_DUE_DATE =
            "SELECT * FROM tasks ORDER BY due_date ASC";

    /**
     * Create a new task in the database
     */
    @Override
    public Task create(Task task) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setTimestamp(3, new Timestamp(task.getDueDate().getTime()));
            ps.setString(4, task.getStatus().name());
            ps.setTimestamp(5, new Timestamp(task.getCreatedAt().getTime()));
            ps.setTimestamp(6, new Timestamp(task.getUpdatedAt().getTime()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }

            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating task: " + e.getMessage(), e);
        }
    }

    /**
     * Get a task by its ID
     */
    @Override
    public Task getById(Long id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TASK_BY_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTask(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting task by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Get all tasks
     */
    @Override
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_TASKS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all tasks: " + e.getMessage(), e);
        }

        return tasks;
    }

    /**
     * Update an existing task
     */
    @Override
    public Task update(Task task) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_TASK)) {

            // Set parameters
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setTimestamp(3, new Timestamp(task.getDueDate().getTime()));
            ps.setString(4, task.getStatus().name());
            ps.setTimestamp(5, new Timestamp(new Date().getTime())); // Update the updated_at timestamp
            ps.setLong(6, task.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating task failed, no rows affected.");
            }

            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating task: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a task by its ID
     */
    @Override
    public boolean delete(Long id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_TASK)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting task: " + e.getMessage(), e);
        }
    }

    /**
     * Get tasks by status
     */
    @Override
    public List<Task> getByStatus(TaskStatus status) {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TASKS_BY_STATUS)) {

            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting tasks by status: " + e.getMessage(), e);
        }

        return tasks;
    }

    /**
     * Get tasks due before a specific date
     */
    @Override
    public List<Task> getByDueDateBefore(Date date) {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TASKS_BY_DUE_DATE_BEFORE)) {

            ps.setTimestamp(1, new Timestamp(date.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting tasks by due date: " + e.getMessage(), e);
        }

        return tasks;
    }

    /**
     * Get tasks sorted by due date (ascending)
     */
    @Override
    public List<Task> getAllSortedByDueDate() {
        List<Task> tasks = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_TASKS_SORTED_BY_DUE_DATE);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting tasks sorted by due date: " + e.getMessage(), e);
        }

        return tasks;
    }

    /**
     * Maps a database result set to a Task object
     */
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setDueDate(rs.getTimestamp("due_date"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setCreatedAt(rs.getTimestamp("created_at"));
        task.setUpdatedAt(rs.getTimestamp("updated_at"));
        return task;
    }
}