import java.io.BufferedOutputStream;
import java.util.HashSet;
import java.util.Set;

public class Connections {
    public static final Set<BufferedOutputStream> userConnectionWriters = new HashSet<>();
}
