package ru.gb.chat.server;

import ru.gb.main.utils.entities.User;
import ru.gb.main.utils.logging.Logger;
import ru.gb.main.utils.repositories.UserRepository;
import ru.gb.main.utils.system.SystemUserFabric;

import javax.crypto.KeyGenerator;
import javax.security.auth.kerberos.EncryptionKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class Program {
    public static void main(String[] args) {
        Logger logger = new Logger("server.log");
        User systemUser = SystemUserFabric.systemUser();
        UserRepository userRepository = new UserRepository(logger);
        userRepository.insertData(new User(systemUser.getLogin(), null));
        ServerController serverController = new ServerController(logger, systemUser);
        serverController.run();
    }
}
