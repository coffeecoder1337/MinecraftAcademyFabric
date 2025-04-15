package net.st1ch.minecraftacademy.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private final Map<String, User> usersByHash = new HashMap<>();

    public User getOrCreateUser(String username, String ipAddress) {
        String hash = hash(username + ipAddress);
        return usersByHash.computeIfAbsent(hash, h -> new User(username, h));
    }

    public User getByToken(UUID token) {
        return usersByHash.values().stream()
                .filter(u -> u.getToken().equals(token))
                .findFirst()
                .orElse(null);
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encodedHash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

