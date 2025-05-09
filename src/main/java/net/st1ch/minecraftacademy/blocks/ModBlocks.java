package net.st1ch.minecraftacademy.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.blocks.custom.RobotSpawnBlock;

public class ModBlocks {
    public static final Block ROBOT_SPAWN_BLOCK = register("robot_spawn_block", new RobotSpawnBlock(Block.Settings.create().strength(1.0f)));

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, Identifier.of(MinecraftAcademy.MOD_ID, path), block);
        Registry.register(Registries.ITEM, Identifier.of(MinecraftAcademy.MOD_ID, path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void registerModBlocks () {
        MinecraftAcademy.LOGGER.info("Registering custom blocks");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.ROBOT_SPAWN_BLOCK);
        });
    }
}
