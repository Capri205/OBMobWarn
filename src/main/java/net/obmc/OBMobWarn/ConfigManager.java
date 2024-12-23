package net.obmc.OBMobWarn;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;

public class ConfigManager {

	static Logger log = Logger.getLogger("Minecraft");

	OBMobWarn plugin;
	Configuration config;
	private boolean validConfig = true;

	private boolean allHostile;
	private long quietTime;
	private boolean doActionBarWarning;
	private boolean doChatWarning;
	private int cleanupInterval;
	
	public static TargetingManager targetingMgr;
	
	private String logmsgprefix;

	public ConfigManager() {
		
		plugin = OBMobWarn.instance;
		targetingMgr = plugin.getTargetingManager();
		logmsgprefix = plugin.getLogMsgPrefix();

		plugin.saveDefaultConfig();
		config = plugin.getConfig();
		
		validConfig = loadConfig();
	}

	// load config file values or use defaults
	public boolean loadConfig() {

		allHostile = config.contains("allhostile") ? config.getBoolean("allhostile") : true;
		quietTime = config.contains("quiettime") ? config.getLong("quiettime") : 60;
		doActionBarWarning = config.contains("actionbarwarn") ? config.getBoolean("actionbarwarn") : true;
		doChatWarning = config.contains("chatwarn") ? config.getBoolean("chatwarn") : false;
		cleanupInterval = config.contains("cleanupinterval") ? config.getInt("cleanupinterval") : 40;

		if ( config.contains( "warnlist" ) ) {
			
			List<String> warnMobs = config.getStringList( "warnlist" );
			Iterator<String> wit = warnMobs.iterator();
			while( wit.hasNext() ) {
				String warnMob = wit.next();
				if ( !targetingMgr.getWarnList().contains( warnMob ) ) {
					try {
						check( warnMob );
						targetingMgr.addWarnEntity( warnMob );
					} catch (Exception e) {
							log.log(Level.INFO, logmsgprefix + "Invalid entity type in config (" + warnMob + ")");
					}
				}
			}
			if ( targetingMgr.getWarnList().size() > 0 ) {
				log.log(Level.INFO, logmsgprefix + "Warning notificaton for " + targetingMgr.getWarnList().size() +" entities");
			} else {
				log.log(Level.INFO, logmsgprefix + "No entities in the config. Not warning on specific entities");
			}

		} else {
			
			log.log(Level.INFO, logmsgprefix + "Config doesn't contain a warn mob list");
		}
		
		// load up the hostile mob list
		if ( config.contains( "hostilemobs" ) ) {

			List<String> hostileMobs = config.getStringList( "hostilemobs" );
			Iterator<String> hit = hostileMobs.iterator();
			while( hit.hasNext() ) {
				String hostileMob = hit.next();
				if ( !plugin.getHostileMobList().contains( hostileMob ) ) {
					try {
						check( hostileMob );
						plugin.getHostileMobList().add( hostileMob );
					} catch ( Exception e ) {
							log.log(Level.INFO, logmsgprefix + "Invalid entity type in hostile mobs list (" + hostileMob + ")");
					}
				}
			}
			if ( plugin.getHostileMobList().size() > 0 ) {
				log.log(Level.INFO, logmsgprefix + "Found " + plugin.getHostileMobList().size() +" hostile mobs");
			} else {
				log.log(Level.INFO, logmsgprefix + "No hostile mobs in the config");
				return false;
			}

		} else {
			log.log(Level.INFO, logmsgprefix + "Config doesn't contain a hostile mob list");
			return false;
		}

		// load up special mob list
		if ( config.contains( "specialmobs" ) ) {

			List<String> specialMobs = config.getStringList( "specialmobs" );
			Iterator<String> sit = specialMobs.iterator();
			while( sit.hasNext() ) {
				String specialMob = sit.next();
				if ( !plugin.getSpecialMobList().contains( specialMob ) ) {
					try {
						check( specialMob );
						plugin.getSpecialMobList().add( specialMob );
					} catch ( Exception e ) {
							log.log(Level.INFO, logmsgprefix + "Invalid entity type in special mobs list (" + specialMob + ")");
					}
				}
			}
			if ( plugin.getSpecialMobList().size() > 0 ) {
				log.log(Level.INFO, logmsgprefix + "Found " + plugin.getSpecialMobList().size() +" special mobs");
			} else {
				log.log(Level.INFO, logmsgprefix + "No special mobs in the config");
				return false;
			}

		} else {
			log.log(Level.INFO, logmsgprefix + "Config doesn't contain a special mob list");
			return false;
		}

		return true;
	}

	// save config
	public void saveConfig() {
		this.getConfig().set("allhostile", allHostile);
		this.getConfig().set("quiettime", quietTime);
		this.getConfig().set("actionbarwarn", doActionBarWarning);
		this.getConfig().set("chatwarn", doChatWarning);
		this.getConfig().set("cleanupinterval",  cleanupInterval);
		this.getConfig().set("warnmobs", targetingMgr.getWarnList());
	}

	// check a mob is really a bukkit entity
	public boolean check(String mob) throws Exception {
		try {
			EntityType.valueOf(mob);
		} catch (Exception e) {
			throw new Exception("Invalid mob");
		}
		return true;
	}

	public Configuration getConfig() {
		return config;
	}
	public boolean isConfigValid() {
		return validConfig;
	}
	
 	public boolean getAllHostile() {
		return allHostile;
	}
	public void setAllHostile(Boolean mode) {
		allHostile = mode;
		config.set("allhostile", allHostile);
		plugin.saveConfig();
	}
	public long getQuietTime() {
		return quietTime;
	}
	public void setQuietTime(long time) {
		quietTime = time;
		config.set("quiettime", quietTime);
		plugin.saveConfig();
	}
	public boolean getDoActionBarWarning() {
		return doActionBarWarning;
	}
	public void setDoActionBarWarning(boolean doActionBar) {
		doActionBarWarning = doActionBar;
		config.set("actionbarwarn", doActionBarWarning);
		plugin.saveConfig();
	}
	public boolean getDoChatWarning() {
		return doChatWarning;
	}
	public void setDoChatWarning(boolean doChat) {
		doChatWarning = doChat;
		config.set("chatwarn", doChatWarning);
		plugin.saveConfig();
	}
	public int getCleanupInterval() {
		return cleanupInterval;
	}
	public void setCleanupInterval(int intervalTicks) {
		cleanupInterval = intervalTicks;
		config.set("cleanupinterval", cleanupInterval);
		plugin.saveConfig();
	}
	public void saveWarnList() {
		config.set("warnlist", plugin.getTargetingManager().getWarnList());
		plugin.saveConfig();
	}
}
