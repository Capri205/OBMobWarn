package net.obmc.OBMobWarn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;

public class PlayerListener implements Listener {

	static Logger log = Logger.getLogger("Minecraft");
	
	OBMobWarn plugin;
	
	private static MobTracker mobTracker = null;

	public enum ListType { HOSTILE, SPECIAL	}

	public PlayerListener() {

		plugin = OBMobWarn.getInstance();
		mobTracker = plugin.getMobTracker();
	}

	// determine if event is one we want to track
	private boolean trackerEvent(Entity entity, LivingEntity target, TargetReason reason) {
		// process event only if:
		//    - target is player
		//    - event entity is a mob
		//    - the entity isn't already being tracked
		//    - it's the event type we are looking for
		//    - the last targeted time difference is greater than our limit
		if ( !(target instanceof Player) || !(entity instanceof Mob)
				&& ( !reason.equals(TargetReason.CLOSEST_PLAYER) ||
					 !reason.equals(TargetReason.TARGET_ATTACKED_ENTITY) ||
					 !reason.equals(TargetReason.TARGET_ATTACKED_NEARBY_ENTITY))) {
			return false;
		}
		
		// let's not warn when a player attacks a mob. Mob will likely
		// (re)target the player immediately after and that will give a warning
		if ( target instanceof Player && (reason.equals(TargetReason.TARGET_ATTACKED_ENTITY) || reason.equals(TargetReason.TARGET_ATTACKED_ENTITY))) {
			return false;
		}
		return true;
	}

	// catch mob targeting player events
	@EventHandler
	public void onMobTargetLE(EntityTargetLivingEntityEvent event) {

		// check this event qualifies
		if (!trackerEvent(event.getEntity(), event.getTarget(), event.getReason())) {
			return;
		}

        // hostile mob and natural targeting (we don't warn if player initiates the attack)
		if (plugin.getHostileMobList().contains( event.getEntity().getType().name())) {
			mobTracker.target((Mob)event.getEntity(), (Player)event.getTarget(), event.getReason(), ListType.HOSTILE);
		}
		// player induced targeting with a special mob
		if (plugin.getSpecialMobList().contains(event.getEntity().getType().name()) && event.getReason().equals(TargetReason.TARGET_ATTACKED_ENTITY)) {
			mobTracker.target((Mob)event.getEntity(), (Player)event.getTarget(), event.getReason(), ListType.SPECIAL);
		}
	}

	// catch mobs dying
	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (mobTracker.containsMob(entity.getUniqueId())) {
			mobTracker.removeMob(entity.getUniqueId());
		}
	}
	
	// catch mobs despawning
	@EventHandler
	public void onMobDespawn(EntityRemoveFromWorldEvent event) {
		Entity entity = event.getEntity();
		if (mobTracker.containsMob(entity.getUniqueId())) {
			mobTracker.removeMob(entity.getUniqueId());
		}
	}
}
