package net.st1ch.minecraftacademy.entity.custom;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RobotEntity extends PathAwareEntity {
    // animations
    public final AnimationState movingHandsAnimationState = new AnimationState();
    private int movingHandsAnimationTimeout = 0;
    private int movingHandsAnimationTicks = 25;

    // params
    private double rotationSpeed = 0.0;
    private double moveSpeed = 0.0;

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

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {
    }

//    @Override
//    public boolean damage(DamageSource source, float amount) {
//        return false;
//    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }

        // rotating with some speed
        this.setYaw(this.getYaw() + (float) this.rotationSpeed);

        // move forward with some speed
        double radians = Math.toRadians(this.getYaw());
        double x = -Math.sin(radians) * this.moveSpeed;
        double z = Math.cos(radians) * this.moveSpeed;

        this.setVelocity(new Vec3d(x, 0, z));
        this.velocityDirty = true;
    }
}

