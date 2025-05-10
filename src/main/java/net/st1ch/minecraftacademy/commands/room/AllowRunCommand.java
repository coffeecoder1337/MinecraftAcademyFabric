package net.st1ch.minecraftacademy.commands.room;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.User;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.room.InvitationManager;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

import java.util.UUID;

public class AllowRunCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                UserManager userManager,
                                UserRoleManager userRoleManager) {

        dispatcher.register(CommandManager.literal("allow_run")
                .then(CommandManager.argument("target", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            ServerPlayerEntity inviter = ctx.getSource().getPlayer();

//                            User inviterUser = userManager.registerOrGetUser(inviter.getName().getString(), inviter.getIp(), inviter);
                            builder.suggest("all");
                            for (User u : userManager.getAllOnlineUsers()) {
                                builder.suggest(u.getName());
                            }
                            return builder.buildFuture();
                        })
                        .then(CommandManager.argument("value", StringArgumentType.word())
                                .suggests((ctx, builder) -> {

                                    builder.suggest("true");
                                    builder.suggest("false");

                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayerEntity inviter = ctx.getSource().getPlayer();
                                    String targetName = StringArgumentType.getString(ctx, "target");
                                    String valueStr = StringArgumentType.getString(ctx, "value").toLowerCase();

                                    if (!valueStr.equals("true") && !valueStr.equals("false")) {
                                        inviter.sendMessage(Text.literal("Неверное значение параметра. Укажите true или false."));
                                        return 0;
                                    }
                                    boolean value = valueStr.equals("true");

                                    UUID inviterId = userManager.generateUUID(inviter.getName().getString(), inviter.getIp());
                                    String roomId = userRoleManager.getRoom(inviterId);

                                    if (roomId == null) {
                                        inviter.sendMessage(Text.literal("Вы не находитесь в комнате."));
                                        return 0;
                                    }
                                    Room room = RoomManager.getInstance().getRoom(roomId);

                                    if (targetName.equals("all")) {
                                        room.setGlobalControlAllowed(value);
                                        inviter.sendMessage(Text.literal("Пользователи текущей комнаты теперь" + (value ? "" : " не") + " могут запускать роботов."));
                                        return 1;
                                    }

                                    ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(targetName);
                                    if (target == null) {
                                        inviter.sendMessage(Text.literal("Игрок не найден."));
                                        return 0;
                                    }

                                    UUID targetToken = userManager.generateUUID(target.getName().getString(), target.getIp());

                                    room.setUserCanRun(targetToken, value);
                                    target.sendMessage(Text.literal("Вам " + (value ? "разрешено" : "запрещено") + " запускать робота."));

                                    inviter.sendMessage(Text.literal("Вы " + (value ? "разрешили" : "запретили") + " игроку " + targetName + " запускать роботов."));
                                    return 1;
                                }))));
    }
}



