package me.theguynextdoor.tribesnextdoor.datatypes.civilisation;

import java.util.HashMap;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.CivilisationUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Civilisation implements ConfigurationSerializable {

	private String name;
	private Tribe chiefTribe;
	private Map<String, Tribe> tribes;
	private CivilisationUtils civilisationUtils;
	private ResidentUtils residentUtils;

	public Civilisation(String name) {
		this.name = name;
		tribes = new HashMap<String, Tribe>();
		civilisationUtils = TribesNextDoor.getInstance().getCivilisationUtils();
		residentUtils = TribesNextDoor.getInstance().getResidentUtils();
	}

	public void create(CommandSender sender, Tribe chiefTribe) {
		this.chiefTribe = chiefTribe;
		addTribe(chiefTribe);
		civilisationUtils.addCivilisation(this);

		Bukkit.broadcastMessage(ChatColor.GOLD + "The civilisation '" + ChatColor.WHITE + getName() + ChatColor.GOLD + "' has risen with the leader " + ChatColor.WHITE
				+ chiefTribe.getChief());
	}

	public String getName() {
		return name;
	}

	public Tribe getChiefTribe() {
		return chiefTribe;
	}

	public Map<String, Tribe> getTribes() {
		return tribes;
	}

	public void addTribe(Tribe tribe) {
		if (!tribes.containsKey(tribe.getName())) {
			if (!tribe.inCivilisation()) {
				tribes.put(tribe.getName(), tribe);
				tribe.setCivilisation(this);
			}
		}
	}

	public void removeTribe(Tribe tribe) {
		if (tribes.containsKey(tribe.getName())) {
			tribes.remove(tribe.getName());
			tribe.setCivilisation(null);
		}
	}

	public void setChiefTribe(Tribe tribe) {
		if (tribes.containsKey(tribe.getName()))
			this.chiefTribe = tribe;
	}

	public void sendCivilisationMessage(String message) {
		for (Tribe tribe : tribes.values()) {
			tribe.sendMessageToResidents(message);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("ChiefTribe", chiefTribe.getName());

		return map;
	}

	public String getLeader() {
		return getChiefTribe().getChief();
	}

	public void delete() {
		for (Tribe tribe : getTribes().values()) {
			for (Resident resident : tribe.getResidents().values()) {
				resident.setInCivChat(false);
			}
			tribe.setCivilisation(null);
		}

		String msg = ChatColor.GOLD + "Your tribe has been invited to join the civilisation " + ChatColor.WHITE + getName();

		for (Resident resident : residentUtils.getResidents()) {
			if (resident.hasLoginMessage(msg)) {
				resident.removeLoginMessage(msg);
			}
		}

		civilisationUtils.removeCivilisation(this);
	}

	public void invite(Tribe tribe) {
		tribe.addInvite(this);

		if (Bukkit.getPlayerExact(tribe.getChief()) != null) {
			Bukkit.getPlayerExact(tribe.getChief()).sendMessage(
					ChatColor.GOLD + "Your tribe has been invited to join the civilisation '" + ChatColor.WHITE + getName() + ChatColor.GOLD + "'");
			Bukkit.getPlayerExact(tribe.getChief()).sendMessage(
					ChatColor.GOLD + "Use the command '" + ChatColor.WHITE + "/civ <accept/decline> " + tribe.getName() + ChatColor.GOLD + "to accept/decline this invite");
		}
	}

}
