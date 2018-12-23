package be.pyrrh4.customcommands.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.Perm;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandCall {

	// fields and constructor
	private String commandName;
	private CommandSender sender;
	private String[] arguments;

	public CommandCall(CommandSender sender, String commandName, String[] arguments) {
		this.sender = sender;
		this.commandName = commandName;
		this.arguments = arguments;
	}

	// getters
	public CommandSender getSender() {
		return sender;
	}

	public Player getSenderAsPlayer() {
		return (Player) sender;
	}

	public boolean senderIsPlayer() {
		return sender instanceof Player;
	}

	public boolean senderHasPermission(Perm permission) {
		return permission == null ? true : permission.has(sender);
	}

	public String getCommandName() {
		return commandName;
	}

	public String getAllArguments() {
		return getAllArguments(0);
	}

	public String getAllArguments(int beginIndex) {
		String result = "";
		for (int i = beginIndex; i < arguments.length; i++) {
			result += " " + arguments[i];
		}
		if (result.length() > 0) {
			result = result.substring(1);
		}
		return result;
	}

	public String[] getArgs() {
		return arguments;
	}

	public int getArgsSize() {
		return arguments.length;
	}

	public String getArgAsString(CommandPattern pattern, int index) {
		return index >= arguments.length ? pattern.getArguments().get(index).getDefaultValue() : arguments[index];
	}

	public int getArgAsInt(CommandPattern pattern, int index) {
		return Integer.parseInt(index >= arguments.length ? pattern.getArguments().get(index).getDefaultValue() : arguments[index]);
	}

	public double getArgAsDouble(CommandPattern pattern, int index) {
		return Double.parseDouble(index >= arguments.length ? pattern.getArguments().get(index).getDefaultValue() : arguments[index]);
	}

	public OfflinePlayer getArgAsOfflinePlayer(CommandPattern pattern, int index) {
		String name = index >= arguments.length ? pattern.getArguments().get(index).getDefaultValue() : arguments[index];
		return Utils.equals(name, "null") ? (senderIsPlayer() ? getSenderAsPlayer() : null) : Utils.getOfflinePlayer(name);
	}

	public Player getArgAsPlayer(CommandPattern pattern, int index) {
		String name = index >= arguments.length ? pattern.getArguments().get(index).getDefaultValue() : arguments[index];
		return Utils.equals(name, "null") ? (senderIsPlayer() ? getSenderAsPlayer() : null) : Utils.getPlayer(name);
	}

}
