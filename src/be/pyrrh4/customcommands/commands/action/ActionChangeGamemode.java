package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionChangeGamemode implements Action {

	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionChangeGamemode(final Player sender, final List<String> data, final String[] args)
	{
		// Resync

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				String target = CustomCommands.inst().replaceString(data.get(0).replace(" ", ""), sender, args);
				GameMode gamemode = null;

				try {
					int id = Integer.parseInt(data.get(1));
					if (id == 0) gamemode = GameMode.SURVIVAL;
					else if (id == 1) gamemode = GameMode.CREATIVE;
					else if (id == 2) gamemode = GameMode.SURVIVAL;
					else if (id == 3) gamemode = GameMode.valueOf("SPECTATOR");
				} catch (Throwable ignored) {}

				// target player
				if (target.equalsIgnoreCase("player")) {
					sender.setGameMode(gamemode);
				}
				// target everyone
				else if (target.equalsIgnoreCase("everyone")) {
					for (Player pl : Utils.getOnlinePlayers()) {
						pl.setGameMode(gamemode);
					}
				}
				// target player in argument
				else {
					try {
						Player newTarget = Utils.getPlayer(target);
						newTarget.setGameMode(gamemode);
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
