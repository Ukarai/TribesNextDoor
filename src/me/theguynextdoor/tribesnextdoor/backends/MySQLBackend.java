package me.theguynextdoor.tribesnextdoor.backends;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.theguynextdoor.tribesnextdoor.TribesNextDoor;
import me.theguynextdoor.tribesnextdoor.backends.database.MySQL;
import me.theguynextdoor.tribesnextdoor.datatypes.civilisation.Civilisation;
import me.theguynextdoor.tribesnextdoor.datatypes.resident.Resident;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.Tribe;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.TribeSettings;
import me.theguynextdoor.tribesnextdoor.datatypes.tribe.TribeSpawn;
import me.theguynextdoor.tribesnextdoor.utils.ChunkUtils;
import me.theguynextdoor.tribesnextdoor.utils.ResidentUtils;

public class MySQLBackend implements Backend {

	private TribesNextDoor plugin;
	private MySQL mysql;

	private String hostname;
	private String portNumber;
	private String database;
	private String username;
	private String password;

	private ChunkUtils chunkUtils;

	public MySQLBackend(String hostname, String portNumber, String database, String username, String password) {
		plugin = TribesNextDoor.getInstance();

		this.hostname = hostname;
		this.portNumber = portNumber;
		this.database = database;
		this.username = username;
		this.password = password;

		chunkUtils = plugin.getChunkUtils();

		init();
	}

	@Override
	public void init() {
		mysql = new MySQL(plugin.getLogger(), "", hostname, portNumber, database, username, password);
		mysql.open();

		// Create tribes table
		mysql.query("CREATE TABLE IF NOT EXISTS TND_TRIBE_DATA " + "(" + "Name VARCHAR(100) PRIMARY KEY, " + "Chief VARCHAR(50), " + "Maker VARCHAR(50), "
				+ "Enter_Message VARCHAR(150), " + "Leave_Message VARCHAR(150), " + "MOTD VARCHAR(150), " + "Civilisation VARCHAR(150), " + "Spawn VARCHAR(150) " + ")");

		// Create chunks table
		mysql.query("CREATE TABLE IF NOT EXISTS TND_CHUNK_DATA (" + "Chunk_Loc VARCHAR(150) PRIMARY KEY, " + "Tribe VARCHAR(150)" + ")");

		// Create resident table
		mysql.query("CREATE TABLE IF NOT EXISTS TND_RESIDENT_DATA (Name VARCHAR(150) PRIMARY KEY, Tribe VARCHAR(150), Role VARCHAR(150))");

		// Create civilisation table
		mysql.query("CREATE TABLE IF NOT EXISTS TND_CIVILISATION_DATA (Name VARCHAR(150) PRIMARY KEY, Chief_Tribe VARCHAR(150))");
	}

	public void denit() {
		mysql.close();
	}

	@Override
	public Map<String, Tribe> loadTribes() {
		Map<String, Tribe> map = new HashMap<String, Tribe>();

		try {
			Statement statement = mysql.getConnection().createStatement();
			ResultSet query = statement.executeQuery("SELECT * FROM TRIBE_DATA");

			while (query.next()) {
				map.put(query.getString(0), loadTribe(query.getString(0)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return map;
	}

	@Override
	public void saveTribes() {
		mysql.query("TRUNCATE TABLE TRIBE_DATA"); // We need to clear the table to account for tribe renaming

		for (Tribe tribe : plugin.getTribeUtils().getTribes().values()) {
			saveTribe(tribe);
		}

		mysql.query("TRUNCATE TABLE CHUNK_DATA");
		
		for(String chunkString : chunkUtils.getClaimed().keySet()) {
			PreparedStatement prep = mysql.prepare("UPDATE CHUNK_DATA SET ");
		}
	}

	@Override
	public Tribe loadTribe(String name) {
		Tribe tribe = new Tribe(name);

		try {
			PreparedStatement ps = mysql.prepare("SELECT FROM TRIBE_DATA WHERE Name = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				tribe.setChief(rs.getString(2));
				tribe.setMaker(rs.getString(3));

				TribeSettings settings = new TribeSettings();
				settings.setEnterMessage(rs.getString(4));
				settings.setLeaveMessage(rs.getString(5));
				settings.setMotd(rs.getString(6));
				tribe.setSettings(settings);

				PreparedStatement resQuery = mysql.prepare("SELECT * FROM TND_RESIDENT_DATA WHERE Tribe = ?");
				resQuery.setString(1, name);

				ResultSet resResult = resQuery.executeQuery();

				while (resResult.next()) {
					tribe.addResident(resResult.getString(1));

					if (resResult.getString(3).equalsIgnoreCase("Elder")) {
						tribe.addElder(resResult.getString(1));
					}
				}

				PreparedStatement chunkQuery = mysql.prepare("SELECT * FROM TND_CHUNK_DATA WHERE Tribe = ?)");
				chunkQuery.setString(1, name);

				ResultSet chunkResult = chunkQuery.executeQuery();
				List<String> claims = new ArrayList<String>();

				while (chunkResult.next()) {
					claims.add(chunkResult.getString(2));
				}

				chunkUtils.loadTribeClaims(tribe, claims);
				tribe.setSpawn(new TribeSpawn(rs.getString(8)));
				chunkUtils.checkClaimAmount(tribe);
				return tribe;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveTribe(Tribe tribe) {
		try {
			PreparedStatement prep = mysql
					.prepare("INSERT INTO TRIBE_DATA VALUES (?,?,?,?,?,?,?,?)");

			prep.setString(1, tribe.getName());
			prep.setString(2, tribe.getChief());
			prep.setString(3, tribe.getMaker());
			prep.setString(4, tribe.getSettings().getEnterMessage());
			prep.setString(5, tribe.getSettings().getLeaveMessage());
			prep.setString(6, tribe.getSettings().getMotd());
			prep.setString(7, tribe.inCivilisation() ? tribe.getCivilisation().getName() : "");
			prep.setString(8, tribe.getSpawn().toString());

			prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Resident> loadResidents(ResidentUtils residentUtils) {
		Map<String, Resident> map = new HashMap<String, Resident>();
		return map;
	}

	@Override
	public Resident loadResident(String name, ResidentUtils residentUtils) {
		return null;
	}

	@Override
	public Map<String, Civilisation> loadCivilisations() {
		Map<String, Civilisation> map = new HashMap<String, Civilisation>();
		return map;
	}

	@Override
	public Civilisation loadCivilisation(String name) {
		return null;
	}

	@Override
	public void saveCivilisations() {

	}

	@Override
	public void saveCivilisation(Civilisation civ) {

	}

}
