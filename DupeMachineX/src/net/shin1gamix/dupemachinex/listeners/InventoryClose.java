package net.shin1gamix.dupemachinex.listeners;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.shin1gamix.dupemachinex.DupeMachineX;
import net.shin1gamix.dupemachinex.MessagesX;
import net.shin1gamix.dupemachinex.utilities.Utils;

/**
 * @author Shin1gamiX
 * 
 *         The purpose of this class is to handle the closure of the
 *         inventories. If a player is found to be a duplicator, the closure of
 *         their inventory shall be handled as well as the ones viewing the
 *         duplication machine, if any.
 *
 */
public class InventoryClose implements Listener {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private final DupeMachineX core;

	public InventoryClose(final DupeMachineX core) {
		this.core = core;
		Bukkit.getPluginManager().registerEvents(this, core);
	}

	/**
	 * Listens on InventoryCloseEvent. Attempts to return all items inside the
	 * duplication machine to the player and remove the player and all possible
	 * viewers from the necessary maps.
	 * 
	 * @return Nothing
	 * @see Ut#addItems(Inventory, ItemStack...)
	 * @since 1.0
	 */
	@EventHandler
	private void onClose(final InventoryCloseEvent e) {
		final Player p = (Player) e.getPlayer();

		/* Is this even a duplication machine inventory? */
		if (!this.core.getInventories().containsKey(p)) {
			return;
		}

		final Inventory inv = this.core.getInventories().get(p); // Get the duplication machine inventory.
		final Inventory pInv = p.getInventory(); // Player inventory.

		this.core.getInventories().remove(p);
		this.core.getInventoriesId().remove(p);

		/* Returns an empty Set<ItemStack> if all items were added successfully. */
		final Map<Integer, ItemStack> items = Utils.addItems(pInv, inv.getContents());

		if (!items.isEmpty()) {
			items.values().forEach(item -> p.getWorld().dropItemNaturally(p.getLocation(), item));
			MessagesX.INVENTORY_FULL.msg(p);
		}

		p.updateInventory();

		/**
		 * Get all viewers of the current duplication machine inventory and close their
		 * inventory
		 * 
		 * @see ViewerInterfere#onViewClose
		 */
		this.core.getViewers().entrySet().stream().filter(entr -> entr.getValue().equals(inv))
				.map(Map.Entry<Player, Inventory>::getKey).forEach(Player::closeInventory);
	}

}
