package me.sebastian420.PandaAntiSpam;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaAntiSpam implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("panda-anti-spam");

	@Override
	public void onInitialize() {
		LOGGER.info("PandaAntiSpam Started!");
	}
}