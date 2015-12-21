import java.util.ArrayList;
import java.util.HashSet;


public class Opportunity {
	public static final String ATTRIBUTE = "attribute";
	public static final String INTEREST = "interest";
	
	int meaning;
	String type;
	Double value; // the monetory value for this opportunity
	HashSet<String> domains;
	String name;
	ArrayList<String> synonyms;
	ArrayList<String> hypernyms;
	ArrayList<String> hyponyms;
	HashSet<String> all;
	
	public Opportunity(String op, ArrayList<String> synonyms,
			ArrayList<String> hypernyms, ArrayList<String> hyponyms, HashSet<String> domains, int meaning, String type, Double value) {
		
		if(synonyms != null){
			this.synonyms = new ArrayList<String>(synonyms);
			
			//this.hypernyms = new ArrayList<String>(hypernyms);
			//this.hyponyms = new ArrayList<String>(hyponyms);
			all = new HashSet<String>();
			all.addAll(synonyms);
			//all.addAll(hyponyms);
			//all.addAll(hypernyms);
		}
		
		this.domains = new HashSet<String>(domains);
		//print(this.domains);
		this.meaning = meaning;
		this.type = type;
		this.value = value;
		//print(all);
	}

	private void print(HashSet<String> words) {
		
		for(String word : words){
			System.out.println("Word: " + word);
		}
		
	}

}
