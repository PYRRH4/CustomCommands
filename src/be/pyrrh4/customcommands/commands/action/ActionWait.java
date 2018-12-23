package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.lib.Logger;
import be.pyrrh4.pyrcore.lib.Logger.Level;

public class ActionWait implements Action
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private int current = 0, max = 0;

	public ActionWait(Player sender, List<String> data, String[] args)
	{
		try {
			max = Integer.parseInt(data.get(0));
		} catch (Throwable exception) {
			Logger.log(Level.WARNING, CustomCommands.inst(), "Could not find the delay.");
		}
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return ++current >= max;
	}
}
