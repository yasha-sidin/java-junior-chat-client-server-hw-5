package ru.gb.chat.server;

import lombok.Getter;
import ru.gb.main.utils.entities.Message;
import ru.gb.main.utils.entities.User;
import ru.gb.main.utils.logging.Logger;
import ru.gb.main.utils.repositories.MessageRepository;
import ru.gb.main.utils.security.MySecurity;
import ru.gb.main.utils.serializator.ObjectController;
import ru.gb.main.utils.system.SystemUserFabric;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class ClientManager implements Runnable {
    private final Socket socket;
    private final ObjectController<Message> messageController;
    private User initiator;
    private User receiver;
    public final static ArrayList<ClientManager> clients = new ArrayList<>();
    public final ObjectController<User> userController;
    public final Logger logger;
    private final MessageRepository messageRepository;
    public final ServerController serverController;

    public ClientManager(Socket socket, ServerController serverController) throws IOException {
        this.serverController = serverController;
        this.messageRepository = serverController.getMessageRepository();
        this.logger = serverController.getLogger();
        this.socket = socket;
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        this.messageController = new ObjectController<>(in, out, logger);
        this.userController = new ObjectController<>(in, out, logger);
    }

    private void onClose() {
        try {
            clients.remove(this);
            System.out.println(socket.getInetAddress() + " disconnected!");
            logger.log(socket.getInetAddress() + " disconnected!");
            messageController.close();
            userController.close();
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void run() {
        Optional<User> initiatorOptional = userController.readJson(User.class);
        if (initiatorOptional.isPresent()) {
            initiator = serverController.tryToLogin(initiatorOptional.get().getLogin(), initiatorOptional.get().getPassword());
            if (initiator.isSystem()) {
                userController.sendJson(initiator);
                onClose();
                return;
            }
            userController.sendJson(initiator);
        }
        Optional<User> receiverOptional = userController.readJson(User.class);
        if (receiverOptional.isPresent() && initiatorOptional.isPresent()) {
            if (receiverOptional.get().getLogin().equals("System")) userController.sendJson(SystemUserFabric.systemUser());
            receiver = serverController.checkUser(receiverOptional.get().getLogin());
            if (receiver == null) {
                receiver = new User();
                receiver.setExists(false);
                userController.sendJson(receiver);
                onClose();
                return;
            }
            userController.sendJson(receiver);
            clients.add(this);
            logger.log(initiator.getLogin() + " connected to chat!");
        } else {
            logger.log("Invalid request from " + socket.getInetAddress().getHostAddress());
            System.out.println("Invalid request from " + socket.getInetAddress().getHostAddress());
            try {
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println("Invalid request! You're not a client. Your ip: " + socket.getInetAddress().getHostAddress());
                printWriter.close();
                onClose();
            } catch (IOException e) {
                onClose();
                logger.log("Error with invalid request. " + e.getMessage());
            }
            return;
        }
        try {
            Optional<List<Message>> optionalMessages = messageRepository.readAllData(Message.class);
            optionalMessages.ifPresent(messages -> messages.stream().filter(this::checkMessagesToBroadcast).forEach(messageController::sendJson));
            while (!socket.isClosed()) {
                Optional<Message> messageFromUser = messageController.readJson(Message.class);
                if (messageFromUser.isPresent()) {
                    messageRepository.insertData(messageFromUser.get());
                    broadcastMessage(messageFromUser.get());
                }
            }
            onClose();
        } catch (Exception e) {
            System.out.println("Exception is on message changing. " + e.getMessage());
            logger.log("Exception is on message changing. " + e.getMessage());
            onClose();
        }
    }

    private boolean checkMessagesToBroadcast(Message message) {
        return message.getInitiator().getLogin().equals(initiator.getLogin()) ||
                message.getReceiver().getLogin().equals(initiator.getLogin()) ||
                message.isForAll();
    }

    private synchronized void broadcastMessage(Message message) {
        for (ClientManager clientManager : clients) {
            if (checkMessagesToBroadcast(message)) {
                clientManager.getMessageController().sendJson(message);
            }
        }
    }
}
