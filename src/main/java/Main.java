import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (var serverSocket = new ServerSocket(4444)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ThreadForClient tfc = new ThreadForClient(socket);
                Thread thrd = new Thread(tfc);
                thrd.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
