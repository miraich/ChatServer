import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    public static final LocalDateTime currentTime = LocalDateTime.now();
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
