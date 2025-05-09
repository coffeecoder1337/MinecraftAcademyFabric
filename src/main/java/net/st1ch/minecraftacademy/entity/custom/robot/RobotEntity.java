package net.st1ch.minecraftacademy.entity.custom.robot;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.st1ch.minecraftacademy.network.UDPServer;

public class RobotEntity extends PathAwareEntity {
    // animations
    public final AnimationState movingHandsAnimationState = new AnimationState();
    private int movingHandsAnimationTimeout = 0;
    private final int movingHandsAnimationTicks = 25;

    // params
    private double rotationSpeed = 0;
    private double moveSpeed = 0;
    public RobotSensors sensors = new RobotSensors(this);
    private int printSensorsTimeout = 40;

    public RobotEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createRobotAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    private void setupAnimationStates() {
        if (this.movingHandsAnimationTimeout <= 0){
            this.movingHandsAnimationTimeout = movingHandsAnimationTicks;
            this.movingHandsAnimationState.start(this.age);
        } else {
            --this.movingHandsAnimationTimeout;
        }
    }

    public void setMovement(double rotationSpeed, double moveSpeed) {
        this.rotationSpeed = rotationSpeed;
        this.moveSpeed = moveSpeed;
    }

    public void sendRobotIdToChat(PlayerEntity player, String robotId) {
        Text robotIdText = Text.literal("ID робота: " + robotId)
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, robotId))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Щелкните, чтобы скопировать"))));

        player.sendMessage(robotIdText, false);
    }

    @Override
    public boolean isPushable() {
        return false;
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

//    @Override
//    public void travel(Vec3d movementInput) {
//        if (!this.isOnGround()) {
//            this.setVelocity(this.getVelocity().add(0, -0.04, 0)); // Гравитация
//        }
//    }

    private void move() {
        if (!this.isOnGround()) {
            this.setVelocity(this.getVelocity().add(0, -0.04, 0)); // gravity
        } else {
            // поворот со скоростью
            float newYaw = this.getYaw() + (float) this.rotationSpeed;

            // Нормализация yaw в пределах [-180, 180]
            if (newYaw > 180.0F) {
                newYaw -= 360.0F;
            } else if (newYaw < -180.0F) {
                newYaw += 360.0F;
            }

            this.setYaw(newYaw);
            this.setHeadYaw(newYaw);

            // move forward with some speed
            double radians = Math.toRadians(this.getYaw());
            double x = -Math.sin(radians) * this.moveSpeed;
            double z = Math.cos(radians) * this.moveSpeed;

            this.setVelocity(new Vec3d(x, this.getVelocity().y, z));
            this.velocityDirty = true;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
//            this.setupAnimationStates();
        } else {
            this.move();
            if (printSensorsTimeout <= 0) { System.out.println(this.sensors.getSensorData()); printSensorsTimeout=40; }
            else --printSensorsTimeout;
        }
    }
}

//// Спавним робота рядом с игроком
//CustomRobotEntity robot = new CustomRobotEntity(ModEntities.ROBOT, world);
//robot.refreshPositionAndAngles(spawnPos.getX() + 2, spawnPos.getY(), spawnPos.getZ() + 2, 0, 0);
//world.spawnEntity(robot);
//
//// Отправляем сообщение в чат с ID робота
//player.sendMessage(Text.of("Ваш робот создан! ID: " + robot.getUuidAsString()), false);
//
