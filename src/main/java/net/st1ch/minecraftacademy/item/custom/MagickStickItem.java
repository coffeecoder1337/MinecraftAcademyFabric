package net.st1ch.minecraftacademy.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Map;

public class MagickStickItem extends Item {
    private static final Map <Block, Block> STICK_MAP =
            Map.of(
                    Blocks.STONE, Blocks.BRICKS,
                    Blocks.GOLD_BLOCK, Blocks.NETHERITE_BLOCK
            );

    public MagickStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Block clicked_block = world.getBlockState(context.getBlockPos()).getBlock();

        if (!STICK_MAP.containsKey(clicked_block)) return super.useOnBlock(context);
        if (world.isClient()) return super.useOnBlock(context);

        world.setBlockState(context.getBlockPos(), STICK_MAP.get(clicked_block).getDefaultState());

        context.getStack().damage(1, ((ServerWorld) world), ((ServerPlayerEntity) context.getPlayer()),
                item -> context.getPlayer().sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));

        world.playSound(null, context.getBlockPos(), SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS);

        return super.useOnBlock(context);
    }
}
