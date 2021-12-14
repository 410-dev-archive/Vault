package encoders;

import utils.CoreBase64;
import utils.CoreSHA;

public class UserEncode {
    public static String generateLoginToken(String username, String password) {
        String encodedPhrase = CoreBase64.encode(username + ":" + password);
        encodedPhrase = CoreSHA.hash512(encodedPhrase, "49f18d477cb426e2501ee4222b534658f2e7e3dad34cb76e1390803e8cf18a8b6025dac31f7f60287b8f638560aae9fe4f606042f57c7c9ea3837f5c6271f561");

        return encodedPhrase;
    }
}
