package net.st1ch.minecraftacademy.item;

import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.item.custom.MagickStickItem;
import net.st1ch.minecraftacademy.item.custom.TrainingBookItem;

public class ModItems {
    public static final Item SOME_ITEM = registerItem(
            "some_item",
            new Item(
                    new Item.Settings().food(
                            new FoodComponent.Builder().build()
                    )
            )
    );
    public static final Item MAGIC_STICK = registerItem(
            "magic_stick",
            new MagickStickItem(
                new Item.Settings().maxDamage(32)
            )
    );

    public static final Item ROBOT_SPAWN_EGG = registerItem("robot_spawn_egg",
            new SpawnEggItem(ModEntities.ROBOT, 0x000000, 0xffffff, new Item.Settings()));

    public static final Item TRAINING_BOOK = registerItem("training_book", new TrainingBookItem(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MinecraftAcademy.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MinecraftAcademy.LOGGER.info("Registering mod items for " + MinecraftAcademy.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(SOME_ITEM);
            fabricItemGroupEntries.add(MAGIC_STICK);
            fabricItemGroupEntries.add(TRAINING_BOOK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(ROBOT_SPAWN_EGG);

        });
    }
}
