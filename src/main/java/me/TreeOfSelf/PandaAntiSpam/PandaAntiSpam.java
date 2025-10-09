package me.TreeOfSelf.PandaAntiSpam;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaAntiSpam implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("panda-anti-spam");
	public static Config config;

	@Override
	public void onInitialize() {
		config = Config.load();
		LOGGER.info("PandaAntiSpam Started!");
	}
}