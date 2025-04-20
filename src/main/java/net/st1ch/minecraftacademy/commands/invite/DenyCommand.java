package net.st1ch.minecraftacademy.commands.invite;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.room.InvitationManager;

import java.util.UUID;

public class DenyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                InvitationManager invitationManager,
                                UserManager userManager) {
        dispatcher.register(CommandManager.literal("deny")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    UUID playerId = userManager.generateUUID(player.getName().getString(), player.getIp());

                    if (!invitationManager.hasInvite(playerId)) {
                        player.sendMessage(Text.literal("Нет активных приглашений."));
                        return 0;
                    }

                    invitationManager.remove(playerId);
                    player.sendMessage(Text.literal("Приглашение отклонено."));
                    return 1;
                }));
    }
}

