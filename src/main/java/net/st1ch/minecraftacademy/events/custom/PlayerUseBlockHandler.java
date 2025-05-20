package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.st1ch.minecraftacademy.room.RoomBlockAccessController;


public class PlayerUseBlockHandler {
    public static void register(
            RoomBlockAccessController blockAccessController
    ) {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;


            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            BlockPos pos = hitResult.getBlockPos().offset(hitResult.getSide());
            if (!blockAccessController.canPlace(serverPlayer, pos)) {
//                player.sendMessage(Text.literal("Вы не можете ставить блоки в комнате."));
                return ActionResult.FAIL;
            }

            blockAccessController.recordPlacedBlock(serverPlayer, pos);
            return ActionResult.PASS;
        });
    }
}
