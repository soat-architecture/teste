package dev.com.soat.autorepairshop.shared;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private DateUtils() {

    }

    private static final String DEFAULT_PATTERN = "yyyy/MM/dd HH:mm";

    public static String toStringFormated(final LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
        return date.format(formatter);
    }
}
