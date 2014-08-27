package me.theguynextdoor.tribesnextdoor.commands.civilisation;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.AdminUtils;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CivilisationChatCommand implements CommandExecutor {

	private TribesNextDoor plugin;
	private ResidentUtils residentUtils;
	private AdminUtils adminUtils;
	private WorldUtils worldUtils;

	public CivilisationChatCommand() {
		plugin = TribesNextDoor.getInstance();
		residentUtils = plugin.getResidentUtils();
		adminUtils = plugin.getAdminUtils();
		worldUtils = plugin.getWorldUtils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (worldUtils.getWorldOptions(player.getWorld().getName()).isTribesEnabled()) {
				Resident resident = residentUtils.getResident(player.getName());

				if (resident.inTribe()) {
					Tribe tribe = resident.getTribe();

					if (tribe.inCivilisation()) {
						if (args.length == 0) {
							if (resident.inTribeChat()) {
								resident.setInTribeChat(false);
								player.sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "Tribe chat: " + ChatColor.RED + "OFF");
							}

							resident.setInCivChat(!resident.inCivChat());
							player.sendMessage(MessageUtils.CIV_CHAT_TOGGLED + (resident.inCivChat() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
						} else {
							Civilisation civ = tribe.getCivilisation();

							String prefix = ChatColor.GRAY + "[Civ] ";
							String name = ChatColor.GOLD + "(" + ChatColor.GRAY + player.getDisplayName() + ChatColor.GOLD + ") ";
							String msg = StringUtils.join(args, " ");

							civ.sendCivilisationMessage(prefix + name + msg);

							String spyPrefix = ChatColor.GRAY + "[" + civ.getName() + "]";
							adminUtils.sendCivilisationSpyMessage(civ, spyPrefix + name + msg);

							String logPrefix = ChatColor.GRAY + "[Civ]";
							plugin.log(logPrefix + spyPrefix + name + msg);
						}
					} else {
						player.sendMessage(MessageUtils.TRIBE_NOT_IN_CIVILISATION);
					}
				} else {
					player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
				}
			} else {
				player.sendMessage(MessageUtils.TRIBES_NOT_ENABLED_IN_THIS_WORLD);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
		return true;
	}

}
