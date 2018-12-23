package be.pyrrh4.customcommands.commands.action;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ActionItem implements Action
{
	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public ActionItem(Player sender, List<String> data, String[] args)
	{
		String target = CustomCommands.inst().replaceString(data.get(0).replace(" ", ""), sender, args);
		String itemName = CustomCommands.inst().replaceString(data.get(1).replace(" ", ""), sender, args);
		ItemStack item = CustomCommands.inst().getData().getBoard().getItem(itemName);

		if (item == null) {
			CustomCommands.inst().error("Could not find item '" + itemName + "'");
			return;
		}

		// target player
		if (target.equalsIgnoreCase("player")) {
			sender.getInventory().addItem(item);
			sender.updateInventory();
		}
		// target everyone
		else if (target.equalsIgnoreCase("everyone")) {
			for (Player pl : Utils.getOnlinePlayers()) {
				pl.getInventory().addItem(item);
				pl.updateInventory();
			}
		}
		// Target player in argument
		else {
			try {
				Player newTarget = Utils.getPlayer(target);
				newTarget.getInventory().addItem(item);
				newTarget.updateInventory();
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
