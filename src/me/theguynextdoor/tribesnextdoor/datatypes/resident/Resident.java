package me.theguynextdoor.tribesnextdoor.datatypes.resident;

import java.util.ArrayList;
import java.util.List;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * User: David Date: 6/7/13 Time: 2:04 PM
 */
public class Resident {

	private String name;
	private Tribe tribe;
	private List<Tribe> invites;
	private boolean inTribeChat;
	private boolean inCivChat;
	private List<String> loginMessages;
	private boolean autoclaim;

	public Resident(String name, ResidentUtils residentUtils) {
		this.name = name;
		invites = new ArrayList<Tribe>();
		inTribeChat = false;
		loginMessages = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public Tribe getTribe() {
		return tribe;
	}

	public void setTribe(Tribe tribe) {
		this.tribe = tribe;
	}

	public boolean inTribe() {
		return tribe != null;
	}

	public boolean isChief() {
		return inTribe() ? getTribe().getChief().equals(getName()) : false;
	}

	public boolean isElder() {
		return inTribe() ? getTribe().getElders().containsKey(getName()) : false;
	}

	public void addInvite(Tribe tribe) {
		if (!invites.contains(tribe)) {
			invites.add(tribe);
		}
	}

	public void removeInvite(Tribe tribe) {
		if (invites.contains(tribe)) {
			invites.remove(tribe);
		}
	}

	public void acceptInvite(Tribe tribe) {
		if (invites.contains(tribe)) {
			tribe.addResident(name);
			invites.remove(tribe);
		}
	}

	public void declineInvite(Tribe tribe) {
		if (invites.contains(tribe)) {
			invites.remove(tribe);
		}
	}

	public boolean hasInvites() {
		return !invites.isEmpty();
	}

	public boolean hasMultipleInvites() {
		return invites.size() > 1;
	}

	public List<Tribe> getInvites() {
		return invites;
	}

	public boolean hasInvite(String name) {
		for (Tribe tribe : invites) {
			if (tribe.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	public void sendMessage(String message) {
		if (Bukkit.getPlayerExact(name) != null) {
			Bukkit.getPlayerExact(name).sendMessage(message);
		}
	}

	public void leaveTribe() {
		if (inTribe()) {
			tribe.sendMessageToResidents(TribesNextDoor.PREFIX + ChatColor.WHITE + name + ChatColor.AQUA + " has left the tribe");
			tribe.removeResident(this);
			setInTribeChat(false);
		}
	}

	public String getRole() {
		if (isChief())
			return "Chief";
		else if (isElder())
			return "Elder";
		else if (inTribe())
			return "Resident";
		else
			return "None";
	}

	public boolean inTribeChat() {
		return inTribeChat;
	}

	public void setInTribeChat(boolean inChat) {
		this.inTribeChat = inChat;
	}

	public boolean inCivChat() {
		return inCivChat;
	}

	public void setInCivChat(boolean inChat) {
		this.inCivChat = inChat;
	}

	public void addLoginMessage(String msg) {
		loginMessages.add(msg);
	}

	public void removeLoginMessage(String msg) {
		loginMessages.remove(msg);
	}

	public void sendLoginMessages(boolean clear) {
		if (Bukkit.getPlayerExact(getName()) != null) {
			for (String msg : loginMessages) {
				Bukkit.getPlayerExact(getName()).sendMessage(msg);

				if (clear)
					removeLoginMessage(msg);
			}
		}
	}

	public boolean hasLoginMessage(String msg) {
		return loginMessages.contains(msg);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Resident) {
			Resident res = (Resident) obj;

			return res.getName().equals(getName());
		}
		return false;
	}

	public void setAutclaim(boolean autoclaim) {
		this.autoclaim = autoclaim;
	}

	public boolean hasAutoclaim() {
		return autoclaim;
	}
}
