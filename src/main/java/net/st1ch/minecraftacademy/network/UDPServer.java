package net.st1ch.minecraftacademy.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServer {
    private static final int PORT = 5005;
    private static final Map<String, RobotEntity> robots = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    public static void registerRobot(String id, RobotEntity robot) {
        robots.put(id, robot);
    }

    public static void startServer() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("UDP сервер запущен на порту " + PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Получено сообщение " + message);

                try {
                    JsonObject json = gson.fromJson(message, JsonObject.class);
                    String id = json.get("id").getAsString();
                    double rotationSpeed = json.get("rotation_speed").getAsDouble();
                    double moveSpeed = json.get("move_speed").getAsDouble();

                    if (robots.containsKey(id)) {
                        RobotEntity robot = robots.get(id);
                        robot.setMovement(rotationSpeed, moveSpeed);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка обработки JSON: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace() ;
        }
    }
}
