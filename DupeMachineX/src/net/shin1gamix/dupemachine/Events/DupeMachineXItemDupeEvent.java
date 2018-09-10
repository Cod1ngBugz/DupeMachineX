package net.shin1gamix.dupemachine.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import net.shin1gamix.dupemachine.Core;

public class DupeMachineXItemDupeEvent extends Event implements Cancellable {

	private final Core core = Core.getPlugin(Core.class);

	/**
	 * @return the core
	 */
	public Core getCore() {
		return core;
	}

	private final Player duplicator;
	private ItemStack item;

	private boolean blackListIgnore;

	public DupeMachineXItemDupeEvent(final Player duplicator, final ItemStack item, final boolean blackListIgnore) {
		this.duplicator = duplicator;
		this.setItem(item);
		this.setBlackListIgnore(blackListIgnore);
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
		return this.getCore().getDupeHandler().isItemBlackListed(this.getItem())
				|| this.getCore().getDupeHandler().isTypeBlackListed(this.getItem());
	}

	/**
	 * @return the blackListIgnore
	 */
	public boolean isBlackListIgnore() {
		return this.blackListIgnore;
	}

	/**
	 * @param blackListIgnore
	 *            the blackListIgnore to set
	 */
	public void setBlackListIgnore(boolean blackListIgnore) {
		this.blackListIgnore = blackListIgnore;
	}

}
