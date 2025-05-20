package net.st1ch.minecraftacademy.commands.room;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotManager;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;
import net.st1ch.minecraftacademy.room.RoomService;
import net.st1ch.minecraftacademy.room.RoomType;

import java.util.UUID;

public class CreateRoomCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                RoomManager roomManager,
                                UserManager userManager,
                                UserRoleManager roleManager,
                                RoomService roomService) {
        dispatcher.register(CommandManager.literal("create_room")
//                .then(CommandManager.argument("type", StringArgumentType.word())
//                        .suggests((ctx, builder) -> {
//                            for (RoomType t : RoomType.values()) {
//                                builder.suggest(t.name());
//                            }
//                            return builder.buildFuture();
//                        })
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());

//                            String typeStr = StringArgumentType.getString(ctx, "type").toUpperCase();
//                            RoomType type = RoomType.valueOf(typeStr);

                            String currentPlayerRoomID = roleManager.getRoom(token);

                            if (currentPlayerRoomID != null) {
                                player.sendMessage(Text.literal("Покиньте текущую комнату для создания новой."));
                                return 0;
                            }

                            Room room = roomManager.createRoom(player, RoomType.COMPETITION);
                            roomService.joinRoom(player, token, room.getId(), Role.ADMIN);
                            RobotManager.spawnForPlayer(player, token, room);

                            return 1;
                        }));
    }
}
