package me.theguynextdoor.tribesnextdoor.utils;

import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

/**
 * 
 * @author David
 *
 */
public class EconomyUtils {
	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	private ConfigUtils configUtils;
	private Economy economy;
	private ConfigUtils config;

	/**
	 * Class Constructor
	 */
	public EconomyUtils() {
		plugin = TribesNextDoor.getInstance();
		configUtils = plugin.getConfigUtils();
		economy = plugin.getEconomy();
		config = plugin.getConfigUtils();
	}

	/**
	 * Setter method for economy
	 * 
	 * @param econ
	 *            - Economy object to be used
	 */
	public void setEconomy(Economy econ) {
		this.economy = econ;
	}

	/**
	 * Method to get the cost of a specific tribe related command
	 * 
	 * @param costItem
	 *            - The enum related to the command
	 * 
	 * @return The cost of the command
	 */
	public double getCost(Costs costItem) {
		return economy != null ? configUtils.getConfig().getDouble(costItem.getConfigPath()) : 0;
	}

	/**
	 * Method to check if the user has the correct amount of money to do a
	 * command
	 * 
	 * @param player
	 *            - The player to be checked
	 * @param costItem
	 *            - The enum related to the command
	 * @return Whether the player has the correct amount of currency
	 */
	public boolean canAfford(Player player, Costs costItem) {
		return economy != null ? config.useEconomy() ? economy.has(player.getName(), getCost(costItem)) : true : true;
	}

	/**
	 * Method to deduct the correct amount of money according to command
	 * 
	 * @param player
	 *            - The player to deduct the money from
	 * 
	 * @param cost
	 *            - The {@link Costs} related to the command to get cost from
	 * @return Whether the currency was successfully deducted
	 */
	public boolean handleFunds(Player player, Costs cost) {
		if (economy != null && config.useEconomy()) {
			EconomyResponse er = economy.withdrawPlayer(player.getName(), getCost(cost));

			if (!er.transactionSuccess())
				player.sendMessage(er.errorMessage);

			return er.transactionSuccess();
		} else
			return true;
	}

	public enum Costs {
		TTP("Costs.Tribe.Ttp"),
		NEW_TRIBE("Costs.Tribe.New"),
		TRIBE_SPAWN("Costs.Tribe.Spawn"),
		TRIBE_RENAME("Costs.Tribe.Rename"),
		TRIBE_CLAIM("Costs.Tribe.Claim"),
		NEW_CIVILISATION("Costs.Civilisation.New");

		private String configPath;

		private Costs(String configPath) {
			this.configPath = configPath;
		}

		/**
		 * Method to get the path of this cost in the config file
		 * 
		 * @return Path
		 */
		public String getConfigPath() {
			return configPath;
		}
	}
}
