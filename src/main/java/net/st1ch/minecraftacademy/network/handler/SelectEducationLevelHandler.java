package net.st1ch.minecraftacademy.network.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.auth.User;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.education.EducationLevel;
import net.st1ch.minecraftacademy.education.EducationLevelLoader;
import net.st1ch.minecraftacademy.network.packet.SelectEducationLevelPacket;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;
import net.st1ch.minecraftacademy.room.RoomService;
import net.st1ch.minecraftacademy.room.RoomType;

import java.util.UUID;

import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;

public class SelectEducationLevelHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                SelectEducationLevelPacket.ID,
                (payload, context) -> {
                    String levelName = payload.levelName();
                    ServerPlayerEntity player = context.player();
                    // Получаем токен игрока
                    User user = userManager.getByPlayer(player);
                    if (user == null) return;
                    UUID token = user.getToken();

                    String currentPlayerRoomID = UserRoleManager.getInstance().getRoom(token);

                    if (currentPlayerRoomID != null) {
                        player.sendMessage(Text.literal("Покиньте текущую комнату для создания новой."));
                        return;
                    }
                    EducationLevel level = EducationLevelLoader.getLevel(levelName);
                    if (level == null) {
                        player.sendMessage(Text.literal("Данный уровень еще не создан"));
                        return;
                    }

                    // Создаем комнату с типом EDUCATION и заданным уровнем
                    int depth = level.layout.size() + 2;
                    int width = level.layout.getFirst().length() + 2;
                    Room room = RoomManager.getInstance().createRoom(player, RoomType.EDUCATION, width, depth);
                    room.addLabyrinth(level);
                    RoomService.getInstance().joinRoom(player, token, room.getId(), Role.ADMIN);

                }
        );
    }
}
