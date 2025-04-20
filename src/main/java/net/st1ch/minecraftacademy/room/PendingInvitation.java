package net.st1ch.minecraftacademy.room;

import net.st1ch.minecraftacademy.auth.Role;

import java.util.UUID;

public class PendingInvitation {
    private final String roomId;
    private final Role role;
    private final UUID inviter;

    public PendingInvitation(String roomId, Role role, UUID inviter) {
        this.roomId = roomId;
        this.role = role;
        this.inviter = inviter;
    }

    public String getRoomId() {
        return roomId;
    }

    public Role getRole() {
        return role;
    }

    public UUID getInviter() {
        return inviter;
    }
}

