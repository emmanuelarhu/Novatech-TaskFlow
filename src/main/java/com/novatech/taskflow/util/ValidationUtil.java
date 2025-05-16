package com.novatech.taskflow.util;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Check if a string is null or empty
     * @param value The string to check
     * @return true if the string is null or empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Check if a string is not null and not empty
     * @param value The string to check
     * @return true if the string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Check if a string is within a length range
     * @param value The string to check
     * @param minLength The minimum length
     * @param maxLength The maximum length
     * @return true if the string is within the length range
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return minLength == 0;
        }

        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Check if a string is a valid email address
     * @param email The email address to check
     * @return true if the email is valid
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Check if a date is not null and not in the past
     * @param date The date to check
     * @return true if the date is not null and not in the past
     */
    public static boolean isNotPastDate(Date date) {
        if (date == null) {
            return false;
        }

        Date today = DateUtil.stripTime(new Date());
        Date dateWithoutTime = DateUtil.stripTime(date);

        return !dateWithoutTime.before(today);
    }

    /**
     * Check if a string is a valid integer
     * @param value The string to check
     * @return true if the string is a valid integer
     */
    public static boolean isInteger(String value) {
        if (isEmpty(value)) {
            return false;
        }

        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if a string is a valid positive integer
     * @param value The string to check
     * @return true if the string is a valid positive integer
     */
    public static boolean isPositiveInteger(String value) {
        if (!isInteger(value)) {
            return false;
        }

        return Integer.parseInt(value) > 0;
    }
}