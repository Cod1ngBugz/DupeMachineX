package net.shin1gamix.dupemachinex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.shin1gamix.dupemachinex.commands.Duplication;
import net.shin1gamix.dupemachinex.listeners.InventoryClose;
import net.shin1gamix.dupemachinex.listeners.PlayerQuit;
import net.shin1gamix.dupemachinex.listeners.ViewerInterfere;
import net.shin1gamix.dupemachinex.utilities.CFG;
import net.shin1gamix.dupemachinex.utilities.DupeHandler;
import net.shin1gamix.dupemachinex.utilities.Metrics;
import net.shin1gamix.dupemachinex.utilities.Utils;
import net.shin1gamix.dupemachinex.utilities.VaultSetup;

public final class DupeMachineX extends JavaPlugin {

	private static DupeMachineX instance;

	public static DupeMachineX getInstance() {
		return instance == null ? instance = DupeMachineX.getPlugin(DupeMachineX.class) : instance;
	}

	/* File Configurations */
	private final CFG messages = new CFG(this, "messages");
	private final CFG blackList = new CFG(this, "blacklist");
	private final CFG whiteList = new CFG(this, "whitelist");
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
				Utils.msg(op, "&7Vault seems to &4not exist &7or is &cdisabled&7.");
				Utils.msg(op, "&cDisabling &3DupeMachineX&c...");
			});
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		this.messages.setup(false);
		MessagesX.repairPaths(this.messages);
		this.blackList.setup(true);
		this.whiteList.setup(true);
		this.machines.setup(true);

		new Duplication(this); // Register the maincommand.
		new InventoryClose(this); // Register the InventoryCloseEvent.
		new ViewerInterfere(this); // Register the Inventory(Click/Close)Event.
		new PlayerQuit(this); // Register the PlayerQuitEvent.

		/* Attempts to fix some missing paths from all machines except the default. */
		this.getDupeHandler().repairMachines();

		/* Start all duplication tasks */
		this.getDupeHandler().startTasks();

		this.enableMetrics(); // Enabling metrics
	}

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 * @see DupeHandler#disableFunction()
	 */
	@Override
	public void onDisable() {
		this.getDupeHandler().disableFunction();
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
	 * @return the inventories
	 */
	public Map<Player, Inventory> getInventories() {
		return this.inventories;
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

	/**
	 * @return the messages
	 */
	public CFG getMessages() {
		return this.messages;
	}

	/**
	 * @return the blackList
	 */
	public CFG getBlackList() {
		return this.blackList;
	}

	/**
	 * @return the whiteList
	 */
	public CFG getWhiteList() {
		return this.whiteList;
	}

}
