/**
 * TaskFlow JavaScript functionality
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize task filtering
    initTaskFiltering();

    // Initialize tooltips
    initTooltips();

    // Check for overdue tasks
    highlightOverdueTasks();

    // Add form validation if on form page
    if (document.getElementById('taskForm')) {
        initFormValidation();
    }
});

/**
 * Initialize task filtering functionality
 */
function initTaskFiltering() {
    const statusFilter = document.querySelector('select[name="status"]');
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            this.form.submit();
        });
    }
}

/**
 * Initialize tooltips for task descriptions
 */
function initTooltips() {
    const taskDescriptions = document.querySelectorAll('.task-item td:nth-child(2)');
    taskDescriptions.forEach(cell => {
        if (cell.textContent.trim().endsWith('...')) {
            const fullText = cell.getAttribute('data-full-text');
            if (fullText) {
                cell.setAttribute('title', fullText);
                cell.style.cursor = 'help';
            }
        }
    });
}

/**
 * Highlight overdue tasks visually
 */
function highlightOverdueTasks() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const taskRows = document.querySelectorAll('.task-item');
    taskRows.forEach(row => {
        const dueDateCell = row.querySelector('td:nth-child(3)');
        const statusCell = row.querySelector('td:nth-child(4)');

        if (dueDateCell && statusCell) {
            const dueDate = new Date(dueDateCell.textContent.trim());
            const status = statusCell.textContent.trim();

            if (dueDate < today && status !== 'Completed' && status !== 'Cancelled') {
                row.classList.add('overdue');
            }
        }
    });
}

/**
 * Submit task form via AJAX
 * @param {Event} event Form submission event
 */
function submitTaskForm(event) {
    event.preventDefault();

    if (!validateTaskForm()) {
        return;
    }

    const form = event.target;
    const formData = new FormData(form);

    // Convert FormData to URL-encoded string
    const urlEncodedData = new URLSearchParams(formData).toString();

    fetch(form.action, {
        method: form.method,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: urlEncodedData
    })
        .then(response => {
            if (response.ok) {
                window.location.href = '/tasks';
            } else {
                return response.text();
            }
        })
        .then(errorText => {
            if (errorText) {
                showErrorMessage('Error: ' + errorText);
            }
        })
        .catch(error => {
            showErrorMessage('Network error: ' + error.message);
        });
}

/**
 * Show error message
 * @param {string} message Error message to display
 */
function showErrorMessage(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'alert alert-danger';
    errorDiv.textContent = message;

    const form = document.getElementById('taskForm');
    form.parentNode.insertBefore(errorDiv, form);

    // Remove after 5 seconds
    setTimeout(() => {
        errorDiv.remove();
    }, 5000);
}

/**
 * Mark a task as completed
 * @param {number} taskId Task ID to mark as completed
 */
function completeTask(taskId) {
    if (confirm('Mark this task as completed?')) {
        fetch(`/api/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                status: 'COMPLETED'
            })
        })
            .then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    return response.json();
                }
            })
            .then(data => {
                if (data && data.error) {
                    alert('Error: ' + data.error);
                }
            })
            .catch(error => {
                alert('Network error: ' + error.message);
            });
    }
}

/**
 * Delete a task
 * @param {number} taskId Task ID to delete
 */
function deleteTask(taskId) {
    if (confirm('Are you sure you want to delete this task? This action cannot be undone.')) {
        fetch(`/api/tasks/${taskId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/tasks';
                } else {
                    return response.json();
                }
            })
            .then(data => {
                if (data && data.error) {
                    alert('Error: ' + data.error);
                }
            })
            .catch(error => {
                alert('Network error: ' + error.message);
            });
    }
}