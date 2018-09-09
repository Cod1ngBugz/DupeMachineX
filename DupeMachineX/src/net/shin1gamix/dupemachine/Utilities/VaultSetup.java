package net.shin1gamix.dupemachine.Utilities;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;
import net.shin1gamix.dupemachine.Core;

public class VaultSetup {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private Core main;

	public VaultSetup(final Core main) {
		this.main = main;
	}

	/**
	 * @return the main
	 */
	public Core getMain() {
		return this.main;
	}

	public boolean isValid() {
		return /* this.setupChat() && this.setupEconomy() && */ this.setupPermissions();
	}

	private Permission permission = null;
	// private Economy economy = null;
	// private Chat chat = null;

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = this.getMain().getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	/*
	 * private boolean setupChat() { RegisteredServiceProvider<Chat> chatProvider =
	 * this.getMain().getServer().getServicesManager()
	 * .getRegistration(net.milkbowl.vault.chat.Chat.class); if (chatProvider !=
	 * null) { chat = chatProvider.getProvider(); }
	 * 
	 * return (chat != null); }
	 * 
	 * private boolean setupEconomy() { RegisteredServiceProvider<Economy>
	 * economyProvider = this.getMain().getServer().getServicesManager()
	 * .getRegistration(net.milkbowl.vault.economy.Economy.class); if
	 * (economyProvider != null) { economy = economyProvider.getProvider(); }
	 * 
	 * return (economy != null); }
	 */

	public Permission getPermission() {
		return permission;
	}

	/*
	 * public Economy getEconomy() { return economy; }
	 * 
	 * public Chat getChat() { return chat; }
	 */

}
