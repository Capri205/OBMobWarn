package net.obmc.OBMobWarn;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class TargetingManager {

	static Logger log = Logger.getLogger("Minecraft");

	private static List<String> warnList = null;
	private String logmsgprefix = null;
	
	// a map of entities and their setting
	public TargetingManager() {
		warnList = new ArrayList<String>();
		logmsgprefix = OBMobWarn.getInstance().getLogMsgPrefix();
	}
	
	// check if entity is on warn list
	public Boolean exists( String entity ) {
		if ( warnList.contains( entity ) ) {
			return true;
		}
		return false;
	}

	// check if entity is on the warn list
	public boolean onWarnList( String entity ) {
		 return warnList.contains( entity );
	}

	// add/remove entity on the warn list
	public void addWarnEntity( String entity ) {
		if ( !warnList.contains( entity ) ) {
			warnList.add( entity );
		}
	}
	public void removeWarnEntity( String entity ) {
		if ( warnList.contains( entity ) ) {
			warnList.remove( entity );
		}
	}
	public void removeAll() {
		warnList.clear();
	}

	// return the current warn list
	public List<String> getWarnList() {
		return warnList;
	}
}