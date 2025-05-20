package net.st1ch.minecraftacademy.commands.invite;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.User;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.room.InvitationManager;
import net.st1ch.minecraftacademy.room.PendingInvitation;

import java.util.UUID;

public class InviteCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                InvitationManager invitationManager,
                                UserManager userManager,
                                UserRoleManager userRoleManager) {

        dispatcher.register(CommandManager.literal("invite")
                .then(CommandManager.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            ServerPlayerEntity inviter = ctx.getSource().getPlayer();
                            User inviterUser = userManager.registerOrGetUser(inviter.getName().getString(), inviter.getIp(), inviter);

                            for (User u : userManager.getAllOnlineUsers()) {
                                if (u != inviterUser) builder.suggest(u.getName());
                            }
                            return builder.buildFuture();
                        })
                        .then(CommandManager.argument("role", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (Role r : Role.values()) {
                                        builder.suggest(r.name());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayerEntity inviter = ctx.getSource().getPlayer();
                                    String targetName = StringArgumentType.getString(ctx, "player");
                                    String roleStr = StringArgumentType.getString(ctx, "role").toUpperCase();
                                    Role role = Role.valueOf(roleStr);

                                    ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(targetName);
                                    if (target == null) {
                                        inviter.sendMessage(Text.literal("Игрок не найден."));
                                        return 0;
                                    }

                                    UUID inviterId = userManager.generateUUID(inviter.getName().getString(), inviter.getIp());
                                    UUID targetId = userManager.generateUUID(target.getName().getString(), target.getIp());

                                    if (inviterId.equals(targetId)) {
                                        inviter.sendMessage(Text.literal("Вы не можете пригласить себя."));
                                        return 0;
                                    }

                                    String roomId = userRoleManager.getRoom(inviterId);
                                    if (roomId == null) {
                                        inviter.sendMessage(Text.literal("Вы не находитесь в комнате."));
                                        return 0;
                                    }

                                    boolean invite = invitationManager.addInvite(targetId, new PendingInvitation(roomId, role, inviterId));
                                    if (!invite) {
                                        inviter.sendMessage(Text.literal("Приглашение не было отправлено."));
                                        return 0;

                                    }
                                    target.sendMessage(Text.literal("Вас пригласили в комнату " + roomId + " с ролью " + role.name() + ". Введите /accept или /deny."));

                                    inviter.sendMessage(Text.literal("Приглашение отправлено игроку " + targetName + "."));
                                    return 1;
                                }))));
    }
}


