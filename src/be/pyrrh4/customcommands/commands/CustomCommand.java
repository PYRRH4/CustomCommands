package be.pyrrh4.customcommands.commands;

import java.util.List;

import be.pyrrh4.customcommands.CustomCommands;

public class CustomCommand extends Command {

	// field and constructor
	private String id;
	private List<String> aliases;

	public CustomCommand(String id, List<String> aliases) {
		super(CustomCommands.inst(), aliases.get(0), aliases.get(0));
		this.id = id;
		this.aliases = aliases;
	}

	// getters
	public String getId() {
		return id;
	}

	public List<String> getAliases() {
		return aliases;
	}

}
