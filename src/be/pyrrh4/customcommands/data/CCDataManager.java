package be.pyrrh4.customcommands.data;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import be.pyrrh4.customcommands.CustomCommands;
import be.pyrrh4.pyrcore.lib.data.DataManager;

public class CCDataManager extends DataManager {

	// constructor
	private CCBoard board = null;
	private UserBoard userBoard = null;
	private UserEvents userEvents = null;

	public CCDataManager(BackEnd backend) {
		super(CustomCommands.inst(), backend);
	}

	// getters
	public CCBoard getBoard() {
		return board;
	}

	public UserBoard getUsers() {
		return userBoard;
	}

	// methods
	@Override
	protected void innerEnable() {
		// disk board
		this.board = new CCBoard();
		board.initAsync(new Callback() { @Override public void callback() {
			board.pullAsync();
		}});
		// users
		this.userBoard = new UserBoard();
		userBoard.initAsync(new Callback() { @Override public void callback() {
			userBoard.pullOnline();
		}});
		// events
		Bukkit.getPluginManager().registerEvents(this.userEvents = new UserEvents(), getPlugin());
	}

	@Override
	protected void innerSynchronize() {
		board.pullAsync();
		userBoard.pullOnline();
	}

	@Override
	protected void innerReset() {
		board.deleteAsync();
		userBoard.deleteAsync();
	}

	@Override
	protected void innerDisable() {
		this.board = null;
		this.userBoard = null;
		HandlerList.unregisterAll(userEvents);
		this.userEvents = null;
	}

}
