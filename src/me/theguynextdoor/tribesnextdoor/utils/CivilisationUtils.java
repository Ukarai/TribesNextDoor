package me.theguynextdoor.tribesnextdoor.utils;

import java.util.HashMap;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;

/**
 * 
 * @author David
 *
 */
public class CivilisationUtils {

	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	/**
	 * Map containing all civilisations <br>
	 * Key - Civilisation name <br>
	 * Value - Civilisation object
	 */
	private Map<String, Civilisation> civilisations;

	/**
	 * Class Constructor
	 */
	public CivilisationUtils() {
		plugin = TribesNextDoor.getInstance();
		civilisations = new HashMap<String, Civilisation>();
	}

	/**
	 * Method to add a civilisation to the map
	 * 
	 * @param civilisation
	 *            - The civilisation object to be added
	 */
	public void addCivilisation(Civilisation civilisation) {
		civilisations.put(civilisation.getName(), civilisation);
	}

	/**
	 * Method to remove a civilisation from the map
	 * 
	 * @param civilisation
	 *            - The civiliisation object to be removed
	 */
	public void removeCivilisation(Civilisation civilisation) {
		civilisations.remove(civilisation.getName());
	}

	/**
	 * Method to load all civilisations from the backend
	 */
	public void loadCivilisations() {
		civilisations = plugin.getBackend().loadCivilisations();
	}

	/**
	 * Method to check if a civilisation name is valid
	 * 
	 * @param name
	 *            - The name to be checked
	 * 
	 * @return Whether the name is valid
	 */
	public boolean validName(String name) {
		boolean invalidChars = plugin.getTribeUtils().containsIllegals(name);
		boolean alreadyExists = civilisations.containsKey(name);

		return !invalidChars && !alreadyExists;
	}

	/**
	 * Method to get the civilisation map
	 * 
	 * @return Map containing all civilisations <br>
	 *         Key - Civilisation name <br>
	 *         Value - Civilisation object
	 */
	public Map<String, Civilisation> getCivilisations() {
		return civilisations;
	}

	/**
	 * Method to check is a civilisation exists with a given name
	 * 
	 * @param name
	 *            - Namme to be checked
	 * 
	 * @return Whether the name is currently in use
	 */
	public boolean isCivilisation(String name) {
		return civilisations.containsKey(name);
	}

	/**
	 * Method to get a civilisation with a given name
	 * 
	 * @param name
	 *            - Name of civilisation to get
	 * 
	 * @return Civilisation object
	 */
	public Civilisation getCivilisation(String name) {
		return civilisations.get(name);
	}
}
