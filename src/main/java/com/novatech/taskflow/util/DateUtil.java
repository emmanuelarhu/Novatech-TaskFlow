package com.novatech.taskflow.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for date operations
 */
public class DateUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Convert a string date in format yyyy-MM-dd to a Date object
     * @param dateStr The date string
     * @return The Date object or null if parsing fails
     */
    public static Date parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Format a Date object to yyyy-MM-dd format
     * @param date The Date object
     * @return The formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    /**
     * Format a Date object to yyyy-MM-dd HH:mm:ss format
     * @param date The Date object
     * @return The formatted datetime string
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_TIME_FORMAT.format(date);
    }

    /**
     * Remove the time component from a Date
     * @param date The Date object
     * @return A new Date object with the time component set to 00:00:00
     */
    public static Date stripTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Add a specified number of days to a Date
     * @param date The Date object
     * @param days The number of days to add
     * @return The new Date with days added
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);

        return calendar.getTime();
    }

    /**
     * Check if a date is today
     * @param date The Date to check
     * @return true if the date is today, false otherwise
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }

        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Get the current date with the time component stripped
     * @return Today's date at 00:00:00
     */
    public static Date today() {
        return stripTime(new Date());
    }
}