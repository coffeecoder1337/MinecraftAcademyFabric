package net.st1ch.minecraftacademy.room;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvitationManager {
    private final Map<UUID, PendingInvitation> invites = new HashMap<>();

    public void addInvite(UUID recipient, PendingInvitation invitation) {
        invites.put(recipient, invitation);
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
