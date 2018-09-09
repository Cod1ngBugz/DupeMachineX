package net.shin1gamix.dupemachine;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.shin1gamix.dupemachine.Utilities.Ut;

public enum MessagesX {

	NO_PERMISSION("&cYou are not allowed to use this command.", "Messages.No-Permission"),
	UNKNOWN_ARGUEMENT("&cIt seems that the command you typed is wrong.", "Messages.Unknown-Arguement"), 
	TARGET_INVENTORY_CLOSED("&cIt seems that &6%target%'s &cdupe machine is not opened.","Messages.Target-Inventory-Closed"), 
	TARGET_OFFLINE("&6%target% &cdoesn't seem to be online.", "Messages.Target-Offline"), 
	ITEM_NAME_INVALID("&cThe name &6%id% &cis an invalid one, consider changing it.","Messages.Item-Name-Invalid"), 
	ITEM_ALREADY_BANNED("&cThe item you're attempting to add is already banned","Messages.Item-Already-Banned"), 
	ITEM_TYPE_ALREADY_BANNED("&cThe item type you're attempting to add is fully banned.","Messages.Item-Type-Already-Banned"), 
	PLUGIN_DISABLE("&cThe plugin is being disabled, closing your inventory...","Messages.Plugin-Disabling"), 
	NO_ITEM_IN_HAND("&cYou must be holding an item in order to ban it!","Messages.No-Item-In-Hand"), 
	HELP_FORMAT("&cHelp-Format message seems to not be working, contact an administrator &nASAP&c!","Messages.Help-Format"), 
	ITEM_BLACKLISTED("&7You have successfully &cblacklisted &7the &3%item% &7with id &e%id%","Messages.Item-Blacklisted"), 
	ITEM_UNBLACKLISTED("&7You have successfully &aunblacklisted &7the id &e%id%","Messages.Item-UnBlacklisted"), 
	INVENTORY_FULL("&cYour inventory was full, dropping leftovers to the floor.","Messaes.Inventory-Full"), 
	ID_NOT_EXIST("&7The item with id &e%id% &7doesn't seem to be blacklisted.","Messages.ID-Not-Exist"), 
	ID_ALREADY_EXIST("&7The id &e%id% &7is already taken, try something different.","Messages.ID-Already-Exist"), 
	PLUGIN_RELOADED("&aThe plugin has been reloaded successfuly!","Messages.Plugin-Reloaded"), 
	UNVIEWABLE("&e%target%&c''s machine is not viewable: &e%machine%","Messages.Unviewable"), 
	VIEW_SELF("&cYou can't view yourself! Try again with a different user.","Messages.View-Self"), 
	DEFAULT_MACHINE_UNCHANGEABLE("&cYou may not interfere with the default machine!","Messages.Default-Machine-Unchangeable"), 
	MACHINE_NOT_EXIST("&cThe machine &e%machine% &cdoesn't seem to exist.","Messages.Machine-Not-Exist"), 
	USER_ALREADY_NOT_IN_MACHINE("&cThe user &e%user% &cis already disallowed for the machine &e%machine%","Messages.User-Already-Not-In-Machine"), 
	USER_ALREADY_IN_MACHINE("&cThe user &e%user% is already allowed for the machine %machine%","Messages.User-Already-In-Machine"),
	RANK_ALREADY_NOT_IN_MACHINE("&cThe rank &e%rank% &cis already disallowed for the machine &e%machine%","Messages.User-Already-Not-In-Machine"),
	RANK_ALREADY_IN_MACHINE("&cThe rank &e%rank% is already allowed for the machine %machine%","Messages.Rank-Already-In-Machine"), 
	USER_ADDED_IN_MACHINE("&aThe user &d%user% &ahas been added in the &d%machine% &amachine.","Messages.Rank-Added-In-Machine"), 
	USER_REMOVED_FROM_MACHINE("&cThe user &e%user% &chas been removed from the &e%machine% &cmachine.","Messages.User-Removed-From-Machine"), 
	RANK_ADDED_IN_MACHINE("&aThe rank &d%rank% &ahas been added in the &d%machine% &amachine.","Messages.Rank-Added-In-Machine"), 
	RANK_REMOVED_FROM_MACHINE("&cThe rank &e%rank% &chas been removed from the &e%machine% &cmachine.","Messages.Rank-Removed-From-Machine");

	private Core instance;

	public Core getCore() {
		return this.instance == null ? Core.getPlugin(Core.class) : this.instance;
	}

	private String message;

	private String path;

	MessagesX(final String message, final String path) {
		this.message = message;
		this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

	/**
	 * Sends an enum message to a CommandSender target.
	 *
	 * @param target
	 *            Target to send the message.
	 */
	public void msg(final CommandSender target) {
		final FileConfiguration file = this.getCore().getCFG().getFile();

		if (!file.contains(this.getPath())) {
			Ut.msg(target, this.getMessage());
			return;
		}

		if (Ut.isList(file, this.getPath())) {
			Ut.msg(target, file.getStringList(this.getPath()));
		} else {
			Ut.msg(target, file.getString(this.getPath()));
		}

	}

	/**
	 * Sends an enum message with placeholders to a CommandSender target.
	 *
	 * @param target
	 *            Target to send the message.
	 * @param map
	 *            The placeholders.
	 * @see Ut#msg(CommandSender, String, Map)
	 * @see Ut#msg(CommandSender, String)
	 * @since 0.1
	 */
	public void msg(final CommandSender target, final Map<String, String> map) {

		if (map == null) {
			Ut.msg(target, this.getMessage());
			return;
		}

		final FileConfiguration file = this.getCore().getCFG().getFile();

		if (!file.contains(this.getPath())) {
			Ut.msg(target, this.getMessage(), map);
			return;
		}

		if (Ut.isList(file, this.getPath())) {
			Ut.msg(target, file.getStringList(this.getPath()), map);

		} else {
			Ut.msg(target, file.getString(this.getPath()), map);
		}

	}

}
