package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionTab implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionTab(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.inst().replaceString(data.get(0).replace(" ", ""), sender, args);
		String header = Utils.fillPlaceholderAPI(sender, Utils.format(CustomCommands.inst().replaceString(data.get(1), sender, args))), footer = Utils.fillPlaceholderAPI(sender, Utils.format(CustomCommands.inst().replaceString(data.get(2), sender, args)));

		// target player
		if (target.equalsIgnoreCase("player")) {
			Messenger.tab(sender, header.replace("&7{receiver}", sender.getName()), footer.replace("&7{receiver}", sender.getName()));
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				Messenger.tab(pl, header.replace("&7{receiver}", pl.getName()), footer.replace("&7{receiver}", pl.getName()));
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				Messenger.tab(newTarget, header.replace("&7{receiver}", newTarget.getName()), footer.replace("&7{receiver}", newTarget.getName()));
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
