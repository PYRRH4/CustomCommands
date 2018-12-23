package be.pyrrh4.customcommands.data;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.data.PCUser;
import be.pyrrh4.pyrcore.lib.event.UserDataProfileChangedEvent;

public class UserEvents implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerJoinEvent event) {
		joinUser(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(UserDataProfileChangedEvent event) {
		PCUser user = event.getUser();
		Player player = user.getPlayer();
		if (player != null) {
			// unregister user
			quitUser(player);
			// register user
			joinUser(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void event(PlayerQuitEvent event) {
		quitUser(event.getPlayer());
	}

	private void joinUser(final Player player) {
		CCUser user = new CCUser(new PCUser(player));
		CustomCommands.inst().getData().getUsers().online.put(player.getUniqueId(), user);
		user.pullAsync(null);
	}

	private void quitUser(final Player player) {
		if (CustomCommands.inst().getData().getUsers().online.containsKey(player.getUniqueId())) {
			CustomCommands.inst().getData().getUsers().online.remove(player.getUniqueId());
		}
	}

}
