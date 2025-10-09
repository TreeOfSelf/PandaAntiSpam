package me.TreeOfSelf.PandaAntiSpam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
	public long cooldownTime = 60000;
	public int messageLimit = 13;
	public String warningMessage = "Messaging too often.";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File("config/PandaAntiSpam.json");

	public static Config load() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				return GSON.fromJson(reader, Config.class);
			} catch (IOException e) {
				PandaAntiSpam.LOGGER.error("Failed to load config, using defaults", e);
			}
		}
		Config config = new Config();
		config.save();
		return config;
	}

	public void save() {
		try {
			CONFIG_FILE.getParentFile().mkdirs();
			try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			PandaAntiSpam.LOGGER.error("Failed to save config", e);
		}
	}
}

