package net.st1ch.minecraftacademy.commands.invite;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.room.*;

import java.util.UUID;

public class AcceptCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                InvitationManager invitationManager,
                                UserManager userManager,
                                UserRoleManager roleManager,
                                RoomManager roomManager) {
        dispatcher.register(CommandManager.literal("accept")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());

                    if (!invitationManager.hasInvite(token)) {
                        player.sendMessage(Text.literal("У вас нет активных приглашений."));
                        return 0;
                    }

                    PendingInvitation invite = invitationManager.get(token);
                    Room room = roomManager.getRoom(invite.getRoomId());

                    RoomService.joinRoom(player, token, room.getId(), invite.getRole(), roomManager, roleManager);

                    return 1;
                }));
    }
}
