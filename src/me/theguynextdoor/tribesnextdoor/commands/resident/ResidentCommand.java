package me.theguynextdoor.tribesnextdoor.commands.resident;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * User: David Date: 7/31/13 Time: 10:01 AM
 */
public class ResidentCommand implements CommandExecutor {

	private ResidentUtils residentUtils;

	public ResidentCommand() {
		residentUtils = TribesNextDoor.getInstance().getResidentUtils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		Resident res = null;

		if (args.length == 0) {
			if (sender instanceof Player) {
				res = residentUtils.getResident(sender.getName());
			} else {
				sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
				return true;
			}
		} else if (args.length == 1) {
			String name = args[0];
			if (residentUtils.isResident(name)) {
				res = residentUtils.getResident(name);
			} else {
				sender.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
				return true;
			}
		} else {
			sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
			return true;
		}

		if (res != null) {
			sender.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.WHITE + res.getName());
			sender.sendMessage(ChatColor.GOLD + "Tribe: " + ChatColor.WHITE + (res.inTribe() ? res.getTribe().getName() : "None"));
			sender.sendMessage(ChatColor.GOLD + "Role: " + ChatColor.WHITE + res.getRole());
		} else {
			sender.sendMessage(TribesNextDoor.PREFIX + ChatColor.RED + "Res is null!");
		}
		return true;
	}
}
