package me.theguynextdoor.tribesnextdoor.backends;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.TribeSettings;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.TribeSpawn;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;
import me.theguynextdoor.tribesnextdoor.utils.TribeUtils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * User: David Date: 6/7/13 Time: 5:03 PM
 */
public class YMLBackend implements Backend {

	private File tribeFileLocation;
	private FileConfiguration tribeFile;
	private File civilisationFileLocation;
	private FileConfiguration civilisationFile;
	private TribesNextDoor plugin;
	private TribeUtils tribeUtils;
	private ChunkUtils chunkUtils;

	public YMLBackend() {
		init();
	}

	@Override
	public void init() {
		plugin = TribesNextDoor.getInstance();
		tribeUtils = plugin.getTribeUtils();
		chunkUtils = plugin.getChunkUtils();
		tribeFileLocation = new File(plugin.getDataFolder(), "Tribes.yml");
		civilisationFileLocation = new File(plugin.getDataFolder(), "Civilisations.yml");

		if (!tribeFileLocation.exists()) {
			try {
				tribeFileLocation.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!civilisationFileLocation.exists()) {
			try {
				civilisationFileLocation.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tribeFile = YamlConfiguration.loadConfiguration(tribeFileLocation);
		civilisationFile = YamlConfiguration.loadConfiguration(civilisationFileLocation);
	}

	@Override
	public Map<String, Tribe> loadTribes() {
		Map<String, Tribe> map = new HashMap<String, Tribe>();

		for (String s : tribeFile.getValues(false).keySet()) {
			map.put(s, loadTribe(s));
		}

		return map;
	}

	@Override
	public void saveTribes() {
		for (String cs : tribeFile.getValues(false).keySet()) {
			tribeFile.set(cs, null);
		}

		for (Tribe tribe : tribeUtils.getTribes().values()) {
			saveTribe(tribe);
		}

		save();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Tribe loadTribe(String name) {
		if (tribeFile.getConfigurationSection(name) != null) {
			Tribe tribe = new Tribe(name);
			Map<String, Object> map = tribeFile.getConfigurationSection(name).getValues(false);

			tribe.setChief((String) map.get("Chief"));
			tribe.setMaker((String) map.get("Maker"));

			for (String resident : (List<String>) map.get("Residents")) {
				tribe.addResident(resident);
			}
			for (String elder : (List<String>) map.get("Elders")) {
				tribe.addElder(elder);
			}

			tribe.addBonusClaims(map.containsKey("BonusClaims") ? (Integer) map.get("BonusClaims") : 0);
			tribe.setSettings(new TribeSettings(tribeFile.getConfigurationSection(name + ".Settings").getValues(false)));
			chunkUtils.loadTribeClaims(tribe, tribeFile.getStringList(tribe.getName() + ".Claimed"));
			tribe.setSpawn(new TribeSpawn(tribeFile.getConfigurationSection(name + ".Spawn").getValues(false)));
			chunkUtils.checkClaimAmount(tribe);
			return tribe;
		}

		return null;
	}

	@Override
	public void saveTribe(Tribe tribe) {
		tribeFile.set(tribe.getName(), tribe.serialize());
		save();
	}

	@Override
	public Map<String, Resident> loadResidents(ResidentUtils residentUtils) {
		Map<String, Resident> map = new HashMap<String, Resident>();

		for (String s : tribeFile.getValues(false).keySet()) {
			for (String res : tribeFile.getStringList(s + ".Residents")) {
				map.put(res, loadResident(res, residentUtils));
			}
		}

		return map;
	}

	@Override
	public Resident loadResident(String name, ResidentUtils residentUtils) {
		Resident resident = new Resident(name, residentUtils);

		for (String s : tribeFile.getValues(false).keySet()) {
			List<String> residents = tribeFile.getStringList(s + ".Residents");

			if (residents.contains(name)) {
				resident.setTribe(tribeUtils.getTribe(s));
			}
		}

		return resident;
	}

	public void save() {
		try {
			tribeFile.save(tribeFileLocation);
			civilisationFile.save(civilisationFileLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Civilisation> loadCivilisations() {
		Map<String, Civilisation> map = new HashMap<String, Civilisation>();

		for (String s : civilisationFile.getValues(false).keySet()) {
			map.put(s, loadCivilisation(s));
		}

		return map;
	}

	@Override
	public Civilisation loadCivilisation(String name) {
		if (civilisationFile.getConfigurationSection(name) != null) {
			Civilisation civ = new Civilisation(name);
			Tribe chiefTribe = tribeUtils.getTribe(civilisationFile.getString(name + ".ChiefTribe"));

			civ.addTribe(chiefTribe);
			civ.setChiefTribe(chiefTribe);

			for (Tribe tribe : tribeUtils.getTribes().values()) {
				if (tribeFile.getString(tribe.getName() + ".Civilisation") != null && tribeFile.getString(tribe.getName() + ".Civilisation").equals(civ.getName())) {
					civ.addTribe(tribe);
				}
			}

			return civ;
		}
		return null;
	}

	@Override
	public void saveCivilisations() {
		for (String cs : civilisationFile.getValues(false).keySet()) {
			civilisationFile.set(cs, null);
		}

		for (Civilisation civ : plugin.getCivilisationUtils().getCivilisations().values()) {
			saveCivilisation(civ);
		}

		save();
	}

	@Override
	public void saveCivilisation(Civilisation civ) {
		civilisationFile.set(civ.getName(), civ.serialize());
	}
}
