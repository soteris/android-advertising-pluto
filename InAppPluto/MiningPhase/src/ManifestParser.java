import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class ManifestParser {

	public static ArrayList<FoundOpportunity> parse(File potentialManifest,
			HashMap<String, HashSet<String>> permissionOpportunitiesMap) {
		// TODO Auto-generated method stub
		ArrayList<FoundOpportunity> found_attributes = new ArrayList<FoundOpportunity>();
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(potentialManifest));
			
			String line = "";
			while((line = bufferedReader.readLine()) != null){
				if(line.contains("<uses-permission")){
					String permission_name = Utils.getPermissionNameFromManiLine(line);
					InAppAttributeExtractor.logger.debug("File: " + potentialManifest.getName() + ". Line: " + line);
					InAppAttributeExtractor.logger.debug("Permission: " + permission_name);
					
					if(permissionOpportunitiesMap.containsKey(permission_name) && permissionOpportunitiesMap.get(permission_name).size() > 0){
						InAppAttributeExtractor.logger.debug("FOUND OPPORTUNITY FOR PERMISSION");
						
						for(String opportunity : permissionOpportunitiesMap.get(permission_name)){
							if(!opportunity.isEmpty()){
								FoundOpportunity found_opportunity = new FoundOpportunity(FoundOpportunity.MANIFEST, 
										potentialManifest.getName(), opportunity, permission_name);
								found_attributes.add(found_opportunity);
							}
						}
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return found_attributes;
	}

}
