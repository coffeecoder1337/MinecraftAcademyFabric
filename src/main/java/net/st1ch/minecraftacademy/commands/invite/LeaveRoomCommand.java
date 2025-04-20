package net.st1ch.minecraftacademy.commands.invite;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.room.RoomManager;
import net.st1ch.minecraftacademy.room.RoomService;

import java.util.UUID;

public class LeaveRoomCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                UserManager userManager,
                                RoomManager roomManager,
                                UserRoleManager roleManager) {

        dispatcher.register(CommandManager.literal("leave_room")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    String name = player.getName().getString();
                    String ip = player.getIp();
                    UUID token = userManager.generateUUID(name, ip);

                    RoomService.leaveRoom(player, token, roomManager, roleManager);

                    return 1;
                }));
    }
}
