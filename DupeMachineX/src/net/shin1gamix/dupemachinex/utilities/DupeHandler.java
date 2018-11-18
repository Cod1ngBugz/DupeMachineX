package net.shin1gamix.dupemachinex.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.shin1gamix.dupemachinex.DupeMachineX;
import net.shin1gamix.dupemachinex.MessagesX;
import net.shin1gamix.dupemachinex.events.DupeMachineXItemDupeEvent;

public final class DupeHandler {
	/**
	 * Returns the class which extends JavaPlugin
	 * 
	 * @return Core -> look above.
	 * @since 0.1
	 */
	private final DupeMachineX core;

	public DupeHandler(final DupeMachineX core) {
		this.core = core;
	}

	/**
	 * Opens a duplication machine for the specified player.
	 * 
	 * 
	 * @param p
	 *            The player to retrieve the machine from.
	 * @see #getAllMachines(boolean, boolean)
	 * @see #getAllUsers(String, boolean)
	 * @see #getAllAllowedRanks(String, boolean)
	 * @return String or null if default -> The name of the machine.
	 * @since 0.1
	 */
	public String getMachine(final Player p) {

		final FileConfiguration machineFile = this.core.getMachines().getFile();

		final Set<String> allMachines = this.getAllMachines(false, false);

		/* A map containing all machines as a key and their priority as a value */
		final Map<String, Integer> machinesMap = allMachines.stream()
				.collect(Collectors.toMap(str -> str, str -> machineFile.getInt("Inventories." + str + ".priority")));

		/* The previous map, just sorted by value (Priorities) */
		final Map<String, Integer> machinesOrdered = machinesMap.entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

		for (final Entry<String, Integer> entry : machinesOrdered.entrySet()) {

			final String key = entry.getKey(); // Null if default was matched -> The machine's name
			final String path = "Inventories." + key + "."; // path for the file

			/* Ensure that default is not being looked up */
			if ((!machineFile.contains(path + "allowed-ranks") && !machineFile.contains(path + "allowed-users"))
					|| key.equalsIgnoreCase("default"))
				continue;

			/* Return the first priority machine if the player is an operator. */
			if (p.isOp()) {
				return key;
			}

			Set<String> allowedList;

			/* Not default, is the player allowed by name? */
			if (machineFile.contains(path + "allowed-users")) {
				allowedList = this.getAllAllowedUsers(key, true);
				if (allowedList.contains(p.getName().toLowerCase())) {
					return key; // Found a match by name.
				}
			}

			/* Is the player's rank allowed? */
			final String playerRank = this.core.getVault().getPermission().getPrimaryGroup(p).toLowerCase();
			allowedList = this.getAllAllowedRanks(key, true);
			if (allowedList.contains(playerRank)) {
				return key;
			}

		}

		return "default"; // Player was nowhere listed nor his rank and was not an operator.
	}

	/**
	 * Opens a duplication machine for the specified player.
	 * 
	 * @param p
	 *            The player blacklisting the item.
	 * @return Nothing
	 * @since 0.1
	 */
	public void openMachine(final Player p) {

		/* Is the duplication machine already opened? */
		if (this.core.getInventories().containsKey(p)) {
			Utils.debug("&3&lDupeMachineX > &6&l" + p.getName()
					+ " &7attempted to open a duplication machine while having one already open!");
			return;
		}

		if (this.core.getInventoriesId().containsKey(p)) {
			this.core.getInventoriesId().remove(p);
		}

		final FileConfiguration machineFile = this.core.getMachines().getFile();

		/* key -> default or the machine's name | this is a no null value. */
		final String key = this.getMachine(p);

		final ConfigurationSection sect = machineFile.getConfigurationSection("Inventories." + key);
		final int rows = sect.getInt("rows");

		/* Let's make sure the rows are between 1 and 6 */
		final Inventory inv = Bukkit.createInventory(null, rows > 6 || rows < 1 ? 1 * 9 : rows * 9,
				Utils.colorize(sect.getString("name")));

		p.openInventory(inv);
		this.core.getInventories().put(p, inv);
		this.core.getInventoriesId().put(p, key);

		/* Inform operators that someone used the dupe command. */
		// Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op ->
		// Utils.msg(op, "&3&l" + p.getName() + " &7&lused &6&l/dupe"));

	}

	/**
	 * Opens an inventory to {@code p} as a viewer.
	 * 
	 * @param p
	 *            The player "viewer".
	 * @param targetStr
	 *            The name of the player who's duplicating.
	 * @return Nothing
	 * @since 0.1
	 */
	public void openOtherMachine(final Player p, final String targetStr) {
		/* Is the player attempting to open a different machine, a viewer? */
		if (this.core.getViewers().containsKey(p)) {
			this.core.getViewers().remove(p);
			p.closeInventory();
		}

		final Player target = Bukkit.getPlayer(targetStr); // Null if the target is offline.

		final Map<String, String> map = new HashMap<>();
		map.put("%target%", targetStr);

		/* Is the target online? */
		if (target == null) {
			MessagesX.TARGET_OFFLINE.msg(p, map, false);
			return;
		}

		if (target.getName().equals(p.getName())) {
			MessagesX.VIEW_SELF.msg(p);
			return;
		}

		final String machine = this.getMachine(target);

		final FileConfiguration machineFile = this.core.getMachines().getFile();
		final boolean viewable = machineFile.getBoolean("Inventories." + machine + ".viewable");
		/* Is the machine viewable? */
		if (!viewable) {
			map.put("%machine%", machine);
			MessagesX.UNVIEWABLE.msg(p, map, false);
			return;
		}

		/* Is the machine of the target opened? */
		if (!this.core.getInventories().containsKey(target)) {
			MessagesX.TARGET_INVENTORY_CLOSED.msg(p, map, false);
			return;
		}

		/* Open the dupe machine for the viewer */
		final Inventory inv = this.core.getInventories().get(target);
		p.openInventory(inv);
		this.core.getViewers().put(p, inv);

	}

	/**
	 * Removes an {@code id} from the blacklist.
	 * 
	 * @param p
	 *            The player unblacklisting the item.
	 * @param id
	 *            The id of the blacklisted item.
	 * @return Nothing
	 * @since 0.1
	 */
	public void removeBlacklistItem(final CommandSender p, final String id) {
		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!Utils.isStringLegal(id, true)) {
			MessagesX.ITEM_NAME_INVALID.msg(p, map, false);
			return;
		}

		final FileConfiguration file = this.core.getBlackList().getFile();

		/* Does the ID exist? */
		if (!file.contains("Items." + id)) {
			MessagesX.ID_NOT_EXIST.msg(p, map, false);
			return;
		}

		file.set("Items." + id, null);
		this.core.getBlackList().saveFile();
		MessagesX.ITEM_UNBLACKLISTED.msg(p, map, false);

	}

	/**
	 * Removes an {@code id} from the blacklist.
	 * 
	 * @param p
	 *            The player unblacklisting the item.
	 * @param id
	 *            The id of the blacklisted item.
	 * @return Nothing
	 * @since 0.1
	 */
	public void removeWhitelistItem(final CommandSender p, final String id) {
		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!Utils.isStringLegal(id, true)) {
			MessagesX.ITEM_NAME_INVALID.msg(p, map, false);
			return;
		}

		final FileConfiguration file = this.core.getWhiteList().getFile();

		/* Does the ID exist? */
		if (!file.contains("Items." + id)) {
			MessagesX.ID_NOT_EXIST.msg(p, map, false);
			return;
		}

		file.set("Items." + id, null);
		this.core.getWhiteList().saveFile();
		// TODO whitelisted

	}

	/**
	 * Adds an {@code item} to the blacklist.
	 * 
	 * @param p
	 *            The player blacklisting the item.
	 * @param id
	 *            The id of the blacklisted item.
	 * @param item
	 *            The item being blacklisted.
	 * @return Nothing
	 * @since 0.1
	 */
	public void addBlacklistItem(final Player p, final String id) {
		final ItemStack item = Utils.getMainItem(p);

		if (item == null || item.getType() == Material.AIR) {
			MessagesX.NO_ITEM_IN_HAND.msg(p);
			return;
		}

		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!Utils.isStringLegal(id, true)) {
			MessagesX.ITEM_NAME_INVALID.msg(p, map, false);
			return;
		}

		final FileConfiguration file = this.core.getBlackList().getFile();

		/* Does the ID already exist? */
		if (file.contains("Items." + id)) {
			MessagesX.ID_ALREADY_EXIST.msg(p, map, false);
			return;
		}

		if (isTypeBlackListed(item)) {
			MessagesX.ITEM_TYPE_ALREADY_BANNED.msg(p);
			return;
		}

		if (isItemBlackListed(item)) {
			MessagesX.ITEM_ALREADY_BANNED.msg(p);
			return;
		}

		file.set("Items." + id, item);
		this.core.getBlackList().saveFile();
		map.put("%item%", item.getType().name().toLowerCase().replace("_", " "));
		MessagesX.ITEM_BLACKLISTED.msg(p, map, false);
	}

	/**
	 * Adds an {@code item} to the blacklist.
	 * 
	 * @param p
	 *            The player blacklisting the item.
	 * @param id
	 *            The id of the blacklisted item.
	 * @param item
	 *            The item being blacklisted.
	 * @return Nothing
	 * @since 0.1
	 */
	public void addWhitelistItem(final Player p, final String id) {
		final ItemStack item = Utils.getMainItem(p);

		if (item == null || item.getType() == Material.AIR) {
			MessagesX.NO_ITEM_IN_HAND.msg(p);
			return;
		}

		final Map<String, String> map = new HashMap<>();
		map.put("%id%", id);

		if (!Utils.isStringLegal(id, true)) {
			MessagesX.ITEM_NAME_INVALID.msg(p, map, false);
			return;
		}

		final FileConfiguration file = this.core.getWhiteList().getFile();

		/* Does the ID already exist? */
		if (file.contains("Items." + id)) {
			MessagesX.ID_ALREADY_EXIST.msg(p, map, false);
			return;
		}

		if (isTypeWhitelisted(item)) {
			MessagesX.ITEM_TYPE_ALREADY_BANNED.msg(p);
			return;
		}

		if (isItemWhitelisted(item)) {
			MessagesX.ITEM_ALREADY_BANNED.msg(p);
			return;
		}

		file.set("Items." + id, item);
		this.core.getWhiteList().saveFile();
		map.put("%item%", item.getType().name().toLowerCase().replace("_", " "));
		MessagesX.ITEM_BLACKLISTED.msg(p, map, false);
	}

	public boolean isTypeBlackListed(final ItemStack item) {
		final FileConfiguration file = this.core.getBlackList().getFile();
		for (final String loop : file.getConfigurationSection("Items").getKeys(false)) {
			final ItemStack itemStack = file.getItemStack("Items." + loop);
			if (itemStack.hasItemMeta() || itemStack.getType() != item.getType()) {
				continue;
			}
			return true;
		}
		return false;
	}

	public boolean isItemBlackListed(final ItemStack item) {
		final FileConfiguration file = this.core.getBlackList().getFile();
		for (final String loop : file.getConfigurationSection("Items").getKeys(false)) {
			final ItemStack itemStack = file.getItemStack("Items." + loop);
			if (itemStack.isSimilar(item)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTypeWhitelisted(final ItemStack item) {
		final FileConfiguration file = this.core.getWhiteList().getFile();
		for (final String loop : file.getConfigurationSection("Items").getKeys(false)) {
			final ItemStack itemStack = file.getItemStack("Items." + loop);
			if (itemStack.hasItemMeta() || itemStack.getType() != item.getType()) {
				continue;
			}
			return true;
		}
		return false;
	}

	public boolean isItemWhitelisted(final ItemStack item) {
		final FileConfiguration file = this.core.getWhiteList().getFile();
		for (final String loop : file.getConfigurationSection("Items").getKeys(false)) {
			final ItemStack itemStack = file.getItemStack("Items." + loop);
			if (Utils.isSimilar(itemStack, item, false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates new repeating tasks for each inventory retrieved from the
	 * configuration folder. It plays a sound every time an item has been
	 * successfully duplicated.
	 * 
	 * @see #playSound(Player)
	 * @see DupeMachineXItemDupeEvent
	 * @return Nothing
	 * @since 0.1
	 */

	public void startTasks() {
		final FileConfiguration machineFile = this.core.getMachines().getFile();
		for (final String id : machineFile.getConfigurationSection("Inventories").getKeys(false)) {
			final int repeatTime = machineFile.getInt("Inventories." + id + ".dupe-ticks");
			final int itemsPerTime = machineFile.getInt("Inventories." + id + ".items-per-dupe");
			final int mode = machineFile.getInt("Inventories." + id + ".mode");
			this.core.getTasks().add(new BukkitRunnable() {
				@Override
				public void run() {

					playerInvs: for (final Entry<Player, Inventory> invs : core.getInventories().entrySet()) {

						final Player player = invs.getKey();

						/* Is the current task being run for the correct inventory? */
						if (!core.getInventoriesId().get(player).equalsIgnoreCase(id)) {
							continue playerInvs;
						}

						final Inventory inv = invs.getValue(); // Dupe inventory

						boolean playSound = false;

						dupeItemLoop: for (final ItemStack i : inv.getContents()) {
							/* Is the item empty or air? */
							if (i == null || i.getType() == Material.AIR) {
								continue dupeItemLoop;
							}

							/* True if the type or item is blacklisted */
							final boolean isBlacklisted = isItemBlackListed(i) || isTypeBlackListed(i);
							final boolean isWhitelisted = isItemWhitelisted(i) || isTypeWhitelisted(i);

							final DupeMachineXItemDupeEvent event = new DupeMachineXItemDupeEvent(player, i,
									isBlacklisted, isWhitelisted, mode);
							Bukkit.getPluginManager().callEvent(event);

							/* Is the item given null or air? */
							if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
								continue dupeItemLoop;
							}

							/* Is the event cancelled? */
							if (event.isCancelled()) {
								continue dupeItemLoop;
							}

							if (event.getMode() == 1) {

								/* Is the item not whitelisted? */
								if (!event.isWhiteListed()) {
									continue dupeItemLoop;
								}

							} else {

								/* Is the item blacklisted? */
								if (event.isBlackListed()) {
									continue dupeItemLoop;
								}

							}

							/* Is the amount already full? If so, skip it. */
							if (i.getAmount() == 64) {
								continue dupeItemLoop;
							}

							/* Is the amount going to exceed the supposed max size? If so, set it to 64 */
							if (i.getAmount() + itemsPerTime > 64) {
								i.setAmount(64);
								if (!playSound) {
									playSound = true;
								}
								continue dupeItemLoop;
							}

							/* Dupe that sh!t ;-; */
							i.setAmount(i.getAmount() + itemsPerTime);
							if (!playSound) {
								playSound = true;
							}

						}

						/* Did any item get duped? If so, play a sound. */

						if (Stream.of(inv.getContents()).filter(item -> item != null).count() > 0 && playSound) {
							playSound(player);
						}

					}

				}
				/* Incase the amount is set to 0, we don't want sh!t to go crazy... */
			}.runTaskTimerAsynchronously(this.core, 0, repeatTime < 1 ? 1l : repeatTime));

		}
	}

	/**
	 * Plays an item pickup sound to a player.
	 * 
	 * @param player
	 *            The player to receive the sound.
	 * @see Player#playSound(Location, String, float, float)
	 * @return Nothing
	 * @since 0.1
	 */
	private void playSound(final Player player) {
		try {
			player.playSound(player.getLocation(), Bukkit.getVersion().contains("1.8") ? Sound.valueOf("ITEM_PICKUP")
					: Sound.valueOf("ENTITY_ITEM_PICKUP"), 1, 1);
		} catch (Exception expected) {
		}
	}

	/**
	 * This method will be called to cancel everything, meaning:
	 * 
	 * 1. All tasks for duplication will be stopped.
	 * 
	 * 2. All opened "machines" will be closed".
	 * 
	 * 3. All viewers will be removed.
	 * 
	 * 4. All Map objects will be cleared.
	 * 
	 * @return Nothing
	 * @since 0.1
	 */
	public void disableFunction() {
		this.core.getTasks().forEach(task -> task.cancel()); // Cancel duplication tasks
		this.core.getTasks().clear();
		this.core.getInventoriesId().clear();

		Iterator<Player> itr;

		/* The plugin is being disabled, close all inventories and send a message */
		itr = this.core.getInventories().keySet().iterator();
		/* Start with the duplicators. */
		while (itr.hasNext()) {
			final Player duper = itr.next();
			MessagesX.PLUGIN_DISABLE.msg(duper);

			final Map<Integer, ItemStack> items = Utils.addItems(duper.getInventory(),
					this.core.getInventories().get(duper).getContents());

			if (!items.isEmpty()) {
				items.values().forEach(item -> duper.getWorld().dropItemNaturally(duper.getLocation(), item));
				MessagesX.INVENTORY_FULL.msg(duper);
				duper.updateInventory();
			}

			this.core.getInventories().remove(duper);
			duper.closeInventory();
		}

		/* Removing all viewers from map and closing their inventory. */
		itr = this.core.getViewers().keySet().iterator();
		while (itr.hasNext()) {
			final Player viewer = itr.next();
			MessagesX.PLUGIN_DISABLE.msg(viewer);
			this.core.getViewers().remove(viewer);
			viewer.closeInventory();
		}
	}

	/**
	 * Adds a users name in a machine.
	 * 
	 * @param defaultToo
	 *            Whether default should be looked up as well.
	 * @param lowCase
	 *            Should everything be converted to lower case.
	 * 
	 * @return Set<String> -> All machine names.
	 * @since 0.1
	 */
	public Set<String> getAllMachines(final boolean defaultToo, final boolean lowCase) {
		final FileConfiguration machineFile = this.core.getMachines().getFile();
		return new HashSet<>(machineFile.getConfigurationSection("Inventories").getKeys(false).stream()
				.filter(str -> defaultToo ? true : !str.equalsIgnoreCase("default"))
				.map(str -> lowCase ? str.toLowerCase() : str).collect(Collectors.toSet()));
	}

	/**
	 * Gets all allowed users from a machine
	 * 
	 * @param machine
	 *            The machine to get users from.
	 * @param lowCase
	 *            Should names be converted to lower case.
	 * @return Set<String>
	 * @since 0.1
	 */
	public Set<String> getAllAllowedUsers(final String machine, final boolean lowCase) {
		if (machine == null) {
			return new HashSet<>();
		}
		final FileConfiguration machineFile = this.core.getMachines().getFile();
		if (!machineFile.contains("Inventories." + machine + ".allowed-users")) {
			return new HashSet<>();
		}
		return new HashSet<>(machineFile.getStringList("Inventories." + machine + ".allowed-users").stream()
				.map(str -> lowCase ? str.toLowerCase() : str).collect(Collectors.toSet()));
	}

	/**
	 * Gets all allowed ranks from a machine
	 * 
	 * @param machineName
	 *            The machine's "id".
	 * @param lowCase
	 *            Whether rank names should be converted to lower case.
	 * @return Set<String>
	 * @since 0.1
	 */
	public Set<String> getAllAllowedRanks(final String machine, final boolean lowCase) {
		if (machine == null) {
			return new HashSet<>();
		}
		final FileConfiguration machineFile = this.core.getMachines().getFile();

		if (!machineFile.contains("Inventories." + machine + ".allowed-ranks")) {
			return new HashSet<>();
		}
		return new HashSet<>(machineFile.getStringList("Inventories." + machine + ".allowed-ranks").stream()
				.map(str -> lowCase ? str.toLowerCase() : str).collect(Collectors.toSet()));
	}

	/**
	 * Returns the correct machine name by the given parameter name.
	 * 
	 * @param machineName
	 *            The machine's "id".
	 * @see #getAllMachines(boolean, boolean)
	 * @return String or Null
	 * @since 0.1
	 */
	public String getMachineNameIgnoreCase(final boolean defaultToo, final String machineName) {
		return this.getAllMachines(defaultToo, true).stream().filter(str -> str.equalsIgnoreCase(machineName))
				.findFirst().orElse(null);
	}

	/**
	 * Adds a users name in a machine.
	 * 
	 * @param cs
	 *            The one to receive the messages.
	 * @param playerName
	 *            The player's name.
	 * @param machineName
	 *            The machine's "id".
	 * @see #getMachineNameIgnoreCase(String)
	 * @see #getAllUsers(String, boolean)
	 * @return Nothing
	 * @since 0.1
	 */
	public void allowPlayer(final CommandSender cs, final String playerName, final String machineName) {
		/* Attempt to add users in the default machine? */
		if (machineName.equalsIgnoreCase("default")) {
			MessagesX.DEFAULT_MACHINE_UNCHANGEABLE.msg(cs);
			return;
		}

		Map<String, String> map = new HashMap<>();
		map.put("%user%", playerName);
		map.put("%machine%", machineName);

		/*
		 * machineName is low case, machine returns normal case
		 * 
		 * Null if none found
		 */
		final String machine = this.getMachineNameIgnoreCase(false, machineName);
		/* None found, most likely */
		if (machine == null) {
			MessagesX.MACHINE_NOT_EXIST.msg(cs, map, false);
			return;
		}

		/* Does the machine already allows for that user? */
		final Set<String> allUsers = this.getAllAllowedUsers(machine, true);
		if (!allUsers.isEmpty() && allUsers.contains(playerName)) {
			MessagesX.USER_ALREADY_IN_MACHINE.msg(cs, map, false);
			return;
		}

		final FileConfiguration machineList = this.core.getMachines().getFile();
		final List<String> machineUsers = machineList.getStringList("Inventories." + machine + ".allowed-users");
		machineUsers.add(playerName);

		machineList.set("Inventories." + machine + ".allowed-users", machineUsers);
		this.core.getMachines().saveFile();
		MessagesX.USER_ADDED_IN_MACHINE.msg(cs, map, false);
	}

	/**
	 * Removes a users name from a machine.
	 * 
	 * @param cs
	 *            The one to receive the messages.
	 * @param playerName
	 *            The player's name.
	 * @param machineName
	 *            The machine's "id".
	 * @see #getMachineNameIgnoreCase(String)
	 * @see #getAllUsers(String, boolean)
	 * @return Nothing
	 * @since 0.1
	 */
	public void disAllowPlayer(final CommandSender cs, final String playerName, final String machineName) {
		/* Attempt to remove users from the default machine? */
		if (machineName.equalsIgnoreCase("default")) {
			MessagesX.DEFAULT_MACHINE_UNCHANGEABLE.msg(cs);
			return;
		}

		Map<String, String> map = new HashMap<>();
		map.put("%machine%", machineName);
		map.put("%user%", playerName);

		/*
		 * machineName is low case, machine returns normal case
		 * 
		 * Null if none found
		 */
		final String machine = this.getMachineNameIgnoreCase(false, machineName);
		/* None found, most likely */
		if (machine == null) {
			MessagesX.MACHINE_NOT_EXIST.msg(cs, map, false);
			return;
		}

		final Set<String> allUsers = this.getAllAllowedUsers(machine, true);
		/* Does the machine already disallows for that user? */
		if (allUsers.isEmpty() || !allUsers.contains(playerName)) {
			MessagesX.USER_ALREADY_NOT_IN_MACHINE.msg(cs, map, false);
			return;
		}

		final FileConfiguration machineList = this.core.getMachines().getFile();
		final List<String> users = machineList.getStringList("Inventories." + machine + ".allowed-users");
		users.remove(playerName);

		machineList.set("Inventories." + machine + ".allowed-users", users);
		this.core.getMachines().saveFile();
		MessagesX.USER_REMOVED_FROM_MACHINE.msg(cs, map, false);
	}

	/**
	 * Reloads all files and runs the disableFunction method. and sends a reload
	 * message to {@code target}.
	 * 
	 * @param target
	 *            The player to receive the reload message.
	 * @see #disableFunction()
	 * @see #startTasks()
	 * @see #repairMachines()
	 * @return Nothing
	 * @since 0.1
	 */
	public void reloadPlugin(final CommandSender target) {
		this.disableFunction();
		this.core.getMessages().reloadFile();
		MessagesX.repairPaths(this.core.getMessages());
		this.core.getWhiteList().reloadFile();
		this.core.getBlackList().reloadFile();
		this.core.getMachines().reloadFile();
		this.repairMachines();
		this.startTasks();
		MessagesX.PLUGIN_RELOADED.msg(target);
	}

	/**
	 * Removes a rank from a machine.
	 * 
	 * @param cs
	 *            The one to receive the messages.
	 * @param rankName
	 *            The rank's name.
	 * @param machineName
	 *            The machine's "id".
	 * @see #getMachineNameIgnoreCase(String)
	 * @see #getAllAllowedRanks(String, boolean)
	 * @return Nothing
	 * @since 0.1
	 */
	public void disAllowRank(final CommandSender cs, final String rankName, final String machineName) {
		/* Attempt to remove users from the default machine? */
		if (machineName.equalsIgnoreCase("default")) {
			MessagesX.DEFAULT_MACHINE_UNCHANGEABLE.msg(cs);
			return;
		}

		Map<String, String> map = new HashMap<>();
		map.put("%machine%", machineName);
		map.put("%rank%", rankName);

		/*
		 * machineName is low case, machine returns normal case -> Null if none found.
		 */
		final String machine = this.getMachineNameIgnoreCase(false, machineName);
		/* None found, most likely */
		if (machine == null) {
			MessagesX.MACHINE_NOT_EXIST.msg(cs, map, false);
			return;
		}

		final Set<String> ranksLow = this.getAllAllowedRanks(machine, true);
		/* Does the machine already disallows for that user? */
		if (!ranksLow.isEmpty() && ranksLow.contains(rankName)) {
			MessagesX.RANK_ALREADY_NOT_IN_MACHINE.msg(cs, map, false);
			return;
		}

		final FileConfiguration machineList = this.core.getMachines().getFile();
		final List<String> ranks = machineList.getStringList("Inventories." + machine + ".allowed-ranks");
		ranks.remove(rankName);

		machineList.set("Inventories." + machine + ".allowed-ranks", ranks);
		this.core.getMachines().saveFile();
		MessagesX.RANK_REMOVED_FROM_MACHINE.msg(cs, map, false);
	}

	/**
	 * Attempts to fix any missing paths in some machines.
	 * 
	 * @see #getAllMachines(boolean, boolean)
	 * @return Nothing
	 * @since 0.1
	 */
	public void repairMachines() {
		final FileConfiguration machineList = this.core.getMachines().getFile();
		final Set<String> machines = this.getAllMachines(false, false);

		for (final String mach : machines) {
			final String priority = "Inventories." + mach + ".priority";
			final String allowedRanks = "Inventories." + mach + ".allowed-ranks";
			final String allowedUsers = "Inventories." + mach + ".allowed-users";
			final String rows = "Inventories." + mach + ".rows";
			final String name = "Inventories." + mach + ".name";
			final String itemsPerDupe = "Inventories." + mach + ".items-per-dupe";
			final String dupeTicks = "Inventories." + mach + ".dupe-ticks";
			final String viewable = "Inventories." + mach + ".viewable";
			if (!machineList.contains(priority)) {
				machineList.set(priority, Utils.getRandomInt(100, 500));
			}
			if (!machineList.contains(allowedRanks)) {
				machineList.set(allowedRanks, new ArrayList<String>());
			}
			if (!machineList.contains(allowedUsers)) {
				machineList.set(allowedUsers, new ArrayList<String>());
			}
			if (!machineList.contains(rows)) {
				machineList.set(rows, 3);
			}
			if (!machineList.contains(name)) {
				machineList.set(name, "&3DupeMachineX");
			}
			if (!machineList.contains(itemsPerDupe)) {
				machineList.set(itemsPerDupe, 1);
			}
			if (!machineList.contains(dupeTicks)) {
				machineList.set(dupeTicks, 40);
			}
			if (!machineList.contains(viewable)) {
				machineList.set(viewable, true);
			}
		}
		this.core.getMachines().saveFile();

	}

	/**
	 * Adds a rank in a machine.
	 * 
	 * @param cs
	 *            The one to receive the messages.
	 * @param rankName
	 *            The rank's name.
	 * @param machineName
	 *            The machine's "id".
	 * @see #getMachineNameIgnoreCase(String)
	 * @see #getAllAllowedRanks(String, boolean)
	 * @return Nothing
	 * @since 0.1
	 */
	public void allowRank(final CommandSender cs, final String rankName, final String machineName) {
		/* Attempt to remove users from the default machine? */
		if (machineName.equalsIgnoreCase("default")) {
			MessagesX.DEFAULT_MACHINE_UNCHANGEABLE.msg(cs);
			return;
		}

		Map<String, String> map = new HashMap<>();
		map.put("%machine%", machineName);
		map.put("%rank%", rankName);

		/*
		 * machineName is low case, machine returns normal case
		 * 
		 * Null if none found
		 */
		final String machine = this.getMachineNameIgnoreCase(false, machineName);
		/* None found, most likely */
		if (machine == null) {
			MessagesX.MACHINE_NOT_EXIST.msg(cs, map, false);
			return;
		}

		final Set<String> ranksLow = this.getAllAllowedRanks(machine, true);
		/* Does the machine already allows for that rank? */
		if (!ranksLow.isEmpty() && ranksLow.contains(rankName)) {
			MessagesX.RANK_ALREADY_IN_MACHINE.msg(cs, map, false);
			return;
		}

		final FileConfiguration machineList = this.core.getMachines().getFile();
		final List<String> ranks = machineList.getStringList("Inventories." + machine + ".allowed-ranks");
		ranks.add(rankName);

		machineList.set("Inventories." + machine + ".allowed-ranks", ranks);
		this.core.getMachines().saveFile();
		MessagesX.RANK_ADDED_IN_MACHINE.msg(cs, map, false);

	}

	public int getMode(final String machine) {
		final int mode = this.core.getMachines().getFile().getInt("Inventories." + machine + ".mode");
		return mode > 1 || mode < 0 ? 0 : mode;
	}

}
