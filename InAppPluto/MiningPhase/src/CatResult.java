import java.util.HashMap;
import java.util.HashSet;


public class CatResult {

	String name;
	int numOfApks = 0;
	int numOfApksWithOps = 0;
	int numOfOpportunities = 0;
	HashSet<String> foundOpportunities;
	public HashMap<String, Integer> opAPKfreq;
	
	public CatResult(String name){
		this.name = name;
		this.foundOpportunities = new HashSet<String>();
	}
}
