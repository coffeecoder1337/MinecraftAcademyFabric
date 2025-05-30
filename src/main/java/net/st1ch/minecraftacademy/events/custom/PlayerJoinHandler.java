package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.st1ch.minecraftacademy.auth.User;

import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;

public class PlayerJoinHandler {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            String ip = player.getIp(); // может быть нужно брать из SocketAddress
            String name = player.getName().getString();

            User user = userManager.registerOrGetUser(name, ip, player);
            user.setOnline(true);
            user.sendTokenMessage(player);

        });
    }
}
