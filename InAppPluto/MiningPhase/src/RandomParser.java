import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * A Random strategy file parser
 * @author soteris
 *
 */
public class RandomParser {

	/**
	 * Randomly assign the presence or absence of an opportunity
	 * @param file
	 * @param opportunities
	 * @return
	 */
	public static ArrayList<FoundOpportunity> assign(File file,
			HashMap<String, Opportunity> opportunities) {
		// TODO Auto-generated method stub
		ArrayList<FoundOpportunity> res = new ArrayList<FoundOpportunity>();
		
		//for every opportunity, flip a coin to decide whether the opportunity was found or not
		for(String op : opportunities.keySet()){
			
			Random coinToss = new Random();
			int coin = coinToss.nextInt(2);
			
			if(coin == 1){
				//add opportunity
				FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), op, "");
				res.add(fop);
			}
		}
		
		return res;
	}

}
