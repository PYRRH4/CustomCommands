package be.pyrrh4.customcommands.commands;

public class InvalidPatternError extends Error {

	public InvalidPatternError(String message) {
		super(message);
	}

	public InvalidPatternError(String message, Throwable cause) {
		super(message + (cause.getMessage() == null ? "" : " : " + cause.getMessage()), cause);
	}

}
