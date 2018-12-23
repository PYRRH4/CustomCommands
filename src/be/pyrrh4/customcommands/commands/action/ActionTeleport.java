package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.Logger;
import be.pyrrh4.pyrcore.lib.Logger.Level;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionTeleport implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionTeleport(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.inst().replaceString(data.get(0).replace(" ", ""), sender, args);
		String locName = CustomCommands.inst().replaceString(data.get(1).replace(" ", ""), sender, args);
		Location loc = CustomCommands.inst().getData().getBoard().getLocation(locName);

		if (loc == null)
		{
			Logger.log(Level.WARNING, CustomCommands.inst(), "Could not find location '" + locName + "'");
			return;
		}

		// target player
		if (target.equalsIgnoreCase("player")) {
			sender.teleport(loc);
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				pl.teleport(loc);
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				newTarget.teleport(loc);
			} catch (Throwable exception) {
				PCLocale.MSG_GENERIC_INVALIDPLAYER.send(sender, "{plugin}", CustomCommands.inst().getName(), "{player}", target);
			}
		}
	}

	// ------------------------------------------------------------
	// Override : is over
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
