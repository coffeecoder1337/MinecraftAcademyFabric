package net.st1ch.minecraftacademy.commands.entity;

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
import net.st1ch.minecraftacademy.entity.client.HiddenEntityManager;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class HideEntityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                UserManager userManager,
                                UserRoleManager userRoleManager) {

        dispatcher.register(CommandManager.literal("hide")
                .then(CommandManager.argument("target_type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("robot");
                            builder.suggest("player");
                            return builder.buildFuture();
                        })
                        .then(CommandManager.argument("target", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    ServerPlayerEntity inviter = ctx.getSource().getPlayer();

                                    User inviterUser = userManager.registerOrGetUser(inviter.getName().getString(), inviter.getIp(), inviter);
                                    String roomId = userRoleManager.getRoom(inviterUser.getToken());
                                    Room room = RoomManager.getInstance().getRoom(roomId);
                                    Map<UUID, Role> users = room.getParticipants();

                                    builder.suggest("all");
                                    for (UUID u: users.keySet()) {
                                        builder.suggest(userManager.getByUUID(u).getName());
                                    }

                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayerEntity inviter = ctx.getSource().getPlayer();
                                    String targetType = StringArgumentType.getString(ctx, "target_type").toLowerCase();
                                    String targetName = StringArgumentType.getString(ctx, "target");

                                    if (!targetType.equals("robot") && !targetType.equals("player")) {
                                        inviter.sendMessage(Text.literal("Неверное значение параметра. Укажите robot или player."));
                                        return 0;
                                    }

                                    UUID inviterId = userManager.generateUUID(inviter.getName().getString(), inviter.getIp());
                                    String roomId = userRoleManager.getRoom(inviterId);

                                    if (roomId == null) {
                                        inviter.sendMessage(Text.literal("Вы не находитесь в комнате."));
                                        return 0;
                                    }
                                    Room room = RoomManager.getInstance().getRoom(roomId);

                                    if (targetName.equals("all")) {
                                        if (targetType.equals("player")) {
                                            HiddenEntityManager.hideAllPlayersExcept(inviter.getGameProfile().getId(), room.getParticipantsUUIDs());
                                            inviter.sendMessage(Text.literal("Пользователи скрыты."));
                                        } else {
                                            RobotEntity inviterRobot = room.getRobotByPlayer(inviterId);
                                            Collection<RobotEntity> robots = room.getAllRobots();
                                            Collection<UUID> robotIDs = new ArrayList<>();
                                            for (RobotEntity robot: robots) {
                                                robotIDs.add(robot.getUuid());
                                            }

                                            HiddenEntityManager.hideAllRobotsExcept(inviterRobot.getUuid(), robotIDs);
                                            inviter.sendMessage(Text.literal("Роботы скрыты."));
                                        }
                                        return 1;
                                    }

                                    ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(targetName);
                                    if (target == null) {
                                        inviter.sendMessage(Text.literal("Игрок не найден."));
                                        return 0;
                                    }

                                    UUID targetToken = userManager.generateUUID(target.getName().getString(), target.getIp());

                                    if (targetType.equals("player")) {
                                        HiddenEntityManager.hidePlayer(target.getGameProfile().getId());
                                        inviter.sendMessage(Text.literal("Пользователь скрыт."));
                                    } else {
                                        HiddenEntityManager.hideRobot(room.getRobotByPlayer(targetToken).getUuid());
                                        inviter.sendMessage(Text.literal("Робот скрыт."));
                                    }

                                    return 1;
                                }))));
    }
}



