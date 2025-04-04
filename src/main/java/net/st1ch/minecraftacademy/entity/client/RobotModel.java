package net.st1ch.minecraftacademy.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;


public class RobotModel<T extends RobotEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer ROBOT = new EntityModelLayer(
            Identifier.of(MinecraftAcademy.MOD_ID, "robot"),
            "main"
    );

    private final ModelPart robot;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart left_arm;
    private final ModelPart left_arm_long;
    private final ModelPart left_hand;
    private final ModelPart right_arm;
    private final ModelPart right_arm_long;
    private final ModelPart right_hand;
    private final ModelPart legs;
    private final ModelPart left_leg;
    private final ModelPart right_leg;
    private final ModelPart hitbox;
    public RobotModel(ModelPart root) {
        this.robot = root.getChild("robot");
        this.head = this.robot.getChild("head");
        this.body = this.robot.getChild("body");
        this.left_arm = this.body.getChild("left_arm");
        this.left_arm_long = this.left_arm.getChild("left_arm_long");
        this.left_hand = this.left_arm.getChild("left_hand");
        this.right_arm = this.body.getChild("right_arm");
        this.right_arm_long = this.right_arm.getChild("right_arm_long");
        this.right_hand = this.right_arm.getChild("right_hand");
        this.legs = this.robot.getChild("legs");
        this.left_leg = this.legs.getChild("left_leg");
        this.right_leg = this.legs.getChild("right_leg");
        this.hitbox = root.getChild("hitbox");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData robot = modelPartData.addChild("robot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 27.0F, 0.0F));

        ModelPartData head = robot.addChild("head", ModelPartBuilder.create().uv(0, 36).cuboid(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -15.0F, -3.0F));

        ModelPartData body = robot.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -15.0F, -6.0F, 8.0F, 9.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left_arm = body.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, -11.4F, -2.0F));

        ModelPartData left_arm_long = left_arm.addChild("left_arm_long", ModelPartBuilder.create().uv(37, 4).cuboid(0.0F, -0.6F, -5.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData left_hand = left_arm.addChild("left_hand", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.6F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(33, 41).cuboid(-0.5F, -0.6F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 40).cuboid(-0.5F, 0.4F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 0.0F, -5.0F));

        ModelPartData right_arm = body.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, -11.4F, -2.0F));

        ModelPartData right_arm_long = right_arm.addChild("right_arm_long", ModelPartBuilder.create().uv(37, 4).cuboid(-1.0F, -0.6F, -5.0F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_hand = right_arm.addChild("right_hand", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.6F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(33, 41).cuboid(-0.5F, -0.6F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 40).cuboid(-0.5F, 0.4F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 0.0F, -5.0F));

        ModelPartData legs = robot.addChild("legs", ModelPartBuilder.create().uv(36, 0).cuboid(-5.0F, 0.0F, -1.0F, 10.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, -1.0F));

        ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(0, 19).cuboid(5.0F, -8.0F, -7.0F, 3.0F, 5.0F, 12.0F, new Dilation(0.0F))
                .uv(18, 36).cuboid(5.0F, -7.0F, -8.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 36).cuboid(5.0F, -7.0F, 5.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 1.0F));

        ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(30, 19).cuboid(6.0F, -8.0F, -7.0F, 3.0F, 5.0F, 12.0F, new Dilation(0.0F))
                .uv(34, 36).cuboid(6.0F, -7.0F, -8.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 40).cuboid(6.0F, -7.0F, 5.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-14.0F, 6.0F, 1.0F));

        ModelPartData hitbox = modelPartData.addChild("hitbox", ModelPartBuilder.create().uv(78, 89).cuboid(-5.0F, -10.0F, -6.0F, 10.0F, 16.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 18.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(RobotEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

        this.animateMovement(RobotAnimations.LOOKING_AROUND, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.movingHandsAnimationState, RobotAnimations.MOVING_HANDS, ageInTicks, 1f);
    }

    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30f, 30f);
        headPitch = MathHelper.clamp(headPitch, -25f, 40f);

        this.head.yaw = headYaw * 0.017453292f; // 0.017453292 = PI / 2
        this.head.pitch = headPitch * 0.017453292f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        robot.render(matrices, vertexConsumer, light, overlay, color);
        //hitbox.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return robot;
    }
}
