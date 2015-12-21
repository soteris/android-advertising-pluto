import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;


public class Stats {

	/**
	 * 
	 * @param groundTruth
	 * @param gtOpFrequencies
	 */
	public static void logApkPerOp(
			HashMap<String, HashSet<String>> groundTruth,
			HashMap<String, Integer> gtOpFrequencies) {
		
		
		try {
			PrintWriter pw = new PrintWriter("manual_stats1.txt");
			
			for(String op : gtOpFrequencies.keySet()){
				//get #apks that have the op
				int numApks = Stats.getNoApkWithOp(op, groundTruth);
				pw.println(op + "\t" + numApks);
				//print it
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static int getNoApkWithOp(String op,
			HashMap<String, HashSet<String>> groundTruth) {
		int res = 0;
		
		for(String appName : groundTruth.keySet()){
			if(groundTruth.get(appName).contains(op)){
				res++;
			}
		}
		
		return res;
	}

	/**
	 * Logs stats for ground truth : for category
	 * @param catGTresult
	 */
	public static void logGTcatResult(CatGroundTruth catGTresult) {
		
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("manual_stats2.txt"), true /* append = true */));
			
			//pw.println("CATEGORY NAME\t" +           "NUMBER OF APKS\t" +         "APKS w/ OPS\t" +                    "TOTAL No OPS");
			pw.println(catGTresult.name + "\t" + catGTresult.noApks + "\t" + catGTresult.getNoApksWithOps() + "\t" + catGTresult.getNoOps());
			//System.out.println(catGTresult.name + "\t" + catGTresult.noApks + "\t" + catGTresult.getNoApksWithOps() + "\t" + catGTresult.getNoOps());
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
