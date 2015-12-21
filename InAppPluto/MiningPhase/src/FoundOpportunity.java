import java.util.HashSet;


public class FoundOpportunity {
	public static final int DB = 1;
	public static final int XML = 2;
	public static final int JSON = 3;
	public static final int MANIFEST = 4;
	public static final int PACKAGE_NAME = 5;
	public static final int UNKNOWN = 6;
	
	public String name;
	public int from = 0;
	public String filename;
	public String origin_word;
	
	/* FROM DB FILES */
	public String table_name;
	public String column_name;
	
	/* FROM XML FILES */
	public String element;
	public String at_name;
	public String at_value;
	public String el_value;
	
	/* FROM JSON FILES */
	public String key;
	public String value;
	
	int frequency = 0;
	
	/**
	 * 
	 * @param from type of file explored {DB, XML, JSON}
	 * @param filename File opportunity found in
	 * @param name Opportunity name
	 * @param origin_word Origin word of match
	 */
	public FoundOpportunity(int from, String filename, String name, String origin_word) {
		
		this.from = from;
		this.filename = filename;
		this.name = name;
		this.origin_word = origin_word;	
	}
	
	public boolean setDBdata(String table_name, String column_name){
		
		if(from != FoundOpportunity.DB){
			return false;
		}
		else{
			this.table_name = table_name;
			this.column_name = column_name;
			return true;
		}
		
	}
	
	/**
	 * 
	 * @param element
	 * @param at_name Attribute name
	 * @param at_value Attribute value
	 * @param el_value Element value
	 * @return
	 */
	public boolean setXMLdata(String element, String at_name, String at_value, String el_value){
		
		if(from != FoundOpportunity.XML){
			return false;
		}
		else{
			this.element = element;
			this.at_name = at_name;
			this.at_value = at_value;
			this.el_value = el_value;
			return true;
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setJSONdata(String key, String value){
		
		if(from != FoundOpportunity.JSON){
			return false;
		}
		else{
			this.key = key;
			this.value = value;
			return true;
		}
		
	}
}
