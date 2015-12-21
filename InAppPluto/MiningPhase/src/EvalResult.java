
public class EvalResult {

	/* True Positives */
	public int TP = 0;
	/* False Positives */
	public int FP = 0;
	/* True Negatives */
	public int TN = 0;
	/* False Negatives */
	public int FN = 0;
	
	/**
	 * What % of tuples matched with opportunities had an actual match
	 * @param tp True positives
	 * @param fp False positives
	 * @return
	 */
	public Double getPrecision(Double tp, Double fp){
		
		if(tp == 0 && fp == 0){
			return -1.0;
		}
		
		return tp/(tp+fp);
	}
	
	/**
	 * What % of tuples were matched
	 * @param tp True positives
	 * @param fn False Negative
	 */
	public Double getRecall(Double tp, Double fn){
		if(tp == 0 && fn == 0){
			return -1.0;
		}
		
		return tp/(tp+fn);
	}

}
