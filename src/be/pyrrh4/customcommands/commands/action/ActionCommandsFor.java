package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionCommandsFor implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionCommandsFor(final Player sender, final List<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// execute commands
				for (int i = 0; i < data.size(); i++) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.format(Utils.fillPlaceholderAPI(sender, CustomCommands.inst().replaceString(data.get(i), sender, args))));
				}
			}
		}.runTask(CustomCommands.inst());
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
