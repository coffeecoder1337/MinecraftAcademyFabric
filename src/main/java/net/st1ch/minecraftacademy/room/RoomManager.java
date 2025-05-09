package net.st1ch.minecraftacademy.room;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoomManager {
    private static RoomManager instance;
    private final Map<String, Room> rooms = new HashMap<>();

    public static RoomManager getInstance() {
        if (instance == null) instance = new RoomManager();
        return instance;
    }

    public Box generateRoomBoundsSpiral(
            Collection<Room> existingRooms,
            BlockPos origin,
            int width, int height, int depth,
            int padding) {

        int step = width + padding;
        int x = 0, z = 0;
        int dx = 1, dz = 0;
        int segmentLength = 1;

        int directionIndex = 0;
        int stepsInCurrentSegment = 0;

        for (int i = 0; i < 1000; i++) {
            int wx = origin.getX() + x * step;
            int wy = origin.getY();
            int wz = origin.getZ() + z * step;

            Box candidate = new Box(
                    wx, wy, wz,
                    wx + width, wy + height, wz + depth
            );

            boolean intersects = false;
            for (Room room : existingRooms) {
                if (room.getBounds().intersects(candidate)) {
                    intersects = true;
                    break;
                }
            }

            if (!intersects) return candidate;

            // Спиральный шаг
            x += dx;
            z += dz;
            stepsInCurrentSegment++;

            if (stepsInCurrentSegment == segmentLength) {
                stepsInCurrentSegment = 0;

                // Повернуть на 90°
                int temp = dx;
                dx = -dz;
                dz = temp;

                directionIndex++;
                if (directionIndex % 2 == 0) {
                    segmentLength++;
                }
            }
        }

        throw new RuntimeException("Не удалось найти свободную позицию для комнаты.");
    }


    public Room createRoom(ServerPlayerEntity player, RoomType type) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Box bounds = generateRoomBoundsSpiral(
                this.getAllRooms(),
                new BlockPos(0, 64, 0),      // центр генерации
                64, 12, 64,              // размеры комнаты
                5                                    // отступ
        );
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

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public boolean isRoomBlock(BlockPos pos){
        for (Map.Entry<String, Room> entry: rooms.entrySet()){
            Room room = entry.getValue();
            if (room.isRoomBlock(pos)) return true;
        }
        return false;
    }

    public Room getRoomByPosition(BlockPos pos) {
        for (Map.Entry<String, Room> entry: rooms.entrySet()){
            Room room = entry.getValue();
            if (room.isRoomBlock(pos)) return room;
        }
        return null;
    }

}

