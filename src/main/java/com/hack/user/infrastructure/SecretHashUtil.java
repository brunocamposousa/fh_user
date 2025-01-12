package com.hack.user.infrastructure;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecretHashUtil {

    public static String generateSecretHash(String email, String clientId, String clientSecret) {
        try {
            String message = email + clientId;
            SecretKeySpec keySpec = new SecretKeySpec(clientSecret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] macData = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(macData);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o SECRET_HASH", e);
        }
    }
}
