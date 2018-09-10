package net.shin1gamix.dupemachine.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.shin1gamix.dupemachine.Core;

public final class UpdateChecker {

	private final Core core;

	public Core getCore() {
		return this.core;
	}

	public String getPluginVersion() {
		return this.getCore().getDescription().getVersion();
	}

	private String spigotVersion;

	public String getSpigotVersion() {
		return this.spigotVersion;
	}

	public UpdateChecker(final Core core) {
		this.core = core;
		this.registerPJE();
	}

	private static final int ID = 60576; // RESOURCES ID
	private static final String RESOURCE_LINK = "https://www.spigotmc.org/resources/" + ID; // RESOURCE LINK

	/*
	 * Attempt to check for an update.
	 * 
	 * Outputs:
	 * 
	 * 1) Resource needs update.
	 * 
	 * 2) Resource up to date.
	 * 
	 * 3) Update check failed due to:
	 * 
	 * 3.1) Server doesn't have any internet connection or is too slow.
	 * 
	 * 3.2) Spigot is down or is having issues.
	 * 
	 * 3.3) Not sure?
	 */
	public void checkForUpdate(final Player player) {

		Bukkit.getScheduler().runTaskAsynchronously(this.getCore(), () -> {
			try {
				HttpsURLConnection connection =(HttpsURLConnection) new URL(
						"https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
				connection.setRequestMethod("GET");
				this.spigotVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				this.sendResult(player, this.getSpigotVersion());
			} catch (IOException e) {
				Ut.msg(player, "&7Hey " + player.getName() + ", it seems that &can error occurred");
				Ut.msg(player, "&7while attempting to search an update for &3DupeMachineX");
				Ut.msg(player, "&9&lLink:&3 &n" + RESOURCE_LINK);
				e.printStackTrace();
			}

		});

	}

	private void sendResult(final Player player, final String spigotVers) {
		Bukkit.getScheduler().runTaskLater(this.getCore(), () -> {
			for (int i = 0; i < 4; i++) {
				Ut.msg(player, "");
			}
			if (!this.getPluginVersion().equals(spigotVers)) {
				Ut.msg(player, "&7Hey " + player.getName() + ", it seems that &3DupeMachineX &ahas an");
				Ut.msg(player, "&aupdate available&7, check it out in the &6&nlink&7 below!");
				Ut.msg(player, "&9&lLink:&3 &n" + RESOURCE_LINK);
				return;
			}
			Ut.msg(player, "&7Hey " + player.getName() + ", it seems that &3DupeMachineX &7is actually");
			Ut.msg(player, "&7updated. Thank you for using the lastest version. &a:-)");
		}, 100l);
	}

	private void registerPJE() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler(priority = EventPriority.MONITOR)
			private void onPlayerJoin(final PlayerJoinEvent event) {
				final Player player = event.getPlayer();
				if (!player.hasPermission("dupemachinex.update")) {
					return;
				}
				checkForUpdate(player);
			}
		}, this.getCore());
	}

}
