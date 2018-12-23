package be.pyrrh4.customcommands.commands;

import be.pyrrh4.pyrcore.lib.messenger.Text;

public class CommandPatternResult implements Comparable<CommandPatternResult> {

	// fields and constructors
	private Result result;
	private int wildcards, argsSize = 0;
	private String errorArg;
	private Text errorText;

	public CommandPatternResult(Result result) {
		this(result, -1, null, null);
	}

	public CommandPatternResult(Result result, int wildcards) {
		this(result, wildcards, null, null);
	}

	public CommandPatternResult(Result result, String errorArg, Text errorText) {
		this(result, 0, errorArg, errorText);
	}

	public CommandPatternResult(Result result, int wildcards, String errorArg, Text errorText) {
		this.result = result;
		this.wildcards = wildcards;
		this.errorArg = errorArg;
		this.errorText = errorText;
	}

	// getters
	public Result getResult() {
		return result;
	}

	public int getArgsSize() {
		return argsSize;
	}

	public void setArgsSize(int argsSize) {
		this.argsSize = argsSize;
	}

	public int getWildcards() {
		return wildcards;
	}

	public String getErrorArg() {
		return errorArg;
	}

	public Text getErrorText() {
		return errorText;
	}

	// methods
	@Override
	public int compareTo(CommandPatternResult o) {
		int c = -Integer.compare(argsSize, o.getArgsSize());
		if (c == 0) c = Integer.compare(wildcards, o.getWildcards());
		return c;
	}

	// result type
	public static enum Result {
		MATCH(true, false),
		ERROR(false, true),
		NONE(false, false);

		private boolean allowWildcards, allowError;

		private Result(boolean allowWildcards, boolean allowError) {
			this.allowWildcards = allowWildcards;
			this.allowError = allowError;
		}

		public boolean isAllowWildcards() {
			return allowWildcards;
		}

		public boolean isAllowError() {
			return allowError;
		}
	}

}
