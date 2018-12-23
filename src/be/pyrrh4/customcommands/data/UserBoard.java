package be.pyrrh4.customcommands.data;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.data.PCUser;
import be.pyrrh4.pyrcore.lib.data.DataBoard;
import be.pyrrh4.pyrcore.lib.data.mysql.Query;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class UserBoard extends DataBoard<CCUser> {

	// fields
	Map<UUID, CCUser> online = new HashMap<UUID, CCUser>();

	// methods
	public Map<UUID, CCUser> getAll() {
		return Collections.unmodifiableMap(online);
	}

	@Override
	public CCUser getElement(Object param) {
		if (param instanceof OfflinePlayer) {
			return online.get(((OfflinePlayer) param).getUniqueId());
		} else if (param instanceof UUID) {
			return online.get((UUID) param);
		} else if (param instanceof PCUser) {
			PCUser user = (PCUser) param;
			return user.isCurrentProfile() ? online.get(user.getUniqueId()) : null;
		}
		throw new IllegalArgumentException("param type " + param.getClass() + " isn't allowed");
	}

	public void pullOnline() {
		for (Player pl : Utils.getOnlinePlayers()) {
			UUID uuid = pl.getUniqueId();
			if (!online.containsKey(uuid)) {
				online.put(uuid, new CCUser(new PCUser(pl.getUniqueId())));
			}
		}
		pullAsync(online.values(), null);
	}

	// data
	@Override
	public CCDataManager getDataManager() {
		return CustomCommands.inst().getData();
	}

	@Override
	protected final File getJsonFile(CCUser element) {
		return new File(PyrCore.inst().getUserDataRootFolder() + "/" + element.getDataId() + "/customcommands_user.json");
	}

	@Override
	protected final void jsonPull() {
		throw new UnsupportedOperationException();// can't pull the whole user board
	}

	@Override
	protected final void jsonDelete() {
		File root = PyrCore.inst().getUserDataRootFolder();
		if (root.exists() && root.isDirectory()) {
			for (File userRoot : root.listFiles()) {
				if (userRoot.exists() && userRoot.isDirectory()) {
					File user = new File(userRoot.getPath() + "/customcommands_user.json");
					if (user.exists()) {
						user.delete();
					}
				}
			}
		}
	}

	// MySQL
	@Override
	protected final String getMySQLTable() {
		return "customcommands_users";
	}

	@Override
	protected final Query getMySQLInitQuery() {
		return new Query("CREATE TABLE IF NOT EXISTS `" + getMySQLTable() + "`("
				+ "id VARCHAR(100) NOT NULL,"
				+ "cooldown_ends LONGTEXT NOT NULL,"
				+ "toggles LONGTEXT NOT NULL,"
				+ "PRIMARY KEY(id)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=?;", "utf8");
	}

	@Override
	protected final void mysqlPull() throws SQLException {
		throw new UnsupportedOperationException();// can't pull the whole user board
	}

	@Override
	protected final void mysqlDelete() {
		getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "`;"));
	}

}
