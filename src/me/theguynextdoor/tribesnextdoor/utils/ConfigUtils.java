package me.theguynextdoor.tribesnextdoor.utils;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * User: David Date: 7/31/13 Time: 10:58 AM
 */
public class ConfigUtils {
	private static FileConfiguration config;

	public ConfigUtils() {
		init();
	}

	public void init() {
		config = TribesNextDoor.getInstance().getConfig();

		config.addDefault("Economy.Use", true);
		config.addDefault("Tribe.ClaimsPerPerson", 6);
		config.addDefault("Tribe.Allow spawn from other worlds", true);
		config.addDefault("Tribe.Damage on block break", 2.0);
		config.addDefault("Tribe.Protection.Explosions", true);
		config.addDefault("Tribe.Name.Max length", 12);
		config.addDefault("Tribe.Name.Min length", 3);
		config.addDefault("Tribe.Name.Show underscore", false);
		// Costs
		config.addDefault("Costs.Tribe.New", 200);
		config.addDefault("Costs.Tribe.Spawn", 50);
		config.addDefault("Costs.Tribe.Ttp", 50);
		config.addDefault("Costs.Tribe.Rename", 100);
		config.addDefault("Costs.Tribe.Claim", 50);
		config.addDefault("Costs.Civilisation.New", 500);
		// Saving
		config.addDefault("Periodic Save.Every.Minutes", 30);
		config.addDefault("Periodic Save.Every.Hours", 0);
		// Backup
		config.addDefault("Backup.Period.Weeks", 1);
		config.addDefault("Backup.Period.Days", 0);
		config.addDefault("Backup.Period.Hours", 0);
		// MySQL
		config.addDefault("MySQL.Enabled", false);
		config.addDefault("MySQL.Hostname", "localhost");
		config.addDefault("MySQL.Database", "minecraft");
		config.addDefault("MySQL.Port Number", "3306");
		config.addDefault("MySQL.Username", "root");
		config.addDefault("MySQL.Password", "password");

		config.options().copyDefaults(true);
		TribesNextDoor.getInstance().saveConfig();
	}

	public static int getClaimsPerPerson() {
		return config.getInt("Tribe.ClaimsPerPerson", 6);
	}

	public static boolean allowSpawnFromOtherWorld() {
		return config.getBoolean("Tribe.Allow spawn from other worlds");
	}

	public static double getDamageOnBlockBreak() {
		return config.getDouble("Tribe.Damage on block break");
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public boolean useEconomy() {
		return config.getBoolean("Economy.Use");
	}

	public int getSavePeriodMinutes() {
		return config.getInt("Periodic Save.Every.Minutes");
	}

	public int getSavePeriodHours() {
		return config.getInt("Periodic Save.Every.Hours");
	}

	public int getBackupPeriodWeeks() {
		return config.getInt("Backup.Period.Weeks");
	}

	public int getBackupPeriodDays() {
		return config.getInt("Backup.Period.Days");
	}

	public int getBackupPeriodHours() {
		return config.getInt("Backup.Period.Hours");
	}

	public boolean getExplosionProtection() {
		return config.getBoolean("Tribe.Protection.Explosions");
	}

	public boolean isMySQLEnabled() {
		return config.getBoolean("MySQL.Enabled");
	}

	public String getMySQLHostname() {
		return config.getString("MySQL.Hostname");
	}

	public String getMySQLDatabase() {
		return config.getString("MySQL.Database");
	}

	public String getMySQLPortNumber() {
		return config.getString("MySQL.Port Number");
	}

	public String getMySQLUsername() {
		return config.getString("MySQL.Username");
	}

	public String getMySQLPassword() {
		return config.getString("MySQL.Password");
	}

	public int getMaxTribeNameLength() {
		return config.getInt("Tribe.Name.Max length") == 0 ? Integer.MAX_VALUE : config.getInt("Tribe.Name.Max length");
	}

	public int getMinTribeNameLength() {
		return config.getInt("Tribe.Name.Min length") > getMaxTribeNameLength() ? 1 : config.getInt("Tribe.Name.Min length");
	}

	public boolean showUnderscore() {
		return config.getBoolean("Tribe.Name.Show underscore");
	}
}
