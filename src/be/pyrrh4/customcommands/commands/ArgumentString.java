package be.pyrrh4.customcommands.commands;

import java.util.List;

import be.pyrrh4.pyrcore.lib.util.Utils;

public class ArgumentString extends Argument {

	// fields and constructor
	private List<String> aliases = Utils.emptyList();

	public ArgumentString(List<String> aliases, String def) {
		super(def, aliases.get(0).toLowerCase());
		for (String alias : aliases) {
			this.aliases.add(alias.toLowerCase());
		}
	}

	// getters
	/**
	 * @return the aliases (lower-case)
	 */
	public List<String> getAliases() {
		return aliases;
	}

	// methods
	@Override
	public boolean equals(Object obj) {
		if (!Utils.instanceOf(obj, ArgumentString.class)) return false;
		if (!super.equals(obj)) return false;
		ArgumentString other = (ArgumentString) obj;
		return this.aliases.equals(other.aliases);
	}

}
