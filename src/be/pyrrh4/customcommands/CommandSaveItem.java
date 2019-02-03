package be.pyrrh4.customcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.command.Param;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandSaveItem extends CommandArgument {

	private static final Param paramItem = new Param(Utils.asList("item", "i"), "id", null, true, true);

	public CommandSaveItem() {
		super(CustomCommands.inst(), null, Utils.asList("saveitem", "setitem"), "save an item", CCPerm.CUSTOMCOMMANDS_ADMIN, true, paramItem);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(CommandCall call) {
		String id = paramItem.getStringAlphanumeric(call);
		if (id != null) {
			Player player = call.getSenderAsPlayer();

			// valid item
			ItemStack item = player.getItemInHand();
			if (item == null || Mat.from(item).isAir()) {
				PCLocale.MSG_GENERIC_NOHANDITEM.send(player, "{plugin}", CustomCommands.inst().getName());
				return;
			}

			// existing item
			if (CustomCommands.inst().getData().getBoard().getItem(id) != null) {
				PCLocale.MSG_GENERIC_IDTAKEN.send(player, "{plugin}", CustomCommands.inst().getName(), "{id}", id);
				return;
			}

			// set item
			CustomCommands.inst().getData().getBoard().setItem(id, item);
			CCLocale.MSG_CUSTOMCOMMANDS_ITEMSAVE.send(player, "{name}", id);
		}
	}
}

