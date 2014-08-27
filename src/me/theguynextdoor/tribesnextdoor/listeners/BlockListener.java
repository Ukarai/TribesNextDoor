package me.theguynextdoor.tribesnextdoor.listeners;

import java.util.Iterator;
import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.ConfigUtils;
import me.theguynextdoor.tribesnextdoor.utils.MessageUtils;
import me.theguynextdoor.tribesnextdoor.utils.PermissionsUtil;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;
import me.theguynextdoor.tribesnextdoor.utils.PermissionsUtil.Permission;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * 
 * @author David
 *
 */
public class BlockListener implements Listener {
	private TribesNextDoor plugin;
	private ResidentUtils residentUtils;
	private ChunkUtils chunkUtils;
	private TribeUtils tribeUtils;
	private WorldUtils worldUtils;
	private ConfigUtils configUtils;

	public BlockListener() {
		plugin = TribesNextDoor.getInstance();
		residentUtils = plugin.getResidentUtils();
		chunkUtils = plugin.getChunkUtils();
		tribeUtils = plugin.getTribeUtils();
		worldUtils = plugin.getWorldUtils();
		configUtils = plugin.getConfigUtils();
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		if (PermissionsUtil.hasPermission(e.getPlayer(), Permission.PROTECTION_BYPASS))
			return;

		Block block = e.getBlock();

		if (worldUtils.getWorldOptions(block.getWorld().getName()).isTribesEnabled()) {
			if (chunkUtils.isClaimed(block.getChunk())) {
				Player player = e.getPlayer();
				Resident resident = residentUtils.getResident(player.getName());
				Tribe tribe = tribeUtils.getTribe(chunkUtils.getOwner(block.getChunk()));

				if (!resident.inTribe() || !resident.getTribe().getName().equals(tribe.getName())) {
					player.damage(ConfigUtils.getDamageOnBlockBreak());
					player.sendMessage(MessageUtils.CANT_BREAK_BLOCK_IN_OTHER_TRIBES);
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent e) {
		if (PermissionsUtil.hasPermission(e.getPlayer(), Permission.PROTECTION_BYPASS))
			return;
		Block block = e.getBlock();

		if (worldUtils.getWorldOptions(block.getWorld().getName()).isTribesEnabled()) {
			if (chunkUtils.isClaimed(block.getChunk())) {
				Player player = e.getPlayer();
				Resident resident = residentUtils.getResident(player.getName());
				Tribe tribe = tribeUtils.getTribe(chunkUtils.getOwner(block.getChunk()));

				if (!resident.inTribe() || !resident.getTribe().getName().equals(tribe.getName())) {
					player.damage(ConfigUtils.getDamageOnBlockBreak());
					player.sendMessage(MessageUtils.CANT_PLACE_BLOCK_IN_OTHER_TRIBES);
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void hangingBreak(HangingBreakByEntityEvent e) {
		if (e.getRemover() instanceof Player) {
			if (PermissionsUtil.hasPermission((Player) e.getRemover(), Permission.PROTECTION_BYPASS))
				return;
			Hanging hanging = e.getEntity();

			if (worldUtils.getWorldOptions(hanging.getWorld().getName()).isTribesEnabled()) {
				if (chunkUtils.isClaimed(hanging.getLocation().getChunk())) {
					Player player = (Player) e.getRemover();
					Resident resident = residentUtils.getResident(player.getName());

					if (!resident.inTribe() || !resident.getTribe().getName().equals(tribeUtils.getTribe(chunkUtils.getOwner(hanging.getLocation().getChunk())).getName())) {
						player.damage(ConfigUtils.getDamageOnBlockBreak());
						player.sendMessage(MessageUtils.CANT_BREAK_BLOCK_IN_OTHER_TRIBES);
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		if (PermissionsUtil.hasPermission(e.getPlayer(), Permission.PROTECTION_BYPASS))
			return;
		if (worldUtils.getWorldOptions(e.getPlayer().getWorld().getName()).isTribesEnabled()) {
			if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
				Block block = e.getClickedBlock();

				if (chunkUtils.isClaimed(block.getChunk())) {
					Material type = block.getType();

					if (type == Material.STONE_BUTTON || type == Material.LEVER || type == Material.STONE_PLATE) {
						Player player = e.getPlayer();
						Resident resident = residentUtils.getResident(player.getName());

						if (!resident.inTribe() || !resident.getTribe().getName().equals(tribeUtils.getTribe(chunkUtils.getOwner(block.getChunk())).getName())) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void explosion(EntityExplodeEvent e) {
		if (configUtils.getExplosionProtection()) {
			Iterator<Block> iter = e.blockList().iterator(); // Wouldn't want a ConcurrentModificationException now would we?

			while (iter.hasNext()) {
				if (chunkUtils.isClaimed(iter.next().getChunk())) {
					iter.remove();
				}
			}
		}
	}

}
