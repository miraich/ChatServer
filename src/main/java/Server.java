import handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static Static.Connections.clientHandlers;

public class Server {
    private ServerSocket messageServerSocket;
//    private ServerSocket userServerSocket;

    //    private static final int USER_PORT = 4445;
    private static final int MESSAGE_PORT = 4450;

    public void start() {
        try {
            messageServerSocket = new ServerSocket(MESSAGE_PORT);
//            userServerSocket = new ServerSocket(USER_PORT);

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

//            new Thread(() -> {
//                while (!userServerSocket.isClosed()) {
//                    try {
//                        Socket userSocket = userServerSocket.accept();
//                        System.out.println("Новое подключение для пользователя: " + userSocket);
//                        for (ClientHandler handler : clientHandlers) {
//                            if (handler.getMessageSocket().getInetAddress()
//                                    .equals(userSocket.getInetAddress())) {
//                                handler.setUserSocket(userSocket);
//                                break;
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}