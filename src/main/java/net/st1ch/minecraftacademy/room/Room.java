package net.st1ch.minecraftacademy.room;

import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.Role;

import java.util.*;

public class Room {
    private final String id;
    private final RoomType type;
    private final Box bounds;
    private final Map<UUID, Role> participants;
    private final List<BlockPos> wallBlocks;

    public Room(String id, RoomType type, Box bounds) {
        this.id = id;
        this.type = type;
        this.bounds = bounds;
        this.participants = new HashMap<>();
        this.wallBlocks = new ArrayList<BlockPos>();
    }

    public String getId() {
        return id;
    }

    public RoomType getType() {
        return type;
    }

    public Box getBounds() {
        return bounds;
    }

    public void buildRoom(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        BlockPos min = new BlockPos((int)bounds.minX, (int)bounds.minY, (int)bounds.minZ);
        BlockPos max = new BlockPos((int)bounds.maxX, (int)bounds.maxY, (int)bounds.maxZ);

        for (int x = min.getX(); x < max.getX(); x++) {
            for (int y = min.getY(); y < max.getY(); y++) {
                for (int z = min.getZ(); z < max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    boolean isWall = x == min.getX() || x == max.getX() - 1 ||
                            y == min.getY() || y == max.getY() - 1 ||
                            z == min.getZ() || z == max.getZ() - 1;

                    if (isWall) {
                        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
                        this.wallBlocks.add(pos);
                    }
                    else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    public void destroyRoom(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        BlockPos min = new BlockPos((int)bounds.minX, (int)bounds.minY, (int)bounds.minZ);
        BlockPos max = new BlockPos((int)bounds.maxX, (int)bounds.maxY, (int)bounds.maxZ);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public boolean isRoomWallBlock(BlockPos pos){
        System.out.println("walls = " + this.wallBlocks);
        System.out.println("can break = " + this.wallBlocks.contains(pos));
        return this.wallBlocks.contains(pos);
    }

    public boolean isRoomBlock(BlockPos pos){
        return this.getBounds().contains(Vec3d.ofCenter(pos));
    }

    public void addParticipant(UUID playerUuid, Role role) {
        participants.put(playerUuid, role);
    }

    public void removeParticipant(UUID token) {
        participants.remove(token);
    }

    public Role getRole(UUID playerUuid) {
        return participants.getOrDefault(playerUuid, Role.OBSERVER);
    }

    public boolean containsPlayer(UUID uuid) {
        return participants.containsKey(uuid);
    }

    public Map<UUID, Role> getParticipants() {
        return participants;
    }


}

