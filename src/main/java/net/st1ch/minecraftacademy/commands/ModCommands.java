package net.st1ch.minecraftacademy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.ClientContext;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

import java.util.function.Supplier;

public class ModCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(CommandManager.literal("remove_all_robots")
                    .executes(context -> {
                        ServerWorld world = context.getSource().getWorld();

                        // Ищем всех роботов в мире и удаляем их
                        for (Entity entity : world.getEntitiesByType(ModEntities.ROBOT, e -> true)) {
                            entity.discard(); // Удаляем
                        }

                        context.getSource().sendFeedback(() -> Text.of("Роботы удалены"), false);
                        return 1;
                    })
            );

        dispatcher.register(CommandManager.literal("pov_robot")
                .executes(context -> {
                    ServerWorld world = context.getSource().getWorld();

                    // Ищем всех роботов в мире и удаляем их
                    RobotEntity robot = world.getEntitiesByType(ModEntities.ROBOT, e -> true).getFirst();
                    robot.sensors.getCameraImage();

                    context.getSource().sendFeedback(() -> Text.of("Изображение сохранено"), false);
                    return 1;
                })
        );
    }


}
