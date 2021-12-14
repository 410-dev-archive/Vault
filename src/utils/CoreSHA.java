package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CoreSHA {
    public static String hash512(String toDigest) {
        return task(toDigest, "", "sha-512");
    }

    public static String hash512(String toDigest, String salt) {
        return task(toDigest, salt, "sha-512");
    }

    public static String hash256(String toDigest) {
        return task(toDigest, "", "sha-256");
    }

    public static String hash256(String toDigest, String salt) {
        return task(toDigest, salt, "sha-256");
    }

    private static String task(String toDigest, String salt, String algorithm) {
        try {
            String hashtext = "";
            for (int i = 0; i < 3; i++) {
                toDigest = toDigest + salt;
                MessageDigest md = MessageDigest.getInstance(algorithm);
                byte[] messageDigest = md.digest(toDigest.getBytes());
                BigInteger no = new BigInteger(1, messageDigest);
                hashtext = no.toString(16);
                while (hashtext.length() < 32) {
                    hashtext = "0" + hashtext;
                }
            }
            return hashtext;
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
