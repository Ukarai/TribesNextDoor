package me.theguynextdoor.tribesnextdoor.commands.tribe;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils.Costs;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TribeTeleportCommand implements CommandExecutor {
	private TribesNextDoor plugin;
	private ResidentUtils residentUtils;
	private EconomyUtils economyUtils;
	private WorldUtils worldUtils;

	public TribeTeleportCommand() {
		plugin = TribesNextDoor.getInstance();
		residentUtils = plugin.getResidentUtils();
		economyUtils = plugin.getEconomyUtils();
		worldUtils = plugin.getWorldUtils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (worldUtils.getWorldOptions(player.getWorld().getName()).isTribesEnabled()) {
				Resident resident = residentUtils.getResident(player.getName());

				if (args.length == 1) {
					if (resident.inTribe()) {
						if (Bukkit.getPlayer(args[0]) != null) {
							Player target = Bukkit.getPlayer(args[0]);
							Resident tRes = residentUtils.getResident(target.getName());

							if (tRes.inTribe()) {
								if (tRes.getTribe().getName().equals(resident.getTribe().getName())) {
									if (economyUtils.canAfford(player, Costs.TTP)) {
										if (economyUtils.handleFunds(player, Costs.TTP)) {
											player.teleport(target);
											player.sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "You have been teleported to " + ChatColor.WHITE + target.getName());
											target.sendMessage(TribesNextDoor.PREFIX + ChatColor.WHITE + player.getName() + ChatColor.AQUA + " has teleported to you");
										}
									} else {
										player.sendMessage(MessageUtils.INSUFFICIENT_FUNDS);
									}
								} else {
									player.sendMessage(MessageUtils.PLAYER_NOT_IN_YOUR_TRIBE);
								}
							} else {
								player.sendMessage(MessageUtils.PLAYER_NOT_IN_TRIBE);
							}
						} else {
							player.sendMessage(MessageUtils.INVALID_TARGET_NOT_ONLINE);
						}
					} else {
						player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
					}
				} else {
					player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
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
