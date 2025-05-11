package net.st1ch.minecraftacademy.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class UDPManager {
    private static final int PORT = 5005;

    private final DatagramSocket socket;
    private final BlockingQueue<UDPPacket> incomingPackets = new LinkedBlockingQueue<>();
    private final Map<UUID, ClientInfo> clients = new ConcurrentHashMap<>();
    private final Map<UUID, RobotEntity> robots = new ConcurrentHashMap<>();
    private boolean running = true;

    public UDPManager() throws SocketException {
        this.socket = new DatagramSocket(PORT);
        System.out.println("[UDP] Сервер запущен на порту " + PORT);
    }

    public void start() {
        new Thread(this::receiveLoop, "UDPReceiver").start();
        new Thread(this::processLoop, "UDPProcessor").start();
        new Thread(this::sensorBroadcastLoop, "SensorBroadcaster").start();
    }

    public void stop() {
        running = false;
        socket.close();
    }

    public void registerRobot(UUID token, RobotEntity robot) {
        robots.put(token, robot);
    }

    private void sensorBroadcastLoop() {
        while (running) {
            try {
                for (Map.Entry<UUID, ClientInfo> entry : clients.entrySet()) {
                    UUID token = entry.getKey();
                    RobotEntity robot = robots.get(token);
                    if (robot == null) return;

                    String roomId = UserRoleManager.getInstance().getRoom(token);
                    Room room = RoomManager.getInstance().getRoom(roomId);
                    boolean canRun = room != null && room.canRun(token);

                    ClientInfo client = entry.getValue();

                    if (!canRun) continue;

                    Map<String, Object> sensors = robot.sensors.getSensorData();
                    sensors.put("type", "sensors");

                    String sensorsJson = new Gson().toJson(sensors);

                    sendJson(sensorsJson, client.address, client.port);
                }

                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.err.println("[SENSORS] Прервано: " + e.getMessage());
                return;
            } catch (Exception e) {
                System.err.println("[SENSORS] Ошибка при отправке: " + e.getMessage());
            }
        }
    }

    private void receiveLoop() {
        byte[] buffer = new byte[8192];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                incomingPackets.put(new UDPPacket(packet.getAddress(), packet.getPort(), msg));
            } catch (Exception e) {
                System.err.println("[UDP] Ошибка приёма: " + e.getMessage());
            }
        }
    }

    private void processLoop() {
        while (running) {
            try {
                UDPPacket pkt = incomingPackets.take();

                Map<String, Object> data = new Gson().fromJson(
                        pkt.content(), new TypeToken<HashMap<String, Object>>() {}.getType()
                );

                String type = data.get("type").toString();
                UUID token = UUID.fromString(data.get("token").toString());

                switch (type) {
                    case "init" -> handleInit(pkt, token);
                    case "control" -> handleControl(pkt, token, data);
                    case "disconnect" -> handleDisconnect(token);
                }
            } catch (Exception e) {
                System.err.println("[UDP] Ошибка обработки пакета: " + e.getMessage());
            }
        }
    }

    private void handleDisconnect(UUID token) {
        RobotEntity robot = robots.get(token);
        if (robot == null) return;

        robot.setMovement(0, 0);
        clients.remove(token);
    }

    private void handleInit(UDPPacket pkt, UUID token) {
        RobotEntity robot = robots.get(token);
        if (robot == null) return;

        String roomId = UserRoleManager.getInstance().getRoom(token);
        Room room = RoomManager.getInstance().getRoom(roomId);
        boolean canRun = room != null && room.canRun(token);

        clients.put(token, new ClientInfo(pkt.address, pkt.port, canRun));

        Map<String, Object> reply = new HashMap<>();
        reply.put("type", "status");
        reply.put("status", canRun ? "granted" : "waiting");
        String replyJson = new Gson().toJson(reply);

        sendJson(replyJson, pkt.address, pkt.port);
    }

    private void handleControl(UDPPacket pkt, UUID token, Map<String, Object> json) {
        RobotEntity robot = robots.get(token);
        if (robot == null) return;

        String roomId = UserRoleManager.getInstance().getRoom(token);
        Room room = RoomManager.getInstance().getRoom(roomId);
        boolean canRun = room != null && room.canRun(token);

        if (!canRun) return;

        double linear = (double)json.get("linear_speed");
        double angular = (double)json.get("angular_speed");
        robot.setMovement(angular, linear);
    }

    public void sendJson(String json, InetAddress addr, int port) {
        try {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(data, data.length, addr, port));
        } catch (Exception e) {
            System.err.println("[UDP] Ошибка отправки: " + e.getMessage());
        }
    }

    public ClientInfo getClient(UUID token) {
        return clients.get(token);
    }

    public Collection<ClientInfo> getAllClients() {
        return clients.values();
    }

    private record UDPPacket(InetAddress address, int port, String content) {}

    public static class ClientInfo {
        public InetAddress address;
        public int port;
        public boolean canRun;

        public ClientInfo(InetAddress address, int port, boolean canRun) {
            this.address = address;
            this.port = port;
            this.canRun = canRun;
        }
    }
}
