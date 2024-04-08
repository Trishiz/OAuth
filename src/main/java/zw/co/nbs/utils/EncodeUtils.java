package zw.co.nbs.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodeUtils {

    public static String basicAuthorization(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
    }
}
