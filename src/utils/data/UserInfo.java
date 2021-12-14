package utils.data;

import utils.CoreBase64;
import utils.CoreSHA;

public class UserInfo {
    private String loginToken;

    private String loginToken2;
    private String decryptToken;

    public UserInfo(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getLoginToken() {
        if (loginToken2 == null) {
            loginToken2 = CoreSHA.hash512(CoreBase64.encode(loginToken), "fa57be7f9810abb21728253ce264207e453ff1d55a1e9fd03a68d210024375e4b30c1cf86babeba8e50092f563047cbdf583509dbd9cf74044c11af589babea");
        }

        return loginToken2;
    }

    public String getDecryptString(String randomizer) {
        if (decryptToken == null) {
            decryptToken = CoreSHA.hash512(getLoginToken() + randomizer, "fa57be7f9810abb21728253ce264207e453ff1d55a1e9fd03b68d210024375e4b30c1cf86babeba8e50092f563047cbdf583509dbd9cf74044c11af589babea");
        }

        return decryptToken;
    }
}
