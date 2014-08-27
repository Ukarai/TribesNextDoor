package me.theguynextdoor.tribesnextdoor.utils.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.util.FileUtil;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.utils.ConfigUtils;

/**
 * 
 * @author David
 * 
 */
public class BackupUtils {
	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	/**
	 * Instance of ConfigUtils
	 */
	private ConfigUtils configUtils;
	/**
	 * Last time the backup was run (in milliseconds)
	 */
	private long lastScheduledBackup;

	/**
	 * Ticks per second
	 */
	private int tps = 20;
	/**
	 * Ticks per minute
	 */
	private int tpm = tps * 60;
	/**
	 * Ticks per hour
	 */
	private int tph = tpm * 60;
	/**
	 * Ticks per day
	 */
	private int tpd = tph * 24;
	/**
	 * Ticks per week
	 */
	private int tpw = tpd * 7;

	/**
	 * Class constructor
	 */
	public BackupUtils() {
		plugin = TribesNextDoor.getInstance();
		configUtils = plugin.getConfigUtils();
		lastScheduledBackup = getLastScheduledBackup();
	}

	/**
	 * 
	 * @return Period between each backup (in ticks)
	 */
	public long getBackupPeriodInTicks() {
		int hours = configUtils.getBackupPeriodHours();
		int days = configUtils.getBackupPeriodDays();
		int weeks = configUtils.getBackupPeriodWeeks();

		long ht = hours * tph;
		long dt = days * tpd;
		long wt = weeks * tpw;

		long total = ht + dt + wt;
		return total;
	}

	/**
	 * 
	 * @return The time between the last backup and now (in milliseconds)
	 */
	public long timeElapsedMillis() {
		return System.currentTimeMillis() - lastScheduledBackup;
	}

	/**
	 * 
	 * @return The time between the last backup and now (in ticks)
	 */
	public long timeElapsedTicks() {
		return timeElapsedMillis() / 1000 * 20;
	}

	/**
	 * 
	 * @return The current date as a string
	 */
	public String dateToString() {
		return DateFormatUtils.format(Calendar.getInstance().getTime(),
				"yyyy-MM-dd HH_mm_SS");
	}

	/**
	 * 
	 * @return The time until the next backup
	 */
	public long scheduleTime() {
		return ((getBackupPeriodInTicks() - timeElapsedTicks()) > 0) ? (getBackupPeriodInTicks() - timeElapsedTicks())
				: 0;
	}

	/**
	 * 
	 * @return Whether the correct amount of time has passed between backups
	 */
	public boolean shouldBackup() {
		return scheduleTime() <= 0;
	}

	/**
	 * Method to backup the data files <br>
	 * This includes the Tribes data file, Civilisation data file and the
	 * Configuration file
	 */
	public void backupDataFiles() {
		File folder = new File(plugin.getDataFolder() + File.separator
				+ "Backups" + File.separator + dateToString());

		folder.mkdirs();

		File newTribeDataFile = new File(folder, "Tribes.yml");
		File newCivDataFile = new File(folder, "Civilisations.yml");
		File newConfigFile = new File(folder, "config.yml");

		File currentTribeDataFile = new File(plugin.getDataFolder(),
				"Tribes.yml");
		File currentCivDataFile = new File(plugin.getDataFolder(),
				"Civilisations.yml");
		File currentConfigFile = new File(plugin.getDataFolder(), "config.yml");

		int i = 1;
		while (newTribeDataFile.exists() || newCivDataFile.exists()
				|| newConfigFile.exists()) {
			newTribeDataFile = new File(folder, "Tribes (" + i + ").yml");
			newCivDataFile = new File(folder, "Civilisations (" + i + ").yml");
			newConfigFile = new File(folder, "config (" + i + ").yml");
			i++;
		}

		try {
			newTribeDataFile.createNewFile();
			newCivDataFile.createNewFile();
			newConfigFile.createNewFile();

			FileUtil.copy(currentTribeDataFile, newTribeDataFile);
			FileUtil.copy(currentCivDataFile, newCivDataFile);
			FileUtil.copy(currentConfigFile, newConfigFile);

			plugin.log("Data backed up");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to get the time since the last backup <br>
	 * Creates file (which stored last backup time) and sets time to current
	 * time if file is not present
	 * 
	 * @return The time since the last backup (in milliseconds)
	 */
	public long getLastScheduledBackup() {
		long lastBackup = System.currentTimeMillis();
		File file = new File(plugin.getDataFolder(), "LastBackup.txt");

		if (!file.exists()) {
			try {
				file.createNewFile();

				setLastScheduledBackupTime(System.currentTimeMillis());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				if (isLong(line)) {
					lastBackup = Long.parseLong(line);
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lastBackup;
	}

	/**
	 * Method to set when the last backup was ran
	 * 
	 * @param millis
	 *            - Time in milliseconds to set when the last backup was ran
	 */
	public void setLastScheduledBackupTime(long millis) {
		this.lastScheduledBackup = millis;

		File file = new File(plugin.getDataFolder(), "LastBackup.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(Long.toString(millis));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to check if a string is a long
	 * 
	 * @param s
	 *            - The string to check
	 * @return Whether the string is a long
	 */
	public boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
