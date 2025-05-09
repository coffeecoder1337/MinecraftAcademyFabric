package net.st1ch.minecraftacademy.blocks.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

public class RobotSpawnBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public RobotSpawnBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;

        ServerWorld serverWorld = (ServerWorld) world;
        Direction facing = state.get(RobotSpawnBlock.FACING); // предполагаем, что блок поддерживает Property FACING

        Room room = RoomManager.getInstance().getRoomByPosition(pos);
        if (room != null) {
            room.setRobotSpawnPoint(pos);
            room.setRobotSpawnFacing(facing);

            // Перемещаем роботов
            for (RobotEntity robot : room.getAllRobots()) {
                BlockPos spawn = pos.up(); // спавн на блок выше
                robot.refreshPositionAndAngles(
                        spawn.getX() + 0.5,
                        spawn.getY(),
                        spawn.getZ() + 0.5,
                        directionToYaw(facing),
                        0f
                );
            }
        }
    }

    public float directionToYaw(Direction direction) {
        return switch (direction) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f;
        };
    }
}

