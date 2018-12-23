package be.pyrrh4.customcommands.commands;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CCLocale;
import be.pyrrh4.customcommands.commands.CommandPatternResult.Result;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class ArgumentModel extends Argument {

	// fields and constructor
	private String model;

	public ArgumentModel(String model, String def, String description) {
		super(def, description);
		this.model = model;
	}

	// getters
	public String getModel() {
		return model;
	}

	// methods
	@Override
	public boolean equals(Object obj) {
		if (!Utils.instanceOf(obj, getClass())) return false;
		if (!super.equals(obj)) return false;
		ArgumentModel other = (ArgumentModel) obj;
		return this.model.equals(other.model);
	}

	// abstract methods
	/**
	 * @param the raw argument that the sender entered
	 * @return the pattern result, including 
	 */
	protected abstract CommandPatternResult call(CommandSender sender, String raw);

	// static methods
	/**
	 * default models : [string], [string:minlength:maxlength], [integer], [double], [player], [player-online], [phrase] or [infinite]
	 * @param model the raw string model to decode
	 * @param def the default value to initialize model (if found one) with
	 * @return the model if the given arg model is valid, null otherwise
	 */
	public static ArgumentModel decode(String model, String def, String description) {
		model = model.toLowerCase();
		// default models
		if (model.startsWith("[string]")) {
			return new ArgumentModelString("[string]", def, description);
		} else if (model.startsWith("[string:")) {
			List<String> split = Utils.split("\\:", model, false);
			if (split.size() == 3 && Utils.isInteger(split.get(1)) && Utils.isInteger(split.get(2).replace("]", ""))) {
				int min = Integer.parseInt(split.get(1)), max = Integer.parseInt(split.get(2).replace("]", ""));
				return new ArgumentModelStringSized("[string:" + min + ":" + max + "]", min, max, def, description);
			}
			return null;
		} else if (model.startsWith("[integer]")) {
			return new ArgumentModelInteger("[integer]", def, description);
		} else if (model.startsWith("[double]")) {
			return new ArgumentModelDouble("[double]", def, description);
		} else if (model.startsWith("[player]")) {
			return new ArgumentModelPlayer("[player]", def, description);
		} else if (model.startsWith("[player-online]")) {
			return new ArgumentModelPlayerOnline("[player-online]", def, description);
		} else if (model.startsWith("[phrase]") || model.startsWith("[infinite]")) {
			return new ArgumentModelPhrase("[phrase]", def, description);
		}
		// unknown
		return null;
	}

	// models
	public static class ArgumentModelString extends ArgumentModel {
		public ArgumentModelString(String model, String def, String description) {
			super(model, def, description);
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			return new CommandPatternResult(Result.MATCH, 1);
		}
	}

	public static class ArgumentModelStringSized extends ArgumentModel {
		private int min, max;
		public ArgumentModelStringSized(String model, int min, int max, String def, String description) {
			super(model, def, description);
			this.min = min;
			this.max = max;
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			return raw.length() >= min && raw.length() <= max ? new CommandPatternResult(Result.MATCH) : new CommandPatternResult(Result.ERROR, raw, CCLocale.MSG_CUSTOMCOMMANDS_INVALIDARGUMENTSSIZE);
		}
	}

	public static class ArgumentModelInteger extends ArgumentModel {
		public ArgumentModelInteger(String model, String def, String description) {
			super(model, def, description);
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			return Utils.isInteger(raw) ? new CommandPatternResult(Result.MATCH) : new CommandPatternResult(Result.ERROR, raw, PCLocale.MSG_GENERIC_INVALIDINT);
		}
	}

	public static class ArgumentModelDouble extends ArgumentModel {
		public ArgumentModelDouble(String model, String def, String description) {
			super(model, def, description);
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			return Utils.isDouble(raw) ? new CommandPatternResult(Result.MATCH) : new CommandPatternResult(Result.ERROR, raw, PCLocale.MSG_GENERIC_INVALIDDOUBLE);
		}
	}

	public static class ArgumentModelPlayer extends ArgumentModel {
		public ArgumentModelPlayer(String model, String def, String description) {
			super(model, def, description);
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			OfflinePlayer player = raw.equals("null") ? (Utils.instanceOf(sender, OfflinePlayer.class) ? (OfflinePlayer) sender : null) : Utils.getOfflinePlayer(raw);
			return player != null ? new CommandPatternResult(Result.MATCH) : new CommandPatternResult(Result.ERROR, raw, PCLocale.MSG_GENERIC_INVALIDPLAYEROFFLINE);
		}
	}

	public static class ArgumentModelPlayerOnline extends ArgumentModel {
		public ArgumentModelPlayerOnline(String model, String def, String description) {
			super(model, def, description);
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			Player player = raw.equals("null") ? (Utils.instanceOf(sender, Player.class) ? (Player) sender : null) : Utils.getPlayer(raw);
			return player != null ? new CommandPatternResult(Result.MATCH) : new CommandPatternResult(Result.ERROR, raw, PCLocale.MSG_GENERIC_INVALIDPLAYER);
		}
	}

	private static final int modelPhraseWildcards = 1000;

	public static class ArgumentModelPhrase extends ArgumentModel {
		public ArgumentModelPhrase(String model, String def, String description) {
			super(model, def, description);
		}
		@Override
		protected CommandPatternResult call(CommandSender sender, String raw) {
			return new CommandPatternResult(Result.MATCH, modelPhraseWildcards);
		}
	}

}
