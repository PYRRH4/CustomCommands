package be.pyrrh4.customcommands.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.commands.ArgumentModel.ArgumentModelPhrase;
import be.pyrrh4.customcommands.commands.CommandPatternResult.Result;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.Perm;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.messenger.Text;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandPattern {

	// fields and constructor
	private List<Argument> arguments = new ArrayList<Argument>();
	private List<String> fullPatterns = new ArrayList<String>();
	private String description;
	private Perm permission;
	private boolean playerOnly;

	/**
	 * Creates a new command arguments pattern.
	 * <br>Separate aliases with | like <pre>alias1|alias2</pre>
	 * Set arguments optional by adding a default values with $ like <pre>[string]$default <b>or</b> [integer]$15</pre>
	 * Add description to model arguments with % like <pre>[integer]%amount_to_charge <b>or</b> [world]%world_name <b>(_ will be replaced by a space)</b></pre>
	 * @param rawPattern the raw pattern
	 */
	public CommandPattern(String rawPattern, String description, String permission, boolean playerOnly) throws InvalidPatternError {
		this.description = description;
		this.permission = new Perm(permission);
		this.playerOnly = playerOnly;
		// empty arguments
		// load
		boolean hasOptionalArguments = false, hasPhraseArgument = false;
		int fullPatternsCount = 0;
		try {
			for (String raw : Utils.split(" ", rawPattern, false, false)) {
				// trying to add argument but already has phrase argument before
				if (hasPhraseArgument) {
					throw new InvalidPatternError("trying to add argument " + raw + " after a phrase argument model");
				}
				// desc
				String desc;
				if (raw.contains("%")) {
					String[] split = raw.split("%");
					raw = split[0];
					desc = split[1].replace("_", " ");
				} else {
					desc = raw.replace("_", " ").replace("[", "").replace("]", "");
				}
				// default value
				String def = null;
				if (raw.contains("$")) {
					// set default value
					String[] split = raw.split("\\$");
					raw = split[0];
					def = split[1];
					hasOptionalArguments = true;
				}
				// trying to add required argument but already has optional arguments before
				else if (hasOptionalArguments) {
					throw new InvalidPatternError("trying to add optional argument " + raw + " after required arguments");
				}
				// model argument
				if (raw.startsWith("[")) {
					// model
					ArgumentModel argModel = ArgumentModel.decode(raw, def, desc);
					if (argModel == null) {
						throw new InvalidPatternError("could not decode argument model " + raw);
					} else if (argModel instanceof ArgumentModelPhrase) {
						hasPhraseArgument = true;
					}
					// add
					arguments.add(argModel);
				}
				// string argument
				else {
					ArgumentString argString = new ArgumentString(Utils.split("\\|", raw, false), def);
					// fullPatterns count
					if (argString.getAliases().size() > fullPatternsCount) {
						fullPatternsCount = argString.getAliases().size();
					}
					// add
					arguments.add(argString);
				}
			}
		} catch (Throwable throwable) {
			throw new InvalidPatternError("could not decode argument pattern " + rawPattern, throwable);
		}
		// load full patterns
		String[] fullPatterns = new String[fullPatternsCount];
		for (int j = 0; j < fullPatternsCount; j++) {
			fullPatterns[j] = "";
		}
		for (int i = 0; i < arguments.size(); i++) {
			Argument arg = arguments.get(i);
			List<String> aliases = Utils.emptyList();
			if (arg instanceof ArgumentModel) aliases.add(((ArgumentModel) arg).getModel());
			else if (arg instanceof ArgumentString) aliases.addAll(((ArgumentString) arg).getAliases());
			for (int j = 0; j < fullPatternsCount; j++) {
				fullPatterns[j] += (j < aliases.size() ? aliases.get(j) : aliases.get(0)) + " ";
			}
		}
		this.fullPatterns = Utils.asList(fullPatterns);
	}

	// getters
	public List<Argument> getArguments() {
		return arguments;
	}

	public List<String> getFullPatterns() {
		return fullPatterns;
	}

	public String getUsage() {
		if (!arguments.isEmpty()) {
			String usage = "";
			for (Argument arg : arguments) {
				String prefix = "", suffix = "";
				if (arg instanceof ArgumentModel) {
					prefix = arg.getDefaultValue() == null ? "[" : "(";
					suffix = arg.getDefaultValue() == null ? "]" : ")";
				}
				usage += " " + prefix + arg.getDescription() + suffix;
			}
			return usage.substring(1);
		}
		return "{no arguments}";
	}

	// methods
	public CommandPatternResult call(CommandCall call) {
		// no permission
		if (permission != null && !call.senderHasPermission(permission)) {
			return new CommandPatternResult(Result.ERROR, null, PCLocale.MSG_GENERIC_NOPERMISSION);
		}
		// not a player
		if (playerOnly && !call.senderIsPlayer()) {
			return new CommandPatternResult(Result.ERROR, null, PCLocale.MSG_GENERIC_NOTPLAYER);
		}
		// result
		Result callResult = Result.NONE;
		int callWildcards = 0;
		String callErrorArg = null;
		Text callErrorText = null;
		// empty args
		if (arguments.isEmpty()) {
			if (call.getArgsSize() == 0) {
				callResult = Result.MATCH;
			}
		}
		// check arguments
		else {
			for (int argIndex = 0; argIndex < arguments.size(); argIndex++) {
				Argument arg = arguments.get(argIndex);
				String callArg = call.getArgAsString(this, argIndex);
				// sender didn't enter this required argument
				if (callArg == null) {
					callResult = Result.NONE;
					break;
				}
				// argument model
				if (arg instanceof ArgumentModel) {
					ArgumentModel argModel = (ArgumentModel) arg;
					CommandPatternResult result = argModel.call(call.getSender(), callArg);
					// error
					if (result.getResult().equals(Result.ERROR)) {
						callResult = Result.ERROR;
						callErrorArg = result.getErrorArg();
						callErrorText = result.getErrorText();
						break;
					}
					// match, so continue
					else if (result.getResult().equals(Result.MATCH)) {
						callResult = Result.MATCH;
						callWildcards += result.getWildcards();
						continue;
					}
				}
				// argument string
				else if (arg instanceof ArgumentString) {
					ArgumentString argString = (ArgumentString) arg;
					// error
					if (!argString.getAliases().contains(call.getArgAsString(this, argIndex))) {
						callResult = Result.NONE;
						break;
					}
					// match, so continue
					else {
						callResult = Result.MATCH;
						continue;
					}
				}
				// unknown arg type
				else {
					callResult = Result.NONE;
					break;
				}
			}
		}
		// unknown
		return new CommandPatternResult(callResult, callResult.isAllowWildcards() ? callWildcards : 0, callResult.isAllowError() ? callErrorArg : null, callResult.isAllowError() ? callErrorText : null);
	}

	/**
	 * @return true if help was showed
	 */
	public boolean showHelp(String commandHelpName, CommandSender sender) {
		if (description == null) return false;
		if (permission != null && !permission.has(sender)) return false;
		if (playerOnly && !Utils.instanceOf(sender, Player.class)) return false;
		String usage = getUsage();
		Messenger.send(sender, Messenger.Level.NORMAL_INFO, "", "</" + commandHelpName + (usage.isEmpty() ? "" : " " + usage) + "> - " + description);
		return true;
	}

	public void perform(CommandCall call) {
		showHelp(call.getCommandName(), call.getSender());
	}

	@Override
	public boolean equals(Object obj) {
		if (!Utils.instanceOf(obj, CommandPattern.class)) return false;
		CommandPattern other = (CommandPattern) obj;
		return this.arguments.equals(other.arguments) && Utils.equals(this.description, other.description) && Utils.equals(this.permission, other.permission) && this.playerOnly == other.playerOnly;
	}

}
