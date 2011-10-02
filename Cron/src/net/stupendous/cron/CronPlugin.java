package net.stupendous.cron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;

import net.stupendous.util.Log;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CronPlugin extends JavaPlugin {
	protected PluginDescriptionFile pdf = null;
	protected String pluginName = null;
	protected Log log = null;
	protected Timer timer = null;
	protected BukkitScheduler scheduler = null;
	protected CronTask task = null;
	
    public void onEnable() {
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log = new Log(pluginName);
        
       
    	CommandExecutor cronCommandExecutor = new CronCommand(this);
    	getCommand("cron").setExecutor(cronCommandExecutor);
    	
    	setTimer();
    	
        log.info("Version %s enabled.", pdf.getVersion());
    }
    
    public void onDisable() {
    	if (pdf == null)
    		return;

		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
    	
        log.info("Version %s disabled.", pdf.getVersion());
    }
    
    protected void setTimer() {
    	// Sets the timer to the start of the next minute.
    	
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
    	
   		timer = new Timer();
    	
    	Calendar now = Calendar.getInstance();
    	now.set(Calendar.SECOND, 0);
    	now.add(Calendar.MINUTE, 1);
    	
    	task = new CronTask(this);
    	
    	// Run the cron every minute.
    	
    	try {
    		timer.scheduleAtFixedRate(task, now.getTime(), 60000);
    	} catch (Exception e) {
    		log.severe("Unable to schedule Cron timer.");
    	}

    }
}

