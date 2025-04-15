package net.st1ch.minecraftacademy.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRoleManager {
    private final Map<UUID, Role> userRoles = new HashMap<>();
    private final Map<UUID, String> userRooms = new HashMap<>();
    private final Map<UUID, UUID> userRobots = new HashMap<>();

    public void assignRole(UUID userToken, Role role) {
        userRoles.put(userToken, role);
    }

    public Role getRole(UUID userToken) {
        return userRoles.getOrDefault(userToken, Role.OBSERVER);
    }

    public void assignRoom(UUID userToken, String roomId) {
        userRooms.put(userToken, roomId);
    }

    public String getRoom(UUID userToken) {
        return userRooms.get(userToken);
    }

    public void assignRobot(UUID userToken, UUID robotId) {
        userRobots.put(userToken, robotId);
    }

    public UUID getRobot(UUID userToken) {
        return userRobots.get(userToken);
    }

    public void clear(UUID userToken) {
        userRoles.remove(userToken);
        userRooms.remove(userToken);
        userRobots.remove(userToken);
    }
}

