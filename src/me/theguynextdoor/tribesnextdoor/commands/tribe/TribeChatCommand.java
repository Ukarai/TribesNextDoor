package me.theguynextdoor.tribesnextdoor.commands.tribe;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
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

public class TribeChatCommand implements CommandExecutor {
	private TribesNextDoor plugin;
	private ResidentUtils residentUtils;
	private AdminUtils adminUtils;
	private WorldUtils worldUtils;

	public TribeChatCommand() {
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
					if (args.length == 0) {
						if (resident.inCivChat()) {
							resident.setInCivChat(false);
							player.sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "Civilisation chat: "
									+ ChatColor.RED + "OFF");
						}
						resident.setInTribeChat(!resident.inTribeChat());
						player.sendMessage(MessageUtils.TRIBE_CHAT_TOGGLED
								+ (resident.inTribeChat() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
					} else {
						Tribe tribe = resident.getTribe();

						String prefix = ChatColor.GRAY + "[Tribe] ";
						String name = ChatColor.DARK_GREEN + "(" + ChatColor.GREEN + player.getDisplayName()
								+ ChatColor.DARK_GREEN + ") ";
						String msg = StringUtils.join(args, " ");

						tribe.sendMessageToResidents(prefix + name + msg);

						String spyPrefix = ChatColor.GRAY + "[" + tribe.getName() + "]";
						adminUtils.sendTribeSpyMessage(tribe, spyPrefix + name + msg);

						String logPrefix = ChatColor.GRAY + "[Tribe]";
						plugin.log(logPrefix + spyPrefix + name + msg);
					}
				} else {
					resident.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
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
