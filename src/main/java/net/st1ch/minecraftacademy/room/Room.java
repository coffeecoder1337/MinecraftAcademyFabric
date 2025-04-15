package net.st1ch.minecraftacademy.room;

import net.minecraft.util.math.Box;
import net.st1ch.minecraftacademy.auth.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Room {
    private final String id;
    private final RoomType type;
    private final Box bounds;
    private final Map<UUID, Role> participants;

    public Room(String id, RoomType type, Box bounds) {
        this.id = id;
        this.type = type;
        this.bounds = bounds;
        this.participants = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public RoomType getType() {
        return type;
    }

    public Box getBounds() {
        return bounds;
    }

    public void addParticipant(UUID playerUuid, Role role) {
        participants.put(playerUuid, role);
    }

    public Role getRole(UUID playerUuid) {
        return participants.getOrDefault(playerUuid, Role.OBSERVER);
    }

    public boolean containsPlayer(UUID uuid) {
        return participants.containsKey(uuid);
    }

    public Map<UUID, Role> getParticipants() {
        return participants;
    }
}

