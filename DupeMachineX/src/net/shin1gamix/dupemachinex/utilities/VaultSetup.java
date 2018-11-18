package net.shin1gamix.dupemachinex.utilities;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;
import net.shin1gamix.dupemachinex.DupeMachineX;

public class VaultSetup {

	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private final DupeMachineX main;

	public VaultSetup(final DupeMachineX main) {
		this.main = main;
	}

	
	public boolean isValid() {
		return /* this.setupChat() && this.setupEconomy() && */ this.setupPermissions();
	}

	private Permission permission = null;
	// private Economy economy = null;
	// private Chat chat = null;

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = this.main.getServer().getServicesManager()
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
		return this.permission;
	}

	/*
	 * public Economy getEconomy() { return economy; }
	 * 
	 * public Chat getChat() { return chat; }
	 */

}
