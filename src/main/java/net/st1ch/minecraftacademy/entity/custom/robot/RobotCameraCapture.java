package net.st1ch.minecraftacademy.entity.custom.robot;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import static com.mojang.blaze3d.platform.GlConst.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11C.*;

public class RobotCameraCapture {
    private static boolean shouldCapture = false;
    private static RobotEntity robotToRender = null;

    public static void requestCapture(RobotEntity robot) {
        robotToRender = robot;
        shouldCapture = true;
    }

    public static void tryCapture() {
        if (!shouldCapture || robotToRender == null) return;

        shouldCapture = false;
        MinecraftClient client = MinecraftClient.getInstance();

        Framebuffer framebuffer = new SimpleFramebuffer(128, 128, true, MinecraftClient.IS_SYSTEM_MAC);
        framebuffer.setClearColor(0, 0, 0, 0);
        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

        Entity oldCamera = client.getCameraEntity();
        client.setCameraEntity(robotToRender);

        Camera camera = client.gameRenderer.getCamera();
        camera.update(client.world, robotToRender, false, false, 1.0F);

        Framebuffer mainFramebuffer = client.getFramebuffer();
        framebuffer.beginWrite(false);

        client.gameRenderer.renderWorld(client.getRenderTickCounter());

//        // glReadPixels должен быть на том же потоке
        NativeImage image = readFramebuffer(framebuffer);
        saveImage(image, "robot_capture.png");
        image.close();

        client.setCameraEntity(oldCamera);
        mainFramebuffer.beginWrite(false);
    }

    private static NativeImage readFramebuffer(Framebuffer framebuffer) {
        int width = framebuffer.textureWidth;
        int height = framebuffer.textureHeight;

        // Создаём изображение
        NativeImage image = new NativeImage(width, height, true);

        // Обязательно выполняем в GL-потоке!
        RenderSystem.assertOnRenderThread();

        // Привязываем framebuffer для чтения
        RenderSystem.bindTexture(framebuffer.getColorAttachment());
        RenderSystem.glBindBuffer(GL_READ_FRAMEBUFFER, framebuffer.fbo);

        // Выделяем буфер памяти вручную
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.malloc(width * height * 4); // RGBA по 1 байту

            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

//            try {
//                image.getBytes().put(buffer);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            image.mirrorVertically();
        }

        return image;
    }

    private static void saveImage(NativeImage image, String name) {
        File file = new File("screenshots/" + name);
        try {
            Files.createDirectories(file.getParentFile().toPath());
            image.writeTo(file.toPath());
            System.out.println("Saved robot view: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
