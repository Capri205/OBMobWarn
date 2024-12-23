package net.obmc.OBMobWarn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.scheduler.BukkitRunnable;

public class MobTrackerChecker extends BukkitRunnable {

	static Logger log = Logger.getLogger("Minecraft");

	OBMobWarn plugin;
	MobTracker mobTracker;

	public MobTrackerChecker() {

		plugin = OBMobWarn.getInstance();
		mobTracker = plugin.getMobTracker();
	}

	@Override
	public void run() {
		mobTracker.cleanUp();
	}

}
