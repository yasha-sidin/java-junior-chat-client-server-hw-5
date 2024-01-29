package ru.gb.chat.client.frames.controller;

import lombok.Setter;
import ru.gb.chat.client.Client;
import ru.gb.chat.client.frames.factories.*;
import ru.gb.main.utils.entities.Message;
import ru.gb.main.utils.entities.User;
import ru.gb.main.utils.logging.Logger;
import ru.gb.main.utils.serializator.ObjectController;
import ru.gb.main.utils.system.SystemUserFabric;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

public class ClientFrame extends JFrame implements Runnable, Closeable {
    private static final int WINDOW_HEIGHT = 900;
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_POSX = 800;
    private static final int WINDOW_POSY = 300;
    private final JTextField messageField = new JTextField(20);
    private JButton sendButton;
    private JTextArea areaForMessages;
    private Socket socket;
    private JLabel label;
    private final Logger logger;
    @Setter
    private User initiator;
    @Setter
    private User receiver;

    public ClientFrame(Logger logger) {
        this.logger = logger;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
    }

    public User tryToLogin(String login, String password) throws IOException {
        ObjectController<User> objectController = new ObjectController<>(socket.getInputStream(), socket.getOutputStream(), logger);
        objectController.sendJson(new User(login, password));
        Optional<User> optional = objectController.readJson(User.class);
        return optional.orElse(null);
    }

    public User getReceiverFromServer(String login) throws IOException {
        ObjectController<User> objectController = new ObjectController<>(socket.getInputStream(), socket.getOutputStream(), logger);
        objectController.sendJson(new User(login, null));
        Optional<User> optional = objectController.readJson(User.class);
        return optional.orElse(null);
    }

    @Override
    public void run() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            socket = new Socket(address, 5000);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ClientFrame.this, e.getMessage(), "Server error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        label = new JLabel(".", SwingConstants.CENTER);
        label.setFont(FontFactory.getMain());
        add(label, BorderLayout.NORTH);

        areaForMessages = new JTextArea();
        areaForMessages.setEditable(false);
        areaForMessages.setFont(FontFactory.getMain());
        JScrollPane jsp = new JScrollPane(areaForMessages);
        add(jsp, BorderLayout.CENTER);

        JPanel formMessage = new JPanel();
        add(formMessage, BorderLayout.SOUTH);
        sendButton = new JButton("Send");
        sendButton.setBackground(ColorFactory.getButtonsColor());
        sendButton.setFocusPainted(false);
        sendButton.setFont(FontFactory.getMain());
        formMessage.add(sendButton, BorderLayout.EAST);
        messageField.setFont(FontFactory.getMain());
        formMessage.add(messageField, BorderLayout.CENTER);

        LoginFrame loginFrame = new LoginFrame(ClientFrame.this);
    }

    public void initLabel() {
        label.setText("Chat(" + "Remote IP: " + socket.getInetAddress().getHostAddress() + ", LocalPort: "
                + socket.getLocalPort() + ", Login: " + initiator.getLogin() + ", receiver login: " +
                (receiver.isSystem() ? "All" : receiver.getLogin())  + ")");
    }

    public void initActions() {
        try {
            Client client = new Client(socket, logger, receiver, initiator, areaForMessages);
            if (receiver.isSystem()) {
                client.setIsForAll(true);
            }
            initLabel();
            client.listenForMessage();
            sendButton.addActionListener(e -> {
                if (messageField.getText().isEmpty()) return;
                sendMessage(client);
            });
            messageField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (messageField.getText().isEmpty()) return;
                        sendMessage(client);;
                    }
                }
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ClientFrame.this, e.getMessage(), "Server error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void sendMessage(Client client) {
        if (socket.isClosed()) {
            JOptionPane.showMessageDialog(ClientFrame.this, "Socket is closed. Reboot app.", "Server error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Message message = new Message(messageField.getText(), initiator, receiver);
        if (receiver.isSystem()) message.setForAll(true);
        messageField.setText("");
        client.sendMessage(message);
    }

    @Override
    public void close() throws IOException {
        try {
            logger.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
