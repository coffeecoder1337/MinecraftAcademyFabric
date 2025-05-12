package net.st1ch.minecraftacademy.competition;

import net.st1ch.minecraftacademy.auth.User;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotManager;
import net.st1ch.minecraftacademy.room.Room;
import net.st1ch.minecraftacademy.room.RoomManager;

import java.util.UUID;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;

public class CompetitionManager {
    private static CompetitionManager instance;
    private final Map<UUID, CompetitionData> competitionData = new ConcurrentHashMap<>();

    public static CompetitionManager getInstance() {
        if (instance == null) instance = new CompetitionManager();
        return instance;
    }

    public void onRobotStart(RobotEntity robot) {
        UUID token = robot.getOwnerToken();
        CompetitionData data = new CompetitionData();
        data.start();
        competitionData.put(token, data);
    }

    public void onRobotFinish(RobotEntity robot) {
        UUID token = robot.getOwnerToken();
        CompetitionData data = competitionData.get(token);
        if (data == null) return;

        data.finish();

        long duration = data.getDuration();
        int points = data.getPointsCount();

        User user = userManager.getByUUID(token);
        ServerPlayerEntity player = user.getPlayer();
        if (token != null) {
            String result = String.format("Игрок %s завершил трассу за %.2f секунд, собрав %d точек!",
                    player.getName().getString(), duration / 1000.0, points);
            player.getServer().getPlayerManager().broadcast(Text.literal(result).formatted(Formatting.GREEN), false);

            String roomID = UserRoleManager.getInstance().getRoom(token);
            Room room = RoomManager.getInstance().getRoom(roomID);
            room.setUserCanRun(token, false);

            RobotManager.moveToSpawn(token, room);

            robot.setMovement(0, 0);
        }
    }

    public void restart(UUID token, RobotEntity robot) {
        String roomID = UserRoleManager.getInstance().getRoom(token);
        Room room = RoomManager.getInstance().getRoom(roomID);
        if (room == null) return;

        CompetitionData data = competitionData.get(token);
        if (data != null) {
            data.reset();
        } else {
            data = new CompetitionData();
            competitionData.put(token, data);
        }

        RobotManager.moveToSpawn(token, room);
        robot.setMovement(0, 0);
        robot.sensors.resetSensors();
//        robot.setStarted(false);
//        robot.setStartTime(0);
//        robot.setEndTime(0);

    }

    public CompetitionData getData(UUID token) {
        return competitionData.get(token);
    }

    public boolean hasStarted(UUID token) {
        CompetitionData data = competitionData.get(token);
        return data != null && data.isRunning();
    }
}
