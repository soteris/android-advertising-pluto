import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


public class Evaluator {
	public static String testOp = "age";
	
	/* True positives */
	public static int TP = 0;
	/* False positives */
	public static int FP = 0;
	/* True Negative */
	public static int TN = 0;
	/* False Negative */
	public static int FN = 0;
	
	public static TreeMap<String, CatEvalResult> result = new TreeMap<String, CatEvalResult>();
	
	/**
	 * What % of tuples matched with opportunities had an actual match
	 * @param tP2 True positives
	 * @param fP2 False positives
	 * @return
	 */
	public static Double getPrecision(int tp, int fp){
		
		if(tp + fp == 0){
			return -1.0;
		}
		
		Double res = (double)tp/((double)tp+fp);
		
		return new BigDecimal(res ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * What % of positive tuples are marked as positive
	 * @param tp True positives
	 * @param fn False Negative
	 */
	public static Double getRecall(int tp, int fn){
		if(tp + fn == 0){
			return -1.0;
		}
		
		Double res = (double)tp/((double)tp+fn);
		
		return new BigDecimal(res ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * What % of tuples were correctly classified
	 * @param tp True positives
	 * @param fn False Negative
	 */
	public static Double getAccuracy(int tp, int fp, int tn, int fn){
		if(tp + fp + tn + fn == 0){
			return -1.0;
		}
		
		double numerator = tp + tn;
		double denominator = tp + fp + tn + fn;
		
		Double res = numerator / denominator;
		
		return new BigDecimal(res ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * True positive recognition rate
	 * @param tp True positives
	 * @param fn False Negative
	 */
	public static Double getSensitivity(int tp, int fn){
		if(tp + fn == 0){
			return -1.0;
		}
		
		double numerator = tp;
		double denominator = tp + fn;
		
		Double res = numerator / denominator;
		
		return new BigDecimal(res ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * True negative recognition rate
	 * @param tp True positives
	 * @param fn False Negative
	 */
	public static Double getSpecificity(int fp, int tn){
		if(tn + fp == 0){
			return -1.0;
		}
		
		double numerator = tn;
		double denominator = tn + fp;
		
		Double res = numerator / denominator;
		
		return new BigDecimal(res ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * Harmonic mean of precision and recall
	 * @param precision
	 * @param recall
	 * @return
	 */
	private static Double getFScore(Double precision, Double recall) {
		// TODO Auto-generated method stub
		if(precision + recall == 0){
			return -1.0;
		}
		
		double numerator = 2 * precision * recall;
		double denominator = precision + recall;
		
		Double res = numerator / denominator;
		
		return new BigDecimal(res).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		
	}


	/**
	 * 
	 * @param categoryResult Data Structure holding packages and matched opportunities per category
	 * @param groundTruth Data structure holding the true attributes per package 
	 * @param opportunities 
	 */
	public static void evaluate(
			HashMap<String, HashMap<String, ArrayList<FoundOpportunity>>> categoryResult,
			HashMap<String, HashSet<String>> groundTruth, HashMap<String, Opportunity> opportunities) {
		
		HashSet<String> metricOps = Utils.getHashSetFromHM(opportunities);
		CatEvalResult overallResults = new CatEvalResult("all", metricOps);
		
		for(String catName : categoryResult.keySet()){
			
			CatEvalResult cer = new CatEvalResult(catName, metricOps);
			
			System.out.println("CATEGORY: " + catName);
			
			for(String appInCat : categoryResult.get(catName).keySet()){
				
				//check if this app is in ground truth
				if(groundTruth.containsKey(appInCat)){
					//evaluate
					
					//for each tool unique found opportunity,
					HashSet<String> foundOps = Utils.getOpNamesFromArrayList(categoryResult.get(catName).get(appInCat));
					
					
					for(String fopName : foundOps){
						
//						if(fopName.compareToIgnoreCase(Evaluator.testOp) != 0){
//							continue;
//						}
						
						//check if the found op is indeed in gt (tp) 
						if(groundTruth.get(appInCat).contains(fopName)){
							System.out.println("True Positive: APK = " + appInCat + ", for op = " + fopName);
							cer.opportunities.get(fopName).TP++;
							overallResults.opportunities.get(fopName).TP++;
							//Evaluator.TP++;
						}
						else{ //or not in gr (fp)
							System.out.println("False Positive: APK = " + appInCat + ", for op = " + fopName);
							//Utils.printStringHashSet(foundOps);
							cer.opportunities.get(fopName).FP++;
							overallResults.opportunities.get(fopName).FP++;
							//Evaluator.FP++;
						}
					}
					
					//get false negatives: for each op in ground truth for this app, check if it is present in FoundOps too, if not FN++
					for(String gt_op : groundTruth.get(appInCat)){
//						if(gt_op.compareToIgnoreCase(Evaluator.testOp) != 0){
//							continue;
//						}
						
						if(!foundOps.contains(gt_op)){
							System.out.println("False Negative: APK = " + appInCat + ", for op = " + gt_op);
							cer.opportunities.get(gt_op).FN++;
							overallResults.opportunities.get(gt_op).FN++;
							//Evaluator.FN++;
						}
					}
					
					//get true negatives
					//for each op check if it is both not in gt for this app and not in tool Found opportunities for this app
					for(String op : opportunities.keySet()){
//						if(op.compareToIgnoreCase(Evaluator.testOp) != 0){
//							continue;
//						}
						
						if(!groundTruth.get(appInCat).contains(op) && !foundOps.contains(op)){
							//System.out.println("True Negative: APK = " + appInCat + ", for op = " + appInCat);
							cer.opportunities.get(op).TN++;
							overallResults.opportunities.get(op).TN++;
							//Evaluator.TN++;
						}
					}
					
				}
				
				
			}
			
			//System.out.println("TP: " + cer.TP + ", FP: " + cer.FP + ", TN: " + cer.TN + ", FN: " + cer.FN);
			Evaluator.result.put(catName, cer);
		}
		
		//System.out.println();
		//System.out.println("OVERALL: TP: " + Evaluator.TP + ", FP: " + Evaluator.FP + ", TN: " + Evaluator.TN + ", FN: " + Evaluator.FN);
		Evaluator.logResults(overallResults, result);
	}

	/**
	 * 
	 * @param overallResults
	 * @param result2
	 */
	private static void logResults(CatEvalResult allResults,
			TreeMap<String, CatEvalResult> catResults) {
		
		Evaluator.logOverallResults(allResults);
		Evaluator.logResultsPerCategory(catResults);
		
	}



	/**
	 * @param allResults 
	 * 
	 */
	private static void logOverallResults(CatEvalResult allResults) {
		
		HashMap<String, EvalResult> results = allResults.opportunities;
		
		try {
			//PrintWriter writer = new PrintWriter("Evaluation_LESK_" + Preferences.LESK_INTEREST_THRESHOLD + ".txt");
			PrintWriter writer = new PrintWriter("Evaluation.txt");
			
			for(String op : results.keySet()){
				Double accuracy = Evaluator.getAccuracy(results.get(op).TP,results.get(op).FP,results.get(op).TN,results.get(op).FN);
				Double sensitivity = Evaluator.getSensitivity(results.get(op).TP, results.get(op).FN);
				Double specificity = Evaluator.getSpecificity(results.get(op).FP, results.get(op).TN);
				Double precision = Evaluator.getPrecision(results.get(op).TP, results.get(op).FP);
				Double recall = Evaluator.getRecall(results.get(op).TP, results.get(op).FN); 
				Double fscore = Evaluator.getFScore(precision, recall);
				
				/*
				writer.println(op + "[" + InAppAttributeExtractor.gtOpFrequencies.get(op) + "]" 
						+ "\t\t: TP = " + results.get(op).TP + ": FP = " + results.get(op).FP + ": TN = " + results.get(op).TN + ": FN = " + results.get(op).FN + 
						"; ACC = " + accuracy +
						", SENSITIVITY = " + sensitivity +
						", SPECIFICITY = " + specificity +
						", PREC = "  + precision +
						", REC = "   + recall +
						", F-score = " + fscore);
					*/
				writer.println(op + "\t" 
						+ InAppAttributeExtractor.gtOpFrequencies.get(op) + "\t"
						+ results.get(op).TP + "\t"
						+ results.get(op).FP + "\t"
						+ results.get(op).TN + "\t"
						+ results.get(op).FN + "\t"
						+ accuracy + "\t"
						+ sensitivity + "\t"
						+ specificity + "\t"
						+ precision + "\t"
						+ recall + "\t"
						+ fscore);
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	private static void logResultsPerCategory(TreeMap<String, CatEvalResult> catResults) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	public static void init() {
		// TODO Auto-generated method stub
		Evaluator.TP = 0;
		Evaluator.FP = 0;
		Evaluator.TN = 0;
		Evaluator.FN = 0;
		
	}
}
