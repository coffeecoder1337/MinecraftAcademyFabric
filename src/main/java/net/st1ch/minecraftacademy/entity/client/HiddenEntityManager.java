package net.st1ch.minecraftacademy.entity.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HiddenEntityManager {
    public static final Set<UUID> hiddenRobots = new HashSet<>();
    public static final Set<UUID> hiddenPlayers = new HashSet<>();

    public static void hideRobot(UUID token) {
        hiddenRobots.add(token);
    }

    public static void showRobot(UUID token) {
        hiddenRobots.remove(token);
    }

    public static void hidePlayer(UUID token) {
        hiddenPlayers.add(token);
    }

    public static void showPlayer(UUID token) {
        hiddenPlayers.remove(token);
    }

    public static void hideAllPlayersExcept(UUID token, Collection<UUID> players) {
        hiddenPlayers.clear();
        for (UUID p : players) {
            if (!p.equals(token)) hiddenPlayers.add(p);
        }
    }

    public static void hideAllRobotsExcept(UUID token, Collection<UUID> robots) {
        hiddenRobots.clear();
        for (UUID p : robots) {
            if (!p.equals(token)) hiddenPlayers.add(p);
        }
    }

    public static void showAllPlayers() {
        hiddenPlayers.clear();
    }

    public static void showAllRobots() {
        hiddenRobots.clear();
    }

    public static boolean isRobotHidden(UUID token) {
        return hiddenRobots.contains(token);
    }

    public static boolean isPlayerHidden(UUID token) {
        return hiddenPlayers.contains(token);
    }
}

