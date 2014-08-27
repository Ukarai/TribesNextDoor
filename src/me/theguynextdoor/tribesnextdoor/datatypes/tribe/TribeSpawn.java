package me.theguynextdoor.tribesnextdoor.datatypes.tribe;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: David Date: 8/1/13 Time: 8:12 AM
 */
public class TribeSpawn implements ConfigurationSerializable {

	private double x, y, z;
	private float pitch, yaw;
	private World world;
	private Location location;

	public TribeSpawn(Location loc) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.pitch = loc.getPitch();
		this.world = loc.getWorld();
		this.yaw = loc.getYaw();
		this.location = loc;
	}

	public TribeSpawn(Map<String, Object> map) {
		this.x = (Double) map.get("x");
		this.y = (Double) map.get("y");
		this.z = (Double) map.get("z");
		this.pitch = Float.intBitsToFloat((int) map.get("pitch"));
		this.yaw = Float.intBitsToFloat((int) map.get("yaw"));

		if (Bukkit.getWorld((String) map.get("world")) != null) {
			this.world = Bukkit.getWorld((String) map.get("world"));
		} else {
			TribesNextDoor.getInstance().getLogger().info("World '" + (String) map.get("world") + "' cannot be found");
			TribesNextDoor.getInstance().getLogger().info("Setting tribe spawn world to: " + Bukkit.getWorlds().get(0).getName());
			this.world = Bukkit.getWorlds().get(0);
		}
		this.location = new Location(world, x, y, z, yaw, pitch);
	}

	public TribeSpawn(String string) {
		String[] split = string.split(",");

		this.x = Double.parseDouble(split[1]);
		this.y = Double.parseDouble(split[2]);
		this.z = Double.parseDouble(split[3]);
		this.pitch = Float.parseFloat(split[4]);
		this.yaw = Float.parseFloat(split[5]);

		if (Bukkit.getWorld(split[0]) != null) {
			this.world = Bukkit.getWorld(split[0]);
		} else {
			TribesNextDoor.getInstance().getLogger().info("World '" + split[0] + "' cannot be found");
			TribesNextDoor.getInstance().getLogger().info("Setting tribe spawn world to: " + Bukkit.getWorlds().get(0).getName());
			this.world = Bukkit.getWorlds().get(0);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("x", x);
		map.put("y", y);
		map.put("z", z);
		map.put("pitch", Float.floatToIntBits(pitch));
		map.put("yaw", Float.floatToIntBits(yaw));
		map.put("world", world.getName());

		return map;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return world.getName() + "," + Math.round(x) + "," + Math.round(y) + "," + Math.round(z) + "," + Math.round(pitch) + "," + Math.round(yaw);
	}
}
