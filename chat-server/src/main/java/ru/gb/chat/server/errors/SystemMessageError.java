package ru.gb.chat.server.errors;

public class SystemMessageError extends Exception {
    public SystemMessageError(String msg) {
        super(msg);
    }
}
