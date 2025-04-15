package net.st1ch.minecraftacademy.room;

import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomManager {
    private final Map<String, Room> rooms = new HashMap<>();

    public Room createRoom(RoomType type, Box bounds) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Room room = new Room(id, type, bounds);
        rooms.put(id, room);
        return room;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    public Map<String, Room> getAllRooms() {
        return rooms;
    }
}

