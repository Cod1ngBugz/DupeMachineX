package net.shin1gamix.dupemachine.Listeners;

import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.shin1gamix.dupemachine.Core;
import net.shin1gamix.dupemachine.MessagesX;

public class InventoryClose implements Listener {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private Core main;

	public InventoryClose(final Core main) {
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
	 * Listens on InventoryCloseEvent. Attempts to return all items inside the
	 * duplication machine to the player and remove the player and all possible
	 * viewers from the necessary maps.
	 * 
	 * @return Nothing
	 * @since 1.0
	 */
	@EventHandler
	private void onClose(final InventoryCloseEvent e) {
		final Player p = (Player) e.getPlayer();

		/* Is this even a duplication machine inventory? */
		if (!this.getMain().getInventories().containsKey(p)) {
			return;
		}

		final Inventory inv = this.getMain().getInventories().get(p); // Get the duplication machine inventory.
		final Inventory pInv = p.getInventory(); // Player inventory.

		/*
		 * Add items to the inventory as much as possible. If the item is not able to be
		 * added it will be added in the map. If all items were added, the map will be
		 * empty.
		 * 
		 * Filtering the map by ignoring all null items (possible empty slots)
		 */
		final Map<Integer, ItemStack> fallenItems = pInv.addItem(
				Stream.of(inv.getContents()).filter(item -> item != null).toArray(size -> new ItemStack[size]));

		/* If map is not empty, some items were not added... Let's drop them. */
		if (!fallenItems.isEmpty()) {
			fallenItems.values().forEach(item -> p.getWorld().dropItemNaturally(p.getLocation(), item));
			MessagesX.INVENTORY_FULL.msg(p);
		}

		p.updateInventory();

		this.getMain().getInventories().remove(p);
		this.getMain().getInventoriesId().remove(p);

		/**
		 * Get all viewers of the current duplication machine inventory and close their
		 * inventory
		 * 
		 * @see ViewerInterfere#onViewClose
		 */
		this.getMain().getViewers().entrySet().stream().filter(entr -> entr.getValue().equals(inv))
				.map(entr -> entr.getKey()).forEach(Player::closeInventory);
	}

}
