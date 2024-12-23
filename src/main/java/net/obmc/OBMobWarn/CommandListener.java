package net.obmc.OBMobWarn;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CommandListener implements CommandExecutor {

	static Logger log = Logger.getLogger("Minecraft");
	private String logmsgprefix = null;
	
	OBMobWarn plugin;
	private TargetingManager targetingMgr;
	private MobTracker mobTracker;
	private ConfigManager configMgr;
	
	public CommandListener() {

		plugin = OBMobWarn.getInstance();
		targetingMgr = plugin.getTargetingManager();
		mobTracker = plugin.getMobTracker();
		configMgr = plugin.getConfgManager();
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {

		// for now only op can use the command
		if (!sender.isOp() && !(args.length == 1 && args[0].toLowerCase().equals("show"))) {
			MessagingUtils.sendChatWarning( (Player)sender,
				Component.text("Sorry, command is reserved for server operators.").color(NamedTextColor.RED)
			);
			return true;
		}

		// usage if no arguments passed
		if (args.length == 0) {
			Usage(sender);
			return true;
		}

		// process the command and any arguments
		if ( command.getName().equalsIgnoreCase("mobwarn")) {

			
			switch ( args[0].toLowerCase() ) {
			
				// toggle all hostile warning and tracking
				case "allhostile":
					configMgr.setAllHostile(!configMgr.getAllHostile());
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("Flag 'allhostile' is now " + configMgr.getAllHostile()).color(NamedTextColor.GREEN)
					);
					break;

				case "quiettime":
					if (args.length == 1) {
						MessagingUtils.sendChatWarning((Player)sender,	Component.text("Provide a value in seconds").color(NamedTextColor.RED));
						return true;
					}
					try {
						configMgr.setQuietTime(Integer.parseInt(args[1]));
						MessagingUtils.sendChatWarning((Player)sender,
							Component.text("Quiet time is now " + configMgr.getQuietTime() + " second" + (configMgr.getQuietTime() != 1 ? "s" : "")).color(NamedTextColor.GREEN)
						);
					} catch (NumberFormatException e) {
						MessagingUtils.sendChatWarning((Player)sender,	Component.text("Provide a valid value in seconds").color(NamedTextColor.RED));
						return true;
					}
					break;

				case "actionbarwarn":
					configMgr.setDoActionBarWarning(!configMgr.getDoActionBarWarning());
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("Flag 'actionbarwarn' is now " + configMgr.getDoActionBarWarning()).color(NamedTextColor.GREEN)
					);
					if (!configMgr.getDoActionBarWarning() && !configMgr.getDoChatWarning()) {
						MessagingUtils.sendChatWarning((Player)sender,
							Component.text("Both actionbar and char warnings are off! No warnings will be given!").color(NamedTextColor.GOLD)
						);
					}
					break;

				case "chatwarn":
					configMgr.setDoChatWarning(!configMgr.getDoChatWarning());
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("Flag 'chatwarn' is now " + configMgr.getDoChatWarning()).color(NamedTextColor.GREEN)
					);
					if (!configMgr.getDoActionBarWarning() && !configMgr.getDoChatWarning()) {
						MessagingUtils.sendChatWarning((Player)sender,
							Component.text("Both actionbar and char warnings are off! No warnings will be given!").color(NamedTextColor.GOLD)
						);
					}
					break;

				case "cleanupinterval":
					if (args.length == 1) {
						MessagingUtils.sendChatWarning((Player)sender,	Component.text("Provide a tick value (20 ticks per second)").color(NamedTextColor.RED));
						return true;
					}
					configMgr.setCleanupInterval(Integer.parseInt(args[1]));
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("Cleanup interval is now " + configMgr.getCleanupInterval()).color(NamedTextColor.GREEN)
					);
					break;

				case "show":
					Player player = (Player)sender;
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("Mobs tracking you:").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
					);
					int mobCount = 0;
					if ( mobTracker.getMobs().size() > 0) {
						for (UUID mobUUID : mobTracker.getMobs()) {
							Mob mob = (Mob)Bukkit.getEntity(mobUUID);
							if (mobTracker.getPlayersTrackingMob(mobUUID).contains(player.getUniqueId())) {
								if (mob != null) {
									int lastTargeted = (int) mobTracker.timeSinceLastTargetedPlayer(mobUUID, player.getUniqueId());
									MessagingUtils.sendChatWarning(player,
										Component.text(mobTracker.getMobName(mob) + "(" + mobUUID + ") " + lastTargeted + " second" + (lastTargeted != 1 ? "s" : "") + " ago").color(NamedTextColor.GOLD)
									);
									mobCount++;
								}
							}
						}
						MessagingUtils.sendChatWarning((Player)sender,
								Component.text("Total of " + mobCount + " mob" +(mobCount != 1 ? "s" : "") + " tracking you").color(NamedTextColor.GOLD)
						);
					} else {
						MessagingUtils.sendChatWarning(player,
							Component.text("No mobs are currently tracking you").color(NamedTextColor.GOLD)
						);						
					}
					break;

				// show current settings 
				case "settings":
					MessagingUtils.sendChatWarning((Player)sender, Component.text("Current settings:").color(NamedTextColor.YELLOW));
					MessagingUtils.sendChatWarning((Player)sender, Component.text("  allhostile: " + configMgr.getAllHostile()).color(NamedTextColor.YELLOW));
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("  quiettime: " + configMgr.getQuietTime() + " second" + (configMgr.getQuietTime() != 1 ? "s" : "")).color(NamedTextColor.YELLOW));
					MessagingUtils.sendChatWarning((Player)sender, Component.text("  actionbarwarn: " + configMgr.getDoActionBarWarning()).color(NamedTextColor.YELLOW));
					MessagingUtils.sendChatWarning((Player)sender, Component.text("  chatwarn: " + configMgr.getDoChatWarning()).color(NamedTextColor.YELLOW));
					MessagingUtils.sendChatWarning((Player)sender,
						Component.text("  cleanupinterval: " + configMgr.getCleanupInterval() + " tick" + (configMgr.getCleanupInterval() != 1 ? "s" : "")).color(NamedTextColor.YELLOW));
					MessagingUtils.sendChatWarning((Player)sender, Component.text("Warn list entities: ").color(NamedTextColor.YELLOW));
					if (targetingMgr.getWarnList().size() == 0) {
						MessagingUtils.sendChatWarning( (Player)sender, Component.text("  No mobs on the warn list").color(NamedTextColor.YELLOW));
					} else {
						for (String e : targetingMgr.getWarnList()) {
							MessagingUtils.sendChatWarning((Player)sender, Component.text("  " + e).color(NamedTextColor.YELLOW));
						}
					}
					break;

				// add a mob to the warn list
				case "add":
					if (args.length == 1) {
						MessagingUtils.sendChatWarning((Player)sender,
							Component.text("No entity provided. use /mobwarn add <mob_type>").color(NamedTextColor.RED));
						return true;
					}
					String addMob = args[1].toLowerCase();
					if (!plugin.getHostileMobList().contains(addMob.toUpperCase()) && !plugin.getSpecialMobList().contains(addMob.toUpperCase())) {
						MessagingUtils.sendChatWarning((Player)sender, Component.text(addMob + " is not a hostile or special mob").color(NamedTextColor.RED));
						return true;
					}
					if (!targetingMgr.getWarnList().contains(addMob)) {
						try {
							check(addMob);
						} catch (Exception e) {
							MessagingUtils.sendChatWarning((Player)sender, Component.text(addMob + " is not a valid mob").color(NamedTextColor.RED));
							return true;
						}
						targetingMgr.addWarnEntity(addMob);
						configMgr.saveWarnList();
						MessagingUtils.sendChatWarning((Player)sender, Component.text(addMob + " added to warn list").color(NamedTextColor.YELLOW));
						if (configMgr.getAllHostile()) {
							configMgr.setAllHostile(!configMgr.getAllHostile());
							MessagingUtils.sendChatWarning((Player)sender, Component.text("all hostile mob tracking disabled").color(NamedTextColor.YELLOW));
						}
					} else {
						MessagingUtils.sendChatWarning((Player)sender, Component.text(addMob + " is already on the warn list").color(NamedTextColor.RED));
					}
					break;

				// remove a mob to the warn list
				case "remove":
					if (args.length == 1) {
						MessagingUtils.sendChatWarning((Player)sender, Component.text("No entity provided. use /mobwarn remove <mob_type>").color(NamedTextColor.RED));
						return true;
					}
					String removeMob = args[1].toLowerCase();
					if (removeMob.equals("all")) {
						targetingMgr.removeAll();
						configMgr.saveWarnList();
						MessagingUtils.sendChatWarning((Player)sender, Component.text("All mobs removed from warn list").color(NamedTextColor.YELLOW));
						configMgr.setAllHostile(true);
						MessagingUtils.sendChatWarning((Player)sender, Component.text("all hostile mob tracking enabled").color(NamedTextColor.YELLOW));

					} else {
						if (targetingMgr.getWarnList().contains(removeMob)) {
							targetingMgr.removeWarnEntity(removeMob);
							configMgr.saveWarnList();
							MessagingUtils.sendChatWarning((Player)sender, Component.text(removeMob + " removed from warn list").color(NamedTextColor.YELLOW));
						} else {
							MessagingUtils.sendChatWarning((Player)sender, Component.text(removeMob + " is not on the warn list").color(NamedTextColor.YELLOW));
						}
					}
					break;

				// list up mobs on the warn list
				case "list":
					if (targetingMgr.getWarnList().size() > 0) {
						MessagingUtils.sendChatWarning((Player)sender, Component.text("Mobs on the warn list:").color(NamedTextColor.YELLOW));
						targetingMgr.getWarnList().forEach( s -> {
							MessagingUtils.sendChatWarning((Player)sender, Component.text("  " + s).color(NamedTextColor.YELLOW));
						});
					} else {
						MessagingUtils.sendChatWarning((Player)sender, Component.text("No mobs on the warn list").color(NamedTextColor.YELLOW));
					}
					break;

				case "help":
				default:
					Usage(sender);
					break;
			}
		}
		return true;
	}

	// check an entity is really an entity
	public boolean check(String entity) throws Exception {
		try {
			EntityType.valueOf(entity);
		} catch (Exception e) {
			throw new Exception("Invalid entity");
		}
		return true;
	}

    void Usage(CommandSender sender) {
    	MessagingUtils.sendChatWarning((Player)sender,
    		Component.text("/mobwarn allhostile - toggle warning for all hostile mobs").color(NamedTextColor.YELLOW)
    	);
    	MessagingUtils.sendChatWarning((Player)sender,
       		Component.text("/mobwarn actionbarwarn - toggle mob warnings to action bar").color(NamedTextColor.YELLOW)
       	);
    	MessagingUtils.sendChatWarning((Player)sender,
           	Component.text("/mobwarn chatwarn - toggle mob warnings to chat").color(NamedTextColor.YELLOW)
        );
    	MessagingUtils.sendChatWarning((Player)sender,
           	Component.text("/mobwarn quiettime - set the cooldown time in seconds on mob re-warning").color(NamedTextColor.YELLOW)
        );
    	MessagingUtils.sendChatWarning((Player)sender,
            Component.text("/mobwarn quiettime - set the time between background cleanup checks").color(NamedTextColor.YELLOW)
        );
    	MessagingUtils.sendChatWarning((Player)sender,
    	   	Component.text("    This is in server ticks (20 ticks per second)").color(NamedTextColor.YELLOW)
    	);
    	MessagingUtils.sendChatWarning((Player)sender,
           	Component.text("/mobwarn checkinterval - ").color(NamedTextColor.YELLOW)
        );
    	MessagingUtils.sendChatWarning((Player)sender,
       		Component.text("/mobwarn settings - shows current config").color(NamedTextColor.YELLOW)
       	);
    	MessagingUtils.sendChatWarning((Player)sender,
    		Component.text("/mobwarn show - show the currently targeting you").color(NamedTextColor.YELLOW)
    	);
     	MessagingUtils.sendChatWarning((Player)sender,
        	Component.text("/mobwarn add <mob_type> - add mob to warn list").color(NamedTextColor.YELLOW)
        );
    	MessagingUtils.sendChatWarning((Player)sender,
           	Component.text("/mobwarn remove <all|mob_type> - remove a mob or all mobs from warn list").color(NamedTextColor.YELLOW)
        );
   		MessagingUtils.sendChatWarning((Player)sender,
       		Component.text("/mobwarn list - list up mobs on the custom warn list").color(NamedTextColor.YELLOW)
   		);
    }
}
