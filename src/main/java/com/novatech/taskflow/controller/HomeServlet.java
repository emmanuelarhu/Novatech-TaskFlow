package com.novatech.taskflow.controller;

import com.novatech.taskflow.model.Task;
import com.novatech.taskflow.model.TaskStatus;
import com.novatech.taskflow.service.TaskService;
import com.novatech.taskflow.service.TaskServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for the home/dashboard page
 */
@WebServlet({"/", "/home", "/dashboard"})
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final TaskService taskService;

    /**
     * Constructor initializing the task service
     */
    public HomeServlet() {
        this.taskService = new TaskServiceImpl();
    }

    /**
     * Handle GET requests for the home page
     * Displays a dashboard with task statistics and upcoming tasks
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get tasks due today
            List<Task> todayTasks = taskService.getTasksDueToday();
            request.setAttribute("todayTasks", todayTasks);

            // Get overdue tasks
            List<Task> overdueTasks = taskService.getOverdueTasks();
            request.setAttribute("overdueTasks", overdueTasks);

            // Get tasks by status for statistics
            List<Task> pendingTasks = taskService.getTasksByStatus(TaskStatus.PENDING);
            List<Task> inProgressTasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS);
            List<Task> completedTasks = taskService.getTasksByStatus(TaskStatus.COMPLETED);

            request.setAttribute("pendingCount", pendingTasks.size());
            request.setAttribute("inProgressCount", inProgressTasks.size());
            request.setAttribute("completedCount", completedTasks.size());

            // Get total tasks
            List<Task> allTasks = taskService.getAllTasks();
            request.setAttribute("totalCount", allTasks.size());

            // Calculate completion rate
            double completionRate = allTasks.isEmpty() ? 0 :
                    (double) completedTasks.size() / allTasks.size() * 100;
            request.setAttribute("completionRate", Math.round(completionRate));

            // Forward to the home page
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}