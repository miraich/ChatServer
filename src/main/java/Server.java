import handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static Static.Connections.clientHandlers;

public class Server {
    private static final int MESSAGE_PORT = 4450;

    public void start() {
        try {
            ServerSocket messageServerSocket = new ServerSocket(MESSAGE_PORT);
            while (!messageServerSocket.isClosed()) {
                try {
                    Socket messageSocket = messageServerSocket.accept();
                    System.out.println("Новое подключение для сообщений: " + messageSocket);
                    ClientHandler handler = new ClientHandler(messageSocket);
                    clientHandlers.add(handler);
                    new Thread(handler).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}