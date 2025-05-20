package net.st1ch.minecraftacademy.room;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.auth.Role;
import net.st1ch.minecraftacademy.blocks.ModBlocks;
import net.st1ch.minecraftacademy.education.EducationLevel;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import net.st1ch.minecraftacademy.network.UDPManager;

import java.util.*;

public class Room {
    private final String id;
    private ServerWorld world;
    private final RoomType type;
    private final Box bounds;
    private final Map<UUID, Role> participants;
    private final Collection<UUID> participantsUUIDs;
    private final Collection<BlockPos> interestBlocks;
    private final Collection<BlockPos> finishBlocks;

    private final List<BlockPos> wallBlocks;
    private final List<BlockPos> labyrinthBlocks;
    private final Map<UUID, Boolean> runPermissions = new HashMap<>();
    private final Map<UUID, RobotEntity> playerRobots = new HashMap<>();
    private BlockPos robotSpawnBlock;
    private Direction robotSpawnFacing = Direction.SOUTH;

    public Room(String id, RoomType type, Box bounds) {
        this.id = id;
        this.type = type;
        this.bounds = bounds;
        this.participants = new HashMap<>();
        this.participantsUUIDs = new ArrayList<>();
        this.wallBlocks = new ArrayList<BlockPos>();
        this.labyrinthBlocks = new ArrayList<BlockPos>();
        this.robotSpawnBlock = null;
        this.interestBlocks = new ArrayList<>();
        this.finishBlocks = new ArrayList<>();
        this.world = null;
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
        this.world = world;

        BlockPos min = new BlockPos((int)bounds.minX, (int)bounds.minY, (int)bounds.minZ);
        BlockPos max = new BlockPos((int)bounds.maxX, (int)bounds.maxY, (int)bounds.maxZ);

        for (int x = min.getX(); x < max.getX(); x++) {
            for (int y = min.getY(); y < max.getY(); y++) {
                for (int z = min.getZ(); z < max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    boolean isWall = x == min.getX() || x == max.getX() - 1 ||
                            y == min.getY() || y == max.getY() - 1 ||
                            z == min.getZ() || z == max.getZ() - 1;

                    boolean isFloor = (min.getX()  < x && x < max.getX()) &&
                            (min.getZ() < z && z < max.getZ()) &&
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
                        this.setRobotSpawnFacing(Direction.SOUTH); // hardcode
                    } else {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    public Room addLabyrinth(EducationLevel level) {
        if (this.world == null) return this;

        BlockPos start = new BlockPos((int) bounds.minX + 1, (int) bounds.minY + 2, (int) bounds.minZ + 1);
        for (int z = 0; z < level.layout.size(); z++) {
            String row = level.layout.get(z);
            for (int x = 0; x < row.length(); x++) {
                char symbol = row.charAt(x);
                BlockPos pos = start.add(x, 0, z);

                switch (symbol) {
                    case '#' -> {
                        world.setBlockState(pos, Blocks.WHITE_WOOL.getDefaultState());
                        world.setBlockState(pos.up(), Blocks.WHITE_WOOL.getDefaultState());
                        this.labyrinthBlocks.add(pos);
                        this.labyrinthBlocks.add(pos.up());
                    }
                    case 's' -> {
                        pos = pos.add(0, -1, 0);
                        Direction dir = Direction.byName(level.spawnFacing.toLowerCase());
                        world.setBlockState(pos, ModBlocks.ROBOT_SPAWN_BLOCK.getDefaultState().with(Properties.HORIZONTAL_FACING, dir.getOpposite()));

                        world.setBlockState(this.robotSpawnBlock, Blocks.WHITE_WOOL.getDefaultState());
                        this.setRobotSpawnPoint(pos);
                        this.setRobotSpawnFacing(dir);

                    }
                    case 'f' -> {
                        pos = pos.add(0, -1, 0);
                        world.setBlockState(pos, ModBlocks.ROBOT_FINISH_BLOCK.getDefaultState());
                    }
                    case 'i' -> {
                        pos = pos.add(0, -1, 0);
                        world.setBlockState(pos, ModBlocks.ROBOT_INTEREST_BLOCK.getDefaultState());
                    }
                    default -> world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }

        return this;
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
        return this.wallBlocks.contains(pos);
    }

    public boolean isLabyrinthBlock(BlockPos pos) {
        return this.labyrinthBlocks.contains(pos);
    }

    public boolean isRoomBlock(BlockPos pos){
        return this.getBounds().contains(Vec3d.ofCenter(pos));
    }

    public void addParticipant(UUID token, UUID playerUuid, Role role) {
        participants.put(token, role);
        participantsUUIDs.add(playerUuid);
    }

    public void removeParticipant(UUID token, UUID uuid) {
        participants.remove(token);
        participantsUUIDs.remove(uuid);
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

    public Collection<UUID> getParticipantsUUIDs() {
        return participantsUUIDs;
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
        runPermissions.put(token, true);
        robot.setOwnerToken(token);

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

    public boolean canRun(UUID token) {
        return runPermissions.get(token);
    }

    public void setGlobalControlAllowed(boolean allowed) {
        for (UUID token : playerRobots.keySet()) {
            runPermissions.put(token, allowed);
        }

        if (!allowed) {
            for (RobotEntity robot : getAllRobots()) {
                robot.setMovement(0, 0);
            }
        }

        // отправка всем игрокам разрешения/запрета на запуск
        for (UDPManager.ClientInfo client : MinecraftAcademy.udpManager.getAllClients()) {
            if (client == null) continue;

            client.canRun = allowed;
            Map<String, Object> response = new HashMap<>();
            response.put("type", "status");
            response.put("status", allowed ? "granted" : "waiting");
            String responseJson = new Gson().toJson(response);

            MinecraftAcademy.udpManager.sendJson(responseJson, client.address, client.port);
        }
    }

    public void setUserCanRun(UUID token, boolean value) {
        runPermissions.put(token, value);

        if (!value) {
            RobotEntity robot = getRobotByPlayer(token);
            robot.setMovement(0, 0);
        }

        // отправка конкретному игроку разрешения/запрета на запуск
        UDPManager.ClientInfo client = MinecraftAcademy.udpManager.getClient(token);
        if (client != null) {
            client.canRun = value;
            Map<String, Object> response = new HashMap<>();
            response.put("type", "status");
            response.put("status", value ? "granted" : "waiting");
            String responseJson = new Gson().toJson(response);

            MinecraftAcademy.udpManager.sendJson(responseJson, client.address, client.port);
        }
    }

    public Collection<RobotEntity> getAllRobots() {
        return this.playerRobots.values();
    }

    public void addInterestBlock(BlockPos pos) {
        this.interestBlocks.add(pos);
    }

    public void removeInterestBlock(BlockPos pos) {
        this.interestBlocks.remove(pos);
    }

    public void addFinishBlock(BlockPos pos) {
        this.finishBlocks.add(pos);
    }

    public void removeFinishBlock(BlockPos pos) {
        this.finishBlocks.remove(pos);
    }
}

