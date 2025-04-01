package net.st1ch.minecraftacademy;

import net.fabricmc.api.ModInitializer;

import net.st1ch.minecraftacademy.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftAcademy implements ModInitializer {
	public static final String MOD_ID = "minecraft-academy";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");

		ModItems.registerModItems();
	}
}