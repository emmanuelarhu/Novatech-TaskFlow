/**
 * Form validation utilities for TaskFlow
 */

/**
 * Validate the task form
 * @returns {boolean} True if form is valid, false otherwise
 */
function validateTaskForm() {
    let isValid = true;

    // Get form elements
    const titleInput = document.getElementById('title');
    const descriptionInput = document.getElementById('description');
    const dueDateInput = document.getElementById('dueDate');

    // Get error message elements
    const titleError = document.getElementById('title-error');
    const descriptionError = document.getElementById('description-error');
    const dueDateError = document.getElementById('dueDate-error');

    // Validate title (required, max 100 chars)
    if (!titleInput.value.trim()) {
        titleError.textContent = 'Title is required';
        titleInput.classList.add('invalid');
        isValid = false;
    } else if (titleInput.value.length > 100) {
        titleError.textContent = 'Title must be 100 characters or less';
        titleInput.classList.add('invalid');
        isValid = false;
    } else {
        titleError.textContent = '';
        titleInput.classList.remove('invalid');
    }

    // Validate description (optional, max 500 chars)
    if (descriptionInput.value.length > 500) {
        descriptionError.textContent = 'Description must be 500 characters or less';
        descriptionInput.classList.add('invalid');
        isValid = false;
    } else {
        descriptionError.textContent = '';
        descriptionInput.classList.remove('invalid');
    }

    // Validate due date (required)
    if (!dueDateInput.value) {
        dueDateError.textContent = 'Due date is required';
        dueDateInput.classList.add('invalid');
        isValid = false;
    } else {
        // For new tasks, validate due date is not in the past
        const isNewTask = !window.location.pathname.match(/\/tasks\/\d+$/);

        if (isNewTask) {
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            const dueDate = new Date(dueDateInput.value);
            dueDate.setHours(0, 0, 0, 0);

            if (dueDate < today) {
                dueDateError.textContent = 'Due date cannot be in the past';
                dueDateInput.classList.add('invalid');
                isValid = false;
            } else {
                dueDateError.textContent = '';
                dueDateInput.classList.remove('invalid');
            }
        } else {
            dueDateError.textContent = '';
            dueDateInput.classList.remove('invalid');
        }
    }

    return isValid;
}

/**
 * Initialize form validation with real-time feedback
 */
function initFormValidation() {
    const form = document.getElementById('taskForm');
    const titleInput = document.getElementById('title');
    const descriptionInput = document.getElementById('description');
    const dueDateInput = document.getElementById('dueDate');

    // Add input event listeners for real-time validation
    titleInput.addEventListener('input', function() {
        validateField(this, 'title-error', validateTitle);
    });

    descriptionInput.addEventListener('input', function() {
        validateField(this, 'description-error', validateDescription);
    });

    dueDateInput.addEventListener('input', function() {
        validateField(this, 'dueDate-error', validateDueDate);
    });

    // Add form submit event listener
    form.addEventListener('submit', function(event) {
        if (!validateTaskForm()) {
            event.preventDefault();
        }
    });

    // Set min date for due date input (for new tasks)
    if (window.location.pathname.includes('/new')) {
        const today = new Date().toISOString().split('T')[0];
        dueDateInput.setAttribute('min', today);
    }
}

/**
 * Validate a field with the given validation function
 * @param {HTMLElement} field The input field to validate
 * @param {string} errorId ID of the error message element
 * @param {Function} validationFn The validation function to use
 */
function validateField(field, errorId, validationFn) {
    const errorElement = document.getElementById(errorId);
    const errorMessage = validationFn(field.value);

    if (errorMessage) {
        errorElement.textContent = errorMessage;
        field.classList.add('invalid');
    } else {
        errorElement.textContent = '';
        field.classList.remove('invalid');
    }
}

/**
 * Validate title field
 * @param {string} value The title value
 * @returns {string|null} Error message or null if valid
 */
function validateTitle(value) {
    if (!value.trim()) {
        return 'Title is required';
    } else if (value.length > 100) {
        return 'Title must be 100 characters or less';
    }

    return null;
}

/**
 * Validate description field
 * @param {string} value The description value
 * @returns {string|null} Error message or null if valid
 */
function validateDescription(value) {
    if (value.length > 500) {
        return 'Description must be 500 characters or less';
    }

    return null;
}

/**
 * Validate due date field
 * @param {string} value The due date value
 * @returns {string|null} Error message or null if valid
 */
function validateDueDate(value) {
    if (!value) {
        return 'Due date is required';
    }

    // For new tasks, validate due date is not in the past
    const isNewTask = window.location.pathname.includes('/new');

    if (isNewTask) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const dueDate = new Date(value);
        dueDate.setHours(0, 0, 0, 0);

        if (dueDate < today) {
            return 'Due date cannot be in the past';
        }
    }

    return null;
}

/**
 * Format a date as YYYY-MM-DD for date inputs
 * @param {Date} date The date to format
 * @returns {string} Formatted date string
 */
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}