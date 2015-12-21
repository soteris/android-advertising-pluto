import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import edu.mit.jwi.item.POS;


public class SimpleParser {

	/**
	 * 
	 * @param opportunitiesSynHyperHyponyms 
	 * @param string
	 * @return 
	 */
	public static ArrayList<FoundOpportunity> parseValues(String potDirPath, HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		// TODO Auto-generated method stub
		File dir = new File(potDirPath);
		ArrayList<FoundOpportunity> res = new ArrayList<FoundOpportunity>();
		
		InAppAttributeExtractor.logger.debug("Inspecting potentialDIr " + potDirPath);
		
		if(!dir.isDirectory()){
			InAppAttributeExtractor.logger.debug("Not a dir!");
			return res;
		}
		
		for(File file : dir.listFiles()){
			if(file.isDirectory()){
				res.addAll(parseAllinDir(file.getAbsolutePath(), opportunitiesSynHyperHyponyms));
			}
			else if(file.isFile()){
				if(file.getName().compareTo("strings.xml") == 0){
					InAppAttributeExtractor.logger.debug("Inspecting file " + file.getName() + " in directory: " + potDirPath);
					res.addAll(SimpleParser.parse(file, opportunitiesSynHyperHyponyms));
				}
			}
			
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param opportunitiesSynHyperHyponyms 
	 * @param string
	 * @return 
	 */
	public static ArrayList<FoundOpportunity> parseAllinDir(String potDirPath, HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		// TODO Auto-generated method stub
		File dir = new File(potDirPath);
		ArrayList<FoundOpportunity> res = new ArrayList<FoundOpportunity>();
		
		InAppAttributeExtractor.logger.debug("Inspecting potentialDIr " + potDirPath);
		
		if(!dir.isDirectory()){
			InAppAttributeExtractor.logger.debug("Not a dir!");
			return res;
		}
		
		for(File file : dir.listFiles()){
			if(file.isDirectory()){
				res.addAll(parseAllinDir(file.getAbsolutePath(), opportunitiesSynHyperHyponyms));
			}
			else if(file.isFile()){
				InAppAttributeExtractor.logger.debug("Inspecting file " + file.getName() + " in directory: " + potDirPath);
				res.addAll(SimpleParser.parse(file, opportunitiesSynHyperHyponyms));
			}
			
		}
		
		return res;
	}

	/**
	 * Checks line by line. Splits the line into words containing only alphabet characters. Splits word if camelCase and check if they match with the opportunities.
	 * @param fileType 
	 * @param file
	 * @param opportunitiesSynHyperHyponyms 
	 * @return
	 */
	public static ArrayList<FoundOpportunity> parse(File file, HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		ArrayList<FoundOpportunity> res = new ArrayList<FoundOpportunity>();
		
		HashMap<String, Integer> catTermFreqs = InAppAttributeExtractor.interestFreqPerCat.get(MyParser.currentCategory);
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			
			String line = "";
			
			while((line = bufferedReader.readLine()) != null){
				
				ArrayList<String> candidateWords = Utils.cleanText(line);
				
				if(candidateWords.size() < Preferences.PARAGRAPH_THR){
				
					for(String candWord : candidateWords){
						ArrayList<String> subWords = Utils.getSubNames(candWord);
						
						for(String subWord : subWords){
							
							String opportunityName = Utils.opportunityFullMatch(subWord, opportunitiesSynHyperHyponyms);
							
							if(opportunityName.compareTo(Preferences.NOT_AN_OPPORTUNITY) != 0){
								//tmp term frequencies
								
								//eof term freqs
								
								String opType = opportunitiesSynHyperHyponyms.get(opportunityName).type;
								if(opType.compareTo(Opportunity.INTEREST) == 0){
									if(opportunityName.compareToIgnoreCase("workout") == 0){// && MyParser.currentCategory.compareToIgnoreCase("health_and_fitness") == 0){
										double sim = SimilarityCalculator.getWeightedLesk(MyParser.currentCategory, opportunityName);
										if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LESK_INTEREST_THRESHOLD){
											//ACCEPT match
											FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), opportunityName, candWord);
											res.add(fop);
										}
										
										//tmp term frequencies
										int vfreq = catTermFreqs.get("workout");
										catTermFreqs.put("workout", ++vfreq);
										//eof term freqs
									}
									else if(opportunityName.compareToIgnoreCase("vehicle") == 0 ){// && 
											//(MyParser.currentCategory.compareToIgnoreCase("transportation") == 0 ||   MyParser.currentCategory.compareToIgnoreCase("shopping") == 0)){
										double sim = SimilarityCalculator.getWeightedLesk(MyParser.currentCategory, opportunityName);
										if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LESK_INTEREST_THRESHOLD){
											//ACCEPT match
											FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), opportunityName, candWord);
											res.add(fop);
										}
										//tmp term frequencies
										int vfreq = catTermFreqs.get("vehicle");
										catTermFreqs.put("vehicle", ++vfreq);
										//eof term freqs
									}	
									
									/*
									//opportunity is interest, check similarity with category
									ArrayList<String> cat_names = Utils.getCatSubNames(MyParser.currentCategory);
									double sim = SimilarityCalculator.calculateMaxSim(cat_names, opportunityName, opType);
									
									if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LESK_INTEREST_THRESHOLD){
										System.out.println("ACCEPTED INTEREST! candWord =" + candWord + ", category = " + MyParser.currentCategory + ", opportunity = " + opportunityName 
												+ ", opType = " + opType + ", sim = " + sim);
										FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), opportunityName, candWord);
										res.add(fop);
									}
									else{
										System.out.println("REJECTED INTEREST! candWord =" + candWord + ", category = " + MyParser.currentCategory + ", opportunity = " + opportunityName 
												+ ", opType = " + opType + ", sim = " + sim);
										FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), opportunityName, candWord);
										res.add(fop);
									}
									*/
								}
								else{
									//attribute
									FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), opportunityName, candWord);
									res.add(fop);
								}
								
								
							}
						}
					}
				
				}
			}
	
			bufferedReader.close();
			
			InAppAttributeExtractor.interestFreqPerCat.put(MyParser.currentCategory, catTermFreqs);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}

}
