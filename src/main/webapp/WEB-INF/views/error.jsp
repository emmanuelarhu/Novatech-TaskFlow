<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TaskFlow - Error</title>
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
                <li><a href="${pageContext.request.contextPath}/tasks">All Tasks</a></li>
                <li><a href="${pageContext.request.contextPath}/tasks/new">Create Task</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <section class="error-container">
            <div class="error-icon">
                <i class="fas fa-exclamation-triangle"></i>
            </div>

            <h2>An Error Occurred</h2>

            <div class="error-message">
                <c:choose>
                    <c:when test="${not empty errorMessage}">
                        <p>${errorMessage}</p>
                    </c:when>
                    <c:when test="${not empty pageContext.exception}">
                        <p>${pageContext.exception.message}</p>
                    </c:when>
                    <c:otherwise>
                        <p>An unexpected error occurred. Please try again later.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Return to Dashboard</a>
                <button onclick="history.back()" class="btn btn-secondary">Go Back</button>
            </div>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 TaskFlow - NovaTech Solutions</p>
    </footer>
</div>
</body>
</html>