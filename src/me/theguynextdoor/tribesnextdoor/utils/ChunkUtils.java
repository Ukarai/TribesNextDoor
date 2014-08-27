package me.theguynextdoor.tribesnextdoor.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.ClaimResult;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils.Costs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * User: David Date: 6/7/13 Time: 2:37 PM
 */
public class ChunkUtils {
	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	/**
	 * Map containing all claimed chunks and their associated Tribe <br>
	 * Key - Chunk location <br>
	 * Value - Tribe object
	 */
	private Map<String, Tribe> claimed;

	/**
	 * Class constructor
	 */
	public ChunkUtils() {
		plugin = TribesNextDoor.getInstance();
		claimed = new LinkedHashMap<String, Tribe>();
	}

	/**
	 * Method to check if a chunk has been claimed
	 * 
	 * @param chunk
	 *            - Chunk to be checked
	 * 
	 * @return Whether the chunk has been claimed or not
	 */
	public boolean isClaimed(Chunk chunk) {
		return claimed.containsKey(getChunkString(chunk));
	}

	/**
	 * Method to claim a chunk without any checks
	 * 
	 * @param tribe
	 *            - Tribe to claim the chunk for
	 * @param chunk
	 *            - Chunk to be claimed
	 */
	public void unsafeClaim(Tribe tribe, Chunk chunk) {
		claimed.put(getChunkString(chunk), tribe);
	}

	/**
	 * Method to safely claim a chunk for a tribe
	 * 
	 * @param chunk
	 *            - Chunk to be claimed
	 * @param tribe
	 *            - Tribe to claim the chunk for
	 * @param player
	 *            - Person sending the claim command (Messages are sent to this
	 *            player)
	 */
	public boolean claimChunkLegit(Chunk chunk, Tribe tribe, Player player) {
		if (getTotalTribeClaims(tribe) < (tribe.getResidents().size() * ConfigUtils.getClaimsPerPerson()) + tribe.getBonusClaims()) {
			int px = player.getLocation().getChunk().getX();
			int pz = player.getLocation().getChunk().getZ();
			Chunk check;

			for (int xx = -1; xx < 2; xx++) {
				for (int zz = -1; zz < 2; zz++) {
					check = player.getWorld().getChunkAt(px + xx, pz + zz);

					if (isClaimed(check) && getOwner(check).equalsIgnoreCase(tribe.getName()) && (xx == 0 || zz == 0)) {
						plugin.getEconomyUtils().handleFunds(player, Costs.TRIBE_CLAIM);
						claimed.put(getChunkString(chunk), tribe);
						player.sendMessage(MessageUtils.CHUNK_CLAIM_SUCCESS);
						return true;
					}
				}
			}
			player.sendMessage(MessageUtils.CHUNK_NOT_CONNECTED);
		} else {
			player.sendMessage(MessageUtils.MAX_CLAIMS_REACHED);
		}

		return false;
	}

	public ClaimResult claimChunk(Player player) {
		Resident resident = plugin.getResidentUtils().getResident(player.getName());

		if (resident.inTribe() && (resident.isChief() || resident.isElder())) {
			Tribe tribe = resident.getTribe();
			Chunk chunk = player.getLocation().getChunk();

			if (!isClaimed(chunk)) {
				if (plugin.getEconomyUtils().canAfford(player, Costs.TRIBE_CLAIM)) {

					if (getTotalTribeClaims(tribe) < (tribe.getResidents().size() * ConfigUtils.getClaimsPerPerson()) + tribe.getBonusClaims()) {
						int px = player.getLocation().getChunk().getX();
						int pz = player.getLocation().getChunk().getZ();
						Chunk check;

						for (int xx = -1; xx < 2; xx++) {
							for (int zz = -1; zz < 2; zz++) {
								check = player.getWorld().getChunkAt(px + xx, pz + zz);

								if (isClaimed(check) && getOwner(check).equalsIgnoreCase(tribe.getName()) && (xx == 0 || zz == 0)) {
									plugin.getEconomyUtils().handleFunds(player, Costs.TRIBE_CLAIM);
									claimed.put(getChunkString(chunk), tribe);
									return ClaimResult.SUCCESS;
								}
							}
						}
						return ClaimResult.NOT_CONNECTED;
					} else {
						return ClaimResult.LIMIT_REACHED;
					}
				} else {
					return ClaimResult.INSUFFICIENT_FUNDS;
				}
			} else {
				return ClaimResult.ALREADY_CLAIMED;
			}
		} else {
			return ClaimResult.NO_PERMISSION;
		}
	}

	/**
	 * Method to unclaim a chunk
	 * 
	 * @param chunk
	 *            - Chunk to be unclaimed
	 */
	public void unclaimChunk(Chunk chunk) {
		if (isClaimed(chunk)) {
			claimed.remove(getChunkString(chunk));
		}
	}

	/**
	 * Removes all the claims for a tribe
	 * 
	 * @param tribe
	 *            - Tribe to have claims removed from
	 */
	public synchronized void removeClaims(Tribe tribe) {
		Iterator<String> it1 = claimed.keySet().iterator(); // We need to use an
															// iterator to
															// prevent a
															// ConcurrentModificationException

		while (it1.hasNext()) {
			String key = it1.next();

			if (claimed.get(key).getName().equals(tribe.getName())) {
				it1.remove();
			}
		}
	}

	/**
	 * Gets the location of a chunk in String form. This is used to for storage
	 * and saving
	 * 
	 * @param chunk
	 *            - Chunk to get location of
	 * @return String form of chunk location
	 */
	public String getChunkString(Chunk chunk) {
		return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
	}

	/**
	 * Method to get the total number of claims a tribe has
	 * 
	 * @param tribe
	 *            - Tribe to get total number of claims
	 * @return Total number of claims the specified tribe has
	 */
	public int getTotalTribeClaims(Tribe tribe) {
		int i = 0;

		for (String s : claimed.keySet()) {
			if (claimed.get(s).getName().equals(tribe.getName())) {
				i++;
			}
		}

		return i;
	}

	/**
	 * Method to get the name of the tribe who owns a chunk
	 * 
	 * @param chunk
	 *            - Chunk to be checked
	 * @return The name of the tribe which owns a chunk or 'This chunk has not
	 *         been claimed' if it isn't claimed
	 */
	public String getOwner(Chunk chunk) {
		String s = getChunkString(chunk);

		return isClaimed(chunk) ? claimed.get(s).getName() : "This chunk has not been claimed";
	}

	/**
	 * Method to get a list of all the chunks a tribe owns
	 * 
	 * @param tribe
	 *            - Tribe to get the chunks owned by
	 * @return A list of the locations of the chunks this tribe has claimed
	 */
	public List<String> getChunks(Tribe tribe) {
		List<String> list = new ArrayList<String>();

		for (String s : claimed.keySet()) {
			if (claimed.get(s).getName().equalsIgnoreCase(tribe.getName())) {
				list.add(s);
			}
		}

		return list;
	}

	/**
	 * Method to add a list of chunk locations and associate them with a tribe
	 * 
	 * @param tribe
	 *            - Tribe to add claims to
	 * @param claims
	 *            - List of chunk locations to be claimed
	 */
	public void loadTribeClaims(Tribe tribe, List<String> claims) {
		for (String claim : claims) {
			claimed.put(claim, tribe);
		}
	}

	/**
	 * 
	 * @return Total number of chunks claimed by all the tribes
	 */
	public int getAmountOfChunksClaimed() {
		return claimed.size();
	}

	/**
	 * Method to check a tribe does not have more claims than its limit and
	 * remove them if it does
	 * 
	 * @param tribe
	 *            - Tribe to be checked
	 */
	public void checkClaimAmount(Tribe tribe) {
		int tribeClaimed = getTotalTribeClaims(tribe);
		int shouldHave = (tribe.getResidents().size() * ConfigUtils.getClaimsPerPerson()) + tribe.getBonusClaims();

		while (tribeClaimed > shouldHave) {
			List<String> list = new ArrayList<String>(claimed.keySet());
			int count = 0;
			int latest = 0;

			for (String s : list) {
				if (claimed.get(s).getName().equalsIgnoreCase(tribe.getName())) {
					latest = count;
				}
				count++;
			}

			if (this.claimed.get(list.get(latest)).getName().equalsIgnoreCase(tribe.getName())) {
				claimed.remove(list.get(latest));
				plugin.log("Removing claim '" + list.get(latest) + "' from " + tribe.getName());

				if (Bukkit.getPlayerExact(tribe.getChief()) != null) {
					Bukkit.getPlayerExact(tribe.getChief()).sendMessage(ChatColor.GOLD + list.get(latest) + " force unclaimed");
				}
			}

			tribeClaimed = getTotalTribeClaims(tribe);
		}

		plugin.getBackend().saveTribe(tribe);
	}

	/**
	 * Key - Chunk location string <br>
	 * Value - Tribe object
	 * 
	 * @return Map containing all chunk locations and the owner of the chunks
	 */
	public Map<String, Tribe> getClaimed() {
		return claimed;
	}

	public void claimOutpost(Tribe tribe, String outpostName, Player sender) {

	}

}
