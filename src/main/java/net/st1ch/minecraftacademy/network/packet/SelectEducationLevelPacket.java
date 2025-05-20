package net.st1ch.minecraftacademy.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.st1ch.minecraftacademy.MinecraftAcademy;

public record SelectEducationLevelPacket(String levelName) implements CustomPayload  {
    public static final Identifier SELECT_EDUCATION_LEVEL_PAYLOAD_ID = Identifier.of(
            MinecraftAcademy.MOD_ID,
            "select_education_level");
    public static final CustomPayload.Id<SelectEducationLevelPacket> ID = new CustomPayload.Id<>(SELECT_EDUCATION_LEVEL_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SelectEducationLevelPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SelectEducationLevelPacket::levelName,
            SelectEducationLevelPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
