package be.pyrrh4.customcommands.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import be.pyrrh4.customcommands.CCLocale;
import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.customcommands.commands.CommandPatternResult.Result;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.util.Pair;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.collection.SortedMap;
import be.pyrrh4.pyrcore.lib.util.collection.SortedMap.Order;
import be.pyrrh4.pyrcore.lib.util.collection.SortedMap.Type;

public class Command {

	// fields and constructor
	private PyrPlugin plugin;
	private String commandName, commandHelpName;
	private List<CommandPattern> patterns = new ArrayList<CommandPattern>();

	public Command(PyrPlugin plugin, String commandName, String commandHelpName, CommandPattern... patterns) throws DuplicatePatternError {
		this.plugin = plugin;
		this.commandName = commandName;
		this.commandHelpName = commandHelpName;
		for (CommandPattern pattern : patterns) {
			addPattern(pattern);
		}
	}

	// getters
	public PyrPlugin getPlugin() {
		return plugin;
	}

	public String getCommandName() {
		return commandName;
	}

	public String getCommandHelpName() {
		return commandHelpName;
	}

	// methods
	public void addPattern(CommandPattern pattern) throws DuplicatePatternError {
		if (pattern == null) return;
		if (patterns.contains(pattern)) throw new DuplicatePatternError("trying to add existing pattern '" + pattern.getUsage() + "' to command " + commandName);
		patterns.add(pattern);
	}

	public void showHelp(CommandSender sender) {
		boolean empty = true;
		for (CommandPattern pattern : patterns) {
			boolean show = pattern.showHelp(commandHelpName, sender);
			if (empty && show) empty = false;
		}
		if (empty) PCLocale.MSG_GENERIC_NOTHING.send(sender, "{plugin}", CustomCommands.inst().getName());
	}

	public void call(CommandCall call) {
		// has args
		SortedMap<CommandPattern, CommandPatternResult> match = new SortedMap<CommandPattern, CommandPatternResult>(Type.VALUE_SORTED, Order.NATURAL);
		Pair<CommandPattern, CommandPatternResult> error = null;
		// calling all patterns
		for (CommandPattern pattern : patterns) {
			CommandPatternResult result = pattern.call(call);
			result.setArgsSize(pattern.getArguments().size());
			if (result.getResult().equals(Result.MATCH)) {
				match.put(pattern, result);
			} else if (result.getResult().equals(Result.ERROR)) {
				if (error == null) {
					error = Pair.create(pattern, result);
				}
			}
		}
		// has matching patterns
		if (!match.isEmpty()) {
			match.getKeyAt(0).perform(call);
			return;
		}
		// has error patterns
		if (error != null) {
			CommandPatternResult result = error.getB();
			result.getErrorText().send(call.getSender(), "{plugin}", CustomCommands.inst().getName(), "{error}", result.getErrorArg() == null ? "?" : result.getErrorArg());
			return;
		}
		// if sender entered no arguments and we're at this point, just show help
		if (call.getArgsSize() == 0) {
			showHelp(call.getSender());
			return;
		}
		// send error message
		CCLocale.MSG_CUSTOMCOMMANDS_UNKNOWNCOMMAND.send(call.getSender());
		// find closest command
		CommandPattern closest = null;
		int closestSimilarity = -1;
		List<String> currentSplit = Utils.split(" ", call.getAllArguments(), false);
		for (CommandPattern commandPattern : patterns) {
			for (String pattern : commandPattern.getFullPatterns()) {
				// fill arguments
				String current = "";
				List<String> patternSplit = Utils.split(" ", pattern, false);
				for (int i = 0; i < patternSplit.size(); i++) {
					// has typed that much
					if (i < currentSplit.size()) {
						// is pattern replacable
						if (patternSplit.get(i).contains("[")) {
							current += currentSplit.get(i) + " ";
						} else {
							current += patternSplit.get(0) + " ";
						}
					}
				}
				// not compared yet
				if (closestSimilarity == -1) {
					closestSimilarity = Utils.getLevenshteinSimilarity(current, call.getAllArguments());
					closest = commandPattern;
				}
				// compare
				else {
					int newSimilarity = Utils.getLevenshteinSimilarity(current, call.getAllArguments());
					if (newSimilarity < closestSimilarity) {
						closestSimilarity = newSimilarity;
						closest = commandPattern;
					}
				}
			}
		}
		// send suggestion if found
		if (closest != null) {
			CCLocale.MSG_CUSTOMCOMMANDS_CLOSESTCOMMAND.send(call.getSender(), "{command}", "/" + commandHelpName + " " + closest.getUsage());
		}
	}

}
