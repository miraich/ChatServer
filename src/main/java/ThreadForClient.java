import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ThreadForClient implements Runnable {
    private final Socket socket;

    public ThreadForClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (var bis = new BufferedInputStream(socket.getInputStream());
             var bos = new BufferedOutputStream(socket.getOutputStream())) {

            synchronized (Connections.userConnectionWriters) {
                Connections.userConnectionWriters.add(bos);
            }

            int bytesRead;
            byte[] buffer = new byte[4192];

            while (socket.isConnected() && ((bytesRead = bis.read(buffer)) != -1)) {
                String received = new String(buffer, 0, bytesRead);
                String dateTime = DateTime.currentTime.format(DateTime.formatter) + "|";
                String response = dateTime + received;

                synchronized (Connections.userConnectionWriters) {
                    for (var writer : Connections.userConnectionWriters) {
                        writer.write(response.getBytes());
                        writer.flush();
                    }
                    System.out.println(response + "send");
                }
            }
            socket.close();
            synchronized (Connections.userConnectionWriters) {
                Connections.userConnectionWriters.remove(bos);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
