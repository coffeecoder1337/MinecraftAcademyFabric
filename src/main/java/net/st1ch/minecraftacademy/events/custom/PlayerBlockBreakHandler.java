package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

import java.util.UUID;

public class PlayerBlockBreakHandler {
    public static void register(
            UserManager userManager,
            UserRoleManager roleManager,
            RoomManager roomManager
    ) {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient) {
                UUID token = userManager.generateUUID(player.getName().getString(), ((ServerPlayerEntity) player).getIp());
                String roomId = roleManager.getRoom(token);
                if (roomId == null) return true;

                Room room = roomManager.getRoom(roomId);
                if (room != null && room.getBounds().contains(Vec3d.ofCenter(pos))) {
                    player.sendMessage(Text.literal("Вы не можете ломать блоки внутри комнаты."));
                    return false;
                }
            }

            return true;
        });
    }
}
