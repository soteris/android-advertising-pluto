import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parses the package name and derives attributes
 * @author soteris
 *
 */
public class PckNameParser {

	/**
	 * 
	 * @param apkName
	 * @param opportunitiesSynHyperHyponyms
	 * @return
	 */
	public static ArrayList<FoundOpportunity> parse(String apkName,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		
		ArrayList<FoundOpportunity> fops = new ArrayList<FoundOpportunity>();
		
		String filename = apkName;
		ArrayList<String> subNames = Utils.getPackageSubNames(apkName);
		
		//for every subName check if it is present in the op list
		for(String potName : subNames){
			
			ArrayList<String> subSubNames = Utils.getSubNames(potName);
			
			for(String subPotName : subSubNames){
				
				if(Utils.opportunityKeyMatch2(subPotName, opportunitiesSynHyperHyponyms)){
					FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.PACKAGE_NAME, filename, subPotName, potName);
					fops.add(fop);
				}
				else{
					//check the synonyms
					//check the synonyms, hypernyms and hyponyms of all ops
					for(String op : opportunitiesSynHyperHyponyms.keySet()){
						
						if(Utils.opportunitySynMatch(opportunitiesSynHyperHyponyms.get(op), subPotName)){
							//match with this op's syn, hyper or hypo
							FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.PACKAGE_NAME, filename, subPotName, potName);
							fops.add(fop);
						}
					}
				}
			}
		}
		
		
		return fops;
	}

}
