package net.st1ch.minecraftacademy.commands.user;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.st1ch.minecraftacademy.auth.User;
import net.st1ch.minecraftacademy.auth.UserManager;

import java.util.UUID;

public class GetTokenCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, UserManager userManager) {
        dispatcher.register(CommandManager.literal("get_token")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    UUID token = userManager.generateUUID(player.getName().getString(), player.getIp());

                    User user = userManager.getByUUID(token);
                    user.sendTokenMessage(player);
                    return 1;
                }));
    }
}
