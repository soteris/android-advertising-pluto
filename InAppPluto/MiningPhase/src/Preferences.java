
public class Preferences {

	public static final String WordnetDictionaryDir = "/home/soteris/Projects/SCEA/scea/AutoTool/InAppAttributeExtractor/dict";
	public static final String resultLog = "resultLog.txt";
	/** Stores unique op list per selected category */
	public static final String uniqueResultLog = "uniqueResultLog.txt";
	public static final String DEFAULT_COL_VALUE = "N/A";
	protected static final String DEFAULT_VALUE = "N/A";
	
	/**
	 * The Resnik similarity threshold. Anything above is accepted as similar enough. 
	 */
	public static final double RESNIK_THRESHOLD = 2.0;
	public static double SIMILARITY_DEFAULT = 0;
	//public static double LCH_THRESHOLD = 1.4;
	public static double LCH_ATTRIBUTE_THRESHOLD = 2.8;
	public static double LESK_INTEREST_THRESHOLD = 0.8;
	
	public static final String packageLoggerFname = "packages.txt";
	public static final String selectedPackageLoggerFname = "selected_packages.txt";
	public static final String NOT_AN_OPPORTUNITY = "LAKSDFH";
	public static final int PARAGRAPH_THR = 10000;
	public static final String termFreqFname = "termFrequencies.txt";
	public static String loggingFilename = "attributeExtractorDebugLog.txt";
	
	/**
	 * Opportunities not present in at least this amount in the ground truth, will not be loaded
	 */
	public static int MIN_OPPORTUNITY_ABSOLUTE_SUPPORT = 0;
	/**
	 * If set to yes, all packages will be randomly assigned a set of opportunities
	 */
	public static boolean RANDOM_STRATEGY = false;
	
	
}
