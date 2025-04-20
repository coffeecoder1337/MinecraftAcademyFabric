package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

import java.util.UUID;

public class PlayerMoveHandler {
    public static void register(
            UserManager userManager,
            UserRoleManager roleManager,
            RoomManager roomManager) {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());
                String roomId = roleManager.getRoom(token);
                if (roomId == null) continue;

                Room room = roomManager.getRoom(roomId);
                if (room == null || !room.getBounds().contains(player.getPos())) {
                    player.sendMessage(Text.literal("Вы не можете выходить за пределы своей комнаты!"));
                    Vec3d center = room.getBounds().getCenter();
                    player.teleport(player.getServerWorld(), center.getX(), center.getY() + 1, center.getZ(), player.getYaw(), player.getPitch());
                }
            }
        });
    }
}
