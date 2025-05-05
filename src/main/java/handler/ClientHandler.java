package handler;

import java.io.*;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.MessageDTO;

import static Static.Connections.*;

public class ClientHandler implements Runnable {
    private final ObjectMapper objectMapper;
    private BufferedOutputStream messageBos;
    private BufferedInputStream messageBis;
    private final Socket messageSocket;
    private boolean connected;
    private final byte[] messageInfoBuffer = new byte[1024];

    public ClientHandler(Socket messageSocket) {
        this.messageSocket = messageSocket;
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper = new ObjectMapper(jsonFactory);
        try {
            if (messageSocket != null) {
                messageBos = new BufferedOutputStream(messageSocket.getOutputStream());
                messageBis = new BufferedInputStream(messageSocket.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        connected = true;
        handleMessage();
    }

    private void handleMessage() {
        int bytesRead;
        while (true) {
            try {
                bytesRead = messageBis.read(messageInfoBuffer, 0, messageInfoBuffer.length);
                if (bytesRead == -1) {
                    break;
                }
                MessageDTO messageDTO = objectMapper.readValue(messageInfoBuffer, MessageDTO.class);
                broadcastMessage(messageDTO);
            } catch (IOException e) {
                e.printStackTrace();
                closeAll(messageBis, messageBos, messageSocket);
            }
        }
    }

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
