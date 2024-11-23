import Static.Connections;
import handler.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (var serverSocket = new ServerSocket(4444)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler ch = new ClientHandler(socket);
                Connections.clientHandlers.add(ch);
                Thread thread = new Thread(ch);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
