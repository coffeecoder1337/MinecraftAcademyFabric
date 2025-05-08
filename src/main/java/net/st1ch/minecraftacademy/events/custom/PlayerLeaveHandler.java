package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.st1ch.minecraftacademy.auth.User;

import java.util.UUID;

import static net.st1ch.minecraftacademy.MinecraftAcademy.roomService;
import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;

public class PlayerLeaveHandler {
    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            String ip = player.getIp(); // может быть нужно брать из SocketAddress
            String name = player.getGameProfile().getName();

            UUID token = userManager.generateUUID(name, ip);
            User user = userManager.getByUUID(token);
            roomService.leaveRoom(player, token);
            user.setOnline(false);

        });
    }
}


