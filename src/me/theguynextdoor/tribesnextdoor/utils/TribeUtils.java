package me.theguynextdoor.tribesnextdoor.utils;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

/**
 * User: David Date: 6/7/13 Time: 2:21 PM
 */
public class TribeUtils {
	private TribesNextDoor plugin;
	private Map<String, Tribe> tribes;
	private ConfigUtils configUtils;

	public TribeUtils() {
		plugin = TribesNextDoor.getInstance();
		tribes = new HashMap<String, Tribe>();
		configUtils = plugin.getConfigUtils();
	}

	public boolean isTribe(String name) {
		for (String tribe : tribes.keySet()) {
			if (tribe.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public Tribe getTribe(String name) {
		for (String s : tribes.keySet()) {
			if (s.equalsIgnoreCase(name)) {
				return tribes.get(s);
			}
		}
		return null;
	}

	public void addTribe(Tribe tribe) {
		tribes.put(tribe.getName(), tribe);
	}

	public boolean isValidName(String name) {
		int length = name.length();

		if (isTribe(name) || containsIllegals(name) || length >= configUtils.getMaxTribeNameLength() || length <= configUtils.getMinTribeNameLength())
			return false;
		else
			return true;
	}

	public boolean containsIllegals(String toExamine) {
		Pattern pattern = Pattern.compile("\\[\\]-+|()\\{\\}\\*");
		Matcher matcher = pattern.matcher(toExamine);

		return matcher.find();
	}

	public void loadTribes() {
		tribes = plugin.getBackend().loadTribes();
	}

	public Map<String, Tribe> getTribes() {
		return tribes;
	}

	public void removeTribe(Tribe tribe) {
		if (tribes.containsKey(tribe.getName())) {
			tribes.remove(tribe.getName());
		}
	}

	public boolean tooCloseToOtherTribes(Location loc) {
		for (Tribe tribe : tribes.values()) {
			if (tribe.getSpawn().getLocation().getWorld().getName().equals(loc.getWorld().getName())) {
				if (tribe.getSpawn().getLocation().distance(loc) < plugin.getWorldUtils().getWorldOptions(loc.getWorld().getName()).getTribeDistanceFromTribe()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean tooCloseToSpawn(Location loc) {
		return loc.distance(loc.getWorld().getSpawnLocation()) < plugin.getWorldUtils().getWorldOptions(loc.getWorld().getName()).getTribeDistanceFromSpawn();
	}
}
