package edu.illinois.seclab.android.tools;

import java.io.File;
import java.util.HashSet;

/**
 * Device Data Structure
 * @author soteris
 *
 */
public class Device {
	
	/** The file were this device's data is stored. */
	private File file = null;
	/** The apps installed on this device. */
	private HashSet<String> hs_apps;

	public Device(File file) {
		this.file = file;
		hs_apps = new HashSet<String>();
	}
	
	public File getFile(){
		return this.file;
	}
	
	/**
	 * Overwrites previous set if any
	 * @param tempSet
	 */
	public void setAppSet(HashSet<String> tempSet) {
		this.hs_apps = tempSet;
	}
	
	/**
	 * Union tempSet with the previous set if any
	 * @param tempSet
	 * @return
	 */
	public boolean setAppSetUnion(HashSet<String> tempSet) {
		return this.hs_apps.addAll(tempSet);
		
	}
	
	public HashSet<String> getAppSet() {
		return this.hs_apps;
	}
	
	public int getAppSetSize() {
		return this.hs_apps.size();
	}

	/**
	 * Converts the hashset of apps into a string representation
	 * @return
	 */
	public String appSetToOutputString() {
		String result = "";
		
		for(String package_name : hs_apps){
			result += package_name + ",";
		}
		
		return result.substring(0, result.lastIndexOf(','));
	}


}
