package net.st1ch.minecraftacademy.room;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.st1ch.minecraftacademy.MinecraftAcademy.userManager;

public class InvitationManager {
    private final Map<UUID, PendingInvitation> invites = new HashMap<>();

    public boolean addInvite(UUID recipient, PendingInvitation invitation) {
        ServerPlayerEntity inviterPlayer = userManager.getByUUID(invitation.getInviter()).getPlayer();
        if (
                RoomManager.getInstance().getRoom(invitation.getRoomId()).getType() == RoomType.EDUCATION &&
                        invitation.getRole() != Role.OBSERVER) {
            inviterPlayer.sendMessage(Text.literal("В комнату обучения можно приглашать только наблюдателей (роль OBSERVER)."));
            return false;
        }

        invites.put(recipient, invitation);
        return true;
    }

    public boolean hasInvite(UUID recipient) {
        return invites.containsKey(recipient);
    }

    public PendingInvitation get(UUID recipient) {
        return invites.get(recipient);
    }

    public void remove(UUID recipient) {
        invites.remove(recipient);
    }
}
