package me.theguynextdoor.tribesnextdoor.utils.data;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

/**
 * 
 * @author David
 *
 */
public class SaveTask implements Runnable {
	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;

	/**
	 * Class constructor
	 */
	public SaveTask() {
		plugin = TribesNextDoor.getInstance();
	}

	/**
	 * Method which saves all plugin related data
	 */
	@Override
	public void run() {
		plugin.getBackend().saveTribes();
		plugin.getBackend().saveCivilisations();
		plugin.getAdminUtils().save();
	}

}
