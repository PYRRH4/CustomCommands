package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionCommandsAs implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionCommandsAs(final Player sender, final List<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				String target = CustomCommands.inst().replaceString(data.get(0).replace(" ", ""), sender, args);

				for (int i = 0; i < data.size(); i++) {
					data.set(i, Utils.format(Utils.fillPlaceholderAPI(sender, data.get(i))));
				}

				// target player
				if (target.equalsIgnoreCase("player")) {
					for (int i = 1; i < data.size(); i++) {
						Bukkit.dispatchCommand(sender, CustomCommands.inst().replaceString(data.get(i), sender, args));
					}
				}
				// target everyone
				else if (target.equalsIgnoreCase("everyone")) {
					for (Player pl : Utils.getOnlinePlayers()) {
						for (int i = 1; i < data.size(); i++) {
							Bukkit.dispatchCommand(pl, CustomCommands.inst().replaceString(data.get(i), sender, args));
						}
					}
				}
				// target player in argument
				else {
					try {
						Player newTarget = Utils.getPlayer(target);
						for (int i = 1; i < data.size(); i++) {
							Bukkit.dispatchCommand(newTarget, CustomCommands.inst().replaceString(data.get(i), sender, args));
						}
					} catch (Throwable exception) {
						PCLocale.MSG_GENERIC_INVALIDPLAYER.send(sender, "{plugin}", CustomCommands.inst().getName(), "{player}", target);
					}
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
