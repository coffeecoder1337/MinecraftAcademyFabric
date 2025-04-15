package net.st1ch.minecraftacademy;

import net.minecraft.client.MinecraftClient;

public class ClientContext {
    private static MinecraftClient client;

    public static void setClient(MinecraftClient mc) {
        client = mc;
    }

    public static MinecraftClient getClient() {
        return client;
    }
}