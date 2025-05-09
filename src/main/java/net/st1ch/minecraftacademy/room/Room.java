package net.st1ch.minecraftacademy.room;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.blocks.ModBlocks;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

import java.util.*;

public class Room {
    private final String id;
    private final RoomType type;
    private final Box bounds;
    private final Map<UUID, Role> participants;
    private final List<BlockPos> wallBlocks;
    private boolean globalControlAllowed = true;
    private final Set<UUID> individuallyAllowed = new HashSet<>();
    private final Map<UUID, RobotEntity> playerRobots = new HashMap<>();
    private BlockPos robotSpawnBlock;
    private Direction robotSpawnFacing = Direction.SOUTH;

    public Room(String id, RoomType type, Box bounds) {
        this.id = id;
        this.type = type;
        this.bounds = bounds;
        this.participants = new HashMap<>();
        this.wallBlocks = new ArrayList<BlockPos>();
        this.robotSpawnBlock = null;
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

                    boolean isFloor = (min.getX() + 1 < x && x < max.getX() - 1) &&
                            (min.getZ() + 1 < z && z < max.getZ() - 1) &&
                            (y == min.getY() + 1);

                    Vec3d center = bounds.getCenter();
                    boolean isRobotSpawn = x == (int)center.x && y == min.getY() + 1 && z == (int)center.z;

                    if (isWall) {
                        world.setBlockState(pos, Blocks.GLASS.getDefaultState());
                        this.wallBlocks.add(pos);
                    } else if (isFloor && !isRobotSpawn) {
                        world.setBlockState(pos, Blocks.WHITE_WOOL.getDefaultState());
                    } else if (isRobotSpawn) {
                        Block spawnBlock = ModBlocks.ROBOT_SPAWN_BLOCK;
                        world.setBlockState(pos, spawnBlock.getDefaultState());
                        this.setRobotSpawnPoint(pos);
                    } else {
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

    public void assignRobotToPlayer(UUID token, RobotEntity robot) {
        // Удаляем старого робота игрока, если он уже существует
        RobotEntity previous = playerRobots.get(token);
        if (previous != null && !previous.isRemoved()) {
            // удаляем старую сущность
            previous.discard();
        }

        // Присваиваем нового робота игроку
        playerRobots.put(token, robot);

        // Сообщаем в чат
//        if (robot.getWorld() instanceof ServerWorld) {
//            ServerPlayerEntity player = ((ServerWorld) robot.getWorld()).getServer()
//                    .getPlayerManager().getPlayer(playerId);
//            if (player != null) {
////                player.sendMessage(Text.literal("Ваш робот успешно создан и привязан."), false);
//            }
//
//        }
    }

    public BlockPos getRobotSpawnPoint() {
        return this.robotSpawnBlock;
    }

    public void setRobotSpawnPoint(BlockPos block) {
        this.robotSpawnBlock = block;
    }

    public void setRobotSpawnFacing(Direction dir) {
        this.robotSpawnFacing = dir;
    }
    public Direction getRobotSpawnFacing() {
        return robotSpawnFacing;
    }

    public RobotEntity getRobotByPlayer(UUID token) {
        return playerRobots.get(token);
    }

    public void removeRobot(UUID token) {
        RobotEntity robot = playerRobots.remove(token);

        if (robot != null && !robot.isRemoved()) {
            robot.discard();
        }
    }

    public boolean canRun(UUID userId) {
        return globalControlAllowed || individuallyAllowed.contains(userId);
    }

    public void setGlobalControlAllowed(boolean allowed) {
        globalControlAllowed = allowed;
    }

    public void allowUser(UUID userId) {
        individuallyAllowed.add(userId);
    }

    public void disallowUser(UUID userId) {
        individuallyAllowed.remove(userId);
    }

    public Collection<RobotEntity> getAllRobots() {
        return this.playerRobots.values();
    }
}

