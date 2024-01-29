package ru.gb.main.utils.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Logger implements AutoCloseable {

    private final String fileName;

    public Logger(String fileName) {
        this.fileName = fileName;
    }

    public void log(String data) {
        try(PrintWriter printWriter = new PrintWriter(new FileOutputStream(fileName, true))) {
            printWriter.println(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {

    }
}
