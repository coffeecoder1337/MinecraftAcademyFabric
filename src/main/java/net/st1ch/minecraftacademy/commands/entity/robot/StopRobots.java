package net.st1ch.minecraftacademy.commands.entity.robot;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;


public class StopRobots {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stop_robots")
                .executes(context -> {
                    ServerWorld world = context.getSource().getWorld();

                    // Ищем всех роботов в мире и удаляем их
                    for (RobotEntity entity : world.getEntitiesByType(ModEntities.ROBOT, e -> true)) {
                        entity.setMovement(0, 0);
                    }

                    context.getSource().sendFeedback(() -> Text.of("Роботы остановлены"), false);
                    return 1;
                })
        );

    }


}
