package me.theguynextdoor.tribesnextdoor.datatypes.tribe;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: David Date: 6/7/13 Time: 2:56 PM
 */
public class TribeSettings implements ConfigurationSerializable {

	private boolean showMotd = true;
	private String motd = "Use '/t set motd <message>' To change this message";
	private boolean showEnterMessage = true;
	private String enterMessage = "Use '/t set msg enter <message>' To change this message";
	private boolean showLeaveMessage = true;
	private String leaveMessage = "Use '/t set msg leave <message>' To change this message";

	public TribeSettings() {
		// Default values are already defined
	}

	public TribeSettings(Map<String, Object> map) {
		this.showMotd = (Boolean) getSafely(map, "showMotd", showMotd);
		this.motd = (String) getSafely(map, "motd", motd);
		this.showEnterMessage = (Boolean) getSafely(map, "showEnterMessage", showEnterMessage);
		this.enterMessage = (String) getSafely(map, "enterMessage", enterMessage);
		this.showLeaveMessage = (Boolean) getSafely(map, "showLeaveMessage", showLeaveMessage);
		this.leaveMessage = (String) getSafely(map, "leaveMessage", leaveMessage);
	}

	public Object getSafely(Map<String, Object> map, String key, Object safe) {
		return map.containsKey(key) ? map.get(key) : safe;
	}

	public boolean isShowMotd() {
		return showMotd;
	}

	public String getMotd() {
		return ChatColor.translateAlternateColorCodes('&', motd);
	}

	public boolean isShowEnterMessage() {
		return showEnterMessage;
	}

	public String getEnterMessage() {
		return ChatColor.translateAlternateColorCodes('&', enterMessage);
	}

	public boolean isShowLeaveMessage() {
		return showLeaveMessage;
	}

	public String getLeaveMessage() {
		return ChatColor.translateAlternateColorCodes('&', leaveMessage);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("showMotd", showMotd);
		map.put("motd", motd);
		map.put("showEnterMessage", showEnterMessage);
		map.put("enterMessage", enterMessage);
		map.put("showLeaveMessage", showLeaveMessage);
		map.put("leaveMessage", leaveMessage);

		return map;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public void setEnterMessage(String enterMessage) {
		this.enterMessage = enterMessage;
	}

	public void setLeaveMessage(String leaveMessage) {
		this.leaveMessage = leaveMessage;
	}
}
