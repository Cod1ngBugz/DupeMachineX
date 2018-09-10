package net.shin1gamix.dupemachine.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.shin1gamix.dupemachine.Core;

public class ViewerInterfere implements Listener {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private Core main;

	public ViewerInterfere(final Core main) {
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
	 * Listens on InventoryClickEvent and cancels any attempts made by the player,
	 * also recognized as a viewer.
	 * 
	 * @return Nothing
	 * @since 0.1
	 */
	@EventHandler(ignoreCancelled = true)
	private void onInterfere(final InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (!this.getMain().getViewers().containsKey(p)) {
			return;
		}
		e.setCancelled(true);
	}

	/**
	 * Listens on InventoryCloseEvent and attempts to remove the viewer from the
	 * necessary map.
	 * 
	 * @return Nothing
	 * @since 0.1
	 */
	@EventHandler
	private void onViewClose(final InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (!this.getMain().getViewers().containsKey(p)) {
			return;
		}
		this.getMain().getViewers().remove(p);
	}

}
