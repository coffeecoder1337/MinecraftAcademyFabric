package net.st1ch.minecraftacademy.commands.room;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
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
                .then(CommandManager.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            for (RoomType t : RoomType.values()) {
                                builder.suggest(t.name());
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());

                            String typeStr = StringArgumentType.getString(ctx, "type").toUpperCase();
                            RoomType type = RoomType.valueOf(typeStr);

                            // Box должна быть рассчитана по позиции игрока или случайно (тут placeholder)
                            Box roomBox = new Box(player.getBlockPos()).offset(new Vec3d(0, 10, 0)).expand(10);
                            Room room = roomManager.createRoom(player, type, roomBox);

                            roomService.joinRoom(player, token, room.getId(), Role.ADMIN, roomManager, roleManager);

                            return 1;
                        })));
    }
}
