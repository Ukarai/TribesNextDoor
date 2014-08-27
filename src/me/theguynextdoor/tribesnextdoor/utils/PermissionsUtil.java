package me.theguynextdoor.tribesnextdoor.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 * @author David
 *
 */
public class PermissionsUtil {

	public enum Permission {
		ADMIN_COMMAND("tribes.admin"), PROTECTION_BYPASS("tribes.bypass"), NEW_TRIBE("tribes.new");

		private String permission;

		private Permission(String permission) {
			this.permission = permission;
		}

		public String getPermission() {
			return permission;
		}
	}

	/**
	 * Method to check if a CommandSender has a specific permission or not
	 * 
	 * @param sender - CommandSender to check the permissions of
	 * 
	 * @param perm - The permission to be checked
	 * 
	 * @return Whether the CommandSender has the permission
	 */
	public static boolean hasPermission(CommandSender sender, Permission perm) {
		return sender.hasPermission(perm.getPermission());
	}
	
	/**
	 * Method to check if a player has a specific permission or not
	 * 
	 * @param sender - player to check the permissions of
	 * 
	 * @param perm - The permission to be checked
	 * 
	 * @return Whether the CommandSender has the permission
	 */
	public static boolean hasPermission(Player player, Permission perm) {
		return player.hasPermission(perm.getPermission());
	}
}
