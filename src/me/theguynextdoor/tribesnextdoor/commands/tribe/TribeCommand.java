package me.theguynextdoor.tribesnextdoor.commands.tribe;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.ClaimResult;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.ConfigUtils;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils.Costs;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.PermissionsUtil;
import me.theguynextdoor.tribesnextdoor.utils.PermissionsUtil.Permission;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * User: David Date: 6/7/13 Time: 3:04 PM
 */
public class TribeCommand implements CommandExecutor {

	private TribesNextDoor plugin;
	private ResidentUtils residentUtils;
	private TribeUtils tribeUtils;
	private ChunkUtils chunkUtils;
	private EconomyUtils economyUtils;
	private WorldUtils worldUtils;
	private ConfigUtils configUtils;

	public TribeCommand() {
		plugin = TribesNextDoor.getInstance();
		residentUtils = plugin.getResidentUtils();
		tribeUtils = plugin.getTribeUtils();
		chunkUtils = plugin.getChunkUtils();
		economyUtils = plugin.getEconomyUtils();
		worldUtils = plugin.getWorldUtils();
		configUtils = plugin.getConfigUtils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !worldUtils.getWorldOptions(((Player) sender).getWorld().getName()).isTribesEnabled()) {
			sender.sendMessage(MessageUtils.TRIBES_NOT_ENABLED_IN_THIS_WORLD);
		} else if (args.length == 0) {
			handleTribeInfoCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("new") || args[0].equalsIgnoreCase("create")) {
			handleNewTribeCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("list")) {
			handleTribeListCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("add")) {
			handleInviteCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("accept")) {
			handleAcceptCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("decline")) {
			handleDeclineCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("leave")) {
			handleLeaveCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("delete")) {
			handleDeleteCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("remove")) {
			handleKickCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("elder")) {
			handleElderCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("claim")) {
			handleClaimCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("here")) {
			handleHereCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("unclaim")) {
			handleUnclaimCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("set")) {
			handleSetCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("debug")) {
			handleDebugCommand(sender);
		} else if (args[0].equalsIgnoreCase("spawn")) {
			handleSpawnCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("rename")) {
			handleRenameCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("prices")) {
			handlePricesCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("invites")) {
			handleInvitesCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("plugin") || args[0].equalsIgnoreCase("pl")) {
			handlePluginInfoCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("perms")) {
			handlePermsCommand(sender);
		} else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
			handleHelpCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("outpost")) {
			handleOutpostCommand(sender, args);
		} else {
			handleOtherTribeInfoCommand(sender, args);
		}
		return true;
	}

	public void handleTribeInfoCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0) { // Eg /t
				Resident resident = residentUtils.getResident(sender.getName());

				if (resident.inTribe()) {
					Tribe tribe = resident.getTribe();

					sender.sendMessage(ChatColor.GOLD + "|------------| " + ChatColor.WHITE + (configUtils.showUnderscore() ? tribe.getName() : tribe.getName().replace('_', ' ')) + ChatColor.GOLD
							+ " |------------|");
					sender.sendMessage(ChatColor.GOLD + "Chief: " + ChatColor.WHITE + tribe.getChief() + ChatColor.GOLD + "  Maker: " + ChatColor.WHITE + tribe.getMaker());
					sender.sendMessage(ChatColor.GOLD + "Civilisation: " + ChatColor.WHITE + (tribe.getCivilisation() != null ? tribe.getCivilisation().getName() : ""));
					sender.sendMessage(ChatColor.GOLD + "Claimed: " + ChatColor.WHITE + chunkUtils.getTotalTribeClaims(tribe) + "/"
							+ ((tribe.getResidents().size() * ConfigUtils.getClaimsPerPerson()) + tribe.getBonusClaims()));
					sender.sendMessage(ChatColor.GOLD + "Elders: " + ChatColor.WHITE + tribe.getElders().keySet().toString().substring(1, tribe.getElders().keySet().toString().length() - 1));
					sender.sendMessage(ChatColor.GOLD + "Residents: " + ChatColor.WHITE + tribe.getResidents().keySet().toString().substring(1, tribe.getResidents().keySet().toString().length() - 1));
				} else {
					sender.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
				}
			} else {
				sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleNewTribeCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 2) { // E.g /t new GuyTribe
				Player player = (Player) sender;
				Resident resident = residentUtils.getResident(player.getName());
				String tribeName = args[1];

				if (PermissionsUtil.hasPermission(player, Permission.NEW_TRIBE)) {
					if (!resident.inTribe()) {
						if (!tribeUtils.tooCloseToOtherTribes(player.getLocation())) {
							if (!tribeUtils.tooCloseToSpawn(player.getLocation())) {
								if (tribeUtils.isValidName(tribeName)) {
									if (!chunkUtils.isClaimed(player.getLocation().getChunk())) {
										if (economyUtils.canAfford(player, Costs.NEW_TRIBE)) {
											if (economyUtils.handleFunds(player, Costs.NEW_TRIBE)) {
												Tribe tribe = new Tribe(tribeName);

												tribe.create(sender, player.getName());
												tribe.setWorld(player.getWorld().getName());
												chunkUtils.unsafeClaim(tribe, player.getLocation().getChunk());
												tribe.setSpawn(player.getLocation());

												plugin.log(player.getName() + " created the tribe " + tribe.getName());
											}
										} else {
											player.sendMessage(MessageUtils.INSUFFICIENT_FUNDS);
										}
									} else {
										sender.sendMessage(MessageUtils.CHUNK_ALREADY_CLAIMED);
									}
								} else {
									if (tribeUtils.containsIllegals(tribeName)) {
										sender.sendMessage(MessageUtils.CONTAINS_ILLEGAL_CHARACTERS);
									} else if (tribeName.length() > configUtils.getMaxTribeNameLength()) {
										sender.sendMessage(MessageUtils.TRIBE_NAME_TOO_LONG + " The maximum length is " + configUtils.getMaxTribeNameLength() + " characters");
									} else if (tribeName.length() < configUtils.getMinTribeNameLength()) {
										sender.sendMessage(MessageUtils.TRIBE_NAME_TOO_SHORT + " The minimum length is " + configUtils.getMinTribeNameLength() + " characters");
									} else {
										sender.sendMessage(MessageUtils.ALREADY_TRIBE_WITH_NAME);
									}
								}
							} else {
								sender.sendMessage(MessageUtils.TOO_CLOSE_TO_SPAWN + ". You need to be at least "
										+ plugin.getWorldUtils().getWorldOptions(player.getWorld().getName()).getTribeDistanceFromSpawn() + " blocks away from spawn");
							}
						} else {
							sender.sendMessage(MessageUtils.TOO_CLOSE_TO_OTHER_TRIBES + ". You need to be at least "
									+ plugin.getWorldUtils().getWorldOptions(player.getWorld().getName()).getTribeDistanceFromTribe() + " blocks away from other tribes");
						}
					} else {
						sender.sendMessage(MessageUtils.ALREADY_IN_TRIBE);
					}
				} else {
					player.sendMessage(MessageUtils.DONT_HAVE_PERMISSION);
				}
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleTribeListCommand(CommandSender sender, String args[]) {
		if (args.length == 1) {
			sender.sendMessage(ChatColor.GOLD + "|-----| List of tribes |-----|");

			StringBuilder sb = new StringBuilder();

			int size = tribeUtils.getTribes().size();
			for (int i = 0; i < size; i++) {
				Tribe tribe = (Tribe) tribeUtils.getTribes().values().toArray()[i];

				sb.append(ChatColor.GOLD + (configUtils.showUnderscore() ? tribe.getName() : tribe.getName().replace('_', ' ')) + ChatColor.AQUA + " [" + tribe.getResidents().size() + "]");
				if (i < tribeUtils.getTribes().size() - 1)
					sb.append(ChatColor.WHITE + ", ");
			}

			sender.sendMessage(sb.toString());
		} else {
			sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
		}
	}

	public void handleInviteCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 2) {
				Resident sendRes = residentUtils.getResident(((Player) sender).getName());

				if (sendRes.inTribe()) {
					if (sendRes.isChief() || sendRes.isElder()) {
						String toAdd = args[1];

						if (residentUtils.isResident(toAdd)) {
							Resident res = residentUtils.getResident(toAdd);

							if (!res.inTribe()) {
								if (!res.hasInvite(sendRes.getTribe().getName())) {
									sendRes.getTribe().invite(res);
									sender.sendMessage(MessageUtils.INVITATION_SENT);
								} else {
									sender.sendMessage(MessageUtils.ALREADY_INVITED_PLAYER);
								}
							} else {
								sender.sendMessage(MessageUtils.PLAYER_ALREADY_IN_TRIBE);
							}
						} else {
							sender.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
						}
					} else {
						sender.sendMessage(MessageUtils.NOT_CHIEF_OR_ELDER);
					}
				} else {
					sender.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
				}
			} else {
				sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleAcceptCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (!resident.inTribe()) {
				if (resident.hasInvites()) {
					if (resident.hasMultipleInvites()) {
						if (args.length == 1) {
							player.sendMessage("|------| List of invites |------|");

							for (Tribe tribe : resident.getInvites()) {
								player.sendMessage(tribe.getName());
							}

							player.sendMessage("");
							player.sendMessage("Type /t accept <name> - to accept the invite");
						} else {
							if (args.length == 2) {
								if (resident.hasInvite(args[1])) {
									resident.acceptInvite(tribeUtils.getTribe(args[1]));
									resident.getTribe().sendMessageToResidents(TribesNextDoor.PREFIX + ChatColor.AQUA + resident.getName() + " has joined the tribe");
								} else {
									sender.sendMessage(MessageUtils.NOT_INVITED_TO_THAT_TRIBE);
								}
							} else {
								sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
							}
						}
					} else {
						resident.acceptInvite(resident.getInvites().get(0));
						resident.getTribe().sendMessageToResidents(TribesNextDoor.PREFIX + ChatColor.AQUA + resident.getName() + " has joined the tribe");
					}
				} else {
					sender.sendMessage(MessageUtils.NO_INVITES);
				}
			} else {
				sender.sendMessage(MessageUtils.ALREADY_IN_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleDeclineCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.hasInvites()) {
				if (args.length == 1) { // Eg /t decline
					if (resident.hasMultipleInvites()) {
						player.sendMessage("|------| List of invites |------|");

						for (Tribe tribe : resident.getInvites()) {
							player.sendMessage(tribe.getName());
						}

						player.sendMessage("");
						player.sendMessage("Type /t decline <name> - to decline the invite");
					} else {
						resident.declineInvite(resident.getInvites().get(0));
						player.sendMessage(MessageUtils.INVITATION_DECLINED);
					}
				} else if (args.length == 2) { // Eg /t decline GuyTribe
					if (resident.hasInvite(args[1])) {
						resident.declineInvite(tribeUtils.getTribe(args[1]));
						player.sendMessage(MessageUtils.INVITATION_DECLINED);
					} else {
						sender.sendMessage(MessageUtils.NOT_INVITED_TO_THAT_TRIBE);
					}
				} else {
					sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
				}
			} else {
				player.sendMessage(MessageUtils.NO_INVITES);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleLeaveCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (args.length == 1) {
				if (resident.inTribe()) {
					if (!resident.isChief()) {
						resident.leaveTribe();
					} else {
						player.sendMessage(MessageUtils.MUST_PASS_ON_OWNERSHIP_OF_TRIBE);
					}
				} else {
					player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
				}
			} else {
				player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleDeleteCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isChief()) {
					if (args.length == 1) {
						Tribe tribe = resident.getTribe();

						tribe.delete();
						Bukkit.broadcastMessage(ChatColor.GOLD + "The tribe " + ChatColor.WHITE + tribe.getName() + ChatColor.GOLD + " has fallen into ruin");

						plugin.log(player.getName() + " deleted the tribe " + tribe.getName());
					} else {
						player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
					}
				} else {
					player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleKickCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) { // Eg /t kick theguynextdoor
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isChief() || resident.isElder()) {
					if (args.length == 2) {
						if (residentUtils.isResident(args[1])) {
							Resident toKick = residentUtils.getResident(args[1]);

							if (!toKick.equals(resident)) {
								Tribe tribe = resident.getTribe();

								tribe.sendMessageToResidents(toKick.getName() + ChatColor.GOLD + " has been kicked from the tribe by " + ChatColor.WHITE + resident.getName());
								tribe.removeResident(toKick);

								plugin.log(player.getName() + " kicked" + toKick.getName() + " from the tribe " + tribe.getName());
							} else {
								player.sendMessage(MessageUtils.MUST_PASS_ON_OWNERSHIP_OF_TRIBE);
							}
						} else {
							player.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
						}
					} else {
						player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
					}
				} else {
					player.sendMessage(MessageUtils.NOT_CHIEF_OR_ELDER);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleElderCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isChief()) {
					if (args.length == 3) {
						Tribe tribe = resident.getTribe();

						if (args[1].equalsIgnoreCase("add")) { // Eg /t elder
																// add
																// theguynextdoor
							if (residentUtils.isResident(args[2])) {
								tribe.addElder(args[2]);
								tribe.sendMessageToResidents(args[2] + ChatColor.GOLD + " is now an elder");
							} else {
								player.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
							}
						} else if (args[1].equalsIgnoreCase("remove")) { // Eg
																			// /t
																			// elder
																			// remove
																			// theguynextdoor
							if (residentUtils.isResident(args[2])) {
								tribe.removeElder(args[2]);
								tribe.sendMessageToResidents(args[2] + ChatColor.GOLD + " is no longer an elder");
							} else {
								player.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
							}
						} else {
							player.sendMessage(MessageUtils.INVALID_SUBCOMMAND);
						}
					} else {
						player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
					}
				} else {
					player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleClaimCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			player.sendMessage(chunkUtils.claimChunk(player).getMessage());
		}
	}

	public void handleHereCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1) {
				Player player = (Player) sender;
				Chunk chunk = player.getLocation().getChunk();

				player.sendMessage(chunkUtils.isClaimed(chunk) ? ChatColor.GOLD + "Owned by: " + ChatColor.WHITE + chunkUtils.getOwner(chunk) : ChatColor.GOLD + chunkUtils.getOwner(chunk));
			} else {
				sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleSetCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.isChief() || resident.isElder()) {
				Tribe tribe = resident.getTribe();

				if (args[1].equalsIgnoreCase("motd")) {
					tribe.getSettings().setMotd(StringUtils.join(args, " ", 2, args.length));
					tribe.sendMessageToResidents(ChatColor.GOLD + tribe.getName() + ": " + ChatColor.WHITE + tribe.getSettings().getMotd());
				} else if (args[1].equalsIgnoreCase("message") || args[1].equalsIgnoreCase("msg")) {
					if (args[2].equalsIgnoreCase("enter")) {
						tribe.getSettings().setEnterMessage(StringUtils.join(args, " ", 3, args.length));
						player.sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "Enter message set to: " + tribe.getSettings().getEnterMessage());
					} else if (args[2].equalsIgnoreCase("leave")) {
						tribe.getSettings().setLeaveMessage(StringUtils.join(args, " ", 3, args.length));
						player.sendMessage(TribesNextDoor.PREFIX + ChatColor.AQUA + "Leave message set to: " + tribe.getSettings().getLeaveMessage());
					}
				} else if (args[1].equalsIgnoreCase("chief")) {
					if (resident.isChief()) {
						if (args.length == 3) {
							if (residentUtils.isResident(args[2])) {
								Resident toBeChief = residentUtils.getResident(args[2]);

								if (toBeChief.inTribe() && toBeChief.getTribe().getName().equals(tribe.getName())) {
									tribe.setChief(args[2]);
								} else {
									player.sendMessage(MessageUtils.PLAYER_NOT_IN_YOUR_TRIBE);
								}
							} else {
								player.sendMessage(MessageUtils.RESIDENT_NOT_LOADED);
							}
						} else {
							player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
						}
					} else {
						player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
					}
				} else if (args[1].equalsIgnoreCase("spawn")) {
					if (resident.isChief()) {
						Chunk newSpawn = player.getLocation().getChunk();

						if (!chunkUtils.isClaimed(newSpawn) || chunkUtils.getOwner(newSpawn).equalsIgnoreCase(tribe.getName())) {
							tribe.setSpawn(player.getLocation());
							player.sendMessage(MessageUtils.SPAWN_SET);
						} else {
							player.sendMessage(MessageUtils.CHUNK_NOT_OWNED);
						}
					}
				} else {
					player.sendMessage(MessageUtils.INVALID_SUBCOMMAND);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_CHIEF_OR_ELDER);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleDebugCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "|------ " + ChatColor.WHITE + "Debug " + ChatColor.GOLD + "------|");
		sender.sendMessage(ChatColor.GOLD + "Residents loaded: " + ChatColor.WHITE + residentUtils.getAmountOfResidentsLoaded());
		sender.sendMessage(ChatColor.GOLD + "Tribes loaded: " + ChatColor.WHITE + tribeUtils.getTribes().size());
		sender.sendMessage(ChatColor.GOLD + "Chunks claimed: " + ChatColor.WHITE + chunkUtils.getAmountOfChunksClaimed());
		sender.sendMessage(ChatColor.GOLD + "Using economy: " + ChatColor.WHITE + (plugin.getEconomy() != null));
	}

	public void handleSpawnCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (ConfigUtils.allowSpawnFromOtherWorld() || player.getWorld().getName().equals(tribe.getWorldName())) {
					if (economyUtils.canAfford(player, Costs.TRIBE_SPAWN)) {
						if (economyUtils.handleFunds(player, Costs.TRIBE_SPAWN)) {
							player.teleport(tribe.getSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
							player.sendMessage(MessageUtils.TP_TO_TRIBE_SPAWN);
						}
					} else {
						player.sendMessage(MessageUtils.INSUFFICIENT_FUNDS);
					}
				} else {
					player.sendMessage(MessageUtils.NOT_IN_CORRECT_WORLD);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleRenameCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isChief()) {
					if (args.length == 2) {
						String name = args[1];

						if (tribeUtils.isValidName(name)) {
							if (economyUtils.canAfford(player, Costs.TRIBE_RENAME)) {
								if (economyUtils.handleFunds(player, Costs.TRIBE_RENAME)) {
									resident.getTribe().rename(name);
								}
							} else {
								player.sendMessage(MessageUtils.INSUFFICIENT_FUNDS);
							}
						} else {
							if (tribeUtils.containsIllegals(name)) {
								player.sendMessage(MessageUtils.CONTAINS_ILLEGAL_CHARACTERS);
							} else {
								player.sendMessage(MessageUtils.ALREADY_TRIBE_WITH_NAME);
							}
						}
					} else {
						player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
					}
				} else {
					player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handlePricesCommand(CommandSender sender, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "|------ " + ChatColor.WHITE + "Prices " + ChatColor.GOLD + "------|");
		sender.sendMessage(ChatColor.GOLD + "New tribe: " + ChatColor.WHITE + economyUtils.getCost(Costs.NEW_TRIBE));
		sender.sendMessage(ChatColor.GOLD + "Tribe claim: " + ChatColor.WHITE + economyUtils.getCost(Costs.TRIBE_CLAIM));
		sender.sendMessage(ChatColor.GOLD + "Tribe spawn: " + ChatColor.WHITE + economyUtils.getCost(Costs.TRIBE_SPAWN));
		sender.sendMessage(ChatColor.GOLD + "Rename tribe: " + ChatColor.WHITE + economyUtils.getCost(Costs.TRIBE_RENAME));
		sender.sendMessage(ChatColor.GOLD + "Ttp: " + ChatColor.WHITE + economyUtils.getCost(Costs.TTP));
		sender.sendMessage(ChatColor.GOLD + "New Civilisation: " + ChatColor.WHITE + economyUtils.getCost(Costs.NEW_CIVILISATION));
	}

	public void handleOtherTribeInfoCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (tribeUtils.isTribe(args[0])) {
				Tribe tribe = tribeUtils.getTribe(args[0]);

				sender.sendMessage(ChatColor.GOLD + "|------------| " + ChatColor.WHITE + (configUtils.showUnderscore() ? tribe.getName() : tribe.getName().replace('_', ' ')) + ChatColor.GOLD
						+ " |------------|");
				sender.sendMessage(ChatColor.GOLD + "Chief: " + ChatColor.WHITE + tribe.getChief() + ChatColor.GOLD + "  Maker: " + ChatColor.WHITE + tribe.getMaker());
				sender.sendMessage(ChatColor.GOLD + "Civilisation: " + ChatColor.WHITE + (tribe.getCivilisation() != null ? tribe.getCivilisation().getName() : ""));
				sender.sendMessage(ChatColor.GOLD + "Claimed: " + ChatColor.WHITE + chunkUtils.getTotalTribeClaims(tribe) + "/"
						+ ((tribe.getResidents().size() * ConfigUtils.getClaimsPerPerson() + tribe.getBonusClaims())));
				sender.sendMessage(ChatColor.GOLD + "Elders: " + ChatColor.WHITE + tribe.getElders().keySet().toString().substring(1, tribe.getElders().keySet().toString().length() - 1));
				sender.sendMessage(ChatColor.GOLD + "Residents: " + ChatColor.WHITE + tribe.getResidents().keySet().toString().substring(1, tribe.getResidents().keySet().toString().length() - 1));
			} else {
				sender.sendMessage(MessageUtils.NO_SUCH_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
		}
	}

	public void handleInvitesCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			player.sendMessage(ChatColor.GOLD + "|------| List of invites |------|");

			for (Tribe tribe : resident.getInvites()) {
				player.sendMessage(tribe.getName());
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handlePluginInfoCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			sender.sendMessage(ChatColor.GOLD + "Plugin name: " + ChatColor.WHITE + "TribesNextDoor");
			sender.sendMessage(ChatColor.GOLD + "Plugin author: " + ChatColor.WHITE + "theguynextdoor");
			sender.sendMessage(ChatColor.GOLD + "Plugin version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.GOLD + "BukkitDev page: " + ChatColor.WHITE + "http://dev.bukkit.org/bukkit-plugins/tribesnextdoor/");
		} else {
			sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
		}
	}

	private void handlePermsCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "|------ " + ChatColor.WHITE + "Perms " + ChatColor.GOLD + "------|");

		for (Permission perm : Permission.values()) {
			sender.sendMessage((PermissionsUtil.hasPermission(sender, perm) ? ChatColor.GREEN : ChatColor.RED) + perm.getPermission());
		}
	}

	private void handleHelpCommand(CommandSender sender, String[] args) {
		String header = ChatColor.GOLD + "|" + ChatColor.STRIKETHROUGH + "                |" + ChatColor.WHITE + " Tribe help " + ChatColor.GOLD + "|" + ChatColor.STRIKETHROUGH + "                |";

		if (args.length == 1) {
			sender.sendMessage(header);
			sender.sendMessage(ChatColor.GOLD + "1 - " + ChatColor.AQUA + "General commands");
			sender.sendMessage(ChatColor.GOLD + "2 - " + ChatColor.AQUA + "Elder commands");
			sender.sendMessage(ChatColor.GOLD + "3 - " + ChatColor.AQUA + "Chief commands");
		} else if (args.length == 2) {
			if (isInt(args[1])) {
				if (Integer.parseInt(args[1]) == 1) {
					sender.sendMessage(header);
					sender.sendMessage(ChatColor.GOLD + "/t - " + ChatColor.AQUA + "Info on your tribe");
					sender.sendMessage(ChatColor.GOLD + "/t <TribeName> - " + ChatColor.AQUA + "Info on another tribe");
					sender.sendMessage(ChatColor.GOLD + "/t new <tribeName> - " + ChatColor.AQUA + "Creates a tribe with the given name at your location");
					sender.sendMessage(ChatColor.GOLD + "/t prices- " + ChatColor.AQUA + "Shows a list of tribe related prices");
					sender.sendMessage(ChatColor.GOLD + "/resident - " + ChatColor.AQUA + "Info on yourself relating to tribes");
					sender.sendMessage(ChatColor.GOLD + "/resident <PlayerName> - " + ChatColor.AQUA + "Info on another player relating to tribes");
					sender.sendMessage(ChatColor.GOLD + "/t spawn " + ChatColor.AQUA + "Teleports your to your tribe's spawn");
					sender.sendMessage(ChatColor.GOLD + "/t list " + ChatColor.AQUA + "Shows a list of all the existing tribes");
					sender.sendMessage(ChatColor.GOLD + "/t accept " + ChatColor.AQUA + "Accepts a tribe invite (If you have one)");
					sender.sendMessage(ChatColor.GOLD + "/t decline " + ChatColor.AQUA + "Declines a tribe invite (If you have one)");
					sender.sendMessage(ChatColor.GOLD + "/t leave " + ChatColor.AQUA + "Remove yourself from your current tribe");
				} else if (Integer.parseInt(args[1]) == 2) {
					sender.sendMessage(header);
					sender.sendMessage(ChatColor.GOLD + "/t add <Name> - " + ChatColor.AQUA + "Adds that player to your tribe");
					sender.sendMessage(ChatColor.GOLD + "                 " + ChatColor.AQUA + "The player must be online");
					sender.sendMessage(ChatColor.GOLD + "                 " + ChatColor.AQUA + "Player must not be in a tribe");
					sender.sendMessage(ChatColor.GOLD + "/t remove <Name> - " + ChatColor.AQUA + "Removes that player to your tribe");
					sender.sendMessage(ChatColor.GOLD + "                    " + ChatColor.AQUA + "Player must be in your tribe");
					sender.sendMessage(ChatColor.GOLD + "/t claim - " + ChatColor.AQUA + "Claims a chunk for you tribe");
					sender.sendMessage(ChatColor.GOLD + "/t unclaim - " + ChatColor.AQUA + "Unclaims a chunk from the tribe");
				} else if (Integer.parseInt(args[1]) == 3) {
					sender.sendMessage(header);
					sender.sendMessage(ChatColor.GOLD + "/t elder add <Name> - " + ChatColor.AQUA + "Adds that player as a tribe elder");
					sender.sendMessage(ChatColor.GOLD + "/t elder remove <Name> - " + ChatColor.AQUA + "Makes it so that player is no longer an elder");
					sender.sendMessage(ChatColor.GOLD + "/t rename <Name> - " + ChatColor.AQUA + "Renames the tribe");
					sender.sendMessage(ChatColor.GOLD + "/t set spawn - " + ChatColor.AQUA + "Sets the tribe spawn to your location");
					sender.sendMessage(ChatColor.GOLD + "/t set msg enter <msg> - " + ChatColor.AQUA + "Sets the message people recieve when they enter chunks claimed by your tribe");
					sender.sendMessage(ChatColor.GOLD + "/t set msg leave <msg> - " + ChatColor.AQUA + "Sets the message people recieve when they leave chunks claimed by your tribe");
					sender.sendMessage(ChatColor.GOLD + "/t set chief <name> - " + ChatColor.AQUA + "Sets the chief of the tribe");
					sender.sendMessage(ChatColor.GOLD + "/t delete - " + ChatColor.AQUA + "Deletes the tribe");
				}
			}
		} else {
			sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
		}
	}

	private void handleOutpostCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isElder() || resident.isChief()) {
					Location loc = player.getLocation();

					if (!chunkUtils.isClaimed(loc.getChunk())) {
						Tribe tribe = resident.getTribe();

						// if (tribe) {

						// }
					} else {
						player.sendMessage(MessageUtils.CHUNK_ALREADY_CLAIMED);
					}
				} else {
					player.sendMessage(MessageUtils.NOT_CHIEF_OR_ELDER);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleUnclaimCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isChief() || resident.isElder()) {
					Tribe tribe = resident.getTribe();
					Chunk chunk = player.getLocation().getChunk();

					if (chunkUtils.isClaimed(chunk) && chunkUtils.getOwner(chunk).equalsIgnoreCase(resident.getTribe().getName())) {
						if (chunkUtils.getTotalTribeClaims(tribe) > 1) {
							if (tribe.getSpawn().getLocation().getChunk() != player.getLocation().getChunk()) {
								chunkUtils.unclaimChunk(chunk);
								player.sendMessage(MessageUtils.CHUNK_UNCLAIM_SUCCESS);
							} else {
								player.sendMessage(MessageUtils.CANT_UNCLAIM_SPAWN);
							}
						} else {
							player.sendMessage(MessageUtils.TOO_FEW_CLAIMS);
						}
					} else {
						player.sendMessage(MessageUtils.CHUNK_NOT_OWNED);
					}
				} else {
					player.sendMessage(MessageUtils.NOT_CHIEF_OR_ELDER);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
