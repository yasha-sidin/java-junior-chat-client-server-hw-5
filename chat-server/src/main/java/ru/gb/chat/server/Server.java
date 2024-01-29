package ru.gb.chat.server;

import ru.gb.main.utils.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private final ServerSocket serverSocket;
    private final Logger logger;
    private final ServerController serverController;

    public Server(ServerController serverController) {
        this.serverController = serverController;
        this.serverSocket = serverController.getServerSocket();
        this.logger = serverController.getLogger();
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientManager clientManager = new ClientManager(socket, serverController);
                Thread thread = new Thread(clientManager);
                thread.start();
            } catch (IOException e) {
                logger.log(e.getMessage());
                System.out.println(e.getMessage());
                try {
                    serverController.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
