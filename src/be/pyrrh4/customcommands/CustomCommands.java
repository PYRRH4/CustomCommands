package be.pyrrh4.customcommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import be.pyrrh4.customcommands.commands.CustomCommand;
import be.pyrrh4.customcommands.commands.CustomPattern;
import be.pyrrh4.customcommands.commands.InvalidPatternError;
import be.pyrrh4.customcommands.commands.action.Action;
import be.pyrrh4.customcommands.commands.action.Action.Type;
import be.pyrrh4.customcommands.commands.action.ActionData;
import be.pyrrh4.customcommands.data.CCDataManager;
import be.pyrrh4.pyrcore.lib.Logger;
import be.pyrrh4.pyrcore.lib.Logger.Level;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.command.CommandRoot;
import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.data.DataManager.BackEnd;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.util.Pair;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CustomCommands extends PyrPlugin implements Listener {

	// ------------------------------------------------------------
	// Instance and constructor
	// ------------------------------------------------------------

	private static CustomCommands instance;

	public CustomCommands() {
		instance = this;
	}

	public static CustomCommands inst() {
		return instance;
	}

	// ------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------

	private List<CustomCommand> commands = new ArrayList<CustomCommand>();

	// ------------------------------------------------------------
	// Data and configuration
	// ------------------------------------------------------------

	private CCDataManager dataManager = null;
	private YMLConfiguration configuration = null;

	@Override
	public YMLConfiguration getConfiguration() {
		return configuration;
	}

	public CCDataManager getData() {
		return dataManager;
	}

	@Override
	protected void unregisterData() {
		dataManager.disable();
	}

	@Override
	public void resetData() {
		dataManager.reset();
	}

	// ------------------------------------------------------------
	// Activation
	// ------------------------------------------------------------

	@Override
	protected boolean preEnable() {
		// spigot resource id
		this.spigotResourceId = 14363;
		// success
		return true;
	}

	@Override
	protected boolean innerReload() {
		// configuration
		this.configuration = new YMLConfiguration(this, new File(getDataFolder() + "/config.yml"), "config.yml", false, true);

		// load locale file
		reloadLocale(CCLocale.file);

		// load custom commands
		commands.clear();
		for (String commandKey : getConfiguration().getKeysForSection("commands", false)) {
			try {
				// command aliases
				List<String> aliases = new ArrayList<String>();
				for (String alias : getConfiguration().getList("commands." + commandKey + ".aliases", Utils.emptyList())) {
					aliases.add(alias.toLowerCase());
				}

				// load arguments patterns
				List<CustomPattern> arguments = new ArrayList<CustomPattern>();
				for (String patternKey : getConfiguration().getKeysForSection("commands." + commandKey + ".arguments", false)) {
					String path = "commands." + commandKey + ".arguments." + patternKey;

					// settings
					String rawPattern = getConfiguration().contains(path + ".usage") ? getConfiguration().getString(path + ".usage", null) : getConfiguration().getString(path + ".pattern", null);
					String description = getConfiguration().getString(path + ".description", "{no_description}");
					String permission = getConfiguration().getString(path + ".permission", null);
					int cooldown = getConfiguration().getInt(path + ".cooldown", 0);
					boolean toggle = getConfiguration().getBoolean(path + ".toggle", false);
					List<String> argWorlds = getConfiguration().getList(path + ".worlds", Utils.emptyList());

					// perform
					List<ActionData> performToggleFalse = new ArrayList<ActionData>(), performToggleTrue = new ArrayList<ActionData>();

					if (getConfiguration().contains(path + ".perform_toggle_false")) {
						for (String actionPath : getConfiguration().getKeysForSection(path + ".perform_toggle_false", false)) {
							performToggleFalse.add(new ActionData(getConfiguration(), path + ".perform_toggle_false." + actionPath));
						}
					} else if (getConfiguration().contains(path + ".perform")) {
						for (String actionPath : getConfiguration().getKeysForSection(path + ".perform", false)) {
							performToggleFalse.add(new ActionData(getConfiguration(), path + ".perform." + actionPath));
						}
					}

					if (getConfiguration().contains(path + ".perform_toggle_true")) {
						for (String actionPath : getConfiguration().getKeysForSection(path + ".perform_toggle_true", false)) {
							performToggleTrue.add(new ActionData(getConfiguration(), path + ".perform_toggle_true." + actionPath));
						}
					}

					// register
					String id = commandKey + "." + patternKey;
					try {
						arguments.add(new CustomPattern(id, cooldown, toggle, performToggleFalse, performToggleTrue, argWorlds, rawPattern, description, permission));
					} catch (InvalidPatternError error) {
						Logger.log(Level.WARNING, this, "Could not load custom command pattern " + id + " : " + error.getMessage());
					}
				}

				// no aliases found
				if (aliases.isEmpty()) {
					Logger.log(Level.WARNING, this, "Could not load custom command " + commandKey + " because it has no aliases");
				}
				// no arguments found
				else if (arguments.isEmpty()) {
					Logger.log(Level.WARNING, this, "Could not load custom command " + commandKey + " because it has no arguments");
				}
				// has arguments
				else {
					// register
					CustomCommand command = new CustomCommand(commandKey, aliases);
					for (CustomPattern pattern : arguments) {
						command.addPattern(pattern);
					}
					// add
					this.commands.add(command);
					Logger.log(Level.SUCCESS, this, "Successfully loaded custom command " + commandKey);
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				error("Could not load custom command " + commandKey);

			}
		}

		// data manager
		if (dataManager == null) {
			BackEnd backend = getConfiguration().getEnumValue("data.backend", BackEnd.class, BackEnd.JSON);
			if (backend == null) {
				backend = BackEnd.JSON;
			}
			this.dataManager = new CCDataManager(backend);
			dataManager.enable();
		} else {
			dataManager.synchronize();
		}

		// success
		return true;
	}

	@Override
	protected boolean enable() {
		// call reload
		innerReload();

		// registration
		Bukkit.getPluginManager().registerEvents(this, this);

		// command
		CommandRoot root = new CommandRoot(this, Utils.asList("customcommands", "ccmd"), null, null, false);
		root.addChild(new CommandSaveItem());
		root.addChild(new CommandSaveLocation());
		registerCommand(root, CCPerm.CUSTOMCOMMANDS_ADMIN);

		// return
		return true;
	}

	// ------------------------------------------------------------
	// DISABLE
	// ------------------------------------------------------------

	@Override
	protected void disable() {
	}

	// ------------------------------------------------------------
	// Events
	// ------------------------------------------------------------

	private long last = 0L;
	
	@EventHandler
	public void event(PlayerCommandPreprocessEvent event) {
		if (System.currentTimeMillis() - last < 5L) return;
		last = System.currentTimeMillis();
		Player sender = event.getPlayer();
		Pair<String, String[]> separate = Utils.separateRoot(event.getMessage().substring(1), false);
		String root = separate.getA();
		String[] args = separate.getB();

		for (CustomCommand command : commands) {
			for (String alias : command.getAliases()) {
				if (alias.equalsIgnoreCase(root)) {
					try {
						event.setCancelled(true);
						command.call(new be.pyrrh4.customcommands.commands.CommandCall(event.getPlayer(), alias, args));
						return;
					} catch (Throwable exception) {
						exception.printStackTrace();
						Logger.log(Level.SEVERE, this, "An error occured processing command " + command.getId() + " (alias '" + alias + "')");
						Messenger.send(sender, Messenger.Level.SEVERE_ERROR, "CustomCommands", "An error occured processing command " + command.getId() + " (alias '" + alias + "') : " + exception.getMessage());
					}
					return;
				}
			}
		}
	}

	// ------------------------------------------------------------
	// Static methods
	// ------------------------------------------------------------

	public String replaceString(String string, Player sender, String[] args) {
		// with specifiers
		for (int i = 0; i < args.length; i++) {
			int cci = i + 1;
			if (string.contains("{arg:" + cci + "}")) string = string.replace("{arg:" + cci + "}", args[i].replace(" ", ""));
			if (string.contains("{args:" + cci + "}")) string = string.replace("{args:" + cci + "}", getFullArgsFrom(args, i));
		}
		// full args
		if (string.contains("{args}")) string = string.replace("{args}", getFullArgsFrom(args, 0));
		if (string.contains("{player}")) string = string.replace("{player}", sender.getName());
		if (string.contains("{playerworld}")) string = string.replace("{playerworld}", sender.getWorld().getName());
		return string;
	}

	private String getFullArgsFrom(String[] args, int i) {
		if (i < 0) return "{invalid_args}";
		String result = "";
		for (int j = i; j < args.length; j++) result += " " + args[j];
		if (result.length() > 0) result = result.substring(1);
		return result;
	}

	public Action createAction(String argumentsId, String type, Player sender, List<String> data, String[] args) {
		// unknown action type
		Type actionType = Type.fromType(type);
		if (actionType == null) {
			error("Unknown action type " + type + " in argument " + argumentsId + ", skipping");
			return null;
		}
		// couldn't create
		Action action = actionType.create(sender, data, args);
		if (action == null) {
			error("Could not create action of type " + actionType + " in argument " + argumentsId + ", skipping");
			return null;
		}
		// success
		return action;
	}

}
