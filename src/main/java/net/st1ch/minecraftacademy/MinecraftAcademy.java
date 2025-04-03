package net.st1ch.minecraftacademy;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.st1ch.minecraftacademy.commands.ModCommands;
import net.st1ch.minecraftacademy.entity.ModEntities;
import net.st1ch.minecraftacademy.entity.custom.RobotEntity;
import net.st1ch.minecraftacademy.events.ModEvents;
import net.st1ch.minecraftacademy.item.ModItems;
import net.st1ch.minecraftacademy.network.UDPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftAcademy implements ModInitializer {
	public static final String MOD_ID = "minecraft-academy";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");

		new Thread(UDPServer::startServer).start();

		ModItems.registerModItems();
		ModEntities.registerModEntities();
		ModEvents.registerModEvents();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ModCommands.register(dispatcher);
		});

		FabricDefaultAttributeRegistry.register(ModEntities.ROBOT, RobotEntity.createRobotAttributes());
	}
}