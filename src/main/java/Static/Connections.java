package Static;

import handler.ClientHandler;

import java.util.concurrent.CopyOnWriteArraySet;

public class Connections {
    public static final CopyOnWriteArraySet<ClientHandler> clientHandlers = new CopyOnWriteArraySet<>();
}
