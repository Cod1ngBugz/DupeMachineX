package net.shin1gamix.dupemachinex.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.shin1gamix.dupemachinex.DupeMachineX;
import net.shin1gamix.dupemachinex.MessagesX;

public class Duplication implements CommandExecutor {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private final DupeMachineX core;

	public Duplication(final DupeMachineX core) {
		this.core = core;
		core.getCommand("dupemachinex").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String lb, String[] args) {

		Player p;
		switch (args.length) {
		case 0:

			if (!(cs instanceof Player)) {
				return true;
			}
			p = (Player) cs;
			if (!p.hasPermission("dupemachinex.use")) {
				MessagesX.NO_PERMISSION.msg(p);
			} else {
				this.core.getDupeHandler().openMachine(p);
			}
			return true;

		case 1:

			switch (args[0].toLowerCase()) {
			case "help":
				if (!cs.hasPermission("dupemachinex.help")) {
					MessagesX.NO_PERMISSION.msg(cs);
				} else {
					MessagesX.HELP_FORMAT.msg(cs);
				}
				return true;

			case "reload":

				if (!cs.hasPermission("dupemachinex.reload")) {
					MessagesX.NO_PERMISSION.msg(cs);
				} else {
					this.core.getDupeHandler().reloadPlugin(cs);
				}
				return true;

			default:

				if (!(cs instanceof Player)) {
					return true;
				}

				p = (Player) cs;
				if (!p.hasPermission("dupemachinex.view")) {
					MessagesX.NO_PERMISSION.msg(p);
				} else {
					this.core.getDupeHandler().openOtherMachine(p, args[0]);
				}

			}

			return true;

		case 2:

			switch (args[0].toLowerCase()) {
			case "blacklist":
				if (!(cs instanceof Player)) {
					return true;
				}
				p = (Player) cs;

				if (!p.hasPermission("dupemachinex.blacklist")) {
					MessagesX.NO_PERMISSION.msg(p);
				} else {
					this.core.getDupeHandler().addBlacklistItem(p, args[1].toLowerCase());
				}
				return true;
			case "unblacklist":
				if (!cs.hasPermission("dupemachinex.unblacklist")) {
					MessagesX.NO_PERMISSION.msg(cs);
				} else {
					this.core.getDupeHandler().removeBlacklistItem(cs, args[1].toLowerCase());
				}
				return true;
			case "whitelist":
				
				if (!(cs instanceof Player)) {
					return true;
				}
				p = (Player) cs;
				if (!p.hasPermission("dupemachinex.whitelist")) {
					MessagesX.NO_PERMISSION.msg(p);
				} else {
					this.core.getDupeHandler().addWhitelistItem(p, args[1].toLowerCase());
				}
				return true;
			case "unwhitelist":
				if (!cs.hasPermission("dupemachinex.unwhitelist")) {
					MessagesX.NO_PERMISSION.msg(cs);
				} else {
					this.core.getDupeHandler().removeWhitelistItem(cs, args[1].toLowerCase());
				}
				return true;

			}

		case 3:
			if (this.runAllowCommand(cs, args)) {
				return true;
			}

		}

		MessagesX.UNKNOWN_ARGUEMENT.msg(cs);
		return true;
	}

	private boolean runAllowCommand(final CommandSender p, final String[] args) {

		final String first = args[1].toLowerCase();
		final String machineName = args[2].toLowerCase();

		if (args[0].equalsIgnoreCase("allowplayer")) {
			if (!p.hasPermission("dupemachinex.allow.player")) {
				MessagesX.NO_PERMISSION.msg(p);
			} else {
				this.core.getDupeHandler().allowPlayer(p, first, machineName);
			}
			return true;
		} else if (args[0].equalsIgnoreCase("disallowplayer")) {
			if (!p.hasPermission("dupemachinex.disallow.player")) {
				MessagesX.NO_PERMISSION.msg(p);
			} else {
				this.core.getDupeHandler().disAllowPlayer(p, first, machineName);
			}
			return true;
		} else if (args[0].equalsIgnoreCase("allowrank")) {
			if (!p.hasPermission("dupemachinex.allow.rank")) {
				MessagesX.NO_PERMISSION.msg(p);
			} else {
				this.core.getDupeHandler().allowRank(p, first, machineName);
			}
			return true;
		} else if (args[0].equalsIgnoreCase("disallowrank")) {
			if (!p.hasPermission("dupemachinex.disallow.rank")) {
				MessagesX.NO_PERMISSION.msg(p);
			} else {
				this.core.getDupeHandler().disAllowRank(p, first, machineName);
			}
			return true;
		}

		return false;
	}

}
