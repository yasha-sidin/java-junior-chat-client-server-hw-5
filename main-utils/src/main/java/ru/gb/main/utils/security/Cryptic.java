package ru.gb.main.utils.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Cryptic {
    private final Cipher cipher;

    public Cryptic() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    public String crypt(Key key, String data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return DatatypeConverter.printHexBinary(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(Key key, String data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] base64 = Base64.decodeBase64(data.getBytes());
        return new String(cipher.doFinal(base64));
    }
}
