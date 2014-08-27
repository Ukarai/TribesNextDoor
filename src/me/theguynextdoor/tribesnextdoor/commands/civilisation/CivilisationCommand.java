package me.theguynextdoor.tribesnextdoor.commands.civilisation;

import java.util.Collection;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.CivilisationUtils;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils.Costs;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CivilisationCommand implements CommandExecutor {

	private TribesNextDoor plugin;
	private ResidentUtils residentUtils;
	private EconomyUtils economyUtils;
	private CivilisationUtils civilisationUtils;
	private TribeUtils tribeUtils;
	private WorldUtils worldUtils;

	public CivilisationCommand() {
		plugin = TribesNextDoor.getInstance();
		residentUtils = plugin.getResidentUtils();
		economyUtils = plugin.getEconomyUtils();
		civilisationUtils = plugin.getCivilisationUtils();
		tribeUtils = plugin.getTribeUtils();
		worldUtils = plugin.getWorldUtils();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !worldUtils.getWorldOptions(((Player) sender).getWorld().getName()).isTribesEnabled()) {
			sender.sendMessage(MessageUtils.TRIBES_NOT_ENABLED_IN_THIS_WORLD);
		} else if (args.length == 0) {
			handleCivilisationInfoCommand(sender);
		} else if (args[0].equalsIgnoreCase("new")) {
			handleNewCivilisationCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("delete")) {
			handleDeleteCivilisationCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("add")) {
			handleAddCivilisationCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("accept")) {
			handleAcceptInviteCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("decline")) {
			handleDeclineInviteCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("remove")) {
			handleRemoveCivilisationCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("leave")) {
			handleLeaveCivilisationCommand(sender, args);
		} else if (args[0].equalsIgnoreCase("list")) {
			handleListCommand(sender, args);
		}
		return true;
	}

	public void handleCivilisationInfoCommand(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (tribe.inCivilisation()) {
					Civilisation civ = tribe.getCivilisation();

					player.sendMessage(ChatColor.GOLD + "|------------| " + ChatColor.WHITE + civ.getName() + ChatColor.GOLD + " |------------|");
					player.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.WHITE + civ.getChiefTribe().getChief());
					player.sendMessage(ChatColor.GOLD + "Chief tribe: " + ChatColor.WHITE + civ.getChiefTribe().getName());
					player.sendMessage(ChatColor.GOLD + "Tribes: " + ChatColor.WHITE
							+ civ.getTribes().keySet().toString().substring(1, civ.getTribes().keySet().toString().length() - 1));
				} else {
					player.sendMessage(MessageUtils.TRIBE_NOT_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleNewCivilisationCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (!tribe.inCivilisation()) {
					if (resident.isChief()) {
						if (civilisationUtils.validName(args[1])) {
							if (economyUtils.canAfford(player, Costs.NEW_CIVILISATION)) {
								if (economyUtils.handleFunds(player, Costs.NEW_CIVILISATION)) {
									Civilisation civ = new Civilisation(args[1]);
									civ.create(player, resident.getTribe());
								}
							} else {
								player.sendMessage(MessageUtils.INSUFFICIENT_FUNDS);
							}
						} else {
							if (tribeUtils.containsIllegals(args[1]))
								player.sendMessage(MessageUtils.CONTAINS_ILLEGAL_CHARACTERS);
							else
								player.sendMessage(MessageUtils.ALREADY_CIVILISATION_WITH_NAME);
						}
					} else {
						player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
					}
				} else {
					player.sendMessage(MessageUtils.TRIBE_ALREADY_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleDeleteCivilisationCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (tribe.inCivilisation()) {
					Civilisation civ = tribe.getCivilisation();

					if (tribe.getName().equals(civ.getChiefTribe().getName())) {
						if (resident.getName().equals(civ.getChiefTribe().getChief())) {
							civ.delete();
							Bukkit.broadcastMessage(ChatColor.GOLD + "The civilisation '" + ChatColor.WHITE + civ.getName() + ChatColor.GOLD + "' has been disbanded!");
						} else {
							player.sendMessage(MessageUtils.NOT_CIV_LEADER);
						}
					} else {
						player.sendMessage(MessageUtils.NOT_IN_CHIEF_TRIBE);
					}
				} else {
					player.sendMessage(MessageUtils.TRIBE_NOT_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleAddCivilisationCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (tribe.inCivilisation()) {
					Civilisation civ = tribe.getCivilisation();

					if (civ.getChiefTribe().getName().equals(tribe.getName())) {
						if (resident.isChief()) {
							if (args.length == 2) {
								if (tribeUtils.isTribe(args[1])) {
									Tribe add = tribeUtils.getTribe(args[1]);

									if (!add.inCivilisation()) {
										add.addInvite(civ);
										player.sendMessage(MessageUtils.INVITATION_SENT);
									} else {
										player.sendMessage(MessageUtils.THAT_TRIBE_ALREADY_IN_CIVILISATION);
									}
								} else {
									player.sendMessage(MessageUtils.NO_SUCH_TRIBE);
								}
							} else {
								player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
							}
						} else {
							player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
						}
					} else {
						player.sendMessage(MessageUtils.NOT_IN_CHIEF_TRIBE);
					}
				} else {
					player.sendMessage(MessageUtils.TRIBE_NOT_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleAcceptInviteCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (!tribe.inCivilisation()) {
					if (resident.isChief()) {

						if (args.length == 1) { // Eg /civ accept
							if (tribe.hasMultipleInvites()) {
								player.sendMessage("|------| List of invites |------|");

								for (Civilisation civ : tribe.getInvites()) {
									player.sendMessage(civ.getName());
								}

								player.sendMessage("");
								player.sendMessage("Type /civ accept <name> - to accept the invite");
							} else {
								tribe.acceptInvite(tribe.getInvites().get(0));
							}
						} else if (args.length == 2) { // Eg /civ accept GuyCiv
							if (civilisationUtils.isCivilisation(args[1])) {
								Civilisation civilisation = civilisationUtils.getCivilisation(args[1]);

								if (tribe.hasInvite(civilisation)) {
									tribe.acceptInvite(civilisation);
								} else {
									player.sendMessage(MessageUtils.NOT_INVITED_TO_THAT_CIVILISATION);
								}
							} else {
								player.sendMessage(MessageUtils.NO_SUCH_CIVILISATION);
							}
						} else {
							player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
						}
					} else {
						player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
					}
				} else {
					player.sendMessage(MessageUtils.TRIBE_ALREADY_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	public void handleDeclineInviteCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				if (resident.isChief()) {
					Tribe tribe = resident.getTribe();

					if (tribe.hasInvites()) {
						if (args.length == 1) { // Eg /civ decline
							if (tribe.hasMultipleInvites()) {
								player.sendMessage("|------| List of invites |------|");

								for (Civilisation civ : tribe.getInvites()) {
									player.sendMessage(civ.getName());
								}

								player.sendMessage("");
								player.sendMessage("Type /civ decline <name> - to decline the invite");
							} else {
								tribe.removeInvite(tribe.getInvites().get(0));
								player.sendMessage(MessageUtils.INVITATION_DECLINED);
							}
						} else if (args.length == 2) { // Eg /t decline GuyTribe
							if (civilisationUtils.isCivilisation(args[1])) {
								Civilisation civ = civilisationUtils.getCivilisation(args[1]);

								if (tribe.hasInvite(civ)) {
									tribe.removeInvite(civ);
									player.sendMessage(MessageUtils.INVITATION_DECLINED);
								} else {
									sender.sendMessage(MessageUtils.NOT_INVITED_TO_THAT_TRIBE);
								}
							} else {
								player.sendMessage(MessageUtils.NO_SUCH_CIVILISATION);
							}
						} else {
							sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
						}
					} else {
						player.sendMessage(MessageUtils.NO_INVITES);
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

	public void handleRemoveCivilisationCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (tribe.inCivilisation()) {
					Civilisation civ = tribe.getCivilisation();

					if (civ.getLeader().equals(player.getName())) {
						if (args.length == 2) {
							if (tribeUtils.isTribe(args[1])) {
								Tribe remove = tribeUtils.getTribe(args[1]);
								if (remove.inCivilisation()) {
									Civilisation tribeCiv = remove.getCivilisation();

									if (tribeCiv.getName().equals(civ.getName())) {
										civ.sendCivilisationMessage(ChatColor.GOLD + "The tribe " + ChatColor.WHITE + remove.getName() + ChatColor.GOLD
												+ " has been removed from the civilisation");
										civ.removeTribe(remove);
									}
								} else {
									player.sendMessage(MessageUtils.THAT_TRIBE_NOT_IN_CIVILISATION);
								}
							} else {
								player.sendMessage(MessageUtils.NO_SUCH_TRIBE);
							}
						} else {
							player.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
						}
					} else {
						player.sendMessage(MessageUtils.NOT_CIV_LEADER);
					}
				} else {
					player.sendMessage(MessageUtils.TRIBE_NOT_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleLeaveCivilisationCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (tribe.inCivilisation()) {
					Civilisation civ = tribe.getCivilisation();

					if (!civ.getChiefTribe().getName().equals(tribe.getName())) {
						if (resident.isChief()) {
							tribe.getCivilisation().sendCivilisationMessage(
									ChatColor.GOLD + "The tribe " + ChatColor.WHITE + tribe.getName() + ChatColor.GOLD + " has been left the civilisation");
							tribe.getCivilisation().removeTribe(tribe);
						} else {
							player.sendMessage(MessageUtils.NEED_TO_BE_CHIEF);
						}
					} else {
						player.sendMessage(MessageUtils.MUST_PASS_ON_OWNERSHIP_OF_CIV);
					}
				} else {
					player.sendMessage(MessageUtils.TRIBE_NOT_IN_CIVILISATION);
				}
			} else {
				player.sendMessage(MessageUtils.NOT_IN_A_TRIBE);
			}
		} else {
			sender.sendMessage(MessageUtils.PLAYER_COMMAND_ONLY);
		}
	}

	private void handleListCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			sender.sendMessage(ChatColor.GOLD + "|-----| List of Civilisations |-----|");
			StringBuilder sb = new StringBuilder();

			int i = 0;
			Collection<Civilisation> civList = civilisationUtils.getCivilisations().values();
			int length = civList.size();

			for (Civilisation civ : civList) {
				sb.append(ChatColor.GRAY + civ.getName() + ChatColor.GOLD + " [" + ChatColor.GRAY + civ.getTribes().size() + ChatColor.GOLD + "]");

				if (i < length - 1)
					sb.append(ChatColor.GRAY + ", ");
			}

			sender.sendMessage(sb.toString());
		} else {
			sender.sendMessage(MessageUtils.INVALID_ARGUMENTS_LENGTH);
		}
	}
}
