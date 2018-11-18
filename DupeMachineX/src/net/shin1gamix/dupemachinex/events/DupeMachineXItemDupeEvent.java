package net.shin1gamix.dupemachinex.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class DupeMachineXItemDupeEvent extends Event implements Cancellable {

	private final Player duplicator;
	private ItemStack item;

	private boolean blackListed;
	private boolean whiteListed;
	private final int mode;

	public DupeMachineXItemDupeEvent(final Player duplicator, final ItemStack item, final boolean blackListed,
			final boolean whiteListed, final int mode) {
		this.duplicator = duplicator;
		this.setItem(item);
		this.setWhiteListed(whiteListed);
		this.setBlackListed(blackListed);
		this.mode = mode;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	private boolean cancel;

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public Player getDuplicator() {
		return this.duplicator;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * @return the item
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * @return the isBlackListed
	 */
	public boolean isBlackListed() {
		return this.blackListed;
	}

	/**
	 * @return the whiteListed
	 */
	public boolean isWhiteListed() {
		return this.whiteListed;
	}

	/**
	 * @param whiteListed
	 *            the whiteListed to set
	 */
	public void setWhiteListed(boolean whiteListed) {
		this.whiteListed = whiteListed;
	}

	/**
	 * @param blackListed
	 *            the blackListed to set
	 */
	public void setBlackListed(boolean blackListed) {
		this.blackListed = blackListed;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

}
