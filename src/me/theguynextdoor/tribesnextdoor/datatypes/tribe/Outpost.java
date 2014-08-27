package me.theguynextdoor.tribesnextdoor.datatypes.tribe;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Outpost implements ConfigurationSerializable {

	private String name;
	private Chunk chunk;
	private Location spawn;

	public Outpost(String name, Chunk chunk, Location loc) {
		this.name = name;
		this.chunk = chunk;
		this.spawn = loc;
	}
	
	public Outpost(Map<String, Object> map) {
		this.name = (String) map.get("name");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}

	public Location getSpawn() {
		return spawn;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("x", spawn.getBlockX());
		map.put("y", spawn.getBlockY());
		map.put("z", spawn.getBlockZ());
		map.put("pitch", Float.floatToIntBits(spawn.getPitch()));
		map.put("yaw", Float.floatToIntBits(spawn.getYaw()));
		map.put("world", spawn.getWorld());
		return map;
	}

}
