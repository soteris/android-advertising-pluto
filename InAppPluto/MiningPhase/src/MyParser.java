import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;


public class MyParser {

	public static String currentCategory = "";
	
	public static HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> parse(String rootDir, 
			HashMap<String, HashSet<String>> permissionOpportunitiesMap, 
			HashMap<String, HashSet<String>> opportunities) {
		// TODO Auto-generated method stub
		
		/**
		 * HashMap<"category name", HashMap<apk name, HashSet<opportunity name>>>
		 */
		HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> categoryResult = new HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>>(); 
		
		/*
		 * For each category directory
		 * 		For each app
		 * 			If .xml -> xmlParse
		 * 			else if .db || .sqlite -> dbParse
		 * 			else if .json -> JsonParser
		 * 			else -> No parser for this file
		 */
		
		File[] f_categories = new File(rootDir).listFiles();
		
		/* FOR EVERY CATEGORY */
		for(File f_category : f_categories){
			if(f_category.isDirectory()){
				//this is a category directory. It contains apk directories
				HashMap<String, ArrayList<FoundOpportunity>> apksInCategory = new HashMap<String, ArrayList<FoundOpportunity>>();
				
				MyParser.currentCategory = f_category.getName().trim().toLowerCase();
				
				/* FOR EVERY APK DIRECTORY */
				File[] f_apkDirs = f_category.listFiles();
				for(File f_apkDir : f_apkDirs){
					
					if(f_apkDir.isDirectory() && !f_apkDir.getName().startsWith(".")){
						// in the apk dir: get interesting files
						
						String apkName = f_apkDir.getName();
						ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
						
						/* EXTRACTED FILES */
						File[] files = f_apkDir.listFiles();
						
						/* Holds found Opportunities for every file */
						ArrayList<ArrayList<FoundOpportunity>> rawApkAttributes = new ArrayList<ArrayList<FoundOpportunity>>();
						for(File file : files){
							
							String fname = file.getName();
							ArrayList<FoundOpportunity> rawAttributes = new ArrayList<FoundOpportunity>();
							
							if(file.isFile() && fname.contains(".") && !fname.startsWith(".")){		
								switch (fname.substring(fname.lastIndexOf('.')) ) {
								case ".dump":
									//db file
									rawAttributes = DBParser.parse(file, InAppAttributeExtractor.dict);
									break;
								case ".xml":
									//xml file
									rawAttributes = XMLParser.parse(file, InAppAttributeExtractor.dict);
									break;
								case ".json":
									//json file
									//rawAttributes = JSONParser.parse(file);
									break;
								case ".txt":
									//rawAttributes = TXTParser.(file);
									break;
								default:
									break;
								}
							
							}
							else if(file.isDirectory() && fname.compareTo("DECOMPILED") == 0){
								//Find and Check the manifest file
								File[] f_decompileDirs = file.listFiles();
								if(f_decompileDirs.length == 1){
									//TODO: Check manifest for permissions that can be mapped to attributes
									//rawAttributes = ManifestParser.parse(f_manifest);
									File[] f_apkDecDir = f_decompileDirs[0].listFiles();
									for(File potentialManifest : f_apkDecDir){
										if(potentialManifest.getName().compareTo("AndroidManifest.xml") == 0){
											//Found Manifest file
											rawAttributes = ManifestParser.parse(potentialManifest, InAppAttributeExtractor.permissionOpportunitiesMap);
											break;
										}
									}
								}
								else{
									InAppAttributeExtractor.logger.debug("Could not locate manifest file for " + f_apkDir.getName());
								}
							}
							
							rawApkAttributes.add(rawAttributes);
							
						}
						
						attributes = Utils.discardDuplicateAttrs(rawApkAttributes);
						apksInCategory.put(apkName, attributes);
					}//eof for every true apk	
					
				}//eof for every potential apk
				
				categoryResult.put(f_category.getName(), apksInCategory);
			}//eof for every true category
		}
		
		return categoryResult;
	}

	/**************************************************************************************************************************/
	/**************************************************************************************************************************/
	/**************************************           APPROACH 2         ******************************************************/
	/**************************************************************************************************************************/
	/**************************************************************************************************************************/

	public static HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> parse2(
			String rootDir, 
			HashMap<String, HashSet<String>> permissionOpportunitiesMap, 
			HashMap<String, Opportunity> opportunities) {
		
		IDictionary dict = Utils.constructWordNetDictionary();
		
		/**
		 * HashMap<"category name", HashMap<apk name, HashSet<opportunity name>>>
		 */
		HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> categoryResult = new HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>>(); 
		
		HashMap<String, Opportunity> opportunitiesSynHyperHyponyms = Utils.getSynHyperHyponyms(opportunities, dict);
		/**
		 * Keep track of unique packages. DOn't want to scan twice apks present in multiple categories.
		 * key= apk name, value = list of categories
		 */
		HashMap<String, ArrayList<String>> uniqueApks = new HashMap<String, ArrayList<String>>();
		
		/*
		 * For each category directory
		 * 		For each app
		 * 			If .xml -> xmlParse
		 * 			else if .db || .sqlite -> dbParse
		 * 			else if .json -> JsonParser
		 * 			else -> No parser for this file
		 */
		
		File[] f_categories = new File(rootDir).listFiles();
		
		/* FOR EVERY CATEGORY */
		for(File f_category : f_categories){
			if(f_category.isDirectory()){
				//this is a category directory. It contains apk directories
				HashMap<String, Integer> interest_freqs_in_cat = new HashMap<String, Integer>();
				interest_freqs_in_cat.put("vehicle", 0); //hardcode interests for now
				interest_freqs_in_cat.put("workout", 0); //hardcode interests for now
				InAppAttributeExtractor.interestFreqPerCat.put(f_category.getName().trim().toLowerCase(), interest_freqs_in_cat);
				
				HashMap<String, ArrayList<FoundOpportunity>> apksInCategory = new HashMap<String, ArrayList<FoundOpportunity>>();
				
				MyParser.currentCategory = f_category.getName().trim().toLowerCase();
				
				/* FOR EVERY APK DIRECTORY */
				File[] f_apkDirs = f_category.listFiles();
				for(File f_apkDir : f_apkDirs){

					if(f_apkDir.isDirectory() && !f_apkDir.getName().startsWith(".")){
						
						// in the apk dir: get interesting files
						boolean apkScannedBefore = false;
						
						if(uniqueApks.containsKey(f_apkDir.getName())){
							//add the category
							ArrayList<String> cats = uniqueApks.get(f_apkDir.getName());
							cats.add(f_category.getName());
							uniqueApks.put(f_apkDir.getName(), cats);
							apkScannedBefore = true;
						}
						else{
							ArrayList<String> cats = new ArrayList<String>();
							cats.add(f_category.getName());
							uniqueApks.put(f_apkDir.getName(), cats);
						}
						
						//test
						//check if this app is in ground truth and hasn't been scanned in a different category
						//if(InAppAttributeExtractor.groundTruth.containsKey(f_apkDir.getName()) && !apkScannedBefore){ //PUT THIS BACK WHEN EVALUATING
						if(!apkScannedBefore && (MyParser.currentCategory.compareTo("medical") == 0 || MyParser.currentCategory.compareTo("health_and_fitness") == 0)){
						
							String apkName = f_apkDir.getName();
							ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
							
							/* Holds found Opportunities for every file */
							ArrayList<ArrayList<FoundOpportunity>> rawApkAttributes = new ArrayList<ArrayList<FoundOpportunity>>();
							
							/* Get attributes extracted from package name */
							rawApkAttributes.add(PckNameParser.parse(apkName, opportunitiesSynHyperHyponyms));
							
							/* EXTRACTED FILES */
							File[] files = f_apkDir.listFiles();
							
							if(Preferences.RANDOM_STRATEGY){
								rawApkAttributes.add(RandomParser.assign(f_apkDir, opportunities));
							}
							else{
								//scan files looking for opportunities
								for(File file : files){
									
									String fname = file.getName();
									ArrayList<FoundOpportunity> rawAttributes = new ArrayList<FoundOpportunity>();
									
									if(file.isFile() && !fname.startsWith(".")){
										
										if(file.getName().contains(".")){
											switch (fname.substring(fname.lastIndexOf('.')) ) {
											case ".dump":
												//db file
												rawAttributes = DBParser.parse(file, opportunitiesSynHyperHyponyms);
												//rawAttributes = DBParser.simpleParse(Utils.FILETYPE_DB, file, opportunitiesSynHyperHyponyms);
												//rawAttributes = SimpleParser.parse(file, opportunitiesSynHyperHyponyms);

												break;
											case ".xml":
												//xml file
												rawAttributes = XMLParser.parse(file, opportunitiesSynHyperHyponyms);
												//rawAttributes = SimpleParser.parse(file, opportunitiesSynHyperHyponyms);
												break;
											case ".json":
												//json file
												rawAttributes = SimpleParser.parse(file, opportunitiesSynHyperHyponyms);
												break;
											case ".txt":
												//rawAttributes = SimpleParser.parse(file, opportunitiesSynHyperHyponyms);
												break;
											default:
												//rawAttributes = SimpleParser.parse(file, opportunitiesSynHyperHyponyms);
												break;
											}
										}
									
									}
									else if(file.isDirectory() && fname.compareTo("DECOMPILED") == 0){
										//Find and Check the manifest file
										File[] f_decompileDirs = file.listFiles();
										
										//debug
										if(f_decompileDirs.length < 1){
											InAppAttributeExtractor.logger.debug("Could not find decompiled data in " + file.getAbsolutePath() + ". Length: " + f_decompileDirs.length);
										}
										else{
											for(File f : f_decompileDirs){
												if(f.getName().compareTo(f_apkDir.getName()) == 0){
													//rawAttributes = ManifestParser.parse(f_manifest);
													File[] f_apkDecDir = f_decompileDirs[0].listFiles();
													for(File potentialManifest : f_apkDecDir){
														if(potentialManifest.getName().compareTo("AndroidManifest.xml") == 0){
															//Found Manifest file
															rawAttributes = ManifestParser.parse(potentialManifest, InAppAttributeExtractor.permissionOpportunitiesMap);
															break;
														}
													}
													
													/* READ SPECIFIC FILE (e.g. strings.xml) */
													rawAttributes.addAll(SimpleParser.parseValues(f_decompileDirs[0].getAbsolutePath() + "/res/values", opportunitiesSynHyperHyponyms));
													/* READ EVERYTHING IN DIR */
													//rawAttributes.addAll(SimpleParser.parseAllinDir(f_decompileDirs[0].getAbsolutePath() + "/res/values", opportunitiesSynHyperHyponyms));
													/* READ EVERYTHING IN DIR */
													rawAttributes.addAll(SimpleParser.parseAllinDir(f_decompileDirs[0].getAbsolutePath() + "/res/layout", opportunitiesSynHyperHyponyms));
												}
											}
										}
									}
									
									rawApkAttributes.add(rawAttributes);
									
								}
							}
							
							
							//////////
							attributes = Utils.discardDuplicateAttrs(rawApkAttributes); //it doesn't discard duplicates, just reformats
							apksInCategory.put(apkName, attributes);		
							
							AppPackage app = new AppPackage(apkName, f_category.getName());
							InAppAttributeExtractor.packages.put(apkName, app);
							InAppAttributeExtractor.packages.get(apkName).opportunities.addAll(Utils.getUniqueOps(attributes));
						
						}
					}//eof for every true apk	
					
				}//eof for every potential apk
				
				categoryResult.put(f_category.getName(), apksInCategory);
			}//eof for every true category
		}
		
		return categoryResult;
		
	}

}
