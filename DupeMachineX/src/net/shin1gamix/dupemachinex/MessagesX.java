package net.shin1gamix.dupemachinex;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

import net.shin1gamix.dupemachinex.utilities.CFG;
import net.shin1gamix.dupemachinex.utilities.Utils;

public enum MessagesX {

	NO_PERMISSION("Messages.No-Permission", "&cYou are not allowed to use this command."),

	UNKNOWN_ARGUEMENT("Messages.Unknown-Arguement", "&cIt seems that the command you typed is wrong."),

	TARGET_INVENTORY_CLOSED("Messages.Target-Inventory-Closed",
			"&cIt seems that &6%target%'s &cdupe machine is not opened."),

	TARGET_OFFLINE("Messages.Target-Offline", "&6%target% &cdoesn't seem to be online."),

	ITEM_NAME_INVALID("Messages.Item-Name-Invalid", "&cThe name &6%id% &cis an invalid one, consider changing it."),

	ITEM_ALREADY_BANNED("Messages.Item-Already-Banned", "&cThe item you're attempting to add is already banned"),

	ITEM_TYPE_ALREADY_BANNED("Messages.Item-Type-Already-Banned",
			"&cThe item type you're attempting to add is fully banned."),

	PLUGIN_DISABLE("Messages.Plugin-Disabling", "&cThe plugin is being disabled, closing your inventory..."),

	NO_ITEM_IN_HAND("Messages.No-Item-In-Hand", "&cYou must be holding an item in order to ban it!"),

	HELP_FORMAT("Messages.Help-Format", "&7&m=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=",
			"&3/dupe &7- &oOpens the dupe machine.",

			"&3/dupe &9<player> &7- &oViews a players dupe machine.",

			"&3/dupe help &7- &oShows this menu.",

			"&3/dupe reload &7- &oReloads the plugin.",

			"&3/dupe blacklist &9<machine id> &7- &oBlacklists a holding item.",
			"&3/dupe unblacklist &9<machine id> &7- &oUnblacklists an id.",
			"&3/dupe whitelist &9<machine id> &7- &oWhitelists a holding item.",
			"&3/dupe unwhitelist &9<machine id> &7- &oUnwhitelists an id.",

			"&3/dupe allowplayer &9<player> <machine id> &7- &oAllows a player to use a machine.",
			"&3/dupe disallowplayer &9<player> <machine id> &7- &oDisallows a player from using a machine.",
			"&3/dupe allowrank &9<rank> <machine id> &7- &oAllows a rank to use a machine.",
			"&3/dupe disallowrank &9<rank> <machine id> &7- &oDisallows a rank from using a machine.",
			"&7&m=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="),

	ITEM_BLACKLISTED("Messages.Item-Blacklisted",
			"&7You have successfully &cblacklisted &7the &3%item% &7with id &e%id%"),

	ITEM_UNBLACKLISTED("Messages.Item-UnBlacklisted", "&7You have successfully &aunblacklisted &7the id &e%id%"),
	
	ITEM_WHITELISTED("Messages.Item-Whitelisted",
			"&7You have successfully &cwhitelisted &7the &3%item% &7with id &e%id%"),

	ITEM_UNWHITELISTED("Messages.Item-UnWhitelisted", "&7You have successfully &aunwhitelisted &7the id &e%id%"),

	INVENTORY_FULL("Messaes.Inventory-Full", "&cYour inventory was full, dropping leftovers to the floor."),

	ID_NOT_EXIST("Messages.ID-Not-Exist", "&7The item with id &e%id% &7doesn't seem to be blacklisted."),

	ID_ALREADY_EXIST("Messages.ID-Already-Exist", "&7The id &e%id% &7is already taken, try something different."),

	PLUGIN_RELOADED("Messages.Plugin-Reloaded", "&aThe plugin has been reloaded successfuly!"),

	UNVIEWABLE("Messages.Unviewable", "&e%target%&c''s machine is not viewable: &e%machine%"),

	VIEW_SELF("Messages.View-Self", "&cYou can't view yourself! Try again with a different user."),

	DEFAULT_MACHINE_UNCHANGEABLE("Messages.Default-Machine-Unchangeable",
			"&cYou may not interfere with the default machine!"),

	MACHINE_NOT_EXIST("Messages.Machine-Not-Exist", "&cThe machine &e%machine% &cdoesn't seem to exist."),

	USER_ALREADY_NOT_IN_MACHINE("Messages.User-Already-Not-In-Machine",
			"&cThe user &e%user% &cis already disallowed for the machine &e%machine%"),

	USER_ALREADY_IN_MACHINE("Messages.User-Already-In-Machine",
			"&cThe user &e%user% is already allowed for the machine %machine%"),

	RANK_ALREADY_NOT_IN_MACHINE("Messages.Rank-Already-Not-In-Machine",
			"&cThe rank &e%rank% &cis already disallowed for the machine &e%machine%"),

	RANK_ALREADY_IN_MACHINE("Messages.Rank-Already-In-Machine",
			"&cThe rank &e%rank% is already allowed for the machine %machine%"),

	USER_ADDED_IN_MACHINE("Messages.Users-Added-In-Machine",
			"&aThe user &d%user% &ahas been added in the &d%machine% &amachine."),

	USER_REMOVED_FROM_MACHINE("Messages.User-Removed-From-Machine",
			"&cThe user &e%user% &chas been removed from the &e%machine% &cmachine."),

	RANK_ADDED_IN_MACHINE("Messages.Rank-Added-In-Machine",
			"&aThe rank &d%rank% &ahas been added in the &d%machine% &amachine."),

	RANK_REMOVED_FROM_MACHINE("Messages.Rank-Removed-From-Machine",
			"&cThe rank &e%rank% &chas been removed from the &e%machine% &cmachine.");

	/** @see #getMessages() */
	private String[] messages;
	private final String path;

	MessagesX(final String path, final String... messages) {
		this.messages = messages;
		this.path = path;
	}

	/**
	 * @return boolean -> Whether or not the messages array contains more than 1
	 *         element. If true, it's more than 1 message/string.
	 */
	private boolean isMultiLined() {
		return this.messages.length > 1;
	}

	/**
	 * @param cfg
	 * @see #setPathToFile(CFG, MessagesX)
	 * @see #setMessageToFile(CFG, MessagesX)
	 */
	public static void repairPaths(final CFG cfg) {
		for (MessagesX mX : MessagesX.values()) {
			if (cfg.getFile().contains(mX.getPath())) {
				setPathToFile(cfg, mX);
				continue;
			}
			setMessageToFile(cfg, mX);
		}
		cfg.saveFile();
	}

	/**
	 * Sets a message from the MessagesX enum to the file.
	 * 
	 * @param cfg
	 * @param mX
	 */
	private static void setMessageToFile(final CFG cfg, final MessagesX mX) {
		if (mX.isMultiLined()) {
			cfg.getFile().set(mX.getPath(), mX.getMessages());
		} else {
			cfg.getFile().set(mX.getPath(), mX.getMessages()[0]);
		}
	}

	/**
	 * Sets the current MessagesX messages to a string/list retrieved from the
	 * messages file.
	 * 
	 * @param cfg
	 * @param mX
	 */
	private static void setPathToFile(final CFG cfg, final MessagesX mX) {
		if (Utils.isList(cfg.getFile(), mX.getPath())) {
			mX.setMessages(cfg.getFile().getStringList(mX.getPath()).toArray(new String[0]));
		} else {
			mX.setMessages(cfg.getFile().getString(mX.getPath()));
		}
	}

	/**
	 * @return the path -> The path of the enum in the file.
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return the messages -> The messages array contains all string(s).
	 */
	public String[] getMessages() {
		return this.messages;
	}

	/**
	 * Sets the current messages to a different string array.
	 * 
	 * @param messages
	 */
	public void setMessages(final String[] messages) {
		this.messages = messages;
	}

	/**
	 * Sets the string message to a different string assuming that the array has
	 * only 1 element.
	 * 
	 * @param messages
	 */
	public void setMessages(final String messages) {
		this.messages[0] = messages;
	}

	/**
	 * @param target
	 * @see #msg(CommandSender, Map)
	 */
	public void msg(final CommandSender target) {
		msg(target, null, false);
	}

	/**
	 * Sends a translated message to a target commandsender with placeholders gained
	 * from a map. If the map is null, no placeholder will be set and it will still
	 * execute.
	 * 
	 * @param target
	 * @param map
	 */
	public void msg(final CommandSender target, final Map<String, String> map, final boolean ignoreCase) {
		Validate.notNull(target);

		if (this.isMultiLined()) {
			Utils.msg(target, this.getMessages(), map, ignoreCase);
		} else {
			Utils.msg(target, this.getMessages()[0], map, ignoreCase);
		}
	}

}
