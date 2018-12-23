package be.pyrrh4.customcommands.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.customcommands.commands.CustomPattern;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.data.PCUser;
import be.pyrrh4.pyrcore.lib.data.DataElement;
import be.pyrrh4.pyrcore.lib.data.mysql.Query;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CCUser extends DataElement {

	// fields and constructor
	private PCUser user;
	private Map<String, Long> cooldownEnds = new HashMap<String, Long>();
	private Map<String, Boolean> toggles = new HashMap<String, Boolean>();

	public CCUser(PCUser user) {
		this.user = user;
	}

	// getters
	public PCUser getUser() {
		return user;
	}

	public long getCooldownEnd(CustomPattern pattern) {
		return cooldownEnds.containsKey(pattern.getArgumentsId()) ? cooldownEnds.get(pattern.getArgumentsId()) : 0L;
	}

	public boolean isToggled(CustomPattern pattern) {
		return toggles.containsKey(pattern.getArgumentsId()) ? toggles.get(pattern.getArgumentsId()) : false;
	}

	// methods
	public void setCooldownEnd(CustomPattern pattern, long cooldownEnd) {
		cooldownEnds.put(pattern.getArgumentsId(), cooldownEnd);
	}

	public boolean hasCooldown(CustomPattern pattern) {
		long end = getCooldownEnd(pattern);
		if (end > 0L) {
			if (System.currentTimeMillis() > end) {
				cooldownEnds.remove(pattern.getArgumentsId());
				// save
				pushAsync();
				return false;
			}
			return true;
		}
		return false;
	}

	public void toggle(CustomPattern arg) {
		boolean newToggle = !isToggled(arg);
		toggles.put(arg.getArgumentsId(), newToggle);
		// save
		pushAsync();
	}

	// data
	private static class JsonData {
		private final Map<String, Long> cooldownEnds;
		private final Map<String, Boolean> toggles;
		private JsonData(CCUser user) {
			this.cooldownEnds = user.cooldownEnds;
			this.toggles = user.toggles;
		}
	}

	@Override
	protected final UserBoard getBoard() {
		return CustomCommands.inst().getData().getUsers();
	}

	@Override
	protected final String getDataId() {
		return user.toString();
	}

	@Override
	protected final void jsonPull() {
		File file = getBoard().getJsonFile(this);
		JsonData data = Utils.loadFromGson(JsonData.class, file, true);
		if (data != null) {
			// reset cache
			cooldownEnds.clear();
			toggles.clear();
			// replace
			cooldownEnds.putAll(data.cooldownEnds);
			toggles.putAll(data.toggles);
		}
	}

	@Override
	protected final void jsonPush() {
		File file = getBoard().getJsonFile(this);
		Utils.saveToGson(new JsonData(this), file);
	}

	@Override
	protected final void jsonDelete() {
		File file = getBoard().getJsonFile(this);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	protected final void mysqlPull(ResultSet set) throws SQLException {
		// reset cache
		cooldownEnds.clear();
		toggles.clear();
		// replace
		Map<String, String> cooldownEnds = PyrCore.UNPRETTY_GSON.fromJson(set.getString("cooldown_ends"), new HashMap<String, String>().getClass());
		for (String id : cooldownEnds.keySet()) {
			this.cooldownEnds.put(id, Long.parseLong(cooldownEnds.get(id)));
		}
		Map<String, String> toggles = PyrCore.UNPRETTY_GSON.fromJson(set.getString("toggles"), new HashMap<String, String>().getClass());
		for (String id : toggles.keySet()) {
			this.toggles.put(id, Boolean.parseBoolean(toggles.get(id)));
		}
	}

	@Override
	protected Query getMySQLPullQuery() {
		return new Query("SELECT * FROM `" + getBoard().getMySQLTable() + "` WHERE `id`=?;", getDataId());
	}

	@Override
	protected final Query getMySQLPushQuery() {
		Map<String, String> copy = new HashMap<String, String>();
		for (String id : this.cooldownEnds.keySet()) {
			copy.put(id, String.valueOf(this.cooldownEnds.get(id)));
		}
		String cooldownEnds = PyrCore.UNPRETTY_GSON.toJson(copy);
		copy.clear();
		for (String id : this.toggles.keySet()) {
			copy.put(id, String.valueOf(this.toggles.get(id)));
		}
		String toggles = PyrCore.UNPRETTY_GSON.toJson(copy);
		return new Query("REPLACE INTO `" + getBoard().getMySQLTable() + "`(`id`,`cooldown_ends`,`toggles`) VALUES(?,?,?);", getDataId(), cooldownEnds, toggles);
	}

	@Override
	protected final Query getMySQLDeleteQuery() {
		return new Query("DELETE FROM `" + getBoard().getMySQLTable() + "` WHERE `id`=?;", getDataId());
	}

}
