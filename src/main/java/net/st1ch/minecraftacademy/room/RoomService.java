package net.st1ch.minecraftacademy.room;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.User;
import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class RoomService {
    private final HashMap<UUID, Vec3d> playerPosition = new HashMap<>();
    private static RoomService instance;

    public static RoomService getInstance() {
        if (instance == null) instance = new RoomService();
        return instance;
    }

    public void joinRoom(ServerPlayerEntity player, UUID token, String roomId, Role role) {
        Room room = RoomManager.getInstance().getRoom(roomId);
        if (room == null) {
            player.sendMessage(Text.literal("Комната не найдена."));
            return;
        }

        UserRoleManager roleManager = UserRoleManager.getInstance();
        roleManager.assignRoom(token, roomId);
        roleManager.assignRole(token, role);
        room.addParticipant(token, player.getGameProfile().getId(), role);

        this.playerPosition.put(token, player.getPos());

        // Телепортировать в комнату
        Vec3d center = room.getBounds().getCenter();
        player.teleport(player.getServerWorld(), center.getX(), center.getY() + 1, center.getZ(), 0, 0);

        // Выдать блоки
        giveStarterBlocks(player);

        // Если у игрока нет робота то создать робота и привязать его к игроку
        if (room.getRobotByPlayer(token) == null && role != Role.OBSERVER) RobotManager.spawnForPlayer(player, token, room);

        player.sendMessage(Text.literal("Вы присоединились к комнате " + roomId + " как " + role.name()));
    }

    public void leaveRoom(ServerPlayerEntity player, UUID token) {
        UserRoleManager roleManager = UserRoleManager.getInstance();
        RoomManager roomManager = RoomManager.getInstance();
        String roomId = roleManager.getRoom(token);
        if (roomId == null) {
            player.sendMessage(Text.literal("Вы не находитесь в комнате."));
            return;
        }

        Role role = roleManager.getRole(token);
        Room room = roomManager.getRoom(roomId);

        room.removeParticipant(token, player.getGameProfile().getId());
        room.removeRobot(token);

        if (role == Role.ADMIN) {
            Collection<Role> allRoomRoles = room.getParticipants().values();
            Collection<UUID> allRoomPlayerTokens = room.getParticipants().keySet();
            if (!allRoomRoles.contains(Role.ADMIN)) {
                for (UUID roomPlayerToken: allRoomPlayerTokens) {
                    User roomUser = userManager.getByUUID(roomPlayerToken);
                    ServerPlayerEntity roomPlayer = roomUser.getPlayer();
                    this.leaveRoom(roomPlayer, roomPlayerToken);
                }
            }
        }

        if (room.getParticipants().isEmpty()) {
            room.destroyRoom(player);
            roomManager.removeRoom(room.getId());
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
