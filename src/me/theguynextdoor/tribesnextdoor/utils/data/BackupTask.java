package me.theguynextdoor.tribesnextdoor.utils.data;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;

/**
 * 
 * @author David
 *
 */
public class BackupTask implements Runnable {

	/**
	 * Instance of main class
	 */
	private TribesNextDoor plugin;
	/**
	 * Instance of BackupUtils
	 */
	private BackupUtils backupUtils;

	/**
	 * Class constructor
	 */
	public BackupTask() {
		plugin = TribesNextDoor.getInstance();
		backupUtils = plugin.getBackupUtils();
	}

	/**
	 * Method to backup data files
	 */
	@Override
	public void run() {
		if (backupUtils.shouldBackup()) {
			backupUtils.backupDataFiles();
			backupUtils.setLastScheduledBackupTime(System.currentTimeMillis());
		}
	}

}
