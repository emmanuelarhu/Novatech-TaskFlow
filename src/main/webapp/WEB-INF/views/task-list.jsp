<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskFlow - Task List</title>
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
                <li><a href="${pageContext.request.contextPath}/home">Dashboard</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/tasks">All Tasks</a></li>
                <li><a href="${pageContext.request.contextPath}/tasks/new">Create Task</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <section class="task-header">
            <h2>Task List</h2>
            <div class="task-filters">
                <form action="${pageContext.request.contextPath}/tasks" method="GET">
                    <select name="status" onchange="this.form.submit()">
                        <option value="">All Statuses</option>
                        <option value="PENDING" ${filteredStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="IN_PROGRESS" ${filteredStatus == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                        <option value="COMPLETED" ${filteredStatus == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                        <option value="CANCELLED" ${filteredStatus == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </form>
            </div>
            <a href="${pageContext.request.contextPath}/tasks/new" class="btn btn-primary">Create New Task</a>
        </section>

        <section class="task-list">
            <c:choose>
                <c:when test="${empty tasks}">
                    <div class="no-tasks">
                        <p>No tasks found.</p>
                        <a href="${pageContext.request.contextPath}/tasks/new" class="btn btn-primary">Create your first task</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="task-table">
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>Description</th>
                            <th>Due Date</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="task" items="${tasks}">
                            <tr class="task-item">
                                <td>${task.title}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty task.description}">
                                            <span class="no-description">No description</span>
                                        </c:when>
                                        <c:when test="${task.description.length() > 50}">
                                            ${task.description.substring(0, 50)}...
                                        </c:when>
                                        <c:otherwise>
                                            ${task.description}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />
                                </td>
                                <td>
                                    <span class="status status-${task.status.name().toLowerCase()}">${task.status.displayName}</span>
                                </td>
                                <td>
                                    <fmt:formatDate value="${task.createdAt}" pattern="yyyy-MM-dd" />
                                </td>
                                <td class="task-actions">
                                    <a href="${pageContext.request.contextPath}/tasks/${task.id}/edit" class="btn btn-edit">Edit</a>

                                    <c:if test="${task.status != 'COMPLETED'}">
                                        <form action="${pageContext.request.contextPath}/tasks/${task.id}/complete" method="POST" style="display: inline;">
                                            <button type="submit" class="btn btn-complete">Complete</button>
                                        </form>
                                    </c:if>

                                    <form action="${pageContext.request.contextPath}/tasks/${task.id}/delete" method="POST"
                                          style="display: inline;" onsubmit="return confirm('Are you sure you want to delete this task?')">
                                        <button type="submit" class="btn btn-delete">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 TaskFlow - NovaTech Solutions</p>
    </footer>
</div>

<script src="${pageContext.request.contextPath}/static/js/task-manager.js"></script>
</body>
</html>