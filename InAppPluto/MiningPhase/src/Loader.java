import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


public class Loader {
	
	/**
	 * 
	 * @param opportunities
	 * @param mIN_OPPORTUNITY_ABSOLUTE_SUPPORT 
	 * @param gtOpFrequencies 
	 * @param inFilename 
	 */
	public static void loadOpportunities(
			HashMap<String, Opportunity> opportunities, HashMap<String, Integer> gtOpFrequencies, int support, String inFilename) {
		// TODO Auto-generated method stub
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inFilename));
			
			String line = "";
			
			while((line = bufferedReader.readLine()) != null ){
				String[] lineComponents = line.split(":");
				
				String op = lineComponents[0].trim().toLowerCase();
				int meaningId = Integer.parseInt(lineComponents[2]);
				String type = lineComponents[3].trim();
				double value = Double.parseDouble(lineComponents[4].trim());
				
				if(gtOpFrequencies.containsKey(op) && gtOpFrequencies.get(op) >= support){
					//FIND DEPENDENCIES
					HashSet<String> dependencies = new HashSet<String>();
					
					if(lineComponents.length > 1){
						//opportunity has dependencies
						
						//get dependencies
						String[] potentialDependencies = lineComponents[1].split(",");
						for(String potentialDependency : potentialDependencies){
							dependencies.add(potentialDependency);
							//System.out.println("Adding domain: " + potentialDependency);
						}
					}
					
					//STORE OPPORTUNITY
					Opportunity oop = new Opportunity(op, null, null, null, dependencies, meaningId, type, value);
					opportunities.put(op, oop);
				}
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param groundTruth
	 * @param gtOpFrequencies 
	 * @param support 
	 * @param string
	 */
	public static void loadGroundTruth(
			HashMap<String, HashSet<String>> groundTruth, HashMap<String, Integer> gtOpFrequencies, int support, String inFilename) {
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inFilename));
			
			String line = "";
			
			while((line = bufferedReader.readLine()) != null ){
				String[] lineComponents = line.split("\t");
				
				line = line.trim();
				
				//FIND DEPENDENCIES
				HashSet<String> dependencies = new HashSet<String>();
				
				if(lineComponents.length > 1){
					//opportunity has dependencies
					
					//get opportunities
					String[] potentialDependencies = lineComponents[1].split(",");
					for(String potentialDependency : potentialDependencies){
						String op = potentialDependency.trim().toLowerCase();
						dependencies.add(op);
						
						if(!gtOpFrequencies.containsKey(op)){
							System.out.println("Put op " + op);
							gtOpFrequencies.put(op, 1);
						}
						else{
							int freq = gtOpFrequencies.get(op);
							gtOpFrequencies.put(op, ++freq);
						}
						
						//System.out.println("Adding domain: " + potentialDependency);
					}
				}
				
				//STORE OPPORTUNITY
				String pck = lineComponents[0].trim();
				groundTruth.put(pck, dependencies);
				
			}
			
			/* prune */
			groundTruthPrune(groundTruth, gtOpFrequencies, support);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Parses the ground truth and removes opportunities with frequency less than the support
	 * @param groundTruth
	 * @param gtOpFrequencies
	 * @param support
	 */
	private static void groundTruthPrune(
			HashMap<String, HashSet<String>> groundTruth,
			HashMap<String, Integer> gtOpFrequencies, int support) {
		// TODO Auto-generated method stub
		
		for(String pck : groundTruth.keySet()){
			//check its opportunities
			HashSet<String> ops = groundTruth.get(pck);
			String[] arr_ops = ops.toArray(new String[ops.size()]);
			for(int i = 0; i < arr_ops.length; i++){
				String op = arr_ops[i];
				System.out.println("Op: " + op);
				if(gtOpFrequencies.get(op) < support){
					//remove
					groundTruth.get(pck).remove(op);
				}
			}
		}
		
	}

	public static void loadPermissionOpportunitiesMap(
			HashMap<String, Opportunity> opportunities,
			HashMap<String, HashSet<String>> permissionOpportunitiesMap,
			String inFilename) {
		// TODO Auto-generated method stub
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inFilename));
			
			String line = "";
			
			while((line = bufferedReader.readLine()) != null ){
				String[] lineComponents = line.split(":");
				
				//FIND OPPORTUNITIES
				HashSet<String> opportunitiesInFile = new HashSet<String>();
				
				if(lineComponents.length > 1){
					//permission is mapped to opportunity
					
					//get opportunities
					String[] potentialOppportunities = lineComponents[1].split(",");
					for(String potentialOpportunity : potentialOppportunities){
						//System.out.println("Found op " + potentialOpportunity + " for " + lineComponents[0]);
						//check if file opportunity is a real opportunity
						if(opportunities.containsKey(potentialOpportunity)){
							//System.out.println("Op " + potentialOpportunity + " validated!");
							opportunitiesInFile.add(potentialOpportunity.trim());
						}
					}
				}
				
				//STORE PERMISSION-OPPORTUNITIES MAP
				permissionOpportunitiesMap.put(lineComponents[0].trim(), opportunitiesInFile);
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
