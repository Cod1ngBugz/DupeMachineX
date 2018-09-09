package net.shin1gamix.dupemachine.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
	 * @since 0.1
	 */
	@EventHandler
	private void onClose(final InventoryCloseEvent e) {
		final Player p = (Player) e.getPlayer();

		/* Is this even a duplication machine inventory? */
		if (!this.getMain().getInventories().containsKey(p))
			return;

		final Inventory inv = this.getMain().getInventories().get(p); // Get the duplication machine inventory.

		/* Adding items to the player's inventory. */
		boolean onGround = false; // Will be used to send a message if the player's inventory is full.
		for (ItemStack i : inv.getContents()) {
			/* Is the item being looped air or null? */
			if (i == null || i.getType() == Material.AIR) {
				continue;
			}

			/* Is the player's inventory full? Drop the item on the ground */
			if (p.getInventory().firstEmpty() == -1) {
				p.getWorld().dropItemNaturally(p.getLocation(), i);
				/* Let's make sure we send a message to the player. */
				onGround = true;
			} else {
				/* Inventory not full, let's add the item in the inventory. */
				p.getInventory().addItem(i);
			}
		}

		if (onGround) {
			MessagesX.INVENTORY_FULL.msg(p);
		}

		this.getMain().getInventories().remove(p);
		this.getMain().getInventoriesId().remove(p);

		/*
		 * Get all viewers of the current duplication machine inventory and close their
		 * inventory
		 */
		this.getMain().getViewers().entrySet().stream().filter(entr -> entr.getValue().equals(inv))
				.map(entr -> entr.getKey()).forEach(Player::closeInventory);
	}

}
