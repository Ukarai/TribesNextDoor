package me.theguynextdoor.tribesnextdoor.utils;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

import org.bukkit.ChatColor;

/**
 * User: David Date: 6/7/13 Time: 4:44 PM
 */
public class MessageUtils {

	/**
	 * Class Constructor
	 */
	private MessageUtils() {
		// Prevent initialisation of this class
	}

	public static final String ALREADY_CIVILISATION_WITH_NAME = TribesNextDoor.PREFIX + ChatColor.RED + "There is already a civilisation with that name";
	public static final String ALREADY_IN_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "You are already in a tribe";
	public static final String ALREADY_INVITED_PLAYER = TribesNextDoor.PREFIX + ChatColor.RED + "You have already invited that player";
	public static final String ALREADY_TRIBE_WITH_NAME = TribesNextDoor.PREFIX + ChatColor.RED + "There is already a tribe with that name";
	public static final String CANT_BREAK_BLOCK_IN_OTHER_TRIBES = TribesNextDoor.PREFIX + ChatColor.RED + "You can't break blocks in other tribes";
	public static final String CANT_PLACE_BLOCK_IN_OTHER_TRIBES = TribesNextDoor.PREFIX + ChatColor.RED + "You can't place blocks in other tribes";
	public static final String CHUNK_ALREADY_CLAIMED = TribesNextDoor.PREFIX + ChatColor.RED + "That chunk is already claimed";
	public static final String CHUNK_CLAIM_SUCCESS = TribesNextDoor.PREFIX + ChatColor.AQUA + "Chunk claim successful";
	public static final String CHUNK_UNCLAIM_SUCCESS = TribesNextDoor.PREFIX + ChatColor.AQUA + "Chunk unclaim successful";;
	public static final String CHUNK_NOT_CONNECTED = TribesNextDoor.PREFIX + ChatColor.RED + "That chunk is not connected to your tribe";
	public static final String CIV_CHAT_TOGGLED = TribesNextDoor.PREFIX + ChatColor.AQUA + "Civ chat has been toggled: ";
	public static final String CONTAINS_ILLEGAL_CHARACTERS = TribesNextDoor.PREFIX + ChatColor.RED + "That name contains illegal characters";
	public static final String DATA_BACKUP = TribesNextDoor.PREFIX + ChatColor.AQUA + "Tribe data has been backed up";
	public static final String DONT_HAVE_PERMISSION = TribesNextDoor.PREFIX + ChatColor.RED + "You don't have permission to do that";
	public static final String INSUFFICIENT_FUNDS = TribesNextDoor.PREFIX + ChatColor.RED + "You do not have the correct amount of money to do that";
	public static final String INVALID_ARGUMENTS_LENGTH = TribesNextDoor.PREFIX + ChatColor.RED + "Invalid number of arguments";
	public static final String INVALID_SUBCOMMAND = TribesNextDoor.PREFIX + ChatColor.RED + "Invalid sub-command";
	public static final String INVALID_TARGET_NOT_ONLINE = TribesNextDoor.PREFIX + ChatColor.RED + "Invalid target: Player not online";
	public static final String INVITATION_DECLINED = TribesNextDoor.PREFIX + ChatColor.AQUA + "Invitation declined";
	public static final String INVITATION_SENT = TribesNextDoor.PREFIX + ChatColor.AQUA + "Invitation sent";
	public static final String MAX_CLAIMS_REACHED = TribesNextDoor.PREFIX + ChatColor.RED + "You have reached your max number of claims";
	public static final String MUST_PASS_ON_OWNERSHIP_OF_CIV = TribesNextDoor.PREFIX + ChatColor.RED + "You must pass on ownership of the civilisation before you can do that";;
	public static final String MUST_PASS_ON_OWNERSHIP_OF_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "You must pass on ownership of the tribe before you can do that";
	public static final String NEED_TO_BE_CHIEF = TribesNextDoor.PREFIX + ChatColor.RED + "You need to be chief to do that";
	public static final String NO_INVITES = TribesNextDoor.PREFIX + ChatColor.RED + "You do not have any invites";
	public static final String NO_SUCH_CIVILISATION = TribesNextDoor.PREFIX + ChatColor.RED + "There is no civilisation with that name";
	public static final String NO_SUCH_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "There is no tribe with that name";
	public static final String NOT_CHIEF_OR_ELDER = TribesNextDoor.PREFIX + ChatColor.RED + "You need to be chief or an elder to do this";
	public static final String NOT_CIV_LEADER = TribesNextDoor.PREFIX + ChatColor.RED + "You are not the leader of the civilisation";
	public static final String NOT_IN_A_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "You are not in a tribe";
	public static final String NOT_IN_CHIEF_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "You are not in the chief tribe";
	public static final String NOT_IN_CORRECT_WORLD = TribesNextDoor.PREFIX + ChatColor.RED + "You are not in the same world as your tribe is based";
	public static final String NOT_INVITED_TO_THAT_CIVILISATION = TribesNextDoor.PREFIX + ChatColor.RED + "You have not been invited to that civilisation";
	public static final String NOT_INVITED_TO_THAT_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "You have not been invited to that tribe";
	public static final String PLAYER_ALREADY_IN_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "That player is already in a tribe";
	public static final String PLAYER_COMMAND_ONLY = TribesNextDoor.PREFIX + ChatColor.RED + "You need to be a player to use this command";
	public static final String PLAYER_NOT_IN_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "That player is not in tribe";
	public static final String PLAYER_NOT_IN_YOUR_TRIBE = TribesNextDoor.PREFIX + ChatColor.RED + "That player is not in your tribe";
	public static final String PLAYER_NOT_ONLINE = TribesNextDoor.PREFIX + ChatColor.RED + "That player is not online at the moment";
	public static final String RESIDENT_NOT_LOADED = TribesNextDoor.PREFIX + ChatColor.RED + "No resident with that name has been loaded";
	public static final String THAT_TRIBE_ALREADY_IN_CIVILISATION = TribesNextDoor.PREFIX + ChatColor.RED + "That tribe is already in a civilisation";
	public static final String THAT_TRIBE_NOT_IN_CIVILISATION = TribesNextDoor.PREFIX + ChatColor.RED + "That tribe is not in a civilisation";
	public static final String TOO_CLOSE_TO_OTHER_TRIBES = TribesNextDoor.PREFIX + ChatColor.RED + "You are too close to another tribe";
	public static final String TOO_CLOSE_TO_SPAWN = TribesNextDoor.PREFIX + ChatColor.RED + "You are too close to spawn";
	public static final String TP_TO_TRIBE_SPAWN = TribesNextDoor.PREFIX + ChatColor.AQUA + "You have been teleported to the tribe spawn";
	public static final String TRIBE_ALREADY_IN_CIVILISATION = TribesNextDoor.PREFIX + ChatColor.RED + "Your tribe is already in a civilisation";
	public static final String TRIBE_CHAT_TOGGLED = TribesNextDoor.PREFIX + ChatColor.AQUA + "Tribe chat has been toggled: ";
	public static final String TRIBE_NOT_IN_CIVILISATION = TribesNextDoor.PREFIX + ChatColor.RED + "Your tribe is not in a civilisation";
	public static final String TRIBES_NOT_ENABLED_IN_THIS_WORLD = TribesNextDoor.PREFIX + ChatColor.RED + "The tribes plugin is not enabled for this world";
	public static final String CHUNK_NOT_OWNED = TribesNextDoor.PREFIX + ChatColor.RED + "You don't own that chunk";
	public static final String SPAWN_SET = TribesNextDoor.PREFIX + ChatColor.AQUA + "Tribe spawn has been set";
	public static final String TOO_FEW_CLAIMS = TribesNextDoor.PREFIX + ChatColor.RED + "You have too few claims to do that";
	public static final String CANT_UNCLAIM_SPAWN = TribesNextDoor.PREFIX + ChatColor.RED + "You can't unclaim the chunk where your tribe spawn is set";
	public static final String TRIBE_NAME_TOO_LONG = TribesNextDoor.PREFIX + ChatColor.RED + "That name is too long.";
	public static final String TRIBE_NAME_TOO_SHORT = TribesNextDoor.PREFIX + ChatColor.RED + "That name is too short.";

}
