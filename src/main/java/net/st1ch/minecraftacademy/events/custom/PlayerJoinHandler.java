package net.st1ch.minecraftacademy.events.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.st1ch.minecraftacademy.auth.User;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;

public class PlayerJoinHandler {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            String ip = player.getIp(); // может быть нужно брать из SocketAddress
            String name = player.getGameProfile().getName();

            User user = userManager.registerOrGetUser(name, ip);
            user.setOnline(true);

            player.sendMessage(Text.literal("Ваш токен: " + user.getToken()));
        });
//        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
//            server.execute(() -> {
//                ServerPlayerEntity player = handler.getPlayer();
//                System.out.println("ID игрока: " + player.getId());
//                ServerWorld world = player.getServerWorld();
//
//                // Устанавливаем точку спавна игрока
//                BlockPos spawnPos = new BlockPos(100, 100, 100); // Координаты спавна
//                player.teleport(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0);
//
//                // Создаём мини-спавн (например, из блоков)
//                buildMiniSpawn(world, spawnPos);
//
//                // Спавним робота рядом с игроком
//                RobotEntity robot = new RobotEntity(ModEntities.ROBOT, world);
//                robot.refreshPositionAndAngles(spawnPos.getX() + 1, spawnPos.getY(), spawnPos.getZ() + 1, 0, 0);
//                String robotID = robot.getUuidAsString();
//                world.spawnEntity(robot);
////            UDPServer.registerRobot(robotID, robot);
//
//                // Отправляем сообщение в чат с ID робота
//                player.sendMessage(Text.of("Ваш робот создан!" ), false);
//                robot.sendRobotIdToChat(player, robotID);
//            });
//        });
    }



    private static void buildMiniSpawn(ServerWorld world, BlockPos pos) {
        // Простая постройка вокруг точки спавна (например, из камня)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                world.setBlockState(pos.add(x, -1, z), net.minecraft.block.Blocks.STONE.getDefaultState());
            }
        }
    }
}
