package com.novatech.taskflow.model;

/**
 * Enum representing the possible statuses of a task
 */
public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convert a string to a TaskStatus enum value
     * @param status The status as a string
     * @return The corresponding TaskStatus enum value, or PENDING if not found
     */
    public static TaskStatus fromString(String status) {
        try {
            return TaskStatus.valueOf(status.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException | NullPointerException e) {
            return PENDING; // Default status
        }
    }
}