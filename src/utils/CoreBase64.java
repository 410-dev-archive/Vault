package utils;
import java.util.Base64;

public class CoreBase64 {
    public static String encode(String toEncode) {
        try {
            byte[] utf8StringBuffer = toEncode.getBytes("UTF-8");
            return Base64.getEncoder().encodeToString(utf8StringBuffer);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decode(String toDecode) {
        try {
            byte[] utf8StringBuffer = Base64.getDecoder().decode(toDecode.getBytes("UTF-8"));
            String decodedFromUtf8 = new String(utf8StringBuffer, "UTF-8");
            return decodedFromUtf8;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
