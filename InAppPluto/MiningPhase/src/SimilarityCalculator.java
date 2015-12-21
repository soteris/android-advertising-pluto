import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
//import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.PorterStemmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;


public class SimilarityCalculator {

	private static final int RESNIK_ID = 4;
	private static final int LCH_ID = 0;
	private static Log simLogger = new Log("simlog.txt");
	
	private static ILexicalDatabase db = new NictWordNet();
//    private static RelatednessCalculator[] rcs = {
//                    new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
//                    new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
//                    };
    
	 //private static RelatednessCalculator[] rcs = {new LeacockChodorow(db)};
	 
	/*
    public static void calculate( String word1, String word2 ) {
        WS4JConfiguration.getInstance().setMFS(true);
        for ( RelatednessCalculator rc : rcs ) {
                double s = rc.calcRelatednessOfWords(word1, word2);
                System.out.println( rc.getClass().getName()+"\t"+s );
        }
    }
    */
    
    /**
     * 
     * @param word1
     * @param word2
     * @return
     */
    public static double calculateResnik( String word1, String word2 ) {
        WS4JConfiguration.getInstance().setMFS(true);
        
        PorterStemmer stemmer = new PorterStemmer();
        
        word1 = stemmer.stemWord(word1);
        word2 = stemmer.stemWord(word2);
        
        RelatednessCalculator rc_resnik = new Resnik(db);
        double s = rc_resnik.calcRelatednessOfWords(word1, word2);
        System.out.println( rc_resnik.getClass().getName()+"\t ("+ word1 + ", " + word2 + ") = " + s );
        
        double s2 = rc_resnik.calcRelatednessOfWords("location", "preference");
        System.out.println( rc_resnik.getClass().getName()+"\t ("+ "location" + ", " +  "preference" + ") = " + s2 );
        
        return s;
    }
    
    /**
     * 
     * @param word1
     * @param word2
     * @param type 
     * @return
     */
    private static double calculateSimilarity(String word1, String word2, String type){
    	ILexicalDatabase db = new NictWordNet();
    	WS4JConfiguration.getInstance().setMFS(true);
    	RelatednessCalculator[] rcs = {new LeacockChodorow(db)}; //default is LCH
    	
    	if(type.compareTo(Opportunity.ATTRIBUTE) == 0){
    		rcs[0] = new LeacockChodorow(db);
    		//rcs[0] = new Lin(db);
    	}
    	else{
    		//interest
    		//rcs[0] = new Resnik(db);
    		return 0;
    		//rcs[0] = new HirstStOnge(db);
    	}
    	//String word1 = "gender";
    	//String word2 = "sex";
    	simLogger.info("");
    	simLogger.info("-----------------------------------------------------");
    	
    	double maxScore = -1D;
    	double[] rc_maxScore = {-1D};
    	
        int i = 0;
        for(RelatednessCalculator rc : rcs){
	        
	        /////TEST/////////
        	/*
	        double score = rc.calcRelatednessOfWords(word1, word2);
	        System.out.println( rc.getClass().getName()+"\t ("+ word1 + ", " + word2 + ") = " + score );
	        
	        if (score > rc_maxScore[i]) { 
            	rc_maxScore[i] = score;
            }
            
            if (score > maxScore) { 
                maxScore = score;
            }
            */
        /////EOFTEST/////////
            
	        
	         
	    	List<POS[]> posPairs = rc.getPOSPairs();
	    	
	    	for(POS[] posPair: posPairs) {
	    	    List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(word1, posPair[0].toString());
	    	    List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(word2, posPair[1].toString());
	
	    	    for(Concept synset1: synsets1) {
	    	        for (Concept synset2: synsets2) {
	    	        	
	    	        	
	    	            Relatedness relatedness = rc.calcRelatednessOfSynset(synset1, synset2);
	    	            double score = relatedness.getScore();
	    	            
	    	            
	    	            if (score > rc_maxScore[i]) { 
	    	            	rc_maxScore[i] = score;
	    	            }
	    	            
	    	            if (score > maxScore) { 
	    	                maxScore = score;
	    	            }
	    	        }
	    	    }
	    	}
			

	    	if (rc_maxScore[i] == -1D) {
	    		rc_maxScore[i] = 0.0;
	    	}
	    	simLogger.info(rc.getClass().getName() + "\t sim{" + word1 + ", " + word2 + "} =  " + rc_maxScore[i]);
	    	i++;
	    	
        }
        
        if (maxScore == -1D) {
    	    maxScore = 0.0;
    	}
	
    	//System.out.println("MaxScore sim('" + word1 + "', '" + word2 + "') =  " + maxScore);
    	
    	return rc_maxScore[0];
    }


    /**
     * 
     * @param list
     * @param set
     * @param type
     * @return 0 if we failed to compare in any way the two or the Maximum Resnik similarity from all possible combos
     */
	public static double calculateMaxSim(ArrayList<String> list,
			HashSet<String> set, String type) {

		double max_sim = -1D;
		
		
		for(String element : set){
			for(String list_element : list){
				element = Utils.cleanColName(element);
				list_element = Utils.cleanColName(list_element);
				
				double tmp = SimilarityCalculator.calculateSimilarity(element, list_element, type);
				
				PorterStemmer stemmer = new PorterStemmer();
		        
		        String stem1 = stemmer.stemWord(element);
		        String stem2 = stemmer.stemWord(list_element);
		        double tmp2 = SimilarityCalculator.calculateSimilarity(stem1, stem2, type);
		        
		        if(tmp2 > tmp){
		        	tmp = tmp2;
		        }
		        
				if(tmp > max_sim){
					max_sim = tmp;
				}
			}
		}
		
		return max_sim;
	}

	/**
	 * 
	 * @param list
	 * @param word
	 * @param type
	 * @return
	 */
	public static double calculateMaxSim(ArrayList<String> list,
			String word, String type) {
		
		double max_sim = -1D;
		
		
		//for(String element : set){
			for(String list_element : list){
				word = Utils.cleanColName(word);
				list_element = Utils.cleanColName(list_element);
				
				double tmp = SimilarityCalculator.calculateSimilarity(word, list_element, type);
				
				PorterStemmer stemmer = new PorterStemmer();
		        
		        String stem1 = stemmer.stemWord(word);
		        String stem2 = stemmer.stemWord(list_element);
		        double tmp2 = SimilarityCalculator.calculateSimilarity(stem1, stem2, type);
		        
		        if(tmp2 > tmp){
		        	tmp = tmp2;
		        }
		        
				if(tmp > max_sim){
					max_sim = tmp;
				}
			}
		//}
		
		return max_sim;
	}

	/**
	 * Get weighted androLesk
	 * @param currentCategory
	 * @param opportunityName
	 * @return
	 */
	public static double getWeightedLesk(String currentCategory,
			String opportunityName) {
		double res = 0.0;
		
		res = WeightedLesk.calcRelatedness(currentCategory, opportunityName);
		//res = MyLesk.calcRelatedness(currentCategory, opportunityName);
		
		return res;
	}
    
}
