package me.theguynextdoor.tribesnextdoor.commands.admin;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.AdminUtils;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.PermissionsUtil;
import me.theguynextdoor.tribesnextdoor.utils.PermissionsUtil.Permission;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;
import me.theguynextdoor.tribesnextdoor.utils.data.BackupUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TribeAdminCommand implements CommandExecutor {
	private TribesNextDoor plugin;
	private BackupUtils backupUtils;
	private TribeUtils tribeUtils;
	private ResidentUtils residentUtils;
	private ChunkUtils chunkUtils;
	private AdminUtils adminUtils;

	public TribeAdminCommand() {
		plugin = TribesNextDoor.getInstance();
		backupUtils = plugin.getBackupUtils();
		tribeUtils = plugin.getTribeUtils();
		residentUtils = plugin.getResidentUtils();
		chunkUtils = plugin.getChunkUtils();
		adminUtils = plugin.getAdminUtils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!PermissionsUtil.hasPermission(sender, Permission.ADMIN_COMMAND)) {
			sender.sendMessage(MessageUtils.DONT_HAVE_PERMISSION);
			return true;
		}
		if (args.length == 0) {
			String header = ChatColor.GOLD + "|" + ChatColor.STRIKETHROUGH + "                |" + ChatColor.WHITE + " Tribe help " + ChatColor.GOLD + "|" + ChatColor.STRIKETHROUGH + "                |";

			sender.sendMessage(header);
			sender.sendMessage(ChatColor.GOLD + "/ta save - " + ChatColor.AQUA + "Saves the data to its respective data file");
			sender.sendMessage(ChatColor.GOLD + "/ta backup - " + ChatColor.AQUA + "Backsup the data files");
			sender.sendMessage(ChatColor.GOLD + "/ta spy tribe/civ - " + ChatColor.AQUA + "Toggles whether you can see other tribe/civ chats");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe new <Name> <Chief> - " + ChatColor.AQUA + "Creates a new tribe with the designated name and chief at the chiefs location");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe <Tribe> add <Name> - " + ChatColor.AQUA + "Adds the specified player to the specified tribe - If possible");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe <Tribe> remove <Name> - " + ChatColor.AQUA + "Removes the specified player from the specified tribe - If possible");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe <Tribe> delete - " + ChatColor.AQUA + "Deletes the specified tribe");
			return true;
		}

		if (args[0].equalsIgnoreCase("spy")) {
			if (sender instanceof Player) {
				if (args[1].equalsIgnoreCase("tribe")) {
					if (args.length == 2) {
						adminUtils.toggleTribeChatSpy(sender.getName());
						sender.sendMessage("Tribe spy toggled: " + (adminUtils.isTribeSpyToggled(sender.getName()) ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
					}
				} else if (args[1].equalsIgnoreCase("civ")) {
					if (args.length == 2) {
						adminUtils.toggleCivilisationChatSpy(sender.getName());
						sender.sendMessage("Civ spy toggled: " + (adminUtils.isCivSpyToggled(sender.getName()) ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
					}
				} else {
					sender.sendMessage(MessageUtils.INVALID_SUBCOMMAND);
				}
			} else {
				sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
			}
		}
		if (args[0].equalsIgnoreCase("save")) {
			plugin.getBackend().saveTribes();
			plugin.getBackend().saveCivilisations();
			plugin.getAdminUtils().save();
			sender.sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "Tribes data saved");
		}
		if (args[0].equalsIgnoreCase("backup")) {
			if (args.length == 1) {
				plugin.getBackend().saveTribes();
				plugin.getBackend().saveCivilisations();
				plugin.getAdminUtils().save();
				backupUtils.backupDataFiles();
				sender.sendMessage(MessageUtils.DATA_BACKUP);
			} else {
				sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
			}
		}
		if (args[0].equalsIgnoreCase("tribe")) {
			if (args[1].equalsIgnoreCase("new")) {
				if (!tribeUtils.isTribe(args[2])) {
					if (args.length == 4) {// Eg /tribeadmin tribe new GuyTribe
											// theguynextdoor
						if (residentUtils.isResident(args[3])) {
							Resident resident = residentUtils.getResident(args[3]);

							if (Bukkit.getPlayerExact(resident.getName()) != null) {
								Player chief = Bukkit.getPlayerExact(resident.getName());
								Tribe tribe = new Tribe(args[2]);

								tribe.create(sender, resident.getName());
								tribe.setWorld(chief.getWorld().getName());
								chunkUtils.unsafeClaim(tribe, chief.getLocation().getChunk());
								tribe.setSpawn(chief.getLocation());
							} else {
								sender.sendMessage(MessageUtils.PLAYER_NOT_ONLINE);
							}
						} else {
							sender.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
						}
					} else {
						sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
					}
				} else {
					sender.sendMessage(MessageUtils.ALREADY_TRIBE_WITH_NAME);
				}
			} else if (tribeUtils.isTribe(args[1])) {
				Tribe tribe = tribeUtils.getTribe(args[1]);

				if (args.length == 3) {
					if (args[2].equalsIgnoreCase("spawn")) {
						if (sender instanceof Player) {
							((Player) sender).teleport(tribe.getSpawn().getLocation());
						} else {
							sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
						}
					} else if (args[2].equalsIgnoreCase("delete")) { // Eg
																		// /tribeadmin
																		// tribe
																		// GuyTribe
																		// delete
						tribe.delete();
						Bukkit.broadcastMessage(ChatColor.GOLD + "The tribe " + ChatColor.WHITE + tribe.getName() + ChatColor.GOLD + " has fallen into ruin");
					}
				}
				if (args.length == 4) {
					if (args[2].equalsIgnoreCase("add")) {
						if (residentUtils.isResident(args[3])) {
							Resident resident = residentUtils.getResident(args[3]);

							if (!resident.inTribe()) {
								tribe.addResident(resident.getName());
								resident.getTribe().sendMessageToResidents(TribesNextDoor.PREFIX + ChatColor.AQUA + resident.getName() + " has joined the tribe");
							} else {
								sender.sendMessage(MessageUtils.PLAYER_ALREADY_IN_TRIBE);
							}
						} else {
							sender.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
						}
					} else if (args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("kick")) {
						if (residentUtils.isResident(args[3])) {
							Resident resident = residentUtils.getResident(args[3]);

							if (resident.inTribe()) {
								if (resident.getTribe().getName().equals(args[1])) {
									resident.getTribe().sendMessageToResidents(TribesNextDoor.PREFIX + ChatColor.AQUA + resident.getName() + " has been kicked from the tribe by " + sender.getName());
									tribe.removeResident(resident);

								} else {
									sender.sendMessage(TribesNextDoor.PREFIX + ChatColor.RED + "That player is not in that tribe");
								}
							} else {
								sender.sendMessage(MessageUtils.PLAYER_NOT_IN_TRIBE);
							}
						} else {
							sender.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
						}
					} else if (args[2].equalsIgnoreCase("rename")) { // Eg /ta
																		// tribe
																		// GuyTribe
																		// rename
																		// <Name>
						String name = args[3];
						if (tribeUtils.isValidName(name)) {
							tribe.rename(name);
						} else {
							if (tribeUtils.containsIllegals(name)) {
								sender.sendMessage(MessageUtils.CONTAINS_ILLEGAL_CHARACTERS);
							} else {
								sender.sendMessage(MessageUtils.ALREADY_TRIBE_WITH_NAME);
							}
						}
					} else {
						sender.sendMessage(MessageUtils.INVALID_SUBCOMMAND);
					}
				}
			} else {
				sender.sendMessage(MessageUtils.NO_SUCH_TRIBE);
			}
		}

		if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			String header = ChatColor.GOLD + "|" + ChatColor.STRIKETHROUGH + "                |" + ChatColor.WHITE + " Tribe help " + ChatColor.GOLD + "|" + ChatColor.STRIKETHROUGH + "                |";

			sender.sendMessage(header);
			sender.sendMessage(ChatColor.GOLD + "/ta save - " + ChatColor.AQUA + "Saves the data to its respective data file");
			sender.sendMessage(ChatColor.GOLD + "/ta backup - " + ChatColor.AQUA + "Backsup the data files");
			sender.sendMessage(ChatColor.GOLD + "/ta spy tribe/civ - " + ChatColor.AQUA + "Toggles whether you can see other tribe/civ chats");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe new <Name> <Chief> - " + ChatColor.AQUA + "Creates a new tribe with the designated name and chief at the chiefs location");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe <Tribe> add <Name> - " + ChatColor.AQUA + "Adds the specified player to the specified tribe - If possible");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe <Tribe> remove <Name> - " + ChatColor.AQUA + "Removes the specified player from the specified tribe - If possible");
			sender.sendMessage(ChatColor.GOLD + "/ta tribe <Tribe> delete - " + ChatColor.AQUA + "Deletes the specified tribe");
		}

		return true;
	}
}
