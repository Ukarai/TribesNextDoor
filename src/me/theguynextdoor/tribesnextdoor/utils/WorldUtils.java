package me.theguynextdoor.tribesnextdoor.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.theguynextdoor.tribesnextdoor.datatypes.WorldOptions;

/**
 * 
 * @author David
 *
 */
public class WorldUtils {

	/**
	 * Hashmap to contain all worlds and their associated world options object
	 * <br> Key - Name of world
	 * <br> Value - WorldOptions object
	 */
	public Map<String, WorldOptions> worldOptions;

	public WorldUtils() {
		worldOptions = new HashMap<String, WorldOptions>();
	}

	/**
	 * Loads the world options for each world
	 */
	public void loadWorlds() {
		for (World world : Bukkit.getWorlds()) {
			worldOptions.put(world.getName(), new WorldOptions(world.getName()));
		}
	}

	/**
	 * Get the options for a given world
	 * 
	 * @param world
	 *            - Name of the world to load
	 * 
	 * @return The world options for that world
	 */
	public WorldOptions getWorldOptions(String world) {
		return worldOptions.get(world);
	}
}
