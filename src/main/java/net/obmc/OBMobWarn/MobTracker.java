package net.obmc.OBMobWarn;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.obmc.OBMobWarn.PlayerListener.ListType;

public class MobTracker {

	static Logger log = Logger.getLogger("Minecraft");

	OBMobWarn plugin;
	private TargetingManager targetingMgr;
	private ConfigManager configMgr;

    private final Map<UUID, Map<UUID, Long>> mobTracker = new ConcurrentHashMap<>();


    public MobTracker() {

    	plugin = OBMobWarn.getInstance();
    	targetingMgr = plugin.getTargetingManager();
    	configMgr = OBMobWarn.getInstance().getConfgManager();
    }

    // process mob and player to see if they go on the tracker or not and what messages to send to the player
	public void target(Mob mob, Player target, TargetReason reason, ListType listType) {

		String mobName = getMobName(mob);

		long timeSinceLastTargeted = 0;

		if (mobTracker.containsKey(mob.getUniqueId()) && mobTracker.get(mob.getUniqueId()).containsKey(target.getUniqueId())) {
			timeSinceLastTargeted =  (System.currentTimeMillis()/1000L) - mobTracker.get(mob.getUniqueId()).get(target.getUniqueId());
		}

		mobTracker.compute(mob.getUniqueId(), (mobKey, playerMap) -> {
			if (playerMap == null) {
				playerMap = new ConcurrentHashMap<>();
			}
			playerMap.put(target.getUniqueId(), System.currentTimeMillis()/1000L);
			return playerMap;
		});

		// check whether to notify			
		if (timeSinceLastTargeted == 0 || timeSinceLastTargeted > configMgr.getQuietTime()) {
			if (listType.equals(ListType.HOSTILE)) {
				sendHostileMessage(mob, mobName, target);
			} else if ( listType.equals(ListType.SPECIAL)) {
				sendSpecialMessage(mob, mobName, target);
			}
		}
	}

	// generate a message
	private void sendHostileMessage(Mob mob, String mobName, Player target) {
		Component warnIndicator = buildMobIndicator(mob.getLocation(), target.getLocation());
		if (configMgr.getAllHostile()) {
			MessagingUtils.sendWarning(mobName, warnIndicator, target);
		} else {
			if (targetingMgr.getWarnList().contains(mobName.toLowerCase())) {
				MessagingUtils.sendWarning(mobName, warnIndicator, target);
			}
		}
	}
	private void sendSpecialMessage(Mob mob, String mobName, Player target) {
		Component warnIndicator = buildMobIndicator(mob.getLocation(), target.getLocation());
		MessagingUtils.sendWarning(mobName, warnIndicator, target);
	}

    // remove mob player tracker entry, but not the target data as that is used for historical purposes
    public void removeMob(UUID mobUUID) {
    	mobTracker.remove(mobUUID);
    }

    public boolean containsMob(UUID mobUUID) {
        return mobTracker.containsKey(mobUUID);
    }
    
    public Set<UUID> getMobs() {
    	return mobTracker.keySet();
    }
    
    public Set<UUID> getPlayersTrackingMob(UUID mobUUID) {
    	return mobTracker.get(mobUUID).keySet();
    }

    // you must check if the mob has the player before calling this externally
    public long timeSinceLastTargetedPlayer(UUID mobUUID, UUID playerUUID) {
   		return  (System.currentTimeMillis()/1000L) - mobTracker.get(mobUUID).get(playerUUID);
    }

    public String getMobName(Mob mob) {
		String mobName = mob.getType().name();
		if (mob.customName() != null) {
			Component entityCustomNameComponent = mob.customName();
			mobName = getPlainTextFromComponent(entityCustomNameComponent);
		}
		return mobName;
    }

    private String getPlainTextFromComponent(Component component) {
    	if (component instanceof TextComponent) {
    		TextComponent textComponent = (TextComponent) component;
    		StringBuilder plainText = new StringBuilder(textComponent.content());
    		for (Component child : textComponent.children()) {
    			plainText.append(getPlainTextFromComponent(child));
    		}
    		return plainText.toString();
    	}
    	return "";
    }
    
    private double getHorizontalDistance(Location mobLoc, Location playerLoc) {
    	
    	double deltaX = mobLoc.getX() - playerLoc.getX();
    	double deltaZ = mobLoc.getZ() - playerLoc.getZ();
    	return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }
    
    public static double getVerticalDistance(Location mobLoc, Location playerLoc) {

    	return mobLoc.getY() - playerLoc.getY(); 
    }
    
    private Component buildMobIndicator(Location mobLoc, Location playerLoc) {

    	Vector playerDir = new Vector(playerLoc.getDirection().getX(), 0, playerLoc.getDirection().getZ()).normalize();
    	Vector toMob = new Vector(mobLoc.getX() - playerLoc.getX(), 0, mobLoc.getZ() - playerLoc.getZ()).normalize();
    	double relativeAngle = Math.toDegrees(Math.atan2(toMob.getZ(), toMob.getX()) - Math.atan2(playerDir.getZ(), playerDir.getX()));
    	relativeAngle = (relativeAngle + 360) % 360;
    	
    	TextComponent.Builder indicator = Component.text();
    	
    	// build out horizontal part of indicator
    	int hBlocks = (int) getHorizontalDistance(mobLoc, playerLoc);
    	if ((relativeAngle >= 315 && relativeAngle < 360) || (relativeAngle >= 0 && relativeAngle < 45)) {
    		indicator.append(Component.text(hBlocks + "\u2B9D")).color(NamedTextColor.GREEN);
    	} else if (relativeAngle >= 45 && relativeAngle < 135) {
    		indicator.append(Component.text(hBlocks + "\u2B9E")).color(NamedTextColor.YELLOW);
    	} else if (relativeAngle >= 135 && relativeAngle < 225) {
    		indicator.append(Component.text(hBlocks + "\u2B9F")).color(NamedTextColor.RED);
    	} else if (relativeAngle >= 225 && relativeAngle < 315) {
    		indicator.append(Component.text(hBlocks + "\u2B9C")).color(NamedTextColor.YELLOW); 
    	} else {
    		return Component.text("??").color(NamedTextColor.RED);
    	}
    	
    	// build out vertical part of indicator
    	int vBlocks = (int) getVerticalDistance(mobLoc, playerLoc);
    	if (vBlocks == 0) {     	// mob same level as player
    		return indicator.build();
    	}
    	if ( vBlocks < 0 ) {		// mob below player
        	indicator.append(Component.text(" -" + Math.abs(vBlocks) + "\u2BC6").color(NamedTextColor.RED));
    	} else {    				// mob above player
	        	indicator.append(Component.text(" +" + vBlocks + "\u2BC5").color(NamedTextColor.GREEN));
    	}
   	
    	return indicator.build();
    }
    
    // clean up any mobs in tracker that aren't valid any longer (despawned or died for example)
    public void cleanUp() {
    	mobTracker.keySet().iterator().forEachRemaining(mobUUID -> {
    		if (Bukkit.getEntity(mobUUID) == null) {
    			mobTracker.remove(mobUUID);
    		}
    	});
    }
}