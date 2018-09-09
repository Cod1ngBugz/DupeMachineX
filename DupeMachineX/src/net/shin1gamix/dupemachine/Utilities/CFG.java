package net.shin1gamix.dupemachine.Utilities;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CFG {

	private final String filename;
	private FileConfiguration config;
	private File cfile;

	private final JavaPlugin p;

	public CFG(final JavaPlugin p, final String string) {
		this.p = p;
		this.filename = string + ".yml";
	}

	public void setup() {

		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}

		this.cfile = new File(p.getDataFolder(), filename);

		if (!this.getCfile().exists()) {
			try {
				this.getCfile().createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			p.saveResource(filename, true);
		}

		this.reloadFile();
	}

	public void saveConfig() {
		try {
			this.getFile().save(this.getCfile());
		} catch (IOException e) {
			Bukkit.getLogger().severe(ChatColor.RED + "Could not save " + filename + "!");
		}
	}

	public void reloadFile() {
		this.setConfig(YamlConfiguration.loadConfiguration(this.getCfile()));
	}

	public FileConfiguration getFile() {
		return this.config;
	}

	private void setConfig(final FileConfiguration config) {
		this.config = config;
	}

	private File getCfile() {
		return this.cfile;
	}

}