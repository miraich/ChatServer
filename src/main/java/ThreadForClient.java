import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static Static.Connections.*;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.UserDTO;
import model.User;

public class ThreadForClient implements Runnable {
    private final ObjectMapper objectMapper;
    private final Socket socket;
    private final ArrayList<User> usersToSend = new ArrayList<>();

    public ThreadForClient(Socket socket) {
        this.socket = socket;
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper = new ObjectMapper(jsonFactory);
    }

    @Override
    public void run() {
        User u = null;
        try {
            var bis = new BufferedInputStream(socket.getInputStream());
            var bos = new BufferedOutputStream(socket.getOutputStream());


            byte[] buffer = new byte[1024];
            int bytesRead;
            UserDTO userDTO;

            while (true) {
                try {
                    bytesRead = bis.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }

                    userDTO = objectMapper.readValue(buffer, UserDTO.class);
                    u = new User(userDTO.getUsername());
                    System.out.println(userDTO.getUsername());

                    addUniqueElementWriters(u, bos);
                    addUniqueElementReaders(u, bis);

                    userConnectionWriters.forEach((key, _) -> usersToSend.add(key));

                    userConnectionWriters.forEach((_, value) -> {
                        try {
                            objectMapper.writeValue(value, usersToSend);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            bis.close();
            bos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (u != null) {
                userConnectionWriters.remove(u);
                userConnectionReaders.remove(u);
                usersToSend.remove(u);
                userConnectionWriters.forEach((_, value) -> {
                    try {
                        objectMapper.writeValue(value, usersToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
