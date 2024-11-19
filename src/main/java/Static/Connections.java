package Static;

import model.User;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {
    public static final ConcurrentHashMap<User, BufferedOutputStream> userConnectionWriters = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<User, BufferedInputStream> userConnectionReaders = new ConcurrentHashMap<>();

    public static void addUniqueElementWriters(User user, BufferedOutputStream outputStream) {
        if (userConnectionWriters.putIfAbsent(user, outputStream) == null) {
            System.out.println("Элемент добавлен: " + user.getUsername());
        } else {
            System.out.println("Элемент уже существует: " + user.getUsername());
        }
    }

    public static void addUniqueElementReaders(User user, BufferedInputStream inputStream) {
        if (userConnectionReaders.putIfAbsent(user, inputStream) == null) {
            System.out.println("Элемент добавлен: " + user.getUsername());
        } else {
            System.out.println("Элемент уже существует: " + user.getUsername());
        }
    }
}
