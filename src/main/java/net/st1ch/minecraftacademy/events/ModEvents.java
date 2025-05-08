package net.st1ch.minecraftacademy.events;

import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.events.custom.*;
import net.st1ch.minecraftacademy.room.InvitationManager;
import net.st1ch.minecraftacademy.room.PlacedBlockManager;
import net.st1ch.minecraftacademy.room.RoomBlockAccessController;
import net.st1ch.minecraftacademy.room.RoomManager;

public class ModEvents {
    public static void registerModEvents(
            InvitationManager invitationManager,
            UserManager userManager,
            UserRoleManager userRoleManager,
            RoomManager roomManager,
            RoomBlockAccessController blockAccessController
    ) {
        PlayerJoinHandler.register();
        PlayerMoveHandler.register(userManager, userRoleManager, roomManager);
        PlayerBlockBreakHandler.register(blockAccessController);
        PlayerUseBlockHandler.register(blockAccessController);
        PlayerLeaveHandler.register();
    }
}
