
package com.gulshan.doctor.appointment.Service.Implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gulshan.doctor.appointment.Entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JaasJwtService {

    @Value("${jaas.app-id}")
    private String appId;

    @Value("${jaas.api-key-id}")
    private String apiKeyId;

    @Value("${jaas.private-key-path}")
    private String privateKeyPath;

    public String generateToken(
            User user,
            String roomId,
            boolean isModerator) {

        try {
            RSAPrivateKey privateKey = loadPrivateKey();

            Algorithm algorithm = Algorithm.RSA256(null, privateKey);

            Instant now = Instant.now();

            Map<String, Object> userContext = new HashMap<>();
            userContext.put("id", user.getId().toString());
            userContext.put("name", user.getName());
            userContext.put("email", user.getEmail());

            // JaaS expects moderator as a string.
            if (isModerator) {
                userContext.put("moderator", "true");
            }

            Map<String, Object> context = new HashMap<>();
            context.put("user", userContext);

            return JWT.create()
                    .withKeyId(apiKeyId)
                    .withHeader(Map.of("typ", "JWT"))
                    .withIssuer("chat")
                    .withAudience("jitsi")
                    .withSubject(appId)
                    .withClaim("room", roomId)
                    .withClaim("context", context)
                    .withIssuedAt(Date.from(now))
                    .withNotBefore(Date.from(now.minusSeconds(5)))
                    .withExpiresAt(Date.from(now.plusSeconds(3600)))
                    .sign(algorithm);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to generate JaaS JWT token", ex);
        }
    }

    private RSAPrivateKey loadPrivateKey() throws Exception {

        String pem = Files.readString(Path.of(privateKeyPath));

        String privateKeyContent = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder()
                .decode(privateKeyContent);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
