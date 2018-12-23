package be.pyrrh4.customcommands.commands;

import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class Argument {

	// fields and constructor
	private String def, description;

	/**
	 * @param def the default value ; if null, the param will be required
	 */
	public Argument(String def, String description) {
		this.def = def;
		this.description = description;
	}

	// getters
	public String getDefaultValue() {
		return def;
	}

	public String getDescription() {
		return description;
	}

	// methods
	@Override
	public boolean equals(Object obj) {
		if (!Utils.instanceOf(obj, Argument.class)) return false;
		Argument other = (Argument) obj;
		return Utils.equals(this.def, other.def) && Utils.equals(this.description, other.description);
	}

}
