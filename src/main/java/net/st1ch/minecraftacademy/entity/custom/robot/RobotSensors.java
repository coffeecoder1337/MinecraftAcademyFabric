package net.st1ch.minecraftacademy.entity.custom.robot;


import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

import static com.mojang.blaze3d.platform.GlConst.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11C.*;

public class RobotSensors {
    private final RobotEntity robot;
//    private final DatagramSocket socket;
//    private final InetAddress clientAddress;
//    private final int clientPort;

    public RobotSensors(RobotEntity robot) {
        this.robot = robot;
//        this.socket = socket;
//        this.clientAddress = clientAddress;
//        this.clientPort = clientPort;
    }

    public Map<String, Object> getSensorData() {
        Map<String, Object> data = new HashMap<>();
        data.put("distance_sensors", getDistanceSensors());
        data.put("lidar_2d", getLidar2D());
        data.put("lidar_3d", getLidar3D());
        data.put("color", getColorSensor());
        return data;
//        sendJson(data);
    }

    private List<Double> getDistanceSensors() {
        List<Double> distances = new ArrayList<>();
        double[] angles = {-30, 0, 30};
        for (double angle : angles) {
            distances.add(castRayDistance(angle));
        }
        return distances;
    }

    private List<Double> getLidar2D() {
        List<Double> distances = new ArrayList<>();
        for (int i = 0; i < 360; i += 10) {
            distances.add(castRayDistance(i));
        }
        return distances;
    }

    private List<List<Double>> getLidar3D() {
        List<List<Double>> distances = new ArrayList<>();
        for (int pitch = -30; pitch <= 30; pitch += 15) {
            List<Double> yaw_distances = new ArrayList<>();

            for (int yaw = 0; yaw < 360; yaw += 15) {
                yaw_distances.add(castRayDistance3D(yaw, pitch));
            }
            distances.add(yaw_distances);
        }
        return distances;
    }

    private int getColorSensor() {
        World world = robot.getWorld();
        BlockPos pos = robot.getBlockPos().down();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return getBlockBrightness(block);
    }

    private double castRayDistance(double angleOffset) {
        Vec3d hit = castRay(angleOffset);
        return hit.distanceTo(robot.getPos().add(0, robot.getStandingEyeHeight(), 0));
    }

    private Vec3d castRay(double angleOffset) {
        World world = robot.getWorld();
        Vec3d start = robot.getPos().add(0, robot.getStandingEyeHeight(), 0);
        double angle = Math.toRadians(robot.getYaw() + angleOffset);
        Vec3d direction = new Vec3d(-Math.sin(angle), 0, Math.cos(angle));
        Vec3d end = start.add(direction.multiply(20));

        HitResult result = raycastEntitiesAndBlocks(start, end, world);
        return result.getPos();
    }

    private double castRayDistance3D(double yaw, double pitch) {
        Vec3d hit = castRayVec3d(yaw, pitch);
        return hit.distanceTo(robot.getPos().add(0, robot.getStandingEyeHeight(), 0));
    }

    private Vec3d castRayVec3d(double yaw, double pitch) {
        World world = robot.getWorld();
        Vec3d start = robot.getPos().add(0, robot.getStandingEyeHeight(), 0);
        double ryaw = Math.toRadians(robot.getYaw() + yaw);
        double rpitch = Math.toRadians(pitch);
        double x = -Math.sin(ryaw) * Math.cos(rpitch);
        double y = -Math.sin(rpitch);
        double z = Math.cos(ryaw) * Math.cos(rpitch);
        Vec3d direction = new Vec3d(x, y, z);
        Vec3d end = start.add(direction.multiply(20));

        HitResult result = raycastEntitiesAndBlocks(start, end, world);
        return result.getPos();
    }

    private HitResult raycastEntitiesAndBlocks(Vec3d start, Vec3d end, World world) {
        HitResult blockResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, robot));
        Box box = new Box(start, end).expand(1.0);
        List<Entity> entities = world.getOtherEntities(robot, box);
        EntityHitResult closestEntity = null;
        double closestDistance = blockResult.getPos().squaredDistanceTo(start);

        for (Entity entity : entities) {
            Box entityBox = entity.getBoundingBox().expand(0.3);
            Optional<Vec3d> hit = entityBox.raycast(start, end);
            if (hit.isPresent()) {
                double dist = hit.get().squaredDistanceTo(start);
                if (dist < closestDistance) {
                    closestDistance = dist;
                    closestEntity = new EntityHitResult(entity, hit.get());
                }
            }
        }
        return closestEntity != null ? closestEntity : blockResult;
    }

    private int getBlockBrightness(Block block) {
        Map<Block, Integer> brightness = Map.of(
                net.minecraft.block.Blocks.WHITE_WOOL, 100,
                net.minecraft.block.Blocks.LIGHT_GRAY_WOOL, 75,
                net.minecraft.block.Blocks.GRAY_WOOL, 50,
                net.minecraft.block.Blocks.BLACK_WOOL, 0
        );
        return brightness.getOrDefault(block, 0);
    }

//    private void sendJson(Map<String, Object> data) throws IOException {
//        String json = new com.google.gson.Gson().toJson(data);
//        byte[] buffer = json.getBytes();
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
//        socket.send(packet);
//    }

    // Первый вариант захвата "изображения от лица робота"
//    public void sendFakeImage() throws IOException {
//        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
//        for (int y = 0; y < 64; y++) {
//            for (int x = 0; x < 64; x++) {
//                img.setRGB(x, y, (x * 4 << 16) | (y * 4 << 8));
//            }
//        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(img, "png", baos);
//        byte[] buffer = baos.toByteArray();
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
//        socket.send(packet);
//    }

    public void getCameraImage(){
        RobotCameraCapture.requestCapture(this.robot);
    }

    public void getCameraImage1(MinecraftClient client) {
        if (client.world == null) return;
        // Используем Framebuffer 128x128
        Framebuffer framebuffer = new SimpleFramebuffer(128, 128, true, MinecraftClient.IS_SYSTEM_MAC);
        framebuffer.setClearColor(0, 0, 0, 0);
        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

//        // Сохраняем текущую камеру
        Entity originalCamera = client.getCameraEntity();

        Camera customCamera = new Camera();
        customCamera.update(client.world, this.robot, false, false, 1.0F);

        Framebuffer mainFramebuffer = client.getFramebuffer();
        framebuffer.beginWrite(false);

        client.gameRenderer.renderWorld(client.getRenderTickCounter());

        // Сохраняем изображение
        RenderSystem.recordRenderCall(() -> {
            NativeImage image = readFramebuffer(framebuffer);
            System.out.println("Got image");

            saveImage(image, "robot_view.png");
        });



        client.setCameraEntity(originalCamera);
        mainFramebuffer.beginWrite(false);
    }

    private void saveImage(NativeImage image, String name) {
        File file = new File("screenshots/" + name);
        try {
            Files.createDirectories(file.getParentFile().toPath());
            image.writeTo(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private NativeImage readFramebuffer(Framebuffer framebuffer) {
        int width = framebuffer.textureWidth;
        int height = framebuffer.textureHeight;

        NativeImage image = new NativeImage(width, height, true);

        // Привязываем буфер для чтения
        RenderSystem.bindTexture(framebuffer.getColorAttachment());
        RenderSystem.glBindBuffer(GL_READ_FRAMEBUFFER, framebuffer.fbo);

        // Чтение пикселей из GPU
        try {
            glReadPixels(
                    0, 0,
                    width, height,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    ByteBuffer.wrap(image.getBytes())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Разворачиваем изображение по вертикали (OpenGL origin — bottom-left)
        image.mirrorVertically();

        return image;
    }
}