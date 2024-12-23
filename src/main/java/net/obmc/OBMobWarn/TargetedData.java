package net.obmc.OBMobWarn;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class TargetedData {

	private UUID playerUUID;
	private String playerName;
	private long targetedTime;
	private EntityType mobType;

	public TargetedData(UUID uuid, String name, long time, EntityType type) {

		this.playerUUID = uuid;
		this.playerName = name;
		this.targetedTime = time;
		this.mobType = type;
	}
	
	public UUID getPlayerUUID() {
		return this.playerUUID;
	}
	public void setPlayerUUID(UUID uuid) {
		this.playerUUID = uuid;
	}
	public String getPlayerName() {
		return this.playerName;
	}
	public void setPlayerName(@NotNull String name) {
		this.playerName = name;
	}
	long getTargetedTime() {
		return this.targetedTime;
	}
	long getTimeSinceTargeted() {
		return (System.currentTimeMillis()/1000L) - this.targetedTime;
	}
	void resetTargetedTime() {
		this.targetedTime = System.currentTimeMillis()/1000L;
	}
	EntityType getMobType() {
		return this.mobType;
	}
}