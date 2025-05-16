package com.novatech.taskflow.controller;

import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;
import com.novatech.taskflow.service.TaskService;
import com.novatech.taskflow.service.TaskServiceImpl;
import com.novatech.taskflow.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RESTful API servlet for task operations
 */
@WebServlet("/api/tasks/*")
public class TaskRestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final TaskService taskService;

    /**
     * Constructor initializing the task service
     */
    public TaskRestServlet() {
        this.taskService = new TaskServiceImpl();
    }

    /**
     * Handle GET requests for the task API:
     * /api/tasks - get all tasks
     * /api/tasks/{id} - get a specific task
     * /api/tasks?status={status} - get tasks by status
     * /api/tasks/overdue - get overdue tasks
     * /api/tasks/today - get tasks due today
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            // Get a specific task by ID
            if (pathInfo != null && !pathInfo.equals("/")) {
                if (pathInfo.equals("/overdue")) {
                    // Get overdue tasks
                    List<Task> tasks = taskService.getOverdueTasks();
                    out.print(toJsonArray(tasks));
                    return;
                } else if (pathInfo.equals("/today")) {
                    // Get tasks due today
                    List<Task> tasks = taskService.getTasksDueToday();
                    out.print(toJsonArray(tasks));
                    return;
                }

                // Get task by ID
                try {
                    Long taskId = Long.parseLong(pathInfo.substring(1));
                    Task task = taskService.getTaskById(taskId);

                    if (task == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(new JSONObject().put("error", "Task not found").toString());
                    } else {
                        out.print(toJson(task).toString());
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(new JSONObject().put("error", "Invalid task ID").toString());
                }
                return;
            }

            // Get all tasks or filter by status
            String statusParam = request.getParameter("status");
            List<Task> tasks;

            if (statusParam != null && !statusParam.isEmpty()) {
                try {
                    TaskStatus status = TaskStatus.valueOf(statusParam.toUpperCase());
                    tasks = taskService.getTasksByStatus(status);
                } catch (IllegalArgumentException e) {
                    tasks = taskService.getAllTasks();
                }
            } else {
                tasks = taskService.getAllTasks();
            }

            out.print(toJsonArray(tasks));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Server error: " + e.getMessage()).toString());
        }
    }

    /**
     * Handle POST requests to create a new task:
     * /api/tasks - create a new task
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Read request body
            String requestBody = request.getReader().lines().collect(Collectors.joining());
            JSONObject taskJson = new JSONObject(requestBody);

            // Create new task
            Task task = new Task();
            populateTaskFromJson(task, taskJson);

            Task createdTask = taskService.createTask(task);

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(toJson(createdTask).toString());

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(new JSONObject().put("error", e.getMessage()).toString());
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(new JSONObject().put("error", "Invalid JSON format: " + e.getMessage()).toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Server error: " + e.getMessage()).toString());
        }
    }

    /**
     * Handle PUT requests to update a task:
     * /api/tasks/{id} - update a task
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Task ID is required").toString());
                return;
            }

            // Get task ID from path
            try {
                Long taskId = Long.parseLong(pathInfo.substring(1));
                Task task = taskService.getTaskById(taskId);

                if (task == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(new JSONObject().put("error", "Task not found").toString());
                    return;
                }

                // Read request body
                String requestBody = request.getReader().lines().collect(Collectors.joining());
                JSONObject taskJson = new JSONObject(requestBody);

                // Update task
                populateTaskFromJson(task, taskJson);

                Task updatedTask = taskService.updateTask(task);

                out.print(toJson(updatedTask).toString());

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Invalid task ID").toString());
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", e.getMessage()).toString());
            } catch (JSONException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Invalid JSON format: " + e.getMessage()).toString());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Server error: " + e.getMessage()).toString());
        }
    }

    /**
     * Handle DELETE requests to delete a task:
     * /api/tasks/{id} - delete a task
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Task ID is required").toString());
                return;
            }

            // Get task ID from path
            try {
                Long taskId = Long.parseLong(pathInfo.substring(1));
                boolean deleted = taskService.deleteTask(taskId);

                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(new JSONObject().put("error", "Task not found").toString());
                }

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JSONObject().put("error", "Invalid task ID").toString());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Server error: " + e.getMessage()).toString());
        }
    }

    /**
     * Convert a Task object to JSON
     * @param task The task to convert
     * @return JSONObject representing the task
     */
    private JSONObject toJson(Task task) {
        JSONObject json = new JSONObject();
        json.put("id", task.getId());
        json.put("title", task.getTitle());
        json.put("description", task.getDescription() != null ? task.getDescription() : "");
        json.put("dueDate", DateUtil.formatDate(task.getDueDate()));
        json.put("status", task.getStatus().name());
        json.put("createdAt", DateUtil.formatDateTime(task.getCreatedAt()));
        json.put("updatedAt", DateUtil.formatDateTime(task.getUpdatedAt()));
        return json;
    }

    /**
     * Convert a list of Task objects to JSON array
     * @param tasks The tasks to convert
     * @return JSON array string
     */
    private String toJsonArray(List<Task> tasks) {
        JSONArray jsonArray = new JSONArray();

        for (Task task : tasks) {
            jsonArray.put(toJson(task));
        }

        return jsonArray.toString();
    }

    /**
     * Populate a task object from JSON
     * @param task The task to populate
     * @param json The JSON object
     * @throws IllegalArgumentException if validation fails
     * @throws JSONException if JSON is malformed
     */
    private void populateTaskFromJson(Task task, JSONObject json) throws IllegalArgumentException, JSONException {
        // Check required fields
        if (!json.has("title") || json.getString("title").trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (!json.has("dueDate") || json.getString("dueDate").trim().isEmpty()) {
            throw new IllegalArgumentException("Due date is required");
        }

        // Set task properties
        task.setTitle(json.getString("title"));

        if (json.has("description")) {
            task.setDescription(json.getString("description"));
        }

        // Parse due date
        String dueDateStr = json.getString("dueDate");
        Date dueDate = DateUtil.parseDate(dueDateStr);
        if (dueDate == null) {
            throw new IllegalArgumentException("Invalid due date format. Use yyyy-MM-dd");
        }
        task.setDueDate(dueDate);

        // Set status if provided
        if (json.has("status")) {
            try {
                task.setStatus(TaskStatus.valueOf(json.getString("status").toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Default to PENDING if invalid status
                task.setStatus(TaskStatus.PENDING);
            }
        }
    }
}