import java.util.HashSet;

/**
 * Android app package
 * @author soteris
 *
 */
public class AppPackage {
	String packageName;
	String category;
	HashSet<String> opportunities;
	
	public AppPackage(String packageName, String category){
		this.packageName = packageName;
		this.category = category;
		this.opportunities = new HashSet<String>();
	}
}
