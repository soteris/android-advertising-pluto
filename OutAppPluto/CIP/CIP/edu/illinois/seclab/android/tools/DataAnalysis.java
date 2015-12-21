package edu.illinois.seclab.android.tools;

import weka.associations.Apriori;
import weka.associations.FPGrowth;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Uses weka library to perform data analysis
 * @author soteris
 *
 */
public class DataAnalysis {

	/**************************************************************************************************/
	/**************************************************************************************************/
	/****************************************** FPGROWTH **********************************************/
	/**************************************************************************************************/
	/**
	 * Calls weka's fpgrowth for association rule generation
	 * 	m_delta = 0.05;
    		m_metricThreshold = 0.9;
   	 	m_numRulesToFind = 10;
		m_lowerBoundMinSupport = 0.1;
    		m_upperBoundMinSupport = 1.0;
		m_minSupport = -1;
	    	m_positiveIndex = 2;
	    	m_transactionsMustContain = "";
		m_rulesMustContain = "";
		m_mustContainOR = false;
	 * @param arffpath
	 * @return
	 */
	private static boolean fpGrowth(String arffpath){
	
	    Log.debug("Running FPGrowth.");	
		// load data
	    Instances data;
		try {
			data = DataSource.read(arffpath);

			FPGrowth fpGrowth = new FPGrowth();
			
			fpGrowth.setMinMetric(0.7); //was 0.7. set min confidence
			//fpGrowth.setLowerBoundMinSupport(0.001);
			//fpGrowth.setDelta(0.1); //amount that support is increased in each iteration
			
			fpGrowth.setMaxNumberOfItems(4); //was 4. use to limit how big the itemsets can get, limit the height of the tree
			//fpGrowth.setNumRulesToFind(10);
			//fpGrowth.setUpperBoundMinSupport(1.0);
			
			fpGrowth.setFindAllRulesForSupportLevel(true);
		    
			fpGrowth.buildAssociations(data); 
			
		    // output associator
		    Utils.writeStringToFile(fpGrowth.toString(), Preferences.rulesFileFPGrowth);
			
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.FAILURE;
		}
	    
		
		return Utils.SUCCESS;
	}
	
	/**************************************************************************************************/
	/**************************************************************************************************/
	/****************************************** APRIORI  **********************************************/
	/**************************************************************************************************/
	/**
	 * Calls weka's apriori for association rule generation
	 * 	m_removeMissingCols = false;
		m_verbose = false;
		m_delta = 0.05;
		m_minMetric = 0.90;
		m_numRules = 10;  				//num of association rules
		m_lowerBoundMinSupport = 0.1;
		m_upperBoundMinSupport = 1.0;
		m_significanceLevel = -1;
		m_outputItemSets = false;
		m_car = false;
 		m_classIndex = -1
	 * @param arffpath
	 * @return
	 */
	private static boolean apriori(String arffpath) {
		
	    Log.debug("Running Apriori.");

		try {
			
		// load data
	    Instances data = DataSource.read(arffpath);
	    data.setClassIndex(data.numAttributes() - 1);

	    // build associator
	    Apriori apriori = new Apriori();
	    apriori.setClassIndex(data.classIndex());
	    apriori.setNumRules(1000);
	    apriori.setOutputItemSets(true);
	    
	    apriori.buildAssociations(data);
	    
	    // output associator
	    Utils.writeStringToFile(apriori.toString(), Preferences.rulesFileApriori);
	    
		} catch (Exception e) {
			e.printStackTrace();
			return Utils.FAILURE;
		}

	    return Utils.SUCCESS;
		
	}


	/**************************************************************************************************/
	/**************************************************************************************************/
	/********************************** RUN AN ASSOCIATION ALGO ***************************************/
	/**************************************************************************************************/
	/**
	 * 
	 * @param arffpath Path to the arff formatted data file
	 * @param mode The algo to run {Preference.APRIORI or Preferences.FPGROWTH}
	 * @return true on success, false on failure
	 */
	public static boolean runAlgo(int mode) {	
	
		if(mode == Preferences.APRIORI){
			return DataAnalysis.apriori(Preferences.arffPath);
		}
		else{
			return DataAnalysis.fpGrowth(Preferences.bin_arffPath);
		}
	}

}
