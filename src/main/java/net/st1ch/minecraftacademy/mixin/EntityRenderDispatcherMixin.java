package net.st1ch.minecraftacademy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.st1ch.minecraftacademy.entity.client.HiddenEntityManager;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity player) {
            if (HiddenEntityManager.isPlayerHidden(player.getGameProfile().getId())) cir.setReturnValue(false);
        }
        if (entity instanceof RobotEntity robot) {
            if (HiddenEntityManager.isRobotHidden(robot.getUuid())) cir.setReturnValue(false);
        }
    }
}