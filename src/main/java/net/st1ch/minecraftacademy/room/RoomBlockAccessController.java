package net.st1ch.minecraftacademy.room;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;

import java.util.UUID;

public class RoomBlockAccessController {

    private final PlacedBlockManager placedBlockManager;
    private final RoomManager roomManager;
    private final UserManager userManager;
    private final UserRoleManager roleManager;

    public RoomBlockAccessController(PlacedBlockManager placedBlockManager,
                                     RoomManager roomManager,
                                     UserManager userManager,
                                     UserRoleManager roleManager) {
        this.placedBlockManager = placedBlockManager;
        this.roomManager = roomManager;
        this.userManager = userManager;
        this.roleManager = roleManager;
    }

    public boolean canPlace(ServerPlayerEntity player, BlockPos pos) {
        UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());
        String roomId = roleManager.getRoom(token);
        Role role = roleManager.getRole(token);

        // игрок вне комнаты может ставить блоки только за ее пределами
        if (roomId == null) return !roomManager.isRoomBlock(pos);
        Room room = roomManager.getRoom(roomId);

        if (!room.isRoomBlock(pos)) return true; // вне комнаты

        if (room.getType() == RoomType.EDUCATION) return false;

        return role == Role.ADMIN || role == Role.OPERATOR;
    }

    public boolean canBreak(ServerPlayerEntity player, BlockPos pos) {
        UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());
        String roomId = roleManager.getRoom(token);
        Role role = roleManager.getRole(token);

        // если не в команте, то можно ломать блоки, не являющиеся блоками комнаты
        if (roomId == null) return !roomManager.isRoomBlock(pos);

        Room room = roomManager.getRoom(roomId);

        // за пределами комнаты
        if (!room.isRoomBlock(pos)) return true;

        if (room.getType() == RoomType.EDUCATION) {
            return placedBlockManager.isPlacedBy(pos, token);
        }

        // админам можно ломать любые блоки, но не блоки комнаты
        if (role == Role.ADMIN) {
            return !room.isRoomWallBlock(pos);
        }

        if (role == Role.OPERATOR) {
            return placedBlockManager.isPlacedBy(pos, token);
        }

        return false; // наблюдатель
    }

    public void recordPlacedBlock(ServerPlayerEntity player, BlockPos pos) {
        UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());
        String roomId = roleManager.getRoom(token);

        if (roomId != null) {
            Room room = roomManager.getRoom(roomId);
            if (room.isRoomBlock(pos)) {
                placedBlockManager.record(pos, token);
            }
        }
    }

    public void removePlacedBlock(BlockPos pos) {
        placedBlockManager.remove(pos);
    }
}

