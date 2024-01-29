package ru.gb.main.utils.security;

import lombok.Getter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

public class MySecurity {
    @Getter
    private static final SecretKey key;

    static {
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keygen.init(128, secureRandom);
        key = keygen.generateKey();
    }
}

