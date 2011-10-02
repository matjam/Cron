package net.stupendous.cron;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.stupendous.util.Log;
import net.stupendous.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bsh.EvalError;
import bsh.Interpreter;

/* This plugin isn't really intended to be used from the CLI. */

public class CronCommand implements CommandExecutor {
	private final CronPlugin plugin;
	private final Log log;
	
	
	public CronCommand(CronPlugin plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}

	enum SubCommand {
		VERSION,
		UNKNOWN;
		
		private static SubCommand toSubCommand(String str) {
			try {
				return valueOf(str);
			} catch (Exception ex) {
				return UNKNOWN;
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {

		}
				
    	if (args.length == 0) {
			replyError(sender, "Not enough arguments to command. Use /%s help to list available commands.", command.getLabel());
			return true;
    	}
    	
    	switch (SubCommand.toSubCommand(args[0].toUpperCase())) {
	    	case VERSION:
	    		reply(sender, "You are running &dCron &fversion &d%s&f.", plugin.pdf.getVersion());
	    		break;
	    	case UNKNOWN:
	    		replyError(sender, "Unknown command. Use /%s help to list available commands.", command.getLabel());
    	}
    	
		return true;
	}

    private void replyError(CommandSender sender, String format, Object... args) {
    	String msg = String.format(format, args);
    	String formattedMessage = Util.parseColor("&2[&a%s&2] &c%s", plugin.pluginName, msg); 
    	
    	if (sender == null) {
    		log.info(formattedMessage);
    	} else {
    		sender.sendMessage(formattedMessage);
    	}
    }
    
    private void reply(CommandSender sender, String format, Object... args) {
    	String msg = String.format(format, args);
    	String formattedMessage = Util.parseColor("&2[&a%s&2] &f%s", plugin.pluginName, msg); 
    	
    	if (sender == null) {
    		log.info(formattedMessage);
    	} else {
    		sender.sendMessage(formattedMessage);
    	}
    }
    

}