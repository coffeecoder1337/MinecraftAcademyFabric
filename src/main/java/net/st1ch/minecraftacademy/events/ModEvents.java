package net.st1ch.minecraftacademy.events;

import net.st1ch.minecraftacademy.events.custom.PlayerJoinHandler;

public class ModEvents {
    public static void registerModEvents() {
        PlayerJoinHandler.register();
    }
}
