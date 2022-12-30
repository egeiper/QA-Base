import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static String getCurrentDate(DatePatterns pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getPattern());
        return LocalDateTime.now().format(formatter);
    }

    public static String getCurrentTime(TimePatterns pattern) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.getPattern());
        return LocalDateTime.now().format(formatter);
    }
}
