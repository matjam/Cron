package net.stupendous.cron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import net.stupendous.util.Log;

import bsh.EvalError;
import bsh.Interpreter;

import com.kenai.crontabparser.*;

public class CronTask extends TimerTask {
	CronPlugin plugin;
	Log log;
	CronTask task = this;
	Interpreter i = new Interpreter();

	CronTask(CronPlugin plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
		
		// Set up some default variables in the interpreter.

		try {
			i.set("log", log);
			i.set("server", plugin.getServer());
			i.set("plugin", plugin);
			
			i.eval("addClassPath(\"plugins/Cron\"); importCommands(\"/commands\");");
		} catch (EvalError e) {
			log.severe("Unable to evaluate script: %s", e.getMessage());
		}
	}
	
	public void run() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				task.syncTask();
			}
		});
	}
	
	public void syncTask() {
		File crontab = new File("plugins/Cron/crontab");

		String line = null;
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(crontab));
			while ((line = br.readLine()) != null) {
				if (!line.matches("^\\s*#.*") && !line.matches("^\\s*$")) {
					String parts[] = line.split("\\s+",7 );
					
					if (parts.length < 6) {
						log.severe("Incorrect format for crontab entry: %s", line);
						continue;
					}
					
					CronTabExpression cronEntry = null;
					
					try {
						cronEntry = CronTabExpression.parse(String.format("%s %s %s %s %s", parts[0], parts[1], parts[2], parts[3], parts[4]));
					} catch (ParseException e) {
						log.severe("Unable to parse crontab entry: %s", e.getMessage());
						continue;
					}
					
					Calendar now = Calendar.getInstance();
					
					if (cronEntry.matches(now)) {
						log.info("Executing script: %s", parts[5]);
						try {
							i.set("arg", null);
							
							if (parts.length == 7) {
								i.set("arg", parts[6]);
							}
							i.source("plugins/Cron/scripts/" + parts[5]);
						} catch (EvalError e) {
							log.severe("Unable to execute script: %s error: %s", parts[5], e.getMessage());
						}
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			log.severe("Unable to find crontab: %s", e.getMessage());
			return;			
		} catch (IOException e) {
			log.severe("Cannot read crontab: %s", e.getMessage());
			return;
		}

		
		
	}
}
