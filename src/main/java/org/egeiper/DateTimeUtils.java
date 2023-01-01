package org.egeiper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static String getCurrentDate(final DatePatterns pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getPattern());
        return LocalDateTime.now().format(formatter);
    }

    public static String getCurrentTime(final TimePatterns pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getPattern());
        return LocalDateTime.now().format(formatter);
    }
}
