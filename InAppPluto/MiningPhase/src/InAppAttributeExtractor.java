import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import edu.mit.jwi.IDictionary;


public class InAppAttributeExtractor {

	/**
	 * key==> Opportunity name, value==> The disambiguation context term
	 */
	public static HashMap<String, Opportunity> opportunities = new HashMap<String, Opportunity>();
	/**
	 * key==> Permission name, value==> a HashSet of opportunities that can be learned with this permission
	 */
	public static HashMap<String, HashSet<String>> permissionOpportunitiesMap = new HashMap<String, HashSet<String>>();
	/**
	 * key==> Package name, value==> a HashSet of opportunities that can be learned from this package
	 */
	public static HashMap<String, HashSet<String>> groundTruth = new HashMap<String, HashSet<String>>();
	/**
	 * key==> Opportunity, value==> frequency
	 */
	public static HashMap<String, Integer> gtOpFrequencies = new HashMap<String, Integer>();
	
	/**
	 * keep track of the frequency of each interest term in each category
	 * key==>category name, value==> <opportunity, frequency>
	 */
	public static HashMap<String, HashMap<String, Integer>> interestFreqPerCat = new HashMap<String, HashMap<String, Integer>>();
	
	
	/**
	 * General logger for logging messages to a file.
	 */
	public static Log logger;
	/**
	 * Wordnet dictionary
	 */
	public static IDictionary dict = null;
	/**
	 * key==>value : packageName==>Package
	 */
	public static TreeMap<String, AppPackage> packages = new TreeMap<String, AppPackage>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		/* Check if user has provided a directory where the dynamic analysis results are stored */
		if(args.length == 0){
			System.out.println("Usage: java InAppAttributeExtractor <directory_that_stores_appDirectories_per_categoryDirectory>");
			System.exit(0);
		}
		
		/* keep track of metrics among different experiments */
		double minLCH = 0.0;
		double maxLCH = 3.6;
		double minRESNIK = 0;
		double maxRESNIK = 12;
		double minPath = 0.0;
		double maxPath = 1.0;
		double minLin = 0.0;
		double maxLin = 1.0;
		
		double minLESK = 0;
		double maxLESK = 125;
		
		double minWLESK = 0;
		double maxWLESK = 1;
		
		/* Set the min support for opportunities */
		Preferences.MIN_OPPORTUNITY_ABSOLUTE_SUPPORT = 0;
		/* Set the strategy */
		Preferences.RANDOM_STRATEGY = false;
		
		//Evaluator.testOp = "age";
		Log evalLogger = new Log("eval" + Evaluator.testOp + ".txt");
		
		//for(double thr = minLesk; thr <= maxLesk; thr += 0.1){
		//for(double thr = minRESNIK; thr <= maxRESNIK; thr += 2){
		//for(double thr = minLCH; thr <= maxLCH; thr += 0.2){
		//for(double thr = minWLESK; thr <= maxWLESK; thr += 0.1){
		
			init();
			
			//Preferences.LCH_ATTRIBUTE_THRESHOLD = 2.8;
			//Preferences.LESK_INTEREST_THRESHOLD = 0.8;
			Preferences.LCH_ATTRIBUTE_THRESHOLD = 2.6;
			Preferences.LESK_INTEREST_THRESHOLD = 0.4;
			Preferences.SIMILARITY_DEFAULT = 0;
			
			/* GET A WORDNET DICTIONARY */
			InAppAttributeExtractor.dict = Utils.constructWordNetDictionary();
			
			/* CREATE LOGGER */
			InAppAttributeExtractor.logger = new Log(Preferences.loggingFilename);
			
			/* LOAD GROUND TRUTH */
			Loader.loadGroundTruth(groundTruth, gtOpFrequencies, Preferences.MIN_OPPORTUNITY_ABSOLUTE_SUPPORT, "groundTruth2.txt");
			Stats.logApkPerOp(groundTruth, gtOpFrequencies);
			//Utils.printOpportunitiesKeys(groundTruth);
			
			/* LOAD OPPORTUNITIES */
			Loader.loadOpportunities(opportunities, gtOpFrequencies, Preferences.MIN_OPPORTUNITY_ABSOLUTE_SUPPORT, "Opportunities.txt");
			//Utils.printOpportunitiesKeys(opportunities);
			
			/* LOAD PERMISSION-OPPORTUNITIES MAP */
			Loader.loadPermissionOpportunitiesMap(opportunities, permissionOpportunitiesMap, "PermissionOpportunities.txt");
			//Utils.printPermissionOpportunitiesMap(permissionOpportunitiesMap);

			/* TRAVERSE LOCAL FILES OF EACH APP */
			HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> categoryResult = MyParser.parse2(args[0], permissionOpportunitiesMap, opportunities);
			Utils.logResult(categoryResult);
			
			/* LOG UNIQUE OPS PER APK IN SELECTED CATEGORIES */
			HashSet<String> selected_cats = new HashSet<String>();
			selected_cats.add("MEDICAL");
			selected_cats.add("HEALTH_AND_FITNESS");
			Utils.logUniqueResult(categoryResult, selected_cats, opportunities);
			
			//tmp print the term frequencies
			Utils.logInterestTermFrequencies(InAppAttributeExtractor.interestFreqPerCat, Preferences.termFreqFname);
			
			Utils.logPackages(InAppAttributeExtractor.packages, Preferences.packageLoggerFname);
			
			//TreeMap<String, AppPackage> selection = Utils.getRandomSelection(InAppAttributeExtractor.packages, 1, InAppAttributeExtractor.packages.size(), 300);
			//Utils.logPackages(selection, Preferences.selectedPackageLoggerFname);
			
			/* TOOL EVALUATION */
			Evaluator.evaluate(categoryResult, groundTruth, opportunities);
			
			evalLogger.info("LCH attr = " + Preferences.LCH_ATTRIBUTE_THRESHOLD + "LCH interest = " + Preferences.LESK_INTEREST_THRESHOLD + ", TP = " 
					+ Evaluator.TP + ", FP = " + Evaluator.FP + ", TN = " + Evaluator.TN + ", FN = " + Evaluator.FN);
			Evaluator.init();
			
		//}
		
	}

	/**
	 * Initializes static data structures
	 */
	private static void init() {
		// TODO Auto-generated method stub
		gtOpFrequencies.clear();
		opportunities.clear();
		permissionOpportunitiesMap.clear();
		groundTruth.clear();
		
		Evaluator.TP = 0;
		Evaluator.FP = 0;
		Evaluator.FN = 0;
		Evaluator.TN = 0;
	}

	

}
