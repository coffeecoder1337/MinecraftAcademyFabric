package net.st1ch.minecraftacademy.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

public class ModEntities {
    public static final EntityType<RobotEntity> ROBOT = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MinecraftAcademy.MOD_ID, "robot"),
            EntityType.Builder.create(RobotEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1f, 1f).build()
    );

    public static void registerModEntities () {
        MinecraftAcademy.LOGGER.info("Registering custom entities");
    }
}
