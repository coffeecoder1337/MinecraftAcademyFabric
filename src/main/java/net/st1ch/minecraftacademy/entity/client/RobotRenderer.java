package net.st1ch.minecraftacademy.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

public class RobotRenderer extends MobEntityRenderer<RobotEntity, RobotModel<RobotEntity>> {
    public RobotRenderer(EntityRendererFactory.Context context) {
        super(context, new RobotModel<>(context.getPart(RobotModel.ROBOT)), 0.5f);
    }

    @Override
    public Identifier getTexture(RobotEntity entity) {
        return Identifier.of(MinecraftAcademy.MOD_ID, "textures/entity/robot/robot.png");
    }

    @Override
    public void render(RobotEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.scale(1.5f, 1.5f, 1.5f);
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
