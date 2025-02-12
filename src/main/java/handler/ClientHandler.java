package handler;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.MessageDTO;
import model.User;

import static Static.Connections.*;

public class ClientHandler implements Runnable {
    private final ObjectMapper objectMapper;
    private BufferedOutputStream messageBos;
    private BufferedInputStream messageBis;
    //    private BufferedOutputStream userBos;
//    private BufferedInputStream userBis;
    private User serverUser;
    private final Socket messageSocket;
    private Socket userSocket;
    private boolean connected;
    private MessageDTO messageDTO;
    private final Set<User> users = new HashSet<>();
    private final DatagramPacket audioPacket;
    private final byte[] audioBuffer = new byte[4096];
    private final byte[] messageInfoBuffer = new byte[1024];
    private final byte[] userInfoBuffer = new byte[1024];

    public ClientHandler(Socket messageSocket) {
        this.messageSocket = messageSocket;
//        this.userSocket = userSocket;
        this.audioPacket = new DatagramPacket(audioBuffer, audioBuffer.length);
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper = new ObjectMapper(jsonFactory);
        try {
            if (messageSocket != null) {
                messageBos = new BufferedOutputStream(messageSocket.getOutputStream());
                messageBis = new BufferedInputStream(messageSocket.getInputStream());
            }
//            if (userSocket != null) {
//                userBos = new BufferedOutputStream(userSocket.getOutputStream());
//                userBis = new BufferedInputStream(userSocket.getInputStream());
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        connected = true;
//        new Thread(this::handleUser).start();
        new Thread(this::handleMessage).start();
    }

    private void handleMessage() {
        int bytesRead;
        while (true) {
            try {
                bytesRead = messageBis.read(messageInfoBuffer, 0, messageInfoBuffer.length);
                if (bytesRead == -1) {
                    break;
                }
                messageDTO = objectMapper.readValue(messageInfoBuffer, MessageDTO.class);
                broadcastMessage(messageDTO);
            } catch (IOException e) {
                e.printStackTrace();
                closeAll(messageBis, messageBos, messageSocket);
            }
        }
    }

//    private void handleUser() {
//        int bytesRead;
//        while (true) {
//            try {
//                bytesRead = userBis.read(userInfoBuffer, 0, userInfoBuffer.length);
//                if (bytesRead == -1) {
//                    break;
//                }
//                UserDTO userDTO = objectMapper.readValue(userInfoBuffer, UserDTO.class);
//                serverUser = new User(userDTO.getUsername());
//                serverUser.setId(User.counter.getAndIncrement());
//                addServerUsersToSet(users);
//                broadcastUsers();
////                broadcastAudio();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //        int bytesRead;
//        connected = true;
//        while (true) {
//            try {
//                bytesRead = bis.read(messageBuffer, 0, messageBuffer.length);
//                if (bytesRead == -1) {
//                    break;
//                }
//                JsonNode jsonNode = objectMapper.readTree(messageBuffer);
//                if (jsonNode.has("username")) { // Проверяем, есть ли поле username
//                    UserDTO userDTO = objectMapper.treeToValue(jsonNode, UserDTO.class);
//                    serverUser = new User(userDTO.getUsername());
//                    serverUser.setId(User.counter.getAndIncrement());
//                    addServerUsersToSet(users);
//                    broadcastUsers();
//                    broadcastAudio();
//
//                    users.clear();
//                } else if (jsonNode.has("message")) {
//                    messageDTO = objectMapper.treeToValue(jsonNode, MessageDTO.class);
//                    broadcastMessage(messageDTO);
//                } else {
//                    System.out.println("Unknown data type received");
//                }
//            } catch (IOException e) {
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

//    private void broadcastUsers() {
//        clientHandlers.forEach((client) -> {
//            try {
//                objectMapper.writeValue(client.userBos, users);
//                client.userBos.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

    private void broadcastMessage(MessageDTO messageDTO) {
        new Thread(() -> clientHandlers.forEach((client) -> {
            try {
                objectMapper.writeValue(client.messageBos, messageDTO);
                client.messageBos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

//    private void broadcastAudio() {
//        new Thread(() -> {
//            try (DatagramSocket audioSocket = new DatagramSocket(50005)) {
//                while (connected) {
//                    audioSocket.receive(audioPacket);
//                    for (ClientHandler client : clientHandlers) {
//                        if (client == this && socket.isConnected()) continue;
//                        DatagramPacket sendPacket = new DatagramPacket(audioPacket.getData(),
//                                audioPacket.getLength(),
//                                audioPacket.getAddress(),
//                                audioPacket.getPort());
//                        audioSocket.send(sendPacket);
//                        System.out.println("Send audio to " + audioPacket.getAddress() + ":" + audioPacket.getPort() + " " +
//                                client.serverUser.getUsername());
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

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

    public void setUserSocket(Socket userSocket) {
        this.userSocket = userSocket;
    }

    public Socket getMessageSocket() {
        return messageSocket;
    }
}
