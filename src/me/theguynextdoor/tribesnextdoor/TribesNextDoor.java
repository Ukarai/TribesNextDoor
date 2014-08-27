package me.theguynextdoor.tribesnextdoor;

import java.io.File;

import me.theguynextdoor.tribesnextdoor.backends.Backend;
import me.theguynextdoor.tribesnextdoor.backends.MySQLBackend;
import me.theguynextdoor.tribesnextdoor.backends.YMLBackend;
import me.theguynextdoor.tribesnextdoor.commands.admin.TribeAdminCommand;
import me.theguynextdoor.tribesnextdoor.commands.civilisation.CivilisationChatCommand;
import me.theguynextdoor.tribesnextdoor.commands.civilisation.CivilisationCommand;
import me.theguynextdoor.tribesnextdoor.commands.resident.ResidentCommand;
import me.theguynextdoor.tribesnextdoor.commands.tribe.TribeChatCommand;
import me.theguynextdoor.tribesnextdoor.commands.tribe.TribeCommand;
import me.theguynextdoor.tribesnextdoor.commands.tribe.TribeTeleportCommand;
import me.theguynextdoor.tribesnextdoor.listeners.BlockListener;
import me.theguynextdoor.tribesnextdoor.listeners.PlayerListener;
import me.theguynextdoor.tribesnextdoor.utils.AdminUtils;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.CivilisationUtils;
import me.theguynextdoor.tribesnextdoor.utils.ConfigUtils;
import me.theguynextdoor.tribesnextdoor.utils.EconomyUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;
import me.theguynextdoor.tribesnextdoor.utils.WorldUtils;
import me.theguynextdoor.tribesnextdoor.utils.data.BackupTask;
import me.theguynextdoor.tribesnextdoor.utils.data.BackupUtils;
import me.theguynextdoor.tribesnextdoor.utils.data.SaveTask;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * User: David Date: 6/7/13 Time: 2:00 PM
 */
public class TribesNextDoor extends JavaPlugin {
	public static final String PREFIX = ChatColor.GOLD + "[TribesNextDoor] ";

	private static TribesNextDoor instance;
	private ResidentUtils residentUtils;
	private TribeUtils tribeUtils;
	private ChunkUtils chunkUtils;
	private ConfigUtils configUtils;
	private WorldUtils worldUtils;
	private EconomyUtils economyUtils;
	private CivilisationUtils civilisationUtils;
	private AdminUtils adminUtils;
	private BackupUtils backupUtils;

	private Backend backend;
	private Economy economy;

	@Override
	public void onEnable() {
		init();

		residentUtils.loadResidents();
		tribeUtils.loadTribes();
		civilisationUtils.loadCivilisations();

		registerListeners();
		registerCommands();

		worldUtils.loadWorlds();
		adminUtils.load();
		setupEconomy();
		economyUtils.setEconomy(economy);

		startSaveTask();
		startBackupTask();
	}

	@Override
	public void onDisable() {
		backend.saveTribes();
		backend.saveCivilisations();
		adminUtils.save();

		if (backend instanceof MySQLBackend)
			((MySQLBackend) backend).denit();
	}

	public static TribesNextDoor getInstance() {
		return instance;
	}

	private void startBackupTask() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BackupTask(), 0, 3600 * 60);
	}

	private void startSaveTask() {
		int ticksPerSec = 20;
		int ticksPerMinute = (ticksPerSec * 60);
		int ticksPerHour = (ticksPerMinute * 3600);

		long minutes = configUtils.getSavePeriodMinutes() * ticksPerMinute;
		long hours = configUtils.getSavePeriodHours() * ticksPerHour;
		long total = hours + minutes;

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(), ticksPerMinute * 30, total);
	}

	private void init() {
		instance = this;
		configUtils = new ConfigUtils();
		residentUtils = new ResidentUtils();
		tribeUtils = new TribeUtils();
		chunkUtils = new ChunkUtils();
		worldUtils = new WorldUtils();
		civilisationUtils = new CivilisationUtils();
		adminUtils = new AdminUtils();
		backend = configUtils.isMySQLEnabled() ? new MySQLBackend(configUtils.getMySQLHostname(),
				configUtils.getMySQLPortNumber(), configUtils.getMySQLDatabase(), configUtils.getMySQLUsername(),
				configUtils.getMySQLPassword()) : new YMLBackend();
		economyUtils = new EconomyUtils();

		File file = new File(getDataFolder() + File.separator + "Backups");
		file.mkdirs();

		backupUtils = new BackupUtils();
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new BlockListener(), this);
	}

	private void registerCommands() {
		getCommand("tribe").setExecutor(new TribeCommand());
		getCommand("resident").setExecutor(new ResidentCommand());
		getCommand("tribechat").setExecutor(new TribeChatCommand());
		getCommand("ttp").setExecutor(new TribeTeleportCommand());
		getCommand("civilisation").setExecutor(new CivilisationCommand());
		getCommand("civilisationchat").setExecutor(new CivilisationChatCommand());
		getCommand("tribeadmin").setExecutor(new TribeAdminCommand());
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(
				net.milkbowl.vault.economy.Economy.class);

		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public void log(String msg) {
		getLogger().info(msg);
	}

	public BackupUtils getBackupUtils() {
		return backupUtils;
	}

	public ResidentUtils getResidentUtils() {
		return residentUtils;
	}

	public TribeUtils getTribeUtils() {
		return tribeUtils;
	}

	public ChunkUtils getChunkUtils() {
		return chunkUtils;
	}

	public Backend getBackend() {
		return backend;
	}

	public ConfigUtils getConfigUtils() {
		return configUtils;
	}

	public WorldUtils getWorldUtils() {
		return worldUtils;
	}

	public Economy getEconomy() {
		return economy;
	}

	public EconomyUtils getEconomyUtils() {
		return economyUtils;
	}

	public CivilisationUtils getCivilisationUtils() {
		return civilisationUtils;
	}

	public AdminUtils getAdminUtils() {
		return adminUtils;
	}
	
}
