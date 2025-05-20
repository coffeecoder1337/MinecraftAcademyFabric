package net.st1ch.minecraftacademy.item.custom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.st1ch.minecraftacademy.education.TrainingBookScreen;

public class TrainingBookItem extends Item {
    public TrainingBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        MinecraftClient.getInstance().setScreen(new TrainingBookScreen());
        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
