package net.st1ch.minecraftacademy;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.st1ch.minecraftacademy.auth.UserManager;
import net.st1ch.minecraftacademy.auth.UserRoleManager;
import net.st1ch.minecraftacademy.blocks.ModBlocks;
import net.st1ch.minecraftacademy.commands.entity.robot.RemoveRobots;
import net.st1ch.minecraftacademy.commands.invite.AcceptCommand;
import net.st1ch.minecraftacademy.commands.invite.DenyCommand;
import net.st1ch.minecraftacademy.commands.invite.InviteCommand;
import net.st1ch.minecraftacademy.commands.invite.LeaveRoomCommand;
import net.st1ch.minecraftacademy.commands.room.CreateRoomCommand;
import net.st1ch.minecraftacademy.commands.user.GetTokenCommand;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.custom.robot.RobotEntity;
import net.st1ch.minecraftacademy.events.ModEvents;
import net.st1ch.minecraftacademy.item.ModItems;
import net.st1ch.minecraftacademy.network.UDPServer;
import net.st1ch.minecraftacademy.room.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftAcademy implements ModInitializer {
	public static final String MOD_ID = "minecraft-academy";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static String secret = "miencraftacademysuperkey";
	public static UserRoleManager userRoleManager = new UserRoleManager();
	public static RoomManager roomManager = RoomManager.getInstance();
	public static InvitationManager invitationManager = new InvitationManager();
	public static UserManager userManager = new UserManager(secret);
	public static RoomService roomService = new RoomService(roomManager, userRoleManager, userManager);
	public static PlacedBlockManager placedBlockManager = new PlacedBlockManager();
	public static RoomBlockAccessController blockAccessController = new RoomBlockAccessController(
			placedBlockManager,
			roomManager,
			userManager,
			userRoleManager);


	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");

		new Thread(UDPServer::startServer).start();

		ModItems.registerModItems();
		ModEntities.registerModEntities();
		ModBlocks.registerModBlocks();
		ModEvents.registerModEvents(invitationManager, userManager, userRoleManager, roomManager, blockAccessController);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RemoveRobots.register(dispatcher);
			InviteCommand.register(dispatcher, invitationManager, userManager, userRoleManager);
			AcceptCommand.register(dispatcher, invitationManager, userManager, userRoleManager, roomManager, roomService);
			DenyCommand.register(dispatcher, invitationManager, userManager);
			CreateRoomCommand.register(dispatcher, roomManager, userManager, userRoleManager, roomService);
			GetTokenCommand.register(dispatcher, userManager);
			LeaveRoomCommand.register(dispatcher, userManager, roomManager, userRoleManager, roomService);
		});

		FabricDefaultAttributeRegistry.register(ModEntities.ROBOT, RobotEntity.createRobotAttributes());
	}
}