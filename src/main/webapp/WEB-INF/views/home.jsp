<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskFlow Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<div class="container">
    <header>
        <div class="logo">
            <img src="${pageContext.request.contextPath}/static/img/logo.png" alt="TaskFlow Logo">
            <h1>TaskFlow</h1>
        </div>
        <nav>
            <ul>
                <li class="active"><a href="${pageContext.request.contextPath}/home">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/tasks">All Tasks</a></li>
                <li><a href="${pageContext.request.contextPath}/tasks/new">Create Task</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <section class="dashboard-header">
            <h2>Welcome to Your Task Dashboard</h2>
            <p>Here's an overview of your current tasks and progress</p>
        </section>

        <section class="stats-cards">
            <div class="stat-card">
                <h3>Pending</h3>
                <div class="stat-value">${pendingCount}</div>
            </div>
            <div class="stat-card">
                <h3>In Progress</h3>
                <div class="stat-value">${inProgressCount}</div>
            </div>
            <div class="stat-card">
                <h3>Completed</h3>
                <div class="stat-value">${completedCount}</div>
            </div>
            <div class="stat-card">
                <h3>Completion Rate</h3>
                <div class="stat-value">${completionRate}%</div>
            </div>
        </section>

        <section class="tasks-section overdue-tasks">
            <h3>Overdue Tasks <span class="task-count">${overdueTasks.size()}</span></h3>
            <div class="task-list">
                <c:choose>
                    <c:when test="${empty overdueTasks}">
                        <p class="no-tasks">No overdue tasks. Great job!</p>
                    </c:when>
                    <c:otherwise>
                        <table class="task-table">
                            <thead>
                            <tr>
                                <th>Title</th>
                                <th>Due Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="task" items="${overdueTasks}">
                                <tr class="task-item overdue">
                                    <td>${task.title}</td>
                                    <td>
                                        <fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />
                                    </td>
                                    <td>
                                        <span class="status status-${task.status.name().toLowerCase()}">${task.status.displayName}</span>
                                    </td>
                                    <td class="task-actions">
                                        <a href="${pageContext.request.contextPath}/tasks/${task.id}/edit" class="btn btn-edit">Edit</a>
                                        <a href="#" onclick="completeTask(${task.id})" class="btn btn-complete">Complete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <section class="tasks-section today-tasks">
            <h3>Due Today <span class="task-count">${todayTasks.size()}</span></h3>
            <div class="task-list">
                <c:choose>
                    <c:when test="${empty todayTasks}">
                        <p class="no-tasks">No tasks due today.</p>
                    </c:when>
                    <c:otherwise>
                        <table class="task-table">
                            <thead>
                            <tr>
                                <th>Title</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="task" items="${todayTasks}">
                                <tr class="task-item">
                                    <td>${task.title}</td>
                                    <td>
                                        <span class="status status-${task.status.name().toLowerCase()}">${task.status.displayName}</span>
                                    </td>
                                    <td class="task-actions">
                                        <a href="${pageContext.request.contextPath}/tasks/${task.id}/edit" class="btn btn-edit">Edit</a>
                                        <a href="#" onclick="completeTask(${task.id})" class="btn btn-complete">Complete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <section class="quick-actions">
            <h3>Quick Actions</h3>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/tasks/new" class="btn btn-primary">Create New Task</a>
                <a href="${pageContext.request.contextPath}/tasks?status=PENDING" class="btn btn-secondary">View Pending Tasks</a>
                <a href="${pageContext.request.contextPath}/tasks?status=COMPLETED" class="btn btn-secondary">View Completed Tasks</a>
            </div>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 TaskFlow - NovaTech Solutions</p>
    </footer>
</div>

<script src="${pageContext.request.contextPath}/static/js/task-manager.js"></script>
<script>
    function completeTask(taskId) {
        if (confirm('Mark this task as completed?')) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/tasks/' + taskId + '/complete';
            document.body.appendChild(form);
            form.submit();
        }
    }
</script>
</body>
</html>