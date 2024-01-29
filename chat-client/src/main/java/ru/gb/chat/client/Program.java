package ru.gb.chat.client;

import ru.gb.chat.client.frames.controller.ClientFrame;
import ru.gb.main.utils.logging.Logger;

import java.io.FileOutputStream;


public class Program {

    public static void main(String[] args) throws Exception {
        Logger logger = new Logger("insertion.log");
        ClientFrame clientFrame = new ClientFrame(logger);
        clientFrame.run();
    }
}
