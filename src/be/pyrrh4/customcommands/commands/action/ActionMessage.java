package be.pyrrh4.customcommands.commands.action;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionMessage implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionMessage(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.inst().replaceString(data.get(0).replace(" ", ""), sender, args);
		List<String> message = new ArrayList<String>();

		for (int i = 1; i < data.size(); i++) {
			String str = Utils.format(CustomCommands.inst().replaceString(data.get(i), sender, args));
			message.add(Utils.fillPlaceholderAPI(sender, str));
		}

		// target player
		if (target.equalsIgnoreCase("player")) {
			for (String str : message) {
				sender.sendMessage(str.replace("&7{receiver}", sender.getName()));
			}
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				for (String str : message) {
					pl.sendMessage(str.replace("&7{receiver}", pl.getName()));
				}
			}
		}
		// target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				for (String str : message) {
					newTarget.sendMessage(str.replace("&7{receiver}", newTarget.getName()));
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				PCLocale.MSG_GENERIC_INVALIDPLAYER.send(sender, "{plugin}", CustomCommands.inst().getName(), "{player}", target);
			}
		}
	}

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public boolean isOver() {
		return true;
	}
}
