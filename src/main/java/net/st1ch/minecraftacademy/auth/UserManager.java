package net.st1ch.minecraftacademy.auth;

import net.minecraft.server.network.ServerPlayerEntity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private final Map<UUID, User> users = new HashMap<>();
    private final String secretKey;

    public UserManager(String secretKey) {
        this.secretKey = secretKey;
    }

    public UUID generateUUID(String name, String ip) {
        try {
            String input = name + ip;
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(key);
            byte[] hash = hmac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return UUID.nameUUIDFromBytes(hash);  // для преобразования в UUID
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User registerOrGetUser(String name, String ip, ServerPlayerEntity player) {
        UUID id = generateUUID(name, ip);
        return users.computeIfAbsent(id, u -> new User(name, u, player));
    }

    public User getByUUID(UUID id) {
        return users.get(id);
    }

    public Collection<User> getAllOnlineUsers() {
        return users.values().stream().filter(User::isOnline).toList();
    }
}

