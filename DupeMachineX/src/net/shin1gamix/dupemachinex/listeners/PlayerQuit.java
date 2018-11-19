package net.shin1gamix.dupemachinex.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.shin1gamix.dupemachinex.DupeMachineX;

/**
 * @author Shin1gamiX
 * 
 *         The purpose of this class is to handle the disconnection of any
 *         player. We want to avoid having memory leaks by not removing useless
 *         data from our maps. Therefore, we remove any user that has
 *         disconnected or has been kicked from all maps.
 *
 */
public class PlayerQuit implements Listener {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private final DupeMachineX core;

	public PlayerQuit(final DupeMachineX core) {
		this.core = core;
		Bukkit.getPluginManager().registerEvents(this, core);
	}

	/**
	 * Listens on PlayerQuitEvent and attempts to remove the player from all maps.
	 * 
	 * @return Nothing
	 * @since 0.1
	 */
	@EventHandler
	private void onQuit(final PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		this.core.getInventories().remove(p);
		this.core.getInventoriesId().remove(p);
		this.core.getViewers().remove(p);
	}

	/**
	 * Listens on PlayerKickEvent and attempts to remove the player from all maps.
	 * 
	 * @return Nothing
	 * @since 0.1
	 */
	@EventHandler
	private void onKick(final PlayerKickEvent e) {
		final Player p = e.getPlayer();
		this.core.getInventories().remove(p);
		this.core.getInventoriesId().remove(p);
		this.core.getViewers().remove(p);
	}

}
