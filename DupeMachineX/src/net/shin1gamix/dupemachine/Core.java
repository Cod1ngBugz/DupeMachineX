package net.shin1gamix.dupemachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.shin1gamix.dupemachine.Commands.Duplication;
import net.shin1gamix.dupemachine.Listeners.InventoryClose;
import net.shin1gamix.dupemachine.Listeners.PlayerQuit;
import net.shin1gamix.dupemachine.Listeners.ViewerInterfere;
import net.shin1gamix.dupemachine.Utilities.CFG;
import net.shin1gamix.dupemachine.Utilities.DupeHandler;
import net.shin1gamix.dupemachine.Utilities.Metrics;
import net.shin1gamix.dupemachine.Utilities.UpdateChecker;
import net.shin1gamix.dupemachine.Utilities.Ut;
import net.shin1gamix.dupemachine.Utilities.VaultSetup;

public class Core extends JavaPlugin {

	/* File Configurations */
	private final CFG cfg = new CFG(this, "config");
	private final CFG itemBase = new CFG(this, "item-handler");
	private final CFG machines = new CFG(this, "machines");

	/* DupeMachine Maps and Tasks */
	private final Map<Player, Inventory> inventories = new HashMap<>();
	private final Map<Player, String> inventoriesId = new HashMap<>();
	private final Map<Player, Inventory> viewers = new HashMap<>();
	private final Set<BukkitTask> tasks = new HashSet<>();

	/* Setting up vault */
	private final VaultSetup vaultSetup = new VaultSetup(this);

	/* Injecting DupeHandler */
	private final DupeHandler dupeHandler = new DupeHandler(this);

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 * @see DupeHandler#repairMachines()
	 * @see VaultSetup#isValid()
	 * @see DupeHandler#startTasks()
	 */
	@Override
	public void onEnable() {

		/* Is vault enabled? */
		if (!this.getVault().isValid()) {
			Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op -> {
				Ut.msg(op, "&7Vault seems to &4not exist &7or is &cdisabled&7.");
				Ut.msg(op, "&cDisabling &3DupeMachineX&c...");
			});
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		this.saveDefaultConfig();
		this.getCFG().setup(); // File for messages.
		this.getItembase().setup();// File to handle banned items.
		this.getMachines().setup(); // File to handle all machines.

		new Duplication(this); // Register the maincommand.
		new InventoryClose(this); // Register the InventoryCloseEvent.
		new ViewerInterfere(this); // Register the Inventory(Click/Close)Event.
		new PlayerQuit(this); // Register the PlayerQuitEvent.

		/* Attempts to fix some missing paths from all machines except the default. */
		this.getDupeHandler().repairMachines();

		/* Start all duplication tasks */
		this.getDupeHandler().startTasks();

		new UpdateChecker(this); // Update check added

		this.enableMetrics(); // Enabling metrics
	}

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 * @see DupeHandler#disableFunction()
	 */
	@Override
	public void onDisable() {
		this.getDupeHandler().disableFunction(true);
	}

	private void enableMetrics() {
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("machine_amount",
				() -> String.valueOf(getDupeHandler().getAllMachines(false, false).size())));
	}

	/**
	 * @return the vaultSet
	 */
	public VaultSetup getVault() {
		return this.vaultSetup;
	}

	/**
	 * @return the cfg
	 */
	public CFG getCFG() {
		return this.cfg;
	}

	/**
	 * @return the inventories
	 */
	public Map<Player, Inventory> getInventories() {
		return this.inventories;
	}

	/**
	 * @return the item base
	 */
	public CFG getItembase() {
		return this.itemBase;
	}

	/**
	 * @return the viewers
	 */
	public Map<Player, Inventory> getViewers() {
		return this.viewers;
	}

	/**
	 * @return the inventoriesId
	 */
	public Map<Player, String> getInventoriesId() {
		return this.inventoriesId;
	}

	/**
	 * @return the tasks
	 */
	public Set<BukkitTask> getTasks() {
		return this.tasks;
	}

	/**
	 * @return the machines
	 */
	public CFG getMachines() {
		return this.machines;
	}

	/**
	 * @return the dupeHandler
	 */
	public DupeHandler getDupeHandler() {
		return this.dupeHandler;
	}

}
