import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;


public class Utils {

	public static final int FILETYPE_DB = 328947;
	public static final int FILETYPE_XML = 328946;
	public static final int FILETYPE_JSON = 328943;

	/**
	 * Given an Arraylist of attributes collected from different files, it returns a set of unique (for noe it return everything) instances of all attributes across files.
	 * @param rawApkAttributes
	 * @return
	 */
	public static ArrayList<FoundOpportunity> discardDuplicateAttrs(
			ArrayList<ArrayList<FoundOpportunity>> rawApkAttributes) {
		
		ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
	
		//TODO: RETURN UNIQUE ATTRIBUTES: FOR NOW LEAVE EVERYTHING VISIBLE
		for(ArrayList<FoundOpportunity> fileOpps : rawApkAttributes){
			for(FoundOpportunity op : fileOpps){
				attributes.add(op);
			}
		}
	
		return attributes;
	}

	/**
	 * 
	 * @param categoryResult
	 */
	public static void printResult(
			HashMap<String, HashMap<String, HashSet<String>>> categoryResult) {
		
		/* For every category */
		for(String catName : categoryResult.keySet()){
			System.out.println("******** " + catName + "********");
			
			//get apks
			HashMap<String, HashSet<String>> apksResult = categoryResult.get(catName);
			
			/* For every apk */
			for(String apkName : apksResult.keySet()){
				System.out.println("\t APK: " + apkName);
				HashSet<String> opportunities = apksResult.get(apkName);
				
				/* For every opportunity */
				for(String opportunity : opportunities){
					/* Print this */
					System.out.println("\t\t Opportunity: " + opportunity);
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @param categoryResult ; cat (apk name - ops)
	 */
	public static void logResult(
			HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> categoryResult) {
		
		int numOfCat = 1;
		int numOfApk = 1;
		int numOfOp = 1;
		
		/*  STATS */
		HashSet<String> uniqueOpportunities = new HashSet<String>();
		int stat_APKS = 0;

		CatResult[] catResults = new CatResult[categoryResult.size()];
		
		try {
			PrintWriter writer = new PrintWriter(Preferences.resultLog, "UTF-8");
			
			/* For every category */
			int i = 0;
			for(String catName : categoryResult.keySet()){
				writer.println(numOfCat + ": " + catName + " : (" + categoryResult.get(catName).size() + ")");
				numOfCat++;
				
				CatResult catResult = new CatResult(catName);
				CatGroundTruth catGTresult = new CatGroundTruth(catName);
				
				//get apks
				HashMap<String, ArrayList<FoundOpportunity>> apksResult = categoryResult.get(catName);
				stat_APKS += apksResult.size();
				catResult.numOfApks = apksResult.size();
				
				//get #apks per op op name ==> freq in cat
				HashMap<String, Integer> opAPKfreq = Utils.getOpApkFreq(apksResult);
				catResult.opAPKfreq = opAPKfreq;
				
				/* For every apk */
				int numOfApksWithOps = 0;
				for(String apkName : apksResult.keySet()){
					writer.println("\t " + numOfApk + ": " + apkName);
					numOfApk++;
					
					//GT
					if(InAppAttributeExtractor.groundTruth.containsKey(apkName)){
						//gt apk belongs to this category
						catGTresult.noApks++;
						catGTresult.add(apkName, InAppAttributeExtractor.groundTruth.get(apkName));
					}
					//EGT
					
					ArrayList<FoundOpportunity> opportunities = apksResult.get(apkName);
					if(opportunities.size() > 0){
						numOfApksWithOps++;
					}
					
					/* For every opportunity */
					for(FoundOpportunity opportunity : opportunities){
						uniqueOpportunities.add(opportunity.name);
						catResult.foundOpportunities.add(opportunity.name);
						catResult.numOfOpportunities++;
						
						/* Print this */
						int op_type = opportunity.from;
						
						switch (op_type) {
						case FoundOpportunity.DB:
							writer.println("\t\t " + numOfOp + ". " + " Opportunity Name: " + opportunity.name + ", column name: " + opportunity.column_name
									+ ", table name: " + opportunity.table_name + ", origin word: " + opportunity.origin_word + ", (in " + opportunity.filename + ")");
							break;
						case FoundOpportunity.XML:
							writer.println("\t\t " + numOfOp + ". " + " Opportunity Name: " + opportunity.name + ", element name: " + opportunity.element
									+ ", attribute name: " + opportunity.at_name + ", element content: " + opportunity.el_value +
									", origin word: " + opportunity.origin_word + ", (in " + opportunity.filename + ")");
							break;
						case FoundOpportunity.JSON:
							
							break;
						case FoundOpportunity.MANIFEST:
							writer.println("\t\t MANIFEST" + numOfOp + ". " + " Opportunity name: " + opportunity.name + ", origin word: " 
									+ opportunity.origin_word + ", (in " + opportunity.filename + ")");
							break;
						case FoundOpportunity.PACKAGE_NAME:
							writer.println("\t\t " + numOfOp + ". " + " Opportunity name: " + opportunity.name + ", origin word: " 
									+ opportunity.origin_word + ", (package: " + opportunity.filename + ")");
							break;
						default:
							writer.println("\t\t " + numOfOp + ". " + " Opportunity name: " + opportunity.name + ", origin word: " 
									+ opportunity.origin_word + ", (in: " + opportunity.filename + ")");
							break;
						}
						
						
						numOfOp++;
					}
					
					numOfOp = 1;
				}
				
				numOfApk = 1;
				catResult.numOfApksWithOps = numOfApksWithOps;
				
				//GT
				Stats.logGTcatResult(catGTresult);
				//EGT
				
				catResults[i] = catResult;
				i++;
			}
			
			writer.println();
			writer.println("-------------------------------------------------");
			writer.println("Total num of APKS : " + stat_APKS);
			writer.println("Unique num of Opportunities: " + uniqueOpportunities.size());
			writer.println("-------------------------------------------------");
			writer.println("Summary Per Category");
			
			for(CatResult catResult : catResults){
				writer.println("CATEGORY: " + catResult.name + ", num of apks: " + catResult.numOfApks + ", num of apks with opportunities: " + catResult.numOfApksWithOps 
						+ ", num of all oppportunities: " + catResult.numOfOpportunities + ", num of unique oportunities: " + catResult.foundOpportunities.size());
				
				for(String op : catResult.foundOpportunities){
					writer.println("\t " + op + " [" + catResult.opAPKfreq.get(op) + "]");
				}
			}
			
			writer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**************************************************************************************************/
	
	/**
	 * 
	 * @param @param categoryResult ; cat (apk name - ops)
	 * @param selected_cats
	 * @param original_opportunities 
	 */
	public static void logUniqueResult(
			HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> categoryResult,
			HashSet<String> selected_cats, HashMap<String, Opportunity> original_opportunities) {
		
		
		try {
			PrintWriter pw = new PrintWriter(Preferences.uniqueResultLog);
			
			for(String catName : categoryResult.keySet()){
				
				if(selected_cats.contains(catName)){
					/* this is a selected category. Get ops per apk */
					//get apks
					HashMap<String, ArrayList<FoundOpportunity>> apksResult = categoryResult.get(catName);
					
					//for every apk get unique ops
					for(String apkName : apksResult.keySet()){
						//get found ops
						ArrayList<FoundOpportunity> opportunities = apksResult.get(apkName);
						if(opportunities == null || opportunities.size() == 0){
							//no ops for this apk
							//pw.println("CATEGORY ( " + catName + " ) : PACKAGE ( " + apkName + " ) : OPS ( 0 )");
							pw.println(catName + ":" + apkName + ":-1:0");
						}
						else{
							//get unique ops
							HashSet<String> uniqueOps = getUniqueOps(opportunities);
							//get app score
							Double score = Utils.getAppScore(uniqueOps, original_opportunities);
							//pw.print("CATEGORY ( " + catName + " ) : PACKAGE ( " + apkName + " ) : OPS ( ");
							pw.print(catName + ":" + apkName + ":");
							
							//print the unique ops
							boolean firstOp = true;
							for(String opName : uniqueOps){
								if(!firstOp) pw.print(";");
								pw.print(opName);
								firstOp = false;
							}
							
							pw.println(":" + score);
						}
					}
				}
				
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**************************************************************************************************/

	/**
	 * Given a set of opportunities, calculates the total monetary value of the set
	 * @param uniqueOps
	 * @param original_opportunities 
	 * @return
	 */
	private static Double getAppScore(HashSet<String> uniqueOps, HashMap<String, Opportunity> original_opportunities) {
		Double score = 0.0;
		
		for(String opName : uniqueOps){
			score += Utils.getOpValue(opName, original_opportunities);
		}
		
		return score;
	}

	/**
	 * Returns the value of the given opportunity or 0 if doesn't exist
	 * @param opName
	 * @param original_opportunities
	 * @return
	 */
	private static Double getOpValue(String opName,
			HashMap<String, Opportunity> original_opportunities) {
		
		Double value = 0.0;
		
		if(original_opportunities.containsKey(opName)){
			value = original_opportunities.get(opName).value;
		}
		
		return value;
	}

	/**************************************************************************************************/
	
	/**
	 * Get Ops with frequency of apks that it was found in
	 * @param apksResult
	 * @return
	 */
	private static HashMap<String, Integer> getOpApkFreq(
			HashMap<String, ArrayList<FoundOpportunity>> apksResult) {
		
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		
		
		for(String apk : apksResult.keySet()){
			ArrayList<FoundOpportunity> fops = apksResult.get(apk);
			HashSet<String> unique_fops = Utils.getUniqueOps(fops); 
			
			for(String fop : unique_fops){
				if(!res.containsKey(fop)){
					res.put(fop, 1);
				}
				else{
					//get frreq
					int freq = res.get(fop);
					res.put(fop, ++freq);
				}
			}
			
		}
		
		return res;
	}

	/**
	 * Return the Wordnet synonyms or an empty ArrayList
	 * @param columnName:
	 * @param dict
	 */
	public static ArrayList<String> getSynonyms(String columnName, POS pos, IDictionary dict) {
		ArrayList<String> synonyms = new ArrayList<String>();
		System.out.println(columnName);
		
		try{
			IIndexWord idxWord = dict.getIndexWord (columnName, pos);
			List<IWordID> wordIDs = idxWord.getWordIDs(); // this should be  idxWord . getWordIDs () . get (0) ; // 1st meaning
			
			for(IWordID wordID : wordIDs){
				IWord word = dict.getWord(wordID) ;
				ISynset synset = word.getSynset() ;
				
				// iterate over words associated with the synset
				for(IWord w : synset.getWords())
					synonyms.add(w.getLemma()) ;
			}
		}
		catch(NullPointerException e){
			InAppAttributeExtractor.logger.error("Null Pointer Exception in Utils.getSynonyms()! Probably word " + columnName + " not in Dictionary!");
			return synonyms;
		}
		
		return synonyms;
	}
	
	/**
	 * Return the Wordnet synonyms or an empty ArrayList. It chooses the meaning indicated in the input with the opportuntiy
	 * @param columnName:
	 * @param dict
	 * @param meaning the id of the sense (to disambiguate meanings)
	 */
	public static ArrayList<String> getSynonyms2(String columnName, POS pos, IDictionary dict, int meaning) {
		ArrayList<String> synonyms = new ArrayList<String>();
		System.out.println(columnName);
		
		try{
			IIndexWord idxWord = dict.getIndexWord (columnName, pos);
			IWordID wordID = idxWord.getWordIDs().get(meaning); // this should be  idxWord . getWordIDs () . get (0) ; // 1st meaning
			
			//for(IWordID wordID : wordIDs){
				IWord word = dict.getWord(wordID) ;
				ISynset synset = word.getSynset() ;
				
				// iterate over words associated with the synset
				for(IWord w : synset.getWords())
					synonyms.add(w.getLemma()) ;
			//}
		}
		catch(NullPointerException e){
			InAppAttributeExtractor.logger.error("Null Pointer Exception in Utils.getSynonyms()! Probably word " + columnName + " not in Dictionary!");
			return synonyms;
		}
		
		return synonyms;
	}
	
	/**
	 * Return the Wordnet hypernyms or an empty ArrayList
	 * @param columnName:
	 * @param dict
	 */
	public static ArrayList<String> getHypernyms(String columnName, POS pos, IDictionary dict) {
		ArrayList<String> hypernymsAndTheirSynonyms = new ArrayList<String>();
		//System.out.println(columnName);
		
		try{
			IIndexWord idxWord = dict.getIndexWord (columnName, pos);
			List<IWordID> wordIDs = idxWord.getWordIDs(); // this should be  idxWord . getWordIDs () . get (0) ; // 1st meaning
			
			for(IWordID wordID : wordIDs){
				IWord word = dict.getWord(wordID) ;
				ISynset synset = word.getSynset() ;
				
				//get the hypernyms: A noun can have multiple hypernyms with different meanings. We take everything for now
				List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
				
				//add the hypernym and its synonyms
				List<IWord> words;
				/* Find all the synonyms for each hypernym */
				for(ISynsetID sid : hypernyms){
					words = dict.getSynset(sid).getWords();
					
					for(Iterator<IWord> i = words.iterator(); i.hasNext();){
						hypernymsAndTheirSynonyms.add(i.next().getLemma());
					}
					
				}
			}
		}
		catch(NullPointerException e){
			InAppAttributeExtractor.logger.error("Null Pointer Exception in Utils.getHypernyms()! Probably word " + columnName + " not in Dictionary!");
			return hypernymsAndTheirSynonyms;
		}
		
		return hypernymsAndTheirSynonyms;
	}
	
	/**
	 * Return the Wordnet hyponyms or an empty ArrayList
	 * @param columnName:
	 * @param dict
	 */
	public static ArrayList<String> getHyponyms(String columnName, POS pos, IDictionary dict) {
		ArrayList<String> hypernymsAndTheirSynonyms = new ArrayList<String>();
		//System.out.println(columnName);
		
		try{
			IIndexWord idxWord = dict.getIndexWord (columnName, pos);
			List<IWordID> wordIDs = idxWord.getWordIDs(); // this should be  idxWord . getWordIDs () . get (0) ; // 1st meaning
			
			for(IWordID wordID : wordIDs){
				IWord word = dict.getWord(wordID) ;
				ISynset synset = word.getSynset() ;
				
				//get the hyponyms: A noun can have multiple hyponyms with different meanings. We take everything for now
				List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPONYM);
				
				//add the hypernym and its synonyms
				List<IWord> words;
				/* Find all the synonyms for each hypernym */
				for(ISynsetID sid : hypernyms){
					words = dict.getSynset(sid).getWords();
					
					for(Iterator<IWord> i = words.iterator(); i.hasNext();){
						hypernymsAndTheirSynonyms.add(i.next().getLemma());
					}
					
				}
			}
		}
		catch(NullPointerException e){
			InAppAttributeExtractor.logger.error("Null Pointer Exception in Utils.getHyponyms()! Probably word " + columnName + " not in Dictionary!");
			return hypernymsAndTheirSynonyms;
		}
		
		return hypernymsAndTheirSynonyms;
	}
	
	/**
	 * TODO: If we won't use dependencies, just to a HashMap search for the keyword
	 * @param columnName
	 * @param opportunities
	 * @param dict 
	 * @return
	 */
	public static String opportunityMatch(String keyword,
			HashMap<String, Opportunity> opportunities) {
		String result = null;
		
		for(String opportunity : opportunities.keySet()){
			//HashSet<String> opportunitydependencies = opportunities.get(opportunity);
			opportunity = opportunity.trim().toLowerCase();
			
			/* First check if this keyword is exactly the same as the opportunity */
			//if(keyword.trim().compareTo(opportunity.trim()) == 0 || opportunity.contains(keyword)){
			if(keyword.trim().compareTo(opportunity.trim()) == 0){
				return opportunity;
			}
			else{
				//check if this is a more specific definition of the opportunity
			}
			//TODO more complex matching relationships
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param keyword
	 * @param opportunitiesSynHyperHyponyms
	 * @return the opportunity that matched
	 */
	public static String opportunityKeyMatch(String keyword,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		// TODO Auto-generated method stub
		String result = null;
		
		for(String op : opportunitiesSynHyperHyponyms.keySet()){
			if(opportunitiesSynHyperHyponyms.containsKey(keyword.trim().toLowerCase()) || Utils.isDerivable(op, keyword)){
				return op;
			}
		}
		
		
		return result;
	}
	
	public static boolean opportunityKeyMatch2(String keyword,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		
		return (opportunitiesSynHyperHyponyms.containsKey(keyword.trim().toLowerCase())) ? true : false;
	}

	/**
	 * 
	 * @param op Opportunity
	 * @param keyword String
	 * @return
	 */
	public static boolean opportunitySynMatch(Opportunity op,
			String keyword) {
		// TODO Auto-generated method stub
		boolean result = false;
		keyword = keyword.trim().toLowerCase();
		
		for(String syn : op.all){
			//if(syn.compareTo(keyword) == 0 || Utils.isDerivable(syn, keyword)){
			if(syn.compareTo(keyword) == 0){
				return true;
			}
		}
		
		
		return result;
	}
	
	/**
	 * Returns true if <b>from</b> contains <b>key</b> and <b>from</b> is not
	 *  a word on itself.
	 * @param key
	 * @param from
	 * @return
	 */
	private static boolean isDerivable(String key, String from) {
		
		if(from.contains(key) && !Utils.isWordnetWord(from)){
			return true;
		}
		
		return false;
	}

	/**
	 * 
	 * @param from
	 * @return
	 */
	private static boolean isWordnetWord(String from) {
		
		IIndexWord idxWord = InAppAttributeExtractor.dict.getIndexWord (from, POS.NOUN);
		return (idxWord != null) ? true : false;
	}

	/**
	 * 
	 * @param columns
	 */
	public static void printColumns(Column[] columns) {
		for(Column column : columns){
			System.out.println("col name: " + column.name + ", col type: " + column.type);
		}
		
	}

	/**
	 * 
	 * @param columnName
	 * @return
	 */
	/*
	public static String getPhraseFromName(String columnName){
		String result = "";
		
		if(columnName.contains("_")){
			String[] candidateNames = columnName.split("_");
			for(String candidateName : candidateNames){
				candidateName = candidateName.trim();
				if(!candidateName.isEmpty() && candidateName.compareTo(" ") != 0){
					result += " " + candidateName;
				}
			}
		}
		else if(Utils.hasLowerCase(columnName) && Utils.hasUpperCase(columnName)){
			
		}
	}
	*/
	
	/**
	 * Given a package name, it splits it into words on every period. Then every word is checked for subwords due to
	 *  camelCase or underscore-based format. 
	 * @param packageName
	 * @return
	 */
	public static ArrayList<String> getPackageSubNames(String packageName){
		ArrayList<String> names = new ArrayList<String>();
		
		String[] candidateNames = packageName.split(".");
		for(String candidateName : candidateNames){
			if(!Utils.wordInPackageNameBlacklist(candidateName)){
				names.addAll(Utils.getSubNames(candidateName));
			}
		}
		
		return names;
	}
	
	/**
	 * 
	 * @param candidateName
	 * @return
	 */
	private static boolean wordInPackageNameBlacklist(String candidateName) {
		candidateName = candidateName.trim().toLowerCase();
		
		if(candidateName.compareTo("com") == 0 ||
				candidateName.compareTo("net") == 0 ||
				candidateName.compareTo("edu") == 0){
			return true;
		}
		
		return false;
	}

	/**
	 * Splits camel-like variable names and underscore-based names
	 * @param columnName
	 * @return
	 */
	public static ArrayList<String> getSubNames(String columnName) {
		ArrayList<String> names = new ArrayList<String>();
		String phrase = "";
		String phrase2 = "";
		
		if(columnName.contains("_")){
			//underscore structure
			String[] candidateNames = columnName.split("_");
			for(String candidateName : candidateNames){
				candidateName = candidateName.trim();
				if(!candidateName.isEmpty() && candidateName.compareTo(" ") != 0){
					names.add(candidateName.toLowerCase());
					phrase += candidateName + " ";
					phrase2 += candidateName + "_";
				}
			}
		}
		else{
			for (String candidateName : columnName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
		        //System.out.println(candidateName);
				if(!candidateName.isEmpty() && candidateName.compareTo(" ") != 0){
					names.add(candidateName.toLowerCase());
		        	phrase += candidateName + " ";
		        	phrase2 += candidateName + "_";
				}
		    }
		}
		
		if(names.size() > 1){
			names.add(phrase.substring(0, phrase.length()).trim().toLowerCase());
			names.add(phrase2.substring(0, phrase2.length() - 1).toLowerCase());
		}
		return names;
	}

	/**
	 * Replaces ',', '.', '/', '\'. '(', ')' with space and splits on space
	 * @param val
	 * @return
	 */
	public static ArrayList<String> cleanText(String sentence) {
		// TODO Auto-generated method stub
		ArrayList<String> words = new ArrayList<String>();
		
//		sentence.replace('.', ' ');
//		sentence.replace(',', ' ');
//		sentence.replace('\'', ' ');
//		sentence.replace('(', ' ');
//		sentence.replace('[', ' ');
//		sentence.replace('{', ' ');
//		sentence.replace(')', ' ');
//		sentence.replace(']', ' ');
//		sentence.replace('}', ' ');
//		sentence.replace(':', ' ');
//		sentence.replace(';', ' ');
//		sentence.replace('&', ' ');
//		sentence.replace('$', ' ');
//		sentence.replace('"', ' ');
//		sentence.replace('`', ' ');
//		sentence.replace('<', ' ');
//		sentence.replace('>', ' ');
//		sentence.replace('=', ' ');
		
		sentence = sentence.replaceAll("[^a-zA-Z]", " ");
		
		sentence = sentence.trim();
		String[] firstOrder = sentence.split("\\s+");	
		
		for(String word : firstOrder){
			if(Utils.isWord(word)){
				words.add(word);
			}
		}
		
		return words;
	}

	/**
	 * 
	 * @param word
	 * @return
	 */
	private static boolean isWord(String word) {
		// TODO Auto-generated method stub

		for(int i = 0; i < word.length(); i++){
			if(word.charAt(i) >= '0' && word.charAt(i) <= '9'){
				return false;
			}
		}
		
		
		return true;
	}

	/**
	 * Strips permission name from a manifest permission line <br />
	 * e.g. <it>uses-permission android:name="com.android.vending.BILLING"</it>
	 * @param line
	 * @return
	 */
	public static String getPermissionNameFromManiLine(String line) {
		String permission = "";
		String prefix = "";
		
		//permission = line.substring(line.lastIndexOf('.') + 1, line.lastIndexOf('"'));
		
		String[] components = line.split("\\s+");
		
		for(String component : components){
			
			if(component.startsWith("android:name=")){
				permission = component.substring(component.indexOf("\"") + 1, component.lastIndexOf("\""));
				permission = permission.substring(permission.lastIndexOf(".") + 1);
				//System.out.println("Permission extracted: " + permission);
			}
		}
		
		return permission;
	}

	/**
	 * 
	 * @param permissionOpportunitiesMap
	 */
	public static void printPermissionOpportunitiesMap(
			HashMap<String, HashSet<String>> permissionOpportunitiesMap) {
		// TODO Auto-generated method stub
		
		for(String permission : permissionOpportunitiesMap.keySet()){
			System.out.println("Permission: " + permission);
			
			for(String opportunity : permissionOpportunitiesMap.get(permission)){
				System.out.println("\t\t Opportunity: " + opportunity);
			}
		}
		
	}

	/**
	 * 
	 * @param opportunities
	 */
	public static void printOpportunitiesKeys(
			HashMap<String, HashSet<String>> opportunities) {
		// TODO Auto-generated method stub
		for(String key : opportunities.keySet()){
			System.out.println("." + key + ".");
		}
	}
	
	/**
	 * edu.mit.jwi_2.3.3_manual.pdf
	 * @return IDictionary or null
	 */
	public static IDictionary constructWordNetDictionary() {
		IDictionary dict = null;
		
		try {
			File d_dict = new File(Preferences.WordnetDictionaryDir);
			
			//construct the dictionary object and open it
			dict = new Dictionary(d_dict);
			
			dict.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return dict;
	}

	/**
	 * Get all wordnet synonyms, hypernyms and hyponyms of the opportunities
	 * @param opportunities
	 * @param dict 
	 * @return A HashMap where the keys are the opportunities. Values are Opportunity objects holding the synonyms, hyponyms  and hypernyms
	 */
	public static HashMap<String, Opportunity> getSynHyperHyponyms(
			HashMap<String, Opportunity> opportunities, IDictionary dict) {
		// TODO Auto-generated method stub
		HashMap<String, Opportunity> result = new HashMap<String, Opportunity>();
		
		for(String opRaw : opportunities.keySet()){
			String op = opRaw.trim().toLowerCase();
			
			ArrayList<String> synonyms = Utils.getSynonyms2(op, POS.NOUN, dict, opportunities.get(opRaw).meaning);
			ArrayList<String> hypernyms = Utils.getHypernyms(op, POS.NOUN, dict);
			ArrayList<String> hyponyms = Utils.getHyponyms(op, POS.NOUN, dict);
			
			Opportunity opportunity = new Opportunity(op, synonyms, hypernyms, hyponyms, opportunities.get(op).domains, opportunities.get(op).meaning, 
					opportunities.get(op).type, opportunities.get(op).value);
			result.put(op, opportunity);
			/*
			if(op.compareTo("weight") == 0){
				System.out.println("Added weight!");
				if(result.get(op).all == null){
					System.out.println("Weight all is null!");
				}
				System.exit(0);
			}
			*/
			
		}
		
		return result;
	}

	/**
	 * 
	 * @param packages
	 * @param fname 
	 */
	public static void logPackages(TreeMap<String, AppPackage> packages, String fname) {
		// TODO Auto-generated method stub
		Log packageLogger = new Log(fname);
		
		int i = 1;
		for(String appName : packages.keySet()){
			AppPackage appPackage = packages.get(appName);
			packageLogger.info(i + "\t" + appPackage.category + "\t" + appPackage.packageName + "\t" + appPackage.opportunities.size());
			i++;
		}
	}

	/**
	 * 
	 * @param packages
	 * @param min
	 * @param max
	 * @param numOfRands
	 * @return
	 */
	public static TreeMap<String, AppPackage> getRandomSelection(
			TreeMap<String, AppPackage> packages, int min, int max, int numOfRands) {
		
		TreeMap<String, AppPackage> results = new TreeMap<String, AppPackage>();
		Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
		HashSet<Integer> rands = new HashSet<Integer>();
		
		for(int i=0; i<numOfRands; i++){
			rands.add(rand.nextInt((max - min) + 1) + min);
		}
	    
		int j = 1;
	    for(String appName : packages.keySet()){
	    	if(rands.contains(j)){
	    		//add
	    		results.put(appName, packages.get(appName));
	    	}
	    	
	    	j++;
	    }

		
		return results;
	}

	/**
	 * Returns a hashet of opportunity names given an ArrayList of FoundOpportunity objects
	 * @param arrayList
	 * @return
	 */
	public static HashSet<String> getOpNamesFromArrayList(
			ArrayList<FoundOpportunity> arrayList) {
		
		HashSet<String> res = new HashSet<String>();
		
		for(FoundOpportunity fop : arrayList){
			res.add(fop.name);
		}
		
		return res;
	}

	public static boolean foundOpsALcontains(
			ArrayList<FoundOpportunity> arrayList, String op) {
		
		for(FoundOpportunity fop : arrayList){
			if(fop.name.compareToIgnoreCase(op) == 0){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Check if key is within the opportunity data structure (checks name, syns and optionally hyper, and  optionally hypos)
	 * @param key
	 * @param opportunitiesSynHyperHyponyms
	 * @return
	 */
	public static String opportunityFullMatch(String key,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		
		if(opportunitiesSynHyperHyponyms.containsKey(key)){
			return key;
		}
		
		for(String opName : opportunitiesSynHyperHyponyms.keySet()){
			
			if(opportunitiesSynHyperHyponyms.get(opName).all.contains(key)){
				return opName;
			}
		}
		
		return Preferences.NOT_AN_OPPORTUNITY;
	}

	/**
	 * Replaces every non alphabet characters with empty
	 * @param dirty
	 * @return
	 */
	public static String cleanColName(String dirty) {
		// TODO Auto-generated method stub
		
		String clean = dirty.replaceAll("[^a-zA-Z]", "");
		
		return clean;
	}

	/**
	 * 
	 * @param foundOps
	 */
	public static void printStringHashSet(HashSet<String> set) {
		for(String element : set){
			System.out.println("Element: " + element);
		}
		
	}

	/**
	 * 
	 * @param opportunities 
	 * @return A HashSet containing the keys of the HashMap input
	 */
	public static HashSet<String> getHashSetFromHM(HashMap<String, Opportunity> opportunities) {

		HashSet<String> res = new HashSet<String>();
		
		for(String element : opportunities.keySet()){
			res.add(element);
		}
		
		return res;
	}

	/**
	 * Replaces all non alphabet characters with space and then splits sentence on spaces
	 * @param dirty
	 * @return
	 */
	public static ArrayList<String> getWords(String dirty) {
		// TODO Auto-generated method stub
		String cleans = dirty.replaceAll("[^a-zA-Z]", " ");
		
		String[] words = cleans.split(" ");
		ArrayList<String> res = new ArrayList<String>();
		
		for(String word : words){
			String lc = word.toLowerCase();
			if(lc.compareTo(word) != 0){
				//camel case
				res.addAll(Utils.splitCamelCase(word));
			}
			else{
				res.add(word);
			}
		}
		
		for(String w : words){
			res.add(w.trim());
		}
		
		return res;
	}

	/**
	 * Splits camel case words. It also includes in the set the subwords produced, merged.
	 * @param word
	 * @return
	 */
	private static ArrayList<String> splitCamelCase(String word) {
		ArrayList<String> res = new ArrayList<String>();
		String phrase = "";
		String phrase2 = "";
		
		for (String candidateName : word.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
	        //System.out.println(candidateName);
			if(!candidateName.isEmpty() && candidateName.compareTo(" ") != 0){
				res.add(candidateName.toLowerCase());
	        	phrase += candidateName + " ";
	        	phrase2 += candidateName + "_";
			}
	    }
		
		res.add(phrase);
		res.add(phrase2);
		
		return res;
	}

	/**
	 * 
	 * @param currentCategory
	 * @return
	 */
	public static ArrayList<String> getCatSubNames(String currentCategory) {
		// TODO Auto-generated method stub
		ArrayList<String> res = new ArrayList<String>();
		
		String[] cands = currentCategory.split("_");
		
		for(String cand : cands){
			if(!isStopword(cand)){
				res.add(cand);
			}
		}
		
		return res;
	}

	/**
	 * Stopwards are and;to;the;app;com;net;org;
	 * @param cand
	 * @return
	 */
	private static boolean isStopword(String cand) {
		// TODO Auto-generated method stub
		String tmp = cand.trim().toLowerCase();
		
		if(tmp.compareTo("and") == 0 || tmp.compareTo("to") == 0 || tmp.compareTo("the") == 0 || tmp.compareTo("app") == 0 
				|| tmp.compareTo("com") == 0 || tmp.compareTo("net") == 0 || tmp.compareTo("org") == 0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * 
	 * @param interestFreqPerCat
	 * @param termfreqfname
	 */
	public static void logInterestTermFrequencies(
			HashMap<String, HashMap<String, Integer>> interestFreqPerCat,
			String fname) {
		
		try {
			PrintWriter pw = new PrintWriter(fname);
			
			for(String cat : interestFreqPerCat.keySet()){
				HashMap<String, Integer> tf = interestFreqPerCat.get(cat);
				
				pw.print("CATEGORY: " + cat);
				
				for(String term : tf.keySet()){
					int freq = tf.get(term);
					
					pw.print(", " + term + ":" + freq);
				}
				
				pw.println();
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static HashSet<String> getUniqueOps(
			ArrayList<FoundOpportunity> attributes) {
		
		HashSet<String> res = new HashSet<String>();
		
		for(FoundOpportunity fop : attributes){
			res.add(fop.name);
		}
		
		return res;
	}
}
