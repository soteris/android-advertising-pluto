package edu.illinois.seclab.android.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.lang.Exception;


public class DataParser {

	private static int numOfDevices = 0;

	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************* EXTRACT THE APPS FROM THE DATASET  *****************************************/
	/**************************************************************************************************/
	/**
	 * Scans all files, and gets the lists of apps if available. Stores the list per device in 
	 * 	the <b>'to'</b> file
	 * @param from The directory where the device files are stored
	 * @param to The directory to store the apps per device
	 * @param lineIdentifier String to identify the apps datapoint
	 * @param appDelimiter Delimiter separating apps
	 * @param withinAppDelimiter Delimiter separating info within an app string
	 * @param hs_blacklist 
	 * @return true on success, false on failure
	 */
	public static boolean filter(String from, String to, String lineIdentifier,
			String appDelimiter, String withinAppDelimiter, HashSet<String> hs_blacklist) {

			File[] devfileNames = new File(from).listFiles(new FilenameFilter() {				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("gz");
				}
			});
		
			
			Report.filesScanned += devfileNames.length;
			
	
			for(File file : devfileNames){
				//For each file get the largest list of installed apps
				Device device = new Device(file);
				String appsLine = "";
				boolean noAppList = true;
	
				try {
					BufferedReader inBufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)),"UTF-8"));
					Log.debug("Inspecting file " + file.getName() + " in " + from + "...");

					for(String line = inBufferedReader.readLine(); line !=null; 
							line = inBufferedReader.readLine()){
			
						if(line.contains(lineIdentifier)){
							//update device list of installed apps if necessary
							appsLine = line;
							noAppList = false;
						}
						else{
							//app|installed not present
							continue;
						}

					}
		
					inBufferedReader.close();
		
					if(noAppList){
						Report.filesNoapp++;
						continue;
					}
		
					Log.debug("Finished scanning file. Extracting apps from datapoint " 
							+ Preferences.lineIdentifier + "...");
					DataParser.findCandidateAppSet(appsLine, lineIdentifier, 
									appDelimiter, withinAppDelimiter, device, hs_blacklist);
				
					if(device == null || device.getAppSetSize() < 1){
						Log.error("No apps for file " + file.getName());
					}
//					else{
//						Report.userAppsFoundTotal += device.getAppSetSize();
//					}
				
					//store app list for this device
					if(storeAppList(device, to + Preferences.outAppsFile) == Utils.FAILURE){
						Log.error("Could not store apps for device file " + file);
					}
					else{
						Report.userAppsStoredTotal += device.getAppSetSize();
					}
		
					Report.filesSuccessfullyScanned++;
		
				} catch (FileNotFoundException e) {
					// File not found
					e.printStackTrace();
					Report.filesFailedToScanned++;
				} catch (IOException e) {
					// IO Exception
					e.printStackTrace();
					Report.filesFailedToScanned++;
				}

			}
					

					
			
			
			return Utils.SUCCESS;
	}
	

	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** FIND APPS OF A DEVICE *****************************************/
	/**************************************************************************************************/

	/**
	 * Stores the app names of the apps for the current datapoint in the given "device" object
	 * @param line A String line with all the attributes for this datapoint
	 * @param lineIdentifier
	 * @param appDelimiter
	 * @param withinAppDelimiter
	 * @param device A Device object to be filled with a Hashset of apps installed on the device
	 * @param hs_blacklist 
	 * @return
	 */
	private static boolean findCandidateAppSet(String line, 
			String lineIdentifier, String appDelimiter, String withinAppDelimiter, 
			Device device, HashSet<String> hs_blacklist) {
		//validate input
		if(line == null || line.compareTo("") == 0){
			return Utils.FAILURE;
		}
		
		HashSet<String> tempSet = getAppSetFromLine(line, lineIdentifier, 
													appDelimiter, withinAppDelimiter, hs_blacklist);
			
		if(tempSet == null || tempSet.size() < 1){
			return Utils.FAILURE;
		}
		
		/* Prune blacklisted apps */
		//TODO:
		
		device.setAppSetUnion(tempSet);

		return Utils.SUCCESS;		
	}
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** GET APP NAMES FROM DATAPOINT **********************************/
	/**************************************************************************************************/
	/**
	 * Extracts the apps from the datapoint. Updates Pluto.hs_apps <br />
     * A comma separated list of installed applications (packages) with the market it was installed from (separated by a colon, i.e. app:market). Since version 1.1.6 the market can be"(error retrieving)" as before sometimes we were unable to collect the market and would then not register that app at all. Since version 1.1.6 there is also the PackageInfo.versionCode appended to the application package, separated by an @-symbol as well as the list of application permissions, separated by the + symbol (i.e. app@version:p1+p2+p3:market). Since version 1.2.0 the application's first install time and last update time (as defined in PackageInfo) as well as flags (as defined in ApplicationInfo), targetSdkVersion and uid are appended to the permissions, again separated by colons (i.e. app@version:p1+p2+p3:1365500000000:1365527172511:156:12:10001:market). In case any of the new values are unavailable, they are left blank. This is always the case for install and update time on API level < 9 devices.
	 * @param line
	 * @param lineIdentifier
	 * @param appDelimiter
	 * @param withinAppDelimiter
	 * @param hs_blacklist 
	 * @return
	 */
	private static HashSet<String> getAppSetFromLine(String line,
			String lineIdentifier, String appDelimiter,
			String withinAppDelimiter, HashSet<String> hs_blacklist) {
		
		HashSet<String> tempSet = new HashSet<String>();
		
		try{
			// Discard everything before the first app name
			line = line.substring(line.indexOf(lineIdentifier) + 
								Preferences.lineIdentifier.length() +
								3);
			//Split apps which are separated by ','
			String[] apps = line.split(appDelimiter);
				
			//traverse all apps and keep their names: only for version 1.2 of device analyzer
			for(String app : apps){
				if(app == null || app.isEmpty()){
					continue;
				}
			
				Report.allAppsTotal++;
			
				//Split the apps' attributes which are separated by ':'
				String[] appAttributes = app.split(Preferences.withinAppDelimiter);
			
				/* Check if ApplicationInfo flags are there */
				int flags = -1;
			
				//app@version : p1+p2+p3 : 1365500000000 : 1365527172511 : <flags> : 12 : 10001 : market
				if(appAttributes.length == 8){
					//flags are the 4th from the end attribute, COULD BE MISSING
					String str_tmpFlags = appAttributes[appAttributes.length - 4];
				
					if(str_tmpFlags != null && !str_tmpFlags.isEmpty() 
							&& str_tmpFlags.compareToIgnoreCase("null") != 0){
						try{
							flags = Integer.parseInt(str_tmpFlags);
							//Log.debug("Flags = " + flags);
							Report.appsWithFlags += 1;
						}
						catch(NumberFormatException e){
							flags = -1;
							Log.debug("Error parsing app's flags! Treating as no flags present.");
						}
					}
				}
			
				//The first one is the app name
				if(appAttributes[0] != null && !appAttributes[0].isEmpty()){
					String appName = DataParser.cleanAppName(appAttributes[0]);
				
					//keep track of system & user apps that come together
                    if(isAppName(appName)){
                        tempSet.add(appName);
                    }
				
					/* Save in Data file only if NOT in blacklist and NOT a platform app and NOT the experiment's app */
					if(!hs_blacklist.contains(appName) && !isPlatformApp(appName, flags) 
							&& !isPreInstalledApp(appName) && isAppName(appName)){
					
						//tempSet.add(appName);
					
						/* ADD IT TO THE GLOBAL HASH SET OF APPS */
						if(Pluto.hs_apps.add(appName)){
							Report.userAppsFoundTotal++;
						}
						Report.userAppsTotal++; 
					
					}
					else{
						/* REPORT STUFF */
						Report.removedAppsTotal++; 
	
						if(hs_blacklist.contains(appName)){
							Report.removedByBlacklist++;
						}
						else if(isPlatformApp(appName, flags)){
							Report.removedByFlags++;
						}
						else if(isPreInstalledApp(appName)){
							Report.removedPreInstalled++;
						}
					}
								
				}
			}
		
		}//eof try
		catch(Exception e){
			Log.error("Error reading from app|installed key! Not in expected format?");
		}	
				
		return tempSet;
	}
	


	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** FILTER UNINTERESTING APPS *************************************/
	/**************************************************************************************************/

	/**
	 * Yes if it begins with com.android or com.google or com.example
	 * @param appName
	 * @return
	 */
	private static boolean isPreInstalledApp(String appName) {
		boolean result = false;
		
		/* Consider the experiment's app as pre-installed (all devices have it) */
		if(appName.compareToIgnoreCase(Preferences.pckNameDevAnalyzer) == 0 ||
				appName.startsWith(Preferences.platformAppPrefix) ||
				appName.startsWith(Preferences.googleAppPrefix) ||
				appName.startsWith(Preferences.exampleAppPrefix) ) {
			result = true;
		}
		
		return result;
	}
	
	private static boolean isPlatformApp(String appName, int flags) {	
		boolean result = false;
		
		//DeviceAnalyze version < 1.2.0 does not have ApplicationInfo flags
		
		if(flags != -1){
			if((flags & Preferences.FLAG_SYSTEM) == 1){
				//system app
				//Log.debug("App: " + appName + ". System app found with flags.");
				result = true;
			}
		}
		else{
			//flags not available: use heuristics
			if(appName.startsWith(Preferences.platformAppPrefix)){
				result = true;
			}
		}
		
		
		return result;						
	}
    
    /**************************************************************************************************/
    /**************************************************************************************************/
    /********************************** CHECK VALID APP NAME ******************************************/
    /**************************************************************************************************/
    
    /**
     * Checks if candidate is a valid app name <br />
     * A full Java-language-style package name for the application. The name should be unique. 
     * The name may contain uppercase or lowercase letters ('A' through 'Z'), numbers, and
     * underscores ('_'). However, individual package name parts may only start with letters.
     * @param candidate
     * @return true is this is a valid the app name, false otherwise
     */
    public static boolean isAppName(String candidate) {
        
        for(int i = 0; i < candidate.length(); i++){
            char c = candidate.charAt(i);
            
            if(!isDigit(c) && !isAlphabetChar(c) && c != '_' && c != '.'){
                return false;
            }
        }
        
        return true;
        
    }
    
    /**************************************************************************************************/
    /**************************************************************************************************/
    /********************************** CHECK IF CHAR IS DIGIT ****************************************/
    /**************************************************************************************************/
  
    /**
     * Checks if a char is digit
     * @param c the character to check
     * @return true if the character is a digit
     */
    private static boolean isDigit(char c) {
        
        if(c >= '0' && c <= '9'){
            return true;
        }
        else{
            return false;
        }
    }
    
    /**************************************************************************************************/
    /**************************************************************************************************/
    /********************************** CHECK IF CHAR IS ALPHABET CHAR ********************************/
    /**************************************************************************************************/
 
    /**
     * Checks if a char is alphabet char [a-z] or [A-Z]
     * @param c the character to check
     * @return true if the character is alphabet char
     */
    private static boolean isAlphabetChar(char c) {
        
        if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
            return true;
        }
        else{
            return false;
        }
    }
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** CLEANUP APP NAME **********************************************/
	/**************************************************************************************************/

	/**
	 * Removes the "@version_num" from the end of the app String
	 * @param dirty
	 * @return A clean representation of the app name
	 */
	private static String cleanAppName(String dirty) {
		String clean = dirty;
		
		// remove spaces
		//clean.replaceAll("\\s+","");
		
		// if the version is present skip it
		int versionIndex = clean.indexOf('@');
		if(versionIndex != -1){
            //@version is present
			clean = clean.substring(0, clean.indexOf('@'));
		}
		
        clean = clean.trim();
        //if for some reason the app name contains multiple entries separated by space before @, then take the first
        String multipleEntries[] = clean.split("\\s+");
		return multipleEntries[0];
	}
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** STORE DEVICE APPS TO FILE *************************************/
	/**************************************************************************************************/

	/**
	 * Stores the set of apps for this "device" to the file "to"
	 * @param device The Device object holding the Set of apps
	 * @param to The filename of the output file
	 */
	private static boolean storeAppList(Device device, String to) {
		//validate
		if(device == null || device.getAppSetSize() == 0){
			Log.debug("No apps to store for this device!");
			return Utils.FAILURE;
		}
		
		try {
			BufferedWriter outBufferedWriter = new BufferedWriter(new FileWriter(to, true));
			
			outBufferedWriter.append(device.appSetToOutputString() + "\n");
			//DEBUG
			numOfDevices++;
			Log.debug("Wrote line " + DataParser.numOfDevices + 
					". It contains " + device.getAppSetSize() + " apps.");
			
			outBufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.error("Could not write app list for device to file!");
			e.printStackTrace();
			return Utils.FAILURE;
		}
		
		return Utils.SUCCESS;
	}

}
