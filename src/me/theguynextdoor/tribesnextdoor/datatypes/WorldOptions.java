package me.theguynextdoor.tribesnextdoor.datatypes;

import java.io.File;
import java.io.IOException;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * @author David
 *
 */
public class WorldOptions {

	private File file;
	private FileConfiguration config;

	public WorldOptions(String world) {
		File folder = new File(TribesNextDoor.getInstance().getDataFolder() + File.separator + "World Options");
		folder.mkdirs();
		file = new File(folder, world + ".yml");

		config = YamlConfiguration.loadConfiguration(file);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config.addDefault("Tribe.Distance.From spawn", 500);
		config.addDefault("Tribe.Distance.From other tribes", 200);
		config.addDefault("TribesNextDoor.Enabled", true);

		config.options().copyDefaults(true);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int getTribeDistanceFromSpawn() {
		return config.getInt("Tribe.Distance.From spawn");
	}

	public int getTribeDistanceFromTribe() {
		return config.getInt("Tribe.Distance.From other tribes");
	}

	public boolean isTribesEnabled() {
		return config.getBoolean("TribesNextDoor.Enabled");
	}
}
