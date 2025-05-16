<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskFlow - ${empty task ? 'Create' : 'Edit'} Task</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/forms.css">
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
                <li><a href="${pageContext.request.contextPath}/tasks">All Tasks</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/tasks/new">Create Task</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <section class="form-header">
            <h2>${empty task ? 'Create New Task' : 'Edit Task'}</h2>
        </section>

        <section class="form-container">
            <form id="taskForm" action="${pageContext.request.contextPath}/tasks${empty task ? '' : '/'.concat(task.id)}" method="POST" onsubmit="return validateForm()">

                <div class="form-group">
                    <label for="title">Title <span class="required">*</span></label>
                    <input type="text" id="title" name="title" value="${task.title}" required maxlength="100">
                    <div class="error-message" id="title-error"></div>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="4" maxlength="500">${task.description}</textarea>
                    <div class="error-message" id="description-error"></div>
                </div>

                <div class="form-group">
                    <label for="dueDate">Due Date <span class="required">*</span></label>
                    <input type="date" id="dueDate" name="dueDate"
                           value="<fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />" required>
                    <div class="error-message" id="dueDate-error"></div>
                </div>

                <div class="form-group">
                    <label for="status">Status</label>
                    <select id="status" name="status">
                        <c:choose>
                            <c:when test="${empty task}">
                                <option value="PENDING" selected>Pending</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="COMPLETED">Completed</option>
                                <option value="CANCELLED">Cancelled</option>
                            </c:when>
                            <c:otherwise>
                                <option value="PENDING" ${task.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                <option value="IN_PROGRESS" ${task.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                <option value="COMPLETED" ${task.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                <option value="CANCELLED" ${task.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">${empty task ? 'Create Task' : 'Update Task'}</button>
                    <a href="${pageContext.request.contextPath}/tasks" class="btn btn-secondary">Cancel</a>

                    <c:if test="${not empty task}">
                        <button type="button" class="btn btn-danger"
                                onclick="if(confirm('Are you sure you want to delete this task?')) {
                                        window.location.href='${pageContext.request.contextPath}/tasks/${task.id}/delete';
                                        }">
                            Delete Task
                        </button>
                    </c:if>
                </div>
            </form>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 TaskFlow - NovaTech Solutions</p>
    </footer>
</div>

<script src="${pageContext.request.contextPath}/static/js/validation.js"></script>
<script>
    function validateForm() {
        let isValid = true;

        // Validate title
        const title = document.getElementById('title').value.trim();
        if (title === '') {
            document.getElementById('title-error').textContent = 'Title is required';
            isValid = false;
        } else if (title.length > 100) {
            document.getElementById('title-error').textContent = 'Title must be 100 characters or less';
            isValid = false;
        } else {
            document.getElementById('title-error').textContent = '';
        }

        // Validate description
        const description = document.getElementById('description').value.trim();
        if (description.length > 500) {
            document.getElementById('description-error').textContent = 'Description must be 500 characters or less';
            isValid = false;
        } else {
            document.getElementById('description-error').textContent = '';
        }

        // Validate due date
        const dueDate = document.getElementById('dueDate').value;
        if (dueDate === '') {
            document.getElementById('dueDate-error').textContent = 'Due date is required';
            isValid = false;
        } else {
            document.getElementById('dueDate-error').textContent = '';
        }

        return isValid;
    }

    // Set minimum date for due date to today (for new tasks)
    window.onload = function() {
        if (!document.getElementById('title').value) {
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('dueDate').setAttribute('min', today);
        }
    };
</script>
</body>
</html>