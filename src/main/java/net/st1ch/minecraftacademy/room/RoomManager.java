package net.st1ch.minecraftacademy.room;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomManager {
    private final Map<String, Room> rooms = new HashMap<>();

    public Room createRoom(ServerPlayerEntity player, RoomType type, Box bounds) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Room room = new Room(id, type, bounds);
        room.buildRoom(player);
        rooms.put(id, room);
        return room;
    }

    public void removeRoom(String id){
        rooms.remove(id);
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

    public boolean isRoomBlock(BlockPos pos){
        for (Map.Entry<String, Room> entry: rooms.entrySet()){
            Room room = entry.getValue();
            if (room.isRoomBlock(pos)) return true;
        }
        return false;
    }

}

