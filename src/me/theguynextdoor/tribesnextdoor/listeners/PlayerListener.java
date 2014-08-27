package me.theguynextdoor.tribesnextdoor.listeners;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.AdminUtils;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.ConfigUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * User: David Date: 6/7/13 Time: 2:07 PM
 */
public class PlayerListener implements Listener {
	private TribesNextDoor plugin;
	private TribeUtils tribeUtils;
	private ResidentUtils residentUtils;
	private ChunkUtils chunkUtils;
	private AdminUtils adminUtils;
	private WorldUtils worldUtils;
	private ConfigUtils configUtils;

	public PlayerListener() {
		plugin = TribesNextDoor.getInstance();
		tribeUtils = plugin.getTribeUtils();
		residentUtils = plugin.getResidentUtils();
		chunkUtils = plugin.getChunkUtils();
		adminUtils = plugin.getAdminUtils();
		worldUtils = plugin.getWorldUtils();
		configUtils = plugin.getConfigUtils();
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		residentUtils.addResident(player.getName());

		if (worldUtils.getWorldOptions(player.getWorld().getName()).isTribesEnabled()) {
			Resident resident = residentUtils.getResident(player.getName());
			resident.sendLoginMessages(true);

			if (resident.inTribe()) {
				player.sendMessage(ChatColor.GOLD + (configUtils.showUnderscore() ? resident.getTribe().getName() : resident.getTribe().getName().replace('_', ' ')) + ": "
						+ ChatColor.WHITE + resident.getTribe().getSettings().getMotd());
			}
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if (worldUtils.getWorldOptions(e.getPlayer().getWorld().getName()).isTribesEnabled()) {
			Chunk cTo = e.getTo().getChunk();
			Chunk cFrom = e.getFrom().getChunk();

			if (cTo != cFrom) {
				Player player = e.getPlayer();

				if (fromWildToTribe(cFrom, cTo)) {
					player.sendMessage(ChatColor.GOLD + "<" + (configUtils.showUnderscore() ? chunkUtils.getOwner(cTo) : chunkUtils.getOwner(cTo).replace('_', ' ')) + "> "
							+ ChatColor.WHITE + tribeUtils.getTribe(chunkUtils.getOwner(cTo)).getSettings().getEnterMessage());
				} else if (fromTribeToWild(cFrom, cTo)) {
					player.sendMessage(ChatColor.GOLD + "<" + (configUtils.showUnderscore() ? chunkUtils.getOwner(cFrom) : chunkUtils.getOwner(cFrom).replace('_', ' ')) + "> "
							+ ChatColor.WHITE + tribeUtils.getTribe(chunkUtils.getOwner(cFrom)).getSettings().getLeaveMessage());
				} else if (fromTribeToDiffTribe(cFrom, cTo)) {
					player.sendMessage(ChatColor.GOLD + "<" + (configUtils.showUnderscore() ? chunkUtils.getOwner(cFrom) : chunkUtils.getOwner(cFrom).replace('_', ' ')) + "> "
							+ ChatColor.WHITE + tribeUtils.getTribe(chunkUtils.getOwner(cFrom)).getSettings().getLeaveMessage());
					player.sendMessage(ChatColor.GOLD + "<" + (configUtils.showUnderscore() ? chunkUtils.getOwner(cTo) : chunkUtils.getOwner(cTo).replace('_', ' ')) + "> "
							+ ChatColor.WHITE + tribeUtils.getTribe(chunkUtils.getOwner(cTo)).getSettings().getEnterMessage());
				}
			}
			
			
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();

		if (worldUtils.getWorldOptions(player.getWorld().getName()).isTribesEnabled()) {
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribe()) {
				Tribe tribe = resident.getTribe();

				if (player.getWorld().getName().equals(tribe.getWorldName())) {
					e.setRespawnLocation(tribe.getSpawn().getLocation());
				}
			}
		}
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (worldUtils.getWorldOptions(e.getPlayer().getWorld().getName()).isTribesEnabled()) {
			Resident resident = residentUtils.getResident(player.getName());

			if (resident.inTribeChat()) {
				if (resident.inTribe()) {
					String prefix = ChatColor.GRAY + "[Tribe]";
					String name = ChatColor.DARK_GREEN + " (" + ChatColor.GREEN + player.getDisplayName() + ChatColor.DARK_GREEN + ") ";
					String msg = e.getMessage();
					Tribe tribe = resident.getTribe();

					tribe.sendMessageToResidents(prefix + name + msg);

					String spyPrefix = ChatColor.GRAY + "[" + tribe.getName() + "]";
					adminUtils.sendTribeSpyMessage(tribe, spyPrefix + name + msg);
					plugin.log(prefix + spyPrefix + name + msg);
					e.setCancelled(true);
				} else {
					resident.setInTribeChat(false);
				}
			} else if (resident.inCivChat()) {
				if (resident.inTribe() || !resident.getTribe().inCivilisation()) {
					String prefix = ChatColor.GRAY + "[Civ]";
					String name = ChatColor.GOLD + " (" + ChatColor.GRAY + player.getDisplayName() + ChatColor.GOLD + ") ";
					String msg = e.getMessage();
					Civilisation civ = resident.getTribe().getCivilisation();

					civ.sendCivilisationMessage(prefix + name + msg);

					String spyPrefix = ChatColor.GRAY + "[" + civ.getName() + "]";
					adminUtils.sendCivilisationSpyMessage(civ, spyPrefix + name + msg);
					plugin.log(prefix + spyPrefix + name + msg);
					e.setCancelled(true);
				} else {
					resident.setInCivChat(false);
				}
			}
		}
	}

	public boolean fromWildToTribe(Chunk from, Chunk to) {
		return (!chunkUtils.isClaimed(from) && chunkUtils.isClaimed(to));
	}

	public boolean fromTribeToWild(Chunk from, Chunk to) {
		return (chunkUtils.isClaimed(from) && !chunkUtils.isClaimed(to));
	}

	public boolean fromTribeToDiffTribe(Chunk from, Chunk to) {
		return (chunkUtils.isClaimed(from) && chunkUtils.isClaimed(to) && !chunkUtils.getOwner(from).equals(chunkUtils.getOwner(to)));
	}

}
