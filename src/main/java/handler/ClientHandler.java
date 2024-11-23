package handler;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static Static.Connections.*;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.MessageDTO;
import dto.UserDTO;
import model.User;

public class ClientHandler implements Runnable {
    private final ObjectMapper objectMapper;
    private BufferedOutputStream bos;
    private BufferedInputStream bis;
    private User serverUser;
    private final Socket socket;
    private boolean connected;
    private MessageDTO messageDTO;
    private final Set<User> users = new HashSet<>();
    private final DatagramPacket audioPacket;
    private final byte[] audioBuffer = new byte[4096];
    private final byte[] messageBuffer = new byte[1024];

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.audioPacket = new DatagramPacket(audioBuffer, audioBuffer.length);
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper = new ObjectMapper(jsonFactory);
        try {
            bos = new BufferedOutputStream(socket.getOutputStream());
            bis = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int bytesRead;
        connected = true;
        while (true) {
            try {
                bytesRead = bis.read(messageBuffer, 0, messageBuffer.length);
                if (bytesRead == -1) {
                    break;
                }
                JsonNode jsonNode = objectMapper.readTree(messageBuffer);
                if (jsonNode.has("username")) { // Проверяем, есть ли поле username
                    UserDTO userDTO = objectMapper.treeToValue(jsonNode, UserDTO.class);
                    serverUser = new User(userDTO.getUsername(), User.counter++);
                    addServerUsersToSet(users);
                    broadcastUsers();
                    broadcastAudio();

                    users.clear();
                } else if (jsonNode.has("message")) {
                    messageDTO = objectMapper.treeToValue(jsonNode, MessageDTO.class);
                    broadcastMessage(messageDTO);
                } else {
                    System.out.println("Unknown data type received");
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        if (!clientHandlers.isEmpty()) {
            closeAll(bis, bos, socket);
            clientHandlers.remove(this);
            addServerUsersToSet(users);
            broadcastUsers();
        }
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while (true) {
//            try {
//
//                bytesRead = bis.read(buffer);
//                if (bytesRead == -1) {
//                    break;
//                }
//                UserDTO userDTO = objectMapper.readValue(buffer, UserDTO.class);
//                serverUser = new User(userDTO.getUsername(), User.counter++);
//                addServerUsersToSet(users);
//                broadcastUsers();
//                users.clear();
//            } catch (IOException e) { // мб здесь обрабатывать сообщение, отличное от юзера...
//                e.printStackTrace();
//                break;
//            }
//        }
//        if (!clientHandlers.isEmpty()) {
//            closeAll(bis, bos, socket);
//            clientHandlers.remove(this);
//            addServerUsersToSet(users);
//            broadcastUsers();
//        }
    }

    private void broadcastUsers() {
        clientHandlers.forEach((client) -> {
            try {
                objectMapper.writeValue(client.bos, users);
                client.bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void broadcastMessage(MessageDTO messageDTO) {
        clientHandlers.forEach((client) -> {
            try {
                objectMapper.writeValue(client.bos, messageDTO);
                client.bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void broadcastAudio() {
        new Thread(() -> {
            try (DatagramSocket audioSocket = new DatagramSocket(50005)) {
                while (connected) {
                    audioSocket.receive(audioPacket);
                    for (ClientHandler client : clientHandlers) {
                        if (client == this && socket.isConnected()) continue;
                        DatagramPacket sendPacket = new DatagramPacket(audioPacket.getData(),
                                audioPacket.getLength(),
                                audioPacket.getAddress(),
                                audioPacket.getPort());
                        audioSocket.send(sendPacket);
                        System.out.println("Send audio to " + audioPacket.getAddress() + ":" + audioPacket.getPort() + " " +
                                client.serverUser.getUsername());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addServerUsersToSet(Set<User> userSet) {
        clientHandlers.forEach((client) -> userSet.add(client.serverUser));
    }

    private void closeAll(BufferedInputStream bis, BufferedOutputStream bos, Socket socket) {
        try {
            if (bos != null) {
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (socket != null) {
                socket.close();
                connected = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
