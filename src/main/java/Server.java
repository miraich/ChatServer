import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {

    public Server() {

    }

    public void Run() {
        try (var serverSocket = new ServerSocket(4444)) {
            //в цикле обрабатываем входящие соединения.
//            while (true) {

            //метод accept ждет, пока кто-то не подключится.
            Socket socket = serverSocket.accept();

//            var br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var dis = new DataInputStream(socket.getInputStream());
//            System.out.println(socket.getInetAddress().getHostAddress());
            System.out.println(dis.readUTF());

//            DataInputStream dis = new DataInputStream(socket.getInputStream());

//            String fileName = dis.readUTF();
//            long fileSize = dis.readLong();


//            DataInputStream din = new DataInputStream(socket.getInputStream());
//            FileOutputStream fos = new FileOutputStream("loaded/" + fileName, false);
//
//            byte[] buffer = new byte[2048];
//            int bytesRead;
//            while ((bytesRead = din.read(buffer)) != -1) {
//                System.out.println(bytesRead);
//                fos.write(buffer, 0, bytesRead);  // Записываем только реальные байты
//            }
//
//            fos.close();
//            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
