package com.novatech.taskflow.controller;

import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;
import com.novatech.taskflow.service.TaskService;
import com.novatech.taskflow.service.TaskServiceImpl;
import com.novatech.taskflow.util.DateUtil;
import com.novatech.taskflow.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet handling task CRUD operations for web interface
 */
@WebServlet("/tasks/*")
public class TaskServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final TaskService taskService;

    /**
     * Constructor initializing the task service
     */
    public TaskServlet() {
        this.taskService = new TaskServiceImpl();
    }

    /**
     * Handle GET requests:
     * /tasks - list all tasks
     * /tasks?status={status} - list tasks by status
     * /tasks/{id} - show a specific task
     * /tasks/new - show the task creation form
     * /tasks/{id}/edit - show the task edit form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String contextPath = request.getContextPath();

        try {
            // Handle path with task ID
            if (pathInfo != null && !pathInfo.equals("/")) {
                if (pathInfo.equals("/new")) {
                    // Show new task form
                    request.getRequestDispatcher("/WEB-INF/views/task-form.jsp").forward(request, response);
                    return;
                }

                // Path format: /{id} or /{id}/edit
                String[] pathParts = pathInfo.split("/");

                if (pathParts.length >= 2) {
                    try {
                        Long taskId = Long.parseLong(pathParts[1]);
                        Task task = taskService.getTaskById(taskId);

                        if (task == null) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found");
                            return;
                        }

                        if (pathParts.length == 3 && pathParts[2].equals("edit")) {
                            // Show edit form
                            request.setAttribute("task", task);
                            request.getRequestDispatcher("/WEB-INF/views/task-form.jsp").forward(request, response);
                        } else {
                            // Show task details
                            request.setAttribute("task", task);
                            request.getRequestDispatcher("/WEB-INF/views/task-list.jsp").forward(request, response);
                        }
                        return;
                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task ID");
                        return;
                    }
                }
            }

            // Handle listing tasks (potentially with filters)
            String statusParam = request.getParameter("status");
            List<Task> tasks;

            if (statusParam != null && !statusParam.isEmpty()) {
                try {
                    TaskStatus status = TaskStatus.valueOf(statusParam.toUpperCase());
                    tasks = taskService.getTasksByStatus(status);
                    request.setAttribute("filteredStatus", status);
                } catch (IllegalArgumentException e) {
                    tasks = taskService.getAllTasks();
                }
            } else {
                tasks = taskService.getAllTasks();
            }

            request.setAttribute("tasks", tasks);
            request.getRequestDispatcher("/WEB-INF/views/task-list.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error retrieving tasks: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handle POST requests:
     * /tasks - create a new task
     * /tasks/{id} - update a task
     * /tasks/{id}/complete - mark a task as completed
     * /tasks/{id}/delete - delete a task
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String action = request.getParameter("action");
        String contextPath = request.getContextPath();

        try {
            // Handle path with task ID
            if (pathInfo != null && !pathInfo.equals("/") && !pathInfo.equals("/new")) {
                String[] pathParts = pathInfo.split("/");

                if (pathParts.length >= 2) {
                    try {
                        Long taskId = Long.parseLong(pathParts[1]);

                        if (pathParts.length == 3) {
                            // Handle specific actions
                            String actionPath = pathParts[2];

                            if ("complete".equals(actionPath)) {
                                taskService.markTaskAsCompleted(taskId);
                                response.sendRedirect(contextPath + "/tasks");
                                return;
                            } else if ("delete".equals(actionPath)) {
                                taskService.deleteTask(taskId);
                                response.sendRedirect(contextPath + "/tasks");
                                return;
                            }
                        }

                        // Update existing task
                        Task task = taskService.getTaskById(taskId);

                        if (task == null) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found");
                            return;
                        }

                        populateTaskFromRequest(task, request);
                        taskService.updateTask(task);
                        response.sendRedirect(contextPath + "/tasks");
                        return;

                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task ID");
                        return;
                    } catch (IllegalArgumentException e) {
                        request.setAttribute("errorMessage", e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                        return;
                    }
                }
            }

            // Create new task
            Task task = new Task();
            populateTaskFromRequest(task, request);
            taskService.createTask(task);
            response.sendRedirect(contextPath + "/tasks");

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error processing task: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Populate a task object from request parameters
     * @param task The task to populate
     * @param request The HTTP request
     * @throws IllegalArgumentException if validation fails
     */
    private void populateTaskFromRequest(Task task, HttpServletRequest request) throws IllegalArgumentException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String dueDateStr = request.getParameter("dueDate");
        String statusStr = request.getParameter("status");

        // Validate required fields
        if (ValidationUtil.isEmpty(title)) {
            throw new IllegalArgumentException("Title is required");
        }

        if (ValidationUtil.isEmpty(dueDateStr)) {
            throw new IllegalArgumentException("Due date is required");
        }

        // Parse due date
        Date dueDate = DateUtil.parseDate(dueDateStr);
        if (dueDate == null) {
            throw new IllegalArgumentException("Invalid due date format. Use yyyy-MM-dd");
        }

        // Set task properties
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);

        // Set status if provided
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                task.setStatus(TaskStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                task.setStatus(TaskStatus.PENDING); // Default status
            }
        }
    }
}