package edu.illinois.seclab.android.tools;

import java.util.HashSet;

/**
 * Android app - frequent pattern mining
 * @author soteris
 *
 */
public class Pluto {

	/** The number of unique apps: we are using their names as the dataset's attributes */
	public static Integer attributes = 0;
	/** A set of unique apps across all users */
	public static HashSet<String> hs_apps = new HashSet<String>();
	
	
	public static void main(String[] args) {
		
		Log.debug("----------GET PROGRAM PARAMETERS----------");
		if(!Utils.getInput(args)){
			System.exit(0);
		}
		
		Log.debug("----------CLEANUP----------");
		Utils.cleanup();
		
		Log.debug("----------LOAD BLACKLIST OF PRE_INSTALLED APPS----------");
		HashSet<String> hs_blacklist = Utils.loadBlacklist(Preferences.fn_blacklist);
		
		Log.debug("----------DATA PREPROCESSING----------");
		/* Find all unique apps in dataset */
		Log.debug("Collecting apps from device files...");
		if(!DataParser.filter(Preferences.inDir, Preferences.outAppsDir,
				Preferences.lineIdentifier, Preferences.appDelimiter, 
				Preferences.withinAppDelimiter, hs_blacklist)){
			System.exit(0);
		}

		Log.debug("Writing arff data file header...");
		/* Write found attributes in data.arff */
		String[] apps = Utils.setToArray(hs_apps);
		ARFF.writeHeader(apps, Preferences.mode);
		
		Log.debug("Writing arff data file actual data...");
		/* Create data entries per device in data.arff */
		//if(!ARFF.writeDataBinary(hs_apps, apps, Preferences.bin_arffPath)){
		if(!ARFF.writeData(hs_apps, apps, Preferences.mode)){
			Log.error("Aborting!");
			System.exit(0);
		}
		Log.debug("DONE writing data to arff file.");
		
		/* Write debugging statistics */
		Log.debug("Generating Report.");
		if(!Report.generateReport()){
			Log.debug("Failed to generate analytics report.");
		}
		
		if(Preferences.DATA_ANALYSIS == Preferences.YES){
			Log.debug("----------DATA ANALYSIS----------");
			long start = System.currentTimeMillis();
			/* Run Weka apriori or fpgrowth on arff file */
			if(!DataAnalysis.runAlgo(Preferences.mode)){
				Log.debug("Association algo failed!");
			}
			long timeMS = System.currentTimeMillis() - start;
			Log.debug("Data Analysis took " + timeMS + " milliseconds.");
		}
		Log.debug("DONE!");
	}

}
