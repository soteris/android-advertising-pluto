package edu.illinois.seclab.android.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * General statistics
 * @author soteris
 *
 */
public class Report {

	/** Total number of csv files scanned.  */
	public static int filesScanned = 0;
	/** Total number of csv files successfully scanned. */
	public static int filesSuccessfullyScanned = 0;
	/** Total number of csv files failed to be scanned. <br /> 
	 * Should be: filesScanned-filesSuccessfullyScanned  */
	public static int filesFailedToScanned = 0;
	/** Total number of apps across all users. Not unique. */
	public static int allAppsTotal = 0;
	/** Total number of unique interesting apps across all users. */
	public static int userAppsFoundTotal = 0;
	/** Total number of unique interesting apps successfully stored across all users. */
	public static int userAppsStoredTotal = 0;
	/** Total number of apps found that the flag attribute is available. */
	public static int appsWithFlags = 0;
	/** Total number of interesting apps found. The apps are not necessarily unique. */
	public static int userAppsTotal = 0;
	/** Total number of removed apps. These could be system apps or pre-installed apps. */
	public static int removedAppsTotal = 0;
	/** No apps revealed*/
	public static int filesNoapp = 0;
	/** No of apps removed by blacklist */
	public static int removedByBlacklist = 0;
	/** No of apps removed by flags */
	public static int removedByFlags = 0;
	/** No of apps removed because is preinstalled. */
	public static int removedPreInstalled = 0;
	
	
	
	/**
	 * 
	 */
	public static boolean generateReport() {
		
		try {
			BufferedWriter outBufferedWriter = new BufferedWriter(new FileWriter(Preferences.reportFile));
		
			outBufferedWriter.write("Total number of csv files scanned: " + filesScanned + "\n");
			outBufferedWriter.write("Total number of csv files with no list of apps: " + filesNoapp + "\n");
			outBufferedWriter.write("Total number of csv files successfully scanned: " 
					+ filesSuccessfullyScanned + " \n");
			outBufferedWriter.write("Total number of csv files failed to be scanned. "
					+ " (Should be: filesScanned-filesSuccessfullyScanned): " 
					+ filesFailedToScanned + " \n");
			
			outBufferedWriter.write("---------------------------------------------------------\n");
			
			outBufferedWriter.write("Total number of apps "
					+ "across all users (not unique): " 
					+ allAppsTotal + " \n");
			outBufferedWriter.write("Sum of per-user-interesting apps successfully "
					+ "stored: " 
					+ userAppsStoredTotal + " \n");
			outBufferedWriter.write("Total number of unique interesting apps "
					+ "across all users (attributes): " 
					+ userAppsFoundTotal + " \n");
			
			outBufferedWriter.write("---------------------------------------------------------\n");
			
			outBufferedWriter.write("Total number of apps found that the flag attribute is available: " 
					+ appsWithFlags + " \n");
			outBufferedWriter.write("Total number of removed apps. "
					+ "(These could be system apps or pre-installed apps): " 
					+ removedAppsTotal + " \n");
			outBufferedWriter.write("Total number of removed apps (blacklist): " 
					+ removedByBlacklist + " \n");
			outBufferedWriter.write("Total number of removed apps (flags): " 
					+ removedByFlags + " \n");
			outBufferedWriter.write("Total number of removed apps (pre-installed): " 
					+ removedPreInstalled + " \n");
			outBufferedWriter.write("Total number of interesting apps found. "
					+ "(The apps are not necessarily unique): " 
					+ userAppsTotal + " \n");
			
			outBufferedWriter.write("---------------------------------------------------------\n");
			
			
			
			outBufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Utils.FAILURE;
		}
		
		return Utils.SUCCESS;
		
	}
	
	
	

}
