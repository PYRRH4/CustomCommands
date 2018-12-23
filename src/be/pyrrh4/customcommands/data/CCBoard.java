package be.pyrrh4.customcommands.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataSingleton;
import be.pyrrh4.pyrcore.lib.data.mysql.Query;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CCBoard extends DataSingleton {

	// fields and constructor
	private Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	private Map<String, Location> locations = new HashMap<String, Location>();

	public CCBoard () {
	}

	// getters
	public Map<String, ItemStack> getAllItems() {
		return Collections.unmodifiableMap(items);
	}

	public ItemStack getItem(String id) {
		return items.get(id);
	}

	public void setItem(String id, ItemStack item) {
		items.put(id, item);
		pushAsync(id, "item", Utils.serializeItem(item));
	}

	public Map<String, Location> getAllLocations() {
		return Collections.unmodifiableMap(locations);
	}

	public Location getLocation(String id) {
		return locations.get(id);
	}

	public void setLocation(String id, Location location) {
		locations.put(id, location);
		pushAsync(id, "location", Utils.serializeWXYZLocation(location));
	}

	// data
	private static final class JsonData {
		private final Map<String, ItemStack> items;
		private final Map<String, Location> locations;
		private JsonData(CCBoard board) {
			this.items = board.items;
			this.locations = board.locations;
		}
	}

	@Override
	public CCDataManager getDataManager() {
		return CustomCommands.inst().getData();
	}

	@Override
	protected final File getJsonFile() {
		return new File(PyrCore.inst().getDataRootFolder() + "/customcommands_board.json");
	}

	@Override
	protected final void jsonPull() {
		File file = getJsonFile();
		if (file.exists()) {
			JsonData data = Utils.loadFromGson(JsonData.class, file, true);
			// clear cache
			items.clear();
			locations.clear();
			// replace
			items.putAll(data.items);
			locations.putAll(data.locations);
		}
	}

	@Override
	protected final void jsonPush() {
		Utils.saveToGson(new JsonData(this), getJsonFile());
	}

	@Override
	protected final void jsonDelete() {
		File file = getJsonFile();
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	protected final String getMySQLTable() {
		return "customcommands_board";
	}

	@Override
	protected final Query getMySQLInitQuery() {
		return new Query("CREATE TABLE IF NOT EXISTS `" + getMySQLTable() + "`(" +
				"id VARCHAR(100) NOT NULL," +
				"type VARCHAR(20) NOT NULL," +
				"data VARCHAR(1000) NOT NULL," +
				"PRIMARY KEY(id)" +
				") ENGINE=InnoDB DEFAULT CHARSET=?;", "utf8");
	}

	@Override
	protected final void mysqlPull() throws SQLException {
		ResultSet set = getDataManager().performMySQLGetQuery(new Query("SELECT * FROM `" + getMySQLTable() + "`;"));
		if (set != null) {
			while (set.next()) {
				// decode
				String id = set.getString("id");
				String type = set.getString("type");
				String data = set.getString("data");
				if (type.equals("item")) {
					items.put(id, Utils.unserializeItem(data));
				} else if (type.equals("location")) {
					locations.put(id, Utils.unserializeWXYZLocation(data));
				}
			}
		}
	}

	@Override
	protected final void mysqlPush(Object... params) {
		if (params == null || params.length != 3) {
			throw new IllegalArgumentException();
		}
		String id = (String) params[0];
		String type = (String) params[1];
		String data = (String) params[2];
		getDataManager().performMySQLUpdateQuery(new Query("REPLACE INTO `" + getMySQLTable() + "`(`id`,`type`,`data`) VALUES(?,?,?);", id, type, data));
	}

	@Override
	protected final void mysqlDelete(Object... params) {
		if (params != null && params.length == 1) {
			String id = (String) params[0];
			getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "` WHERE `id`=?;", id));
		} else {
			getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "`;"));
		}
	}

}
