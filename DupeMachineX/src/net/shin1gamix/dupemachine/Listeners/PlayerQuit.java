package net.shin1gamix.dupemachine.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.shin1gamix.dupemachine.Core;

public class PlayerQuit implements Listener {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private Core main;

	public PlayerQuit(final Core main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	/**
	 * @return the main
	 */
	public Core getMain() {
		return this.main;
	}

	/**
	 * Listens on PlayerQuitEvent and attempts to remove the player from all maps.
	 * 
	 * @return Nothing
	 * @since 0.1
	 */
	@EventHandler
	private void onInterfere(final PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		this.getMain().getInventories().remove(p);
		this.getMain().getInventoriesId().remove(p);
		this.getMain().getViewers().remove(p);

	}

}
