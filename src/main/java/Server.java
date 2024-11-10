import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    public void Start() {
        try (var serverSocket = new ServerSocket(4444)) {
            while (serverSocket.isBound()) {
                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    try {
                        var br = new BufferedInputStream(socket.getInputStream());
                        int bytesRead = 0;
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = br.read(buffer, 0, buffer.length)) != -1) {
                            System.out.print("Получено от клиента:" + new String(buffer, 0, bytesRead));
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
