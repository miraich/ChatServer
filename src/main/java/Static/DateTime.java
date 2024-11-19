package Static;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    public static final ZonedDateTime currentTime = ZonedDateTime.now();
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
}
