package net.st1ch.minecraftacademy.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.st1ch.minecraftacademy.network.packet.SelectEducationLevelPacket;

public class PayloadC2SRegister {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(SelectEducationLevelPacket.ID, SelectEducationLevelPacket.CODEC);
    }
}
