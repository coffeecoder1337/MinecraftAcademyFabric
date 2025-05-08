package net.st1ch.minecraftacademy.room;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlacedBlockManager {
    private final Map<BlockPos, UUID> placedBlocks = new HashMap<>();

    public void record(BlockPos pos, UUID userId) {
        placedBlocks.put(pos.toImmutable(), userId);
    }

    public UUID getPlacer(BlockPos pos) {
        return placedBlocks.get(pos);
    }

    public void remove(BlockPos pos) {
        placedBlocks.remove(pos);
    }

    public boolean isPlacedBy(BlockPos pos, UUID userId) {
        return userId.equals(placedBlocks.get(pos));
    }

    public boolean isTracked(BlockPos pos) {
        return placedBlocks.containsKey(pos);
    }
}

