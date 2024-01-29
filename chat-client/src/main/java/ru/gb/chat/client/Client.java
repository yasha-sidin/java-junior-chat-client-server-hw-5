package ru.gb.chat.client;

import lombok.Setter;
import ru.gb.main.utils.entities.Message;
import ru.gb.main.utils.entities.User;
import ru.gb.main.utils.logging.Logger;
import ru.gb.main.utils.serializator.ObjectController;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private final Socket socket;
    @Setter
    private User receiver;
    @Setter
    private User initiator;
    private final AtomicBoolean isForAll = new AtomicBoolean(false);
    private final ObjectController<Message> objectController;
    private final JTextArea textArea;

    public Client(Socket socket, Logger logger, User receiver, User initiator, JTextArea textArea) throws IOException {
        this.textArea = textArea;
        this.socket = socket;
        this.receiver = receiver;
        this.initiator = initiator;
        objectController = new ObjectController(socket.getInputStream(), socket.getOutputStream(), logger);
    }

    public boolean sendMessage(Message message) {
        return objectController.sendJson(message);
    }

    public void listenForMessage() {
        new Thread(() -> {
            Optional<Message> optional = Optional.empty();
            while (!socket.isClosed()) {
                optional = objectController.readJson(Message.class);
                optional.ifPresent((msg) -> {
                    if (isForAll.get()) {
                        printMessage(msg);
                    } else {
                        if ((msg.getReceiver().getLogin().equals(receiver.getLogin()) && msg.getInitiator().getLogin().equals(initiator.getLogin())) ||
                                (msg.getInitiator().getLogin().equals(receiver.getLogin()) && msg.getReceiver().getLogin().equals(initiator.getLogin()))) {
                            printMessage(msg);
                        }
                    }
                });
            }
        }).start();
    }

    private void printMessage(Message msg) {
        textArea.append("(" + msg.getTime() + ") " +
                (msg.getInitiator().getLogin().equals(initiator.getLogin()) ? "You " : msg.getInitiator().getLogin()) +
                ": " + msg.getBody() + "\n");
    }

    public void setIsForAll(boolean value) {
        isForAll.set(value);
    }
}
