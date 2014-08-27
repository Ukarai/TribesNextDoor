package me.theguynextdoor.tribesnextdoor.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * @author David
 *
 */
public class AdminUtils {

	/**
	 * List of players whom can see all tribe chat messages
	 */
	public List<String> spyTribeChat;
	/**
	 * List of players whom can see all civilisation chat messages
	 */
	public List<String> spyCivilisationChat;
	/**
	 * ResidentUtils instance
	 */
	private ResidentUtils residentUtils;
	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	/**
	 * File which points to location of file containing all players with spy
	 * chats toggled on
	 */
	private File fileLoc;
	/**
	 * YML file containing all names of players with spy chats toggled on
	 */
	private FileConfiguration file;

	/**
	 * Class constructor
	 */
	public AdminUtils() {
		plugin = TribesNextDoor.getInstance();
		spyTribeChat = new ArrayList<String>();
		spyCivilisationChat = new ArrayList<String>();
		residentUtils = TribesNextDoor.getInstance().getResidentUtils();
		fileLoc = new File(plugin.getDataFolder(), "ChatSpies.yml");
		if (!fileLoc.exists()) {
			try {
				fileLoc.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		file = YamlConfiguration.loadConfiguration(fileLoc);
	}

	/**
	 * Method to toggle tribe chat spy on <br>
	 * Players with it toggled on can see all tribe chat messages people send
	 * 
	 * @param name
	 *            - Name of player to toggle tribe chat spy for
	 */
	public void toggleTribeChatSpy(String name) {
		if (spyTribeChat.contains(name))
			spyTribeChat.remove(name);
		else
			spyTribeChat.add(name);
	}

	/**
	 * Method to toggle civilisation chat spy on <br>
	 * Players with it toggled on can see all civilisation chat messages people
	 * send
	 * 
	 * @param name
	 *            - Name of player to toggle civilisation chat spy for
	 */
	public void toggleCivilisationChatSpy(String name) {
		if (spyCivilisationChat.contains(name))
			spyCivilisationChat.remove(name);
		else
			spyCivilisationChat.add(name);
	}

	/**
	 * Method to send a message to all players with tribe chat spy toggled on
	 * 
	 * @param tribe
	 *            - Tribe the message is being sent from
	 * @param msg
	 *            - The message to be sent
	 */
	public void sendTribeSpyMessage(Tribe tribe, String msg) {
		for (String name : spyTribeChat) {
			if (Bukkit.getPlayerExact(name) != null) {
				Resident resident = residentUtils.getResident(name);

				if (!(resident.inTribe() && resident.getTribe().getName().equals(tribe.getName()))) {
					Bukkit.getPlayerExact(name).sendMessage(msg);
				}
			}
		}
	}

	/**
	 * Method to send a message to all players with civilisation chat spy
	 * toggled on
	 * 
	 * @param civ
	 *            - Civilisation the message is being sent from
	 * @param msg
	 *            - The message to be sent
	 */
	public void sendCivilisationSpyMessage(Civilisation civ, String msg) {
		for (String name : spyCivilisationChat) {
			if (Bukkit.getPlayerExact(name) != null) {
				Resident resident = residentUtils.getResident(name);

				if (!(resident.inTribe() && resident.getTribe().inCivilisation() && resident.getTribe().getCivilisation().getName().equals(civ.getName()))) {
					Bukkit.getPlayerExact(name).sendMessage(msg);
				}
			}
		}
	}

	/**
	 * Method to check if a player has tribe chat spy toggled on
	 * 
	 * @param name
	 *            - Name of player to check
	 * @return Whether the player has tribe chat spy toggled or not
	 */
	public boolean isTribeSpyToggled(String name) {
		return spyTribeChat.contains(name);
	}

	/**
	 * Method to check if a player has civilisation chat spy toggled on
	 * 
	 * @param name
	 *            - Name of player to check
	 * @return WWhether the player has civilisation chat spy toggled or not
	 */
	public boolean isCivSpyToggled(String name) {
		return spyCivilisationChat.contains(name);
	}

	/**
	 * Method to save players information to whether they have
	 * tribe/civilisation chat spy toggled on
	 */
	public void save() {
		for (String s : file.getValues(false).keySet()) {
			file.set(s, null);
		}

		for (String tribe : spyTribeChat) {
			file.set(tribe + ".TribeChat", true);
		}
		for (String civ : spyCivilisationChat) {
			file.set(civ + ".CivChat", true);
		}

		try {
			file.save(fileLoc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to load tribe/civilisation chat spies from file
	 */
	public void load() {
		for (String s : file.getValues(false).keySet()) {
			if (file.getBoolean(s + ".TribeChat", false)) {
				spyTribeChat.add(s);
			}
			if (file.getBoolean(s + ".CivChat", false)) {
				spyCivilisationChat.add(s);
			}
		}
	}
}
