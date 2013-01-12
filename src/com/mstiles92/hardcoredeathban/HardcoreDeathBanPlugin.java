package com.mstiles92.hardcoredeathban;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.mstiles92.hardcoredeathban.commands.*;

/**
 * HardcoreDeathBanPlugin is the main class of this Bukkit plugin.
 * It handles enabling and disabling of this plugin, loading config
 * files, and other general methods needed for this plugin's operation.
 * 
 * @author mstiles92
 */
public class HardcoreDeathBanPlugin extends JavaPlugin {
	public RevivalCredits credits = null;
	public Bans bans = null;
	
	private final SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm a z");
	private final SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		try {
			credits = new RevivalCredits(this, "credits.yml");
			bans = new Bans(this, "bans.yml");
		} catch (Exception e) {
			getLogger().warning(ChatColor.RED + "Error opening a config file. Plugin will now be disabled.");
			getPluginLoader().disablePlugin(this);
		}
		
		getServer().getPluginManager().registerEvents(new HardcoreDeathBanListener(this), this);
		
		getCommand("deathban").setExecutor(new DeathbanCommand(this));
		getCommand("credits").setExecutor(new CreditsCommand(this));
	}
	
	public void onDisable() {
		credits.save();
		bans.save();
		saveConfig();
	}
	
	public void log(String message) {
		if (getConfig().getBoolean("Verbose")) {
			getLogger().info(message);
		}
	}
	
	public String replaceVariables(String msg, String name) {
		final Calendar now = Calendar.getInstance();
		final Calendar unbanTime = bans.getUnbanCalendar(name);
		
		msg = msg.replaceAll("%server%", this.getServer().getServerName());
		if (name != null) {
			msg = msg.replaceAll("%player%", name);
		}
		
		msg = msg.replaceAll("%currenttime%", TimeFormat.format(now.getTime()));
		msg = msg.replaceAll("%currentdate%", DateFormat.format(now.getTime()));
		
		if (unbanTime != null) {
			msg = msg.replaceAll("%unbantime%", TimeFormat.format(unbanTime.getTime()));
			msg = msg.replaceAll("%unbandate%", DateFormat.format(unbanTime.getTime()));
			msg = msg.replaceAll("%bantimeleft%", Bans.buildTimeDifference(now, unbanTime));
		}
		return msg;
	}
}
