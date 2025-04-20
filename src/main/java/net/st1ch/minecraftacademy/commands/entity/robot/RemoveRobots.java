package net.st1ch.minecraftacademy.commands.entity.robot;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.entity.ModEntities;


public class RemoveRobots {
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

    }


}
