package edu.illinois.seclab.android.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Utils {

	public static final boolean SUCCESS = true;
	public static final boolean FAILURE = false;
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/**************************** CONVERT A SET OF STRING TO AN ARRAY OF STRINGS **********************/
	/**************************************************************************************************/
	/**
	 * Create a String array from HashSet<String>
	 * @param hs_apps
	 * @return
	 */
	public static String[] setToArray(HashSet<String> hs_apps) {
		String[] res = new String[hs_apps.size()];
		
		Iterator<String> iter = hs_apps.iterator();
		
		int i = 0;
		
		while(iter.hasNext()){
			res[i] = (String) iter.next();
			i++;
		}
		
		return res;
	}

	/**************************************************************************************************/
	/**************************************************************************************************/
	/************************************** WRITE A STRING TO A FILE **********************************/
	/**************************************************************************************************/
	/**
	 * 
	 * @param apriori
	 * @param filename
	 * @return
	 */
	public static boolean writeStringToFile(String out, String filename) {
		
		BufferedWriter outBufferedWriter;
		try {
			outBufferedWriter = new BufferedWriter(new FileWriter(filename));
			
			outBufferedWriter.write(out);
			outBufferedWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return Utils.FAILURE;
		}
		
		return Utils.SUCCESS;
		
	}
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/**********************************  GET DIRS FROM DIR ********************************************/
	/**************************************************************************************************/

	/**
	 * Finds all files with the provided <i>extension</i> in <i>from</i> directory
	 * @param from The directory to scan
	 * @param extension Gets only the files with this extension. Do not provide the dot.
	 * @return a File[] array with all the files in 'from' directory, or null if error
	 */
	public static ArrayList<File> getDirectories(String from) {
		File folder = new File(from);
		File[] listOfDirs = folder.listFiles();
		ArrayList<File> out = new ArrayList<File>();
		
		try{
			for (int i = 0; i < listOfDirs.length; i++) {

				if(listOfDirs[i].isDirectory() && (listOfDirs[i].getName().compareTo(".") != 0) 
					&& (listOfDirs[i].getName().compareTo("..") != 0)
					&& (listOfDirs[i].getName().compareTo(".svn") != 0)){
					
						out.add(listOfDirs[i]);
				}
			}
		}
		catch (NullPointerException e){
			Log.error("No directories found in dir: " + from);
			out = null;
		}
		
		return out; //could be null
	}

	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** GET FILES FROM DIR ********************************************/
	/**************************************************************************************************/

	/**
	 * Finds all files with the provided <i>extension</i> in <i>from</i> directory
	 * @param from The directory to scan
	 * @param extension Gets only the files with this extension. Do not provide the dot.
	 * @return a File[] array with all the files in 'from' directory, or null if error
	 */
	public static ArrayList<File> getFiles(String from, String extension) {
		File folder = new File(from);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> out = new ArrayList<File>();
		
		Log.debug("Looking for files in " + from);
		
		try{
			for (int i = 0; i < listOfFiles.length; i++) {
				//Log.debug("Found file " + listOfFiles[i].getName());
				String ext = listOfFiles[i].getName();
				if(listOfFiles[i].isFile() && ext.substring(ext.indexOf('.')).compareTo("." + extension) == 0){
					out.add(listOfFiles[i]);
				}
			}
		}
		catch (NullPointerException e){
			Log.error("No files found in dir: " + from);
			out = null;
		}
		
		return out; //could be null
	}
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** CLEANUP PLUTO's DIRECTORY *************************************/
	/**************************************************************************************************/
	
	/**
	 * Remove files generated from previous runs of the tool
	 */
	public static void cleanup() {
		// TODO: Remove ./*.txt, and ./*.arff
		
		/* Remove txt files */
		ArrayList<File> filesTXT = Utils.getFiles(".", "txt");
		
		for(File file : filesTXT){
			/* Do not delete the blacklist file */
			if(file.getName().compareTo(Preferences.fn_blacklist.substring(2)) == 0){
				continue;
			}
			/* delete all other txt files */
			if(!file.delete()){
				Log.debug("Could not delete: " + file.getPath());
			}
		}
		
		/* Remove arff files */
		ArrayList<File> filesARFF = Utils.getFiles(".", "arff");
		
		for(File file : filesARFF){
			if(!file.delete()){
				Log.debug("Could not delete: " + file.getPath());
			}
		}
	}

	/**************************************************************************************************/
	/**************************************************************************************************/
	/************************************** LOAD BLACKLIST ********************************************/
	/**************************************************************************************************/
	/**
	 * 
	 * @param fn_blacklist
	 * @return
	 */
	public static HashSet<String> loadBlacklist(String fn_blacklist) {
		HashSet<String> hs_blacklist = new HashSet<String>();
		
		try {
			BufferedReader in_bufferedReader = new BufferedReader(new FileReader(Preferences.fn_blacklist));
			
			String line = "";
			while( (line = in_bufferedReader.readLine()) != null){
				hs_blacklist.add(line.trim());
			}
			
			in_bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.warning("FAILED TO LOAD BLACKLIST! Could not find file!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.warning("FAILED TO LOAD BLACKLIST! Could not read line!");
		}
		
		
		return hs_blacklist;
	}

	/**************************************************************************************************/
	/**************************************************************************************************/
	/************************************** GET PROGRAM PARAMS ****************************************/
	/**************************************************************************************************/
	/**
	 * Validate the program's parameters
	 * @param args The programs parameters
	 * Fills up Preferences.inDir: to store the directory holding the csv input files
	 * Fills up Preferences.mode: to store the mode of operation. 0 for Apriori, 1 for FPGrowth
	 * @return true on success
	 */
	public static boolean getInput(String[] args) {
		boolean dirGiven = false;
		
		/* DEFAULT */
		Preferences.inDir = Preferences.DEFAULT_INDIR;
		Preferences.mode = Preferences.FPGROWTH;
		Preferences.DATA_ANALYSIS = Preferences.YES;
		
		if(args == null || args.length == 0){
			Menu.help();
			return Utils.FAILURE;
		}
		if(args.length % 2 != 0){
			//accept only pairs of command identifiers and values
			Log.error("Odd number of inputs!");
			Menu.help();
			return Utils.FAILURE;
		}
		
		/* OVERWRITE DEFAULT OPTIONS IF PROVIDED BY THE USER */
		for(int i = 0; i < args.length; i++){
			String arg = args[i].trim().toUpperCase();
			//Log.debug("args[" + i + "] = " + args[i]);
			
			switch(arg){
				case Preferences.OPTION_INDIR: //-D
					/* Get the directory holding the device files. Mandatory! */
					Preferences.inDir = args[++i];
					dirGiven = true;
					break;
				case Preferences.OPTION_MODE: //-M
					/* Get the mode of operation */
					try{
						Preferences.mode = Integer.parseInt(args[++i]);
					}
					catch(NumberFormatException e){
						Log.error("Could not read mode value from -M command! Not an integer!");
						Menu.help();
						return Utils.FAILURE;
					}
					break;
				case Preferences.OPTION_RUNALGO: //-A
					/* Get the command to run the association algo or not */
					try{
						Preferences.DATA_ANALYSIS = Integer.parseInt(args[++i]);
						if( Preferences.DATA_ANALYSIS != Preferences.NO && 
								Preferences.DATA_ANALYSIS != Preferences.YES ){
							throw new NumberFormatException("Option RunAlgo neither 0 nor 1!");
						}
					}
					catch(NumberFormatException e){
						Log.error("Could not read mode value from -M command! Not an integer!");
						Menu.help();
						return Utils.FAILURE;
					}
					break;
				default:
					Log.error("Unknown command!");
					Menu.help();
					return Utils.FAILURE;
			}
		}
		
		if(dirGiven){
			return Utils.SUCCESS;
		}
		else{
			return Utils.FAILURE;
		}
		
	}
	
}
