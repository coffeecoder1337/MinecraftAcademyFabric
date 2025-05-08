package net.st1ch.minecraftacademy.room;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.UserRoleManager;

import java.util.HashMap;
import java.util.UUID;

public class RoomService {
    private final HashMap<UUID, Vec3d> playerPosition = new HashMap<>();

    public void joinRoom(ServerPlayerEntity player, UUID token, String roomId, Role role,
                                RoomManager roomManager, UserRoleManager roleManager) {
        Room room = roomManager.getRoom(roomId);
        if (room == null) {
            player.sendMessage(Text.literal("Комната не найдена."));
            return;
        }

        roleManager.assignRoom(token, roomId);
        roleManager.assignRole(token, role);
        room.addParticipant(token, role);

        this.playerPosition.put(token, player.getPos());

        // Телепортировать в комнату
        Vec3d center = room.getBounds().getCenter();
        player.teleport(player.getServerWorld(), center.getX(), center.getY() + 1, center.getZ(), 0, 0);

        // Выдать блоки
        giveStarterBlocks(player);

        player.sendMessage(Text.literal("Вы присоединились к комнате " + roomId + " как " + role.name()));
    }

    public void leaveRoom(ServerPlayerEntity player, UUID token,
                                 RoomManager roomManager, UserRoleManager roleManager) {
        String roomId = roleManager.getRoom(token);
        if (roomId == null) {
            player.sendMessage(Text.literal("Вы не находитесь в комнате."));
            return;
        }

        Room room = roomManager.getRoom(roomId);
        if (room != null) {
            room.removeParticipant(token);
            if (room.getParticipants().isEmpty()) {
                room.destroyRoom(player);
                roomManager.removeRoom(room.getId());
            }
        }

        roleManager.clear(token);

        // Очистка блоков
        clearWoolBlocks(player);


        // Телепорт обратно
        Vec3d spawn = this.playerPosition.remove(token);
        player.teleport(player.getServerWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);

        player.sendMessage(Text.literal("Вы покинули комнату " + roomId));
    }

    private static void giveStarterBlocks(ServerPlayerEntity player) {
        ItemStack[] blocks = {
                new ItemStack(Items.WHITE_WOOL, 64),
                new ItemStack(Items.LIGHT_GRAY_WOOL, 64),
                new ItemStack(Items.GRAY_WOOL, 64),
                new ItemStack(Items.BLACK_WOOL, 64)
        };
        for (ItemStack stack : blocks) {
            player.getInventory().insertStack(stack);
        }
    }

    private static void clearWoolBlocks(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.WHITE_WOOL ||
                    stack.getItem() == Items.LIGHT_GRAY_WOOL ||
                    stack.getItem() == Items.GRAY_WOOL ||
                    stack.getItem() == Items.BLACK_WOOL) {
                player.getInventory().removeStack(i);
            }
        }
    }
}
