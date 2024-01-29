package ru.gb.chat.client.frames.controller;

import ru.gb.chat.client.frames.factories.*;
import ru.gb.main.utils.entities.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginFrame extends JFrame {
    private static final int WINDOW_HEIGHT = 400;
    private static final int WINDOW_WIDTH = 350;
    private static final int WINDOW_POSX = 750;
    private static final int WINDOW_POSY = 150;
    private JTextField textFieldLogin;
    private JTextField textFieldReceiver;
    private JPasswordField textFieldPassword;
    private final ClientFrame clientFrame;
    public LoginFrame(ClientFrame clientFrame) {
        this.clientFrame = clientFrame;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setBackground(ColorFactory.getMain());

        JLabel label = new JLabel("<html><center>Set your login and password to connect the server</center></html>", SwingConstants.CENTER);
        label.setFont(FontFactory.getMain());
        add(label, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        JLabel labelToTextFieldLogin = new JLabel("Login: ");
        labelToTextFieldLogin.setFont(FontFactory.getMain());
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        fieldsPanel.add(labelToTextFieldLogin, cs);
        textFieldLogin = new JTextField(10);
        textFieldLogin.setFont(FontFactory.getMain());
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        fieldsPanel.add(textFieldLogin, cs);
        JLabel labelToTextFieldPassword = new JLabel("Password: ");
        labelToTextFieldPassword.setFont(FontFactory.getMain());
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        fieldsPanel.add(labelToTextFieldPassword, cs);
        textFieldPassword = new JPasswordField(10);
        textFieldPassword.setFont(FontFactory.getMain());
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        fieldsPanel.add(textFieldPassword, cs);
        JLabel labelReceiver = new JLabel("Receiver");
        labelReceiver.setFont(FontFactory.getMain());
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        fieldsPanel.add(labelReceiver, cs);
        textFieldReceiver = new JTextField(10);
        textFieldReceiver.setFont(FontFactory.getMain());
        textFieldReceiver.setText("All");
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        fieldsPanel.add(textFieldReceiver, cs);

        add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        JButton buttonLogin = new JButton("Login");
        buttonLogin.setFont(FontFactory.getMain());
        buttonLogin.setFocusPainted(false);
        buttonLogin.setBackground(ColorFactory.getButtonsColor());
        buttonLogin.addActionListener(e -> {
            if (textFieldLogin.getText().isEmpty() || textFieldReceiver.getText().isEmpty()) return;
            try {
                User initiator = clientFrame.tryToLogin(textFieldLogin.getText(), new String(textFieldPassword.getPassword()));
                if (initiator.isSystem()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "This is system name! Try again.", "Server error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                if (!initiator.isLoginn()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Bad login choice! Try again.", "Server error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                if (initiator.isFirstRegister()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "You've successfully registered:\nlogin: " + initiator.getLogin() +
                            "\npassword: " + initiator.getPassword(), "Registration", JOptionPane.INFORMATION_MESSAGE);
                }
                String textFromFieldReceiver = textFieldReceiver.getText();
                if (textFromFieldReceiver.equals("System")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "This is system name! Try again.", "Server error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                User receiver = clientFrame.getReceiverFromServer(textFromFieldReceiver.equals("All") ? "System" : textFromFieldReceiver);
                if (!receiver.isExists()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "This receiver doesn't exist! Try again.", "Server error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                clientFrame.setInitiator(initiator);
                clientFrame.setReceiver(receiver);
                setVisible(false);
                clientFrame.initActions();
                clientFrame.setVisible(true);
            } catch (IOException er) {
                JOptionPane.showMessageDialog(LoginFrame.this, er.getMessage(), "Server error", JOptionPane.ERROR_MESSAGE);
                setVisible(false);
                System.exit(0);
            }
        });
        buttonsPanel.add(buttonLogin);
        JButton buttonExit = new JButton("Exit");
        buttonExit.setFont(FontFactory.getMain());
        buttonExit.setFocusPainted(false);
        buttonExit.setBackground(ColorFactory.getButtonsColor());
        buttonExit.addActionListener(e -> {
            setVisible(false);
            System.exit(0);
        });
        buttonsPanel.add(buttonExit);
        add(buttonsPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }
}
