package net.st1ch.minecraftacademy.entity.custom.robot;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.network.UDPServer;
import net.st1ch.minecraftacademy.room.Room;

import java.util.UUID;

public class RobotSpawner {
    public static void spawnForPlayer(ServerPlayerEntity player, UUID token, Room room) {
        ServerWorld world = player.getServerWorld();
        BlockPos spawn = room.getRobotSpawnPoint();

        RobotEntity robot = new RobotEntity(ModEntities.ROBOT, world);
        robot.setCustomName(Text.literal(player.getName().getString() + "Robot"));

        Direction facing = room.getRobotSpawnFacing();
        float yaw = facing.asRotation();

        robot.setPosition(Vec3d.ofCenter(spawn.up()));
        robot.setYaw(yaw);
        robot.setHeadYaw(yaw);
        robot.setBodyYaw(yaw);

//        UDPServer.registerRobot(token.toString(), robot);
        MinecraftAcademy.udpManager.registerRobot(token, robot);
        world.spawnEntity(robot);
        room.assignRobotToPlayer(token, robot);
    }

    public static void spawn(ServerWorld world, BlockPos spawn) {
        RobotEntity robot = new RobotEntity(ModEntities.ROBOT, world);
        UDPServer.registerRobot(robot.getUuidAsString(), robot);
        robot.setPosition(Vec3d.ofCenter(spawn));
        world.spawnEntity(robot);
    }
}
