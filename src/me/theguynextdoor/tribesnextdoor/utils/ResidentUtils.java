package me.theguynextdoor.tribesnextdoor.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.backends.Backend;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;

/**
 * User: David Date: 6/7/13 Time: 2:08 PM
 */
public class ResidentUtils {
	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	/**
	 * Hashmap containing all residents objects <br>
	 * Key - Player name <br>
	 * Value - Resident object
	 */
	private Map<String, Resident> residents;

	/**
	 * Class constructor
	 */
	public ResidentUtils() {
		plugin = TribesNextDoor.getInstance();
		residents = new HashMap<String, Resident>();
	}

	/**
	 * Checks if there is a resident object for given name
	 * 
	 * @param name
	 *            - Name of player to check
	 * 
	 * @return Whether there is a stored resident object for given name
	 */
	public boolean isResident(String name) {
		return residents.containsKey(name);
	}

	/**
	 * Gets the resident object for a given player name
	 * 
	 * @param name
	 *            - Name of player to get resident object of
	 * 
	 * @return Resident object associated with player name
	 */
	public Resident getResident(String name) {
		return residents.get(name);
	}

	/**
	 * Adds a resident to resident map
	 * 
	 * @param name
	 *            - Name of player/resident to add
	 */
	public void addResident(String name) {
		if (!isResident(name)) {
			Resident resident = new Resident(name, this);
			residents.put(name, resident);
		}
	}

	/**
	 * Load residents using backend
	 */
	public void loadResidents() {
		Backend backend = plugin.getBackend();

		residents = backend.loadResidents(this);
	}

	/**
	 * Method to get the total number of residents
	 * 
	 * @return Number of residents
	 */
	public int getAmountOfResidentsLoaded() {
		return residents.size();
	}

	/**
	 * Method to get residents map
	 * 
	 * @return Resident map
	 */
	public Collection<Resident> getResidents() {
		return residents.values();
	}
}
