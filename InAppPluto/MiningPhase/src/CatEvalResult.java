import java.util.HashMap;
import java.util.HashSet;

/**
 * Evaluation results per category
 * @author soteris
 *
 */
public class CatEvalResult {
	String category_name;
	HashMap<String, EvalResult> opportunities;
	
	
	
	public CatEvalResult(String category_name, HashSet<String> opportunities){
		category_name = this.category_name;
		
		this.opportunities = initOpportunities(opportunities);
	}

	/**
	 * 
	 * @param opportunities2
	 * @return
	 */
	private HashMap<String, EvalResult> initOpportunities(
			HashSet<String> ops) {
		
		HashMap<String, EvalResult> res = new HashMap<String, EvalResult>();
		
		for(String op : ops){
			EvalResult eres = new EvalResult();
			res.put(op, eres);
		}
		
		return res;
	}
	
}
