package utils;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CoreCryptography {

    public static String encrypt(String input, String keyStr) throws Exception {
        keyStr = CoreSHA.hash512(keyStr);
        input = CoreBase64.encode(input);
        SecretKeySpec secretKey;
        MessageDigest sha = null;
        String encryptedString = "";
        byte[] key;
        try {
            key = keyStr.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] passin = cipher.doFinal(input.trim().getBytes("UTF-8"));

            Base64 base64 = new Base64();
            encryptedString = base64.encodeToString(passin);
            encryptedString = CoreBase64.encode(encryptedString);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return encryptedString;
    }

    public static String decrypt(String input, String keyStr) throws Exception {
        keyStr = CoreSHA.hash512(keyStr);
        input = CoreBase64.decode(input);
        byte[] key;
        String decryptedString = "";
        SecretKeySpec secretKey;

        MessageDigest sha = null;
        try {
            key = keyStr.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] passin = input.trim().getBytes();
            decryptedString = (new String(cipher.doFinal(Base64.decodeBase64(passin))));
            decryptedString = CoreBase64.decode(decryptedString);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return decryptedString;
    }
}
