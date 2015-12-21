import java.util.HashMap;
import java.util.HashSet;


public class CatGroundTruth {

	String name = "";
	
	int noApks = 0;
	int noApkswOps = 0;
	int noOps = 0;
	/**
	 * key=apk name, value=a hashet of opportunities
	 */
	HashMap<String,HashSet<String>> apks;
	
	public CatGroundTruth(String name) {
		this.name = name;
		apks = new HashMap<String,HashSet<String>>();
	}


	public void add(String apkName, HashSet<String> hashSet) {
		// TODO Auto-generated method stub
		apks.put(apkName, hashSet);
	}
	
	/**
	 * Returns the number of apks in this category that have at least one opportunity
	 * @return
	 */
	public int getNoApksWithOps(){
		int res = 0;
		
		for(String apk : apks.keySet()){
			if(apks.get(apk) != null && apks.get(apk).size() > 0){
				res++;
			}
		}
		
		return res;
	}


	public int getNoOps() {
		int res = 0;
		
		for(String apk : apks.keySet()){
			if(apks.get(apk) != null && apks.get(apk).size() > 0){
				res += apks.get(apk).size();
			}
		}
		
		return res;
	}

}
