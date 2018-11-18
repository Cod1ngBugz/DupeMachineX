package net.shin1gamix.dupemachinex.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.shin1gamix.dupemachinex.DupeMachineX;

public class ViewerInterfere implements Listener {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private final DupeMachineX core;

	public ViewerInterfere(final DupeMachineX core) {
		this.core = core;
		Bukkit.getPluginManager().registerEvents(this, core);
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
		if (!this.core.getViewers().containsKey(p)) {
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
		if (!this.core.getViewers().containsKey(p)) {
			return;
		}
		this.core.getViewers().remove(p);
	}

}
