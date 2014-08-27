package me.theguynextdoor.tribesnextdoor.datatypes.tribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.ConfigUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * User: David Date: 6/7/13 Time: 2:21 PM
 */
public class Tribe implements ConfigurationSerializable {

	private TribesNextDoor plugin;
	private TribeUtils tribeUtils;
	private ResidentUtils residentUtils;
	private ChunkUtils chunkUtils;
	private ConfigUtils configUtils;
	private String name;
	private String chief;
	private String maker;
	private TribeSettings settings;
	private Map<String, Resident> residents;
	private Map<String, Resident> elders;
	private String worldName;
	private TribeSpawn spawn;
	private Civilisation civilisation;
	private List<Civilisation> invites;
	private int bonusClaims;

	//
	private Map<String, Outpost> outposts;

	public Tribe(String name) {
		this.name = name;
		plugin = TribesNextDoor.getInstance();
		tribeUtils = plugin.getTribeUtils();
		residentUtils = plugin.getResidentUtils();
		chunkUtils = plugin.getChunkUtils();
		configUtils = plugin.getConfigUtils();
		settings = new TribeSettings();
		residents = new HashMap<String, Resident>();
		elders = new HashMap<String, Resident>();
		civilisation = null;
		invites = new ArrayList<Civilisation>();
		outposts = new HashMap<String, Outpost>();
	}

	public void create(CommandSender sender, String chief) {
		setMaker(sender.getName());
		setChief(chief);
		residentUtils.getResident(chief).setTribe(this);
		residents.put(chief, residentUtils.getResident(chief));
		tribeUtils.addTribe(this);

		Bukkit.broadcastMessage(ChatColor.GOLD + "The tribe '" + ChatColor.WHITE + (configUtils.showUnderscore() ? getName() : getName().replace('_', ' ')) + ChatColor.GOLD
				+ "' has been made with the chief " + ChatColor.WHITE + getChief() + ChatColor.GOLD + "!");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChief() {
		return chief;
	}

	public void setChief(String chief) {
		this.chief = chief;

		if (elders.containsKey(chief)) {
			elders.remove(chief);
		}
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public Map<String, Resident> getResidents() {
		return residents;
	}

	public Map<String, Resident> getElders() {
		return elders;
	}

	public TribeSettings getSettings() {
		return settings;
	}

	public void addToTribe(String name) {
		if (residentUtils.isResident(name)) {
			Resident resident = residentUtils.getResident(name);

			if (!resident.inTribe()) {
				residents.put(name, resident);
				resident.setTribe(this);

				sendMessageToResidents(name + " has joined the tribe!");
			}
		}
	}

	public void addResident(String name) {
		if (residentUtils.isResident(name)) {
			Resident resident = residentUtils.getResident(name);

			residents.put(name, resident);
			resident.setTribe(this);
		}
	}

	public void addElder(String name) {
		if (residents.containsKey(name)) {
			if (!elders.containsKey(name)) {
				elders.put(name, residents.get(name));

				sendMessageToResidents(name + " is now an elder!");
			}
		}
	}

	public void sendMessageToResidents(String message) {
		for (Resident resident : residents.values()) {
			resident.sendMessage(message);
		}
	}

	public void invite(Resident resident) {
		resident.addInvite(this);

		if (Bukkit.getPlayer(resident.getName()) != null) {
			Bukkit.getPlayer(resident.getName()).sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "You have been invited to join the tribe '" + getName() + "'");
			Bukkit.getPlayer(resident.getName()).sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "Use '/t accept " + getName() + "' to accept this invite");
		} else {
			String msg = TribesNextDoor.PREFIX + ChatColor.AQUA + "You have been invited to join the tribe '" + getName() + "'";
			resident.addLoginMessage(msg);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("Chief", getChief());
		map.put("Maker", getMaker());
		map.put("Residents", new ArrayList<String>(getResidents().keySet()));
		map.put("Elders", new ArrayList<String>(getElders().keySet()));
		map.put("Settings", settings.serialize());
		map.put("Claimed", chunkUtils.getChunks(this));
		map.put("Spawn", spawn.serialize());
		map.put("Civilisation", civilisation != null ? civilisation.getName() : "");
		map.put("BonusClaims", getBonusClaims());

		return map;
	}

	public void removeResident(Resident resident) {
		if (residents.containsKey(resident.getName())) {
			residents.remove(resident.getName());
			resident.setTribe(null);

			if (elders.containsKey(resident.getName())) {
				elders.remove(resident.getName());
				residentUtils.getResident(name).setAutclaim(false);
			}

			chunkUtils.checkClaimAmount(this);
			resident.setAutclaim(false);
		}
	}

	public void removeElder(String name) {
		if (elders.containsKey(name)) {
			elders.remove(name);
			residentUtils.getResident(name).setAutclaim(false);
		}
	}

	public void delete() {
		for (Resident resident : residentUtils.getResidents()) {
			if (resident.inTribe() && resident.getTribe().getName().equals(getName())) {
				resident.setTribe(null);
			}
			if (resident.hasInvite(getName())) {
				resident.removeInvite(this);
			}
			String msg = TribesNextDoor.PREFIX + ChatColor.AQUA + "You have been invited to join the tribe '" + getName() + "'";

			if (resident.hasLoginMessage(msg)) {
				resident.removeLoginMessage(msg);
			}
			
			resident.setAutclaim(false);
		}

		chunkUtils.removeClaims(this);
		tribeUtils.removeTribe(this);
	}

	public void setSettings(TribeSettings settings) {
		this.settings = settings;
	}

	public void setWorld(String worldName) {
		this.worldName = worldName;
	}

	public void setSpawn(TribeSpawn spawn) {
		this.spawn = spawn;
	}

	public void setSpawn(Location location) {
		this.spawn = new TribeSpawn(location);
		this.worldName = spawn.getLocation().getWorld().getName();
	}

	public TribeSpawn getSpawn() {
		return spawn;
	}

	public String getWorldName() {
		return worldName;
	}

	public void rename(String name2) {
		String oldName = getName();
		boolean inCiv = inCivilisation();

		if (inCiv) {
			Civilisation civ = getCivilisation();

			civ.removeTribe(this);
		}
		tribeUtils.removeTribe(this);

		setName(name2);
		tribeUtils.addTribe(this);
		if (inCiv)
			getCivilisation().addTribe(this);

		Bukkit.broadcastMessage(ChatColor.GOLD + "The tribe " + ChatColor.WHITE + oldName + ChatColor.GOLD + " shall now go by the name of " + ChatColor.WHITE + getName());
	}

	public Civilisation getCivilisation() {
		return civilisation;
	}

	public void setCivilisation(Civilisation civilisation) {
		this.civilisation = civilisation;
	}

	public boolean inCivilisation() {
		return civilisation != null;
	}

	public void addInvite(Civilisation civ) {
		invites.add(civ);

		String msg = ChatColor.GOLD + "Your tribe has been invited to join the civilisation " + ChatColor.WHITE + civ.getName();
		String msg2 = ChatColor.GOLD + "Use " + ChatColor.WHITE + "/civ accept/decline " + civ.getName() + ChatColor.GOLD + " to accept/decline (respectively)";

		if (Bukkit.getPlayerExact(chief) != null) {
			Bukkit.getPlayerExact(chief).sendMessage(msg);
			Bukkit.getPlayerExact(chief).sendMessage(msg2);
		} else {
			residentUtils.getResident(chief).addLoginMessage(msg);
			residentUtils.getResident(chief).addLoginMessage(msg2);
		}
	}

	public void removeInvite(Civilisation civ) {
		invites.remove(civ);
	}

	public void acceptInvite(Civilisation civ) {
		if (invites.contains(civ)) {
			civ.addTribe(this);
			removeInvite(civ);
			civ.sendCivilisationMessage(ChatColor.GOLD + "The tribe " + ChatColor.WHITE + getName() + ChatColor.GOLD + " has joined the civilisation");
		}
	}

	public boolean hasInvite(Civilisation civ) {
		return invites.contains(civ);
	}

	public boolean hasMultipleInvites() {
		return invites.size() > 1;
	}

	public List<Civilisation> getInvites() {
		return invites;
	}

	public int getNumberOfInvites() {
		return invites.size();
	}

	public boolean hasInvites() {
		return getNumberOfInvites() > 0;
	}

	public Map<String, Outpost> getOutposts() {
		return outposts;
	}

	public int getBonusClaims() {
		return bonusClaims;
	}

	public void addBonusClaims(int x) {
		bonusClaims += x;
	}

	public void removeBonusClaims(int x) {
		bonusClaims -= x;
	}

}
