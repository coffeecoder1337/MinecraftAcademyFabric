package net.st1ch.minecraftacademy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.client.RobotModel;
import net.st1ch.minecraftacademy.entity.client.RobotRenderer;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotCameraCapture;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

public class MinecraftAcademyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(RobotModel.ROBOT, RobotModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ROBOT, RobotRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientContext.setClient(client);
        });

        HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {
            RobotCameraCapture.tryCapture();
        }));
    }
}
