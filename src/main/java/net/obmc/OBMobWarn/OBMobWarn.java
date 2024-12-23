package net.obmc.OBMobWarn;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class OBMobWarn extends JavaPlugin {

	static Logger log = Logger.getLogger("Minecraft");

	public static OBMobWarn instance;
	private ConfigManager configMgr;
    private PlayerListener playerlistener;
	public TargetingManager targetingMgr;
	private MobTracker mobTracker;

	private List<String> hostileMobList = new ArrayList<String>();
	private List<String> specialMobList = new ArrayList<String>();

	private static String plugin = "OBMobWarn";
	private static String pluginprefix = "[" + plugin + "]";
	private static String logmsgprefix = pluginprefix + " » ";

    public OBMobWarn() {
    	instance = this;
    }

    public static OBMobWarn getInstance() {
    	return instance;
    }
    
    public void onEnable() {

    	targetingMgr = new TargetingManager();

    	// config
    	configMgr = new ConfigManager();
    	if ( !configMgr.isConfigValid() ) {
			getServer().getPluginManager().disablePlugin(this);
			return;
    	}

    	mobTracker = new MobTracker();
    	new MobTrackerChecker();

        // event listener for player events
		this.playerlistener = new PlayerListener();
        this.getServer().getPluginManager().registerEvents((Listener)this.playerlistener, (Plugin)this);

        // event listener for commands
        this.getCommand("mobwarn").setExecutor(new CommandListener());

        // start up our mob tracker checker task which will clean down the tracker
        // of any mobs that no longer exist
        BukkitTask checkPopulation = new MobTrackerChecker().runTaskTimer(
        		this,
				configMgr.getConfig().getLong("cleanupinterval"),
				configMgr.getConfig().getLong("cleanupinterval")
		);
		log.log(Level.INFO, getLogMsgPrefix() + "Cleanup initialized (taskid " + checkPopulation.getTaskId() + ")");
    }
    
    // save config disable the plugin
    public void onDisable() {
    	if (configMgr.isConfigValid()) {
    		configMgr.saveConfig();;
    	}
    }

	public List<String> getHostileMobList() {
		return this.hostileMobList;
	}
	public List<String> getSpecialMobList() {
		return this.specialMobList;
	}

	public ConfigManager getConfgManager() {
		return configMgr;
	}
	public TargetingManager getTargetingManager() {
		return targetingMgr;
	}
	public MobTracker getMobTracker() {
		return mobTracker;
	}

	// consistent messaging
	public static String getPluginName() {
		return plugin;
	}
	public static String getPluginPrefix() {
		return pluginprefix;
	}
	public String getLogMsgPrefix() {
		return logmsgprefix;
	}
}