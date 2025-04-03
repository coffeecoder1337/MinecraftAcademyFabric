package net.st1ch.minecraftacademy;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.client.RobotModel;
import net.st1ch.minecraftacademy.entity.client.RobotRenderer;

public class MinecraftAcademyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(RobotModel.ROBOT, RobotModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ROBOT, RobotRenderer::new);
    }
}
