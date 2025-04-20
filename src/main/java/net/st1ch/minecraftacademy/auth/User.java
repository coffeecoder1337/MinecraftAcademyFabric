package net.st1ch.minecraftacademy.auth;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.UUID;

public class User {
    private final String name;
    private final UUID token;
    private boolean online;

    public User(String name, UUID token) {
        this.name = name;
        this.token = token;
        this.online = true;
    }

    public UUID getToken() {
        return token;
    }

    private Text getTokenText() {
        return Text.literal("Ваш токен: " + token)
                .setStyle(Style.EMPTY.withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                token.toString()
                        ))
                        .withHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Text.literal("Щелкните, чтобы скопировать")
                                )
                        )
                );

    }

    public void sendTokenMessage(ServerPlayerEntity player) {
        player.sendMessage(getTokenText());
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
