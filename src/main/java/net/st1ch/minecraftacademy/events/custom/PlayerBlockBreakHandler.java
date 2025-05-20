package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.room.RoomBlockAccessController;

public class PlayerBlockBreakHandler {
    public static void register(
            RoomBlockAccessController blockAccessController
    ) {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return true;

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            if (!blockAccessController.canBreak(serverPlayer, pos)) {
//                player.sendMessage(Text.literal("Вы не можете ломать этот блок."));
                return false;
            }

            blockAccessController.removePlacedBlock(pos);
            return true;


        });
    }
}
