package ru.gb.chat.server;

import lombok.Getter;
import ru.gb.main.utils.entities.User;
import ru.gb.main.utils.logging.Logger;
import ru.gb.main.utils.repositories.MessageRepository;
import ru.gb.main.utils.repositories.UserRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ServerController implements AutoCloseable, Runnable {
    private final Logger logger;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private ServerSocket serverSocket;
    private final User systemUser;

    public ServerController(Logger logger, User systemUser) {
        this.logger = logger;
        this.systemUser = systemUser;
        userRepository = new UserRepository(logger);
        messageRepository = new MessageRepository();
    }

    private User register(String login, String password) {
        User user = new User(login, password);
        userRepository.insertData(user);
        logger.log("User with login " + login + " was registered successfully!");
        System.out.println(user + " registered successfully!");
        user.setFirstRegister(true);
        user.setLoginn(true);
        return user;
    }

    public User checkUser(String login) {
        Optional<User> optional = userRepository.readUserByLogin(login);
        return optional.orElse(null);
    }

    public User tryToLogin(String login, String password) {
        if (Objects.equals(login, systemUser.getLogin())) {
            User user = new User();
            user.setSystem(true);
            return user;
        }
        Optional<User> optional = userRepository.readUserByLogin(login);
        if (optional.isPresent()) {
            if (optional.get().getPassword().equals(password)) {
                logger.log(optional.get() + " login successfully!");
                System.out.println(optional.get() + " login successfully!");
                optional.get().setLoginn(true);
                return optional.get();
            }
            return optional.get();
        } else {
            return register(login, password);
        }
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
        logger.close();
    }

    @Override
    public void run() {
        try {
            System.out.println("Server running...");
            serverSocket = new ServerSocket(5000);
            Server server = new Server(this);
            server.run();
        } catch (IOException e) {
            logger.log(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
