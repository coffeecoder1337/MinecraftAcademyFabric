package net.st1ch.minecraftacademy.auth;

import java.util.UUID;

public class User {
    private final String username;
    private final String ipHash;
    private final UUID token;
    private boolean online;

    public User(String username, String ipHash) {
        this.username = username;
        this.ipHash = ipHash;
        this.token = UUID.randomUUID();
        this.online = true;
    }

    public String getIpHash() {
        return ipHash;
    }

    public String getUsername() {
        return username;
    }

    public UUID getToken() {
        return token;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
