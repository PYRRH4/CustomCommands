package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.entity.Player;

public interface Action
{
	// ------------------------------------------------------------
	// Type
	// ------------------------------------------------------------

	public static enum Type
	{
		SEND_MESSAGE("send message", ActionMessage.class),
		WAIT_TICKS("wait ticks", ActionWait.class),
		EXECUTE_COMMANDS_FOR("execute commands for", ActionCommandsFor.class),
		EXECUTE_COMMANDS_AS("execute commands as", ActionCommandsAs.class),
		SEND_TITLE("send title", ActionTitle.class),
		SEND_ACTIONBAR("send actionbar", ActionActionbar.class),
		GIVE_ITEM("give item", ActionItem.class),
		TELEPORT("teleport", ActionTeleport.class),
		CHANGE_GAMEMODE("change gamemode", ActionChangeGamemode.class),
		CHANGE_TAB("change tab", ActionTab.class);

		private String type;
		private Class<? extends Action> clazz;

		private Type(String type, Class<? extends Action> clazz) {
			this.type = type;
			this.clazz = clazz;
		}

		public Action create(Player sender, List<String> data, String[] args)
		{
			try {
				Action action = clazz.getConstructor(Player.class, List.class, String[].class).newInstance(sender, data, args);
				return action;
			} catch (Throwable exception) {
				exception.printStackTrace();
				return null;
			}
		}

		public static Type fromType(String type)
		{
			for (Type value : values()) {
				if (value.type.equalsIgnoreCase(type)) {
					return value;
				}
			}

			return null;
		}
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	public boolean isOver();
}

