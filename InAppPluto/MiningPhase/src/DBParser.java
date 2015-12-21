import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;


public class DBParser {

	/**
	 * Checks a db.dump file for attributes.  Currently works only on CREATE statements
	 * @param file
	 * @param dict 
	 * @return 
	 */
	public static ArrayList<FoundOpportunity> parse(File file, IDictionary dict) {
		// TODO Auto-generated method stub
		String currentDBname = file.getName().substring(0, file.getName().indexOf('.'));
		/**
		 * Store attributes found in this file
		 */
		//ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
		
		//InAppAttributeExtractor.logger.debug("Parsing database " + currentDBname);
		
		/**
		 * Keep track of db tables
		 */
		ArrayList<Table> tables = new ArrayList<Table>();
		
		/* LOAD TABLES IN MEMORY */
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			
			String line = "";
			
			Table currentTable;
			
			while((line = bufferedReader.readLine()) != null){
				if(isCreateStatement(line)){
					/* LOAD TABLE IN MEMORY */
					String tableName = getTableName(line);
					//System.out.println("TABLE NAME: " + tableName);
					
					Column[] columns = getColumns(line);
					
					//TEST
					//Utils.printColumns(columns);
					if(tableName != null && columns != null && columns.length > 0){
						currentTable = new Table(tableName, file.getName(), columns);
						tables.add(currentTable);
					}
					
				
					
				}
			}
			
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Find attributes derived from each table */
		return getAttributes(tables, InAppAttributeExtractor.opportunities, dict);
		
	}

	/**
	 * 
	 * @param tables
	 * @param opportunities
	 * @param dict 
	 * @return
	 */
	private static ArrayList<FoundOpportunity> getAttributes(ArrayList<Table> tables,
			HashMap<String, Opportunity> opportunities, IDictionary dict) {
		// TODO Auto-generated method stub
		ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
		
		//attributes.add("TEST_ATTRIBUTE");
		for(Table table : tables){
			//System.out.println("getAttributes: table=" + table.name + ". Num of Columns: " + table.columns.length);
			
			for(Column column : table.columns){
				//System.out.println("getAttributes: columnName=" + column.name);
				//System.out.println("getAttributes: columnType=" + column.type);
				String columnName = column.name;
				
				/* Split column names if necessary to multiple names */
				ArrayList<String> subColumnNames = Utils.getSubNames(columnName);
				for(String subColumnName : subColumnNames){
					String resultOpportunity = "";
					if((opportunities.containsKey(subColumnName))){
						FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.DB, table.database, subColumnName, subColumnName);
						foundOpportunity.setDBdata(table.name, subColumnName);
						attributes.add(foundOpportunity);
					}
					else{
						//check synonyms
						ArrayList<String> colSynonymsAndHypernyms = Utils.getSynonyms(subColumnName, POS.NOUN, dict);
						//ArrayList<String> colSynonymsAndHypernyms = Utils.getHypernyms(subColumnName, POS.NOUN, dict);
						//colSynonymsAndHypernyms.addAll(Utils.getHypernyms(subColumnName, POS.NOUN, dict));
						
						
					
						for(String colSynonymOrHypernym : colSynonymsAndHypernyms){
							//check all opportunities to find matches with this synonym-hypernym
							if((resultOpportunity = Utils.opportunityMatch(colSynonymOrHypernym, opportunities)) != null){
								FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.DB, table.database, resultOpportunity, subColumnName);
								foundOpportunity.setDBdata(table.name, columnName);
								attributes.add(foundOpportunity);
							}
						}
					}
				}
						
			}
		}
		
		return attributes;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private static Column[] getColumns(String line) { 
		int i = 0;
		
		Column[] columns = null;
		//InAppAttributeExtractor.logger.debug(line);
		
		if(line.contains("(") && line.contains(")")){	
			String rawColumns = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
			
			//InAppAttributeExtractor.logger.debug(rawColumns);
			
			String[] strColumns = rawColumns.split(",");
			
			columns = new Column[strColumns.length];
			
			for(int j = 0; j < strColumns.length; j++){
				//get name and type of column
				Column column = new Column();
				
				//InAppAttributeExtractor.logger.debug(strColumns[j]);
				
				if(strColumns[j].contains(" ")){
					column.name = strColumns[j].substring(0, strColumns[j].indexOf(" "));
					column.type = strColumns[j].substring(strColumns[j].indexOf(" ") + 1);
				}
				else{
					column.name = strColumns[j];
					column.type = Preferences.DEFAULT_COL_VALUE;
				}
				
				column.name = Utils.cleanColName(column.name);
				
				columns[i] = column;
				i++;
				
				//System.out.println("\tCOLUMN NAME: " + column.name + ", COLUMN TYPE: " + column.type);
			}
		
		}
		else{
			InAppAttributeExtractor.logger.info("Discarded table create statement: " + line);
		}
		
		return columns;
	}

	/**
	 * Gets the table name given a sqlite CREATE command
	 * @param line
	 * @return
	 */
	private static String getTableName(String line) {
		String name = "";
		
		String[] contents = line.split("\\s+");
		
		return (contents.length >= 3) ? contents[2] : name;
	}

	/**
	 * 
	 * @param line
	 * @return
	 */
	private static boolean isCreateStatement(String command) {
		// TODO Auto-generated method stub
		return (command.startsWith("CREATE")) ? true : false;		
	}
	
	/**
	 * 
	 * @param line
	 * @return
	 */
	private static boolean isInsertStatement(String command) {
		// TODO Auto-generated method stub
		return (command.startsWith("INSERT")) ? true : false;		
	}

	
	/*********************************************************************************************/
	/*********************************************************************************************/
	/*********************************************************************************************/
	/*********************************************************************************************/
	
	/**
	 * 
	 * @param file
	 * @param opportunitiesSynHyperHyponyms
	 * @return
	 */
	public static ArrayList<FoundOpportunity> parse(File file,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		String currentDBname = file.getName().substring(0, file.getName().indexOf('.'));
		/**
		 * Store attributes found in this file
		 */
		//ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
		
		//InAppAttributeExtractor.logger.debug("Parsing database " + currentDBname);
		
		/**
		 * Keep track of db tables
		 */
		ArrayList<Table> tables = new ArrayList<Table>();
		
		/* LOAD TABLES IN MEMORY */
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			
			String line = "";
			
			Table currentTable;
			
			while((line = bufferedReader.readLine()) != null){
				if(isCreateStatement(line)){
					/* LOAD TABLE IN MEMORY */
					String tableName = getTableName(line);
					//System.out.println("TABLE NAME: " + tableName);
					
					Column[] columns = getColumns(line);
					
					//TEST
					//Utils.printColumns(columns);
					if(tableName != null && columns != null && columns.length > 0){
						currentTable = new Table(tableName, file.getName(), columns);
						tables.add(currentTable);
					}
					
				
					
				}
			}
			
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Find attributes derived from each table */
		return getAttributes2(tables, opportunitiesSynHyperHyponyms);
	}
	
	/**
	 * 
	 * @param tables
	 * @param opportunities
	 * @param dict 
	 * @return
	 */
	private static ArrayList<FoundOpportunity> getAttributes2(ArrayList<Table> tables,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		// TODO Auto-generated method stub
		ArrayList<FoundOpportunity> attributes = new ArrayList<FoundOpportunity>();
		
		//attributes.add("TEST_ATTRIBUTE");
		for(Table table : tables){
			//System.out.println("getAttributes: table=" + table.name + ". Num of Columns: " + table.columns.length);
			
			for(Column column : table.columns){
				//System.out.println("getAttributes: columnName=" + column.name);
				//System.out.println("getAttributes: columnType=" + column.type);
				String columnName = column.name;
				
				/* Split column names if necessary to multiple names */
				ArrayList<String> subColumnNames = Utils.getSubNames(columnName);
				for(String subColumnName : subColumnNames){
					//String resultOpportunity = Utils.opportunityKeyMatch(subColumnName, opportunitiesSynHyperHyponyms);
					boolean match = Utils.opportunityKeyMatch2(subColumnName, opportunitiesSynHyperHyponyms);
					if(match && 
							isDBColumnDomainOK(subColumnName, table.name, table.database, opportunitiesSynHyperHyponyms, subColumnName)){
						FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.DB, table.database, subColumnName, subColumnName);
						foundOpportunity.setDBdata(table.name, subColumnName);
						attributes.add(foundOpportunity);
					}
					else{
						
						//check the synonyms, hypernyms and hyponyms of all ops
						for(String op : opportunitiesSynHyperHyponyms.keySet()){
							//System.out.println("Checking Op: " + op + ", and subColumnName: " + subColumnName);
							/*
							if(opportunitiesSynHyperHyponyms.get(op).all == null){
								System.out.println("Null HashSet all for " + op);
							}
							*/
							
							if(Utils.opportunitySynMatch(opportunitiesSynHyperHyponyms.get(op), subColumnName) && 
									isDBColumnDomainOK(subColumnName, table.name, table.database, opportunitiesSynHyperHyponyms, op)){
								//match with this op's syn, hyper or hypo
								FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.DB, table.database, op, subColumnName);
								foundOpportunity.setDBdata(table.name, columnName);
								attributes.add(foundOpportunity);
							}
						}
					}
				}
						
			}
		}
		
		return attributes;
	}

	/**
	 * 
	 * @param columnName
	 * @param tableName
	 * @param databaseName
	 * @param opportunitiesSynHyperHyponyms
	 * @param opportunityName
	 * @return
	 */
	private static boolean isDBColumnDomainOK(String columnName, String tableName,
			String databaseName,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms,
			String opportunityName) {
		
		String opType = opportunitiesSynHyperHyponyms.get(opportunityName).type;
		if(opType == Opportunity.ATTRIBUTE){
		
			//System.out.println(opportunityName);	
			HashSet<String> opDomains = opportunitiesSynHyperHyponyms.get(opportunityName).domains;
	//		String domain = "";
			
	//		for(String d : opDomains){
	//			domain = d;
	//			break;
	//		}
			
			ArrayList<String> tableNames = Utils.getSubNames(tableName);
			double sim = SimilarityCalculator.calculateMaxSim(tableNames, opDomains, opType);
	
			//double sim = SimilarityCalculator.calculateResnik(tableName.trim().toLowerCase(), domain.trim().toLowerCase());
			//if(sim != Preferences.SIMILARITY_DEFAULT && sim > Preferences.LCH_THRESHOLD){
			if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LCH_ATTRIBUTE_THRESHOLD){
				System.out.println("ACCEPTED! columnName =" + columnName + ", tableName = " + tableName + ", opportunity = " + opportunityName 
						+ "opType = " + opType + ", sim = " + sim);
				return true;
			}
			else{
				System.out.println("REJECTED! columnName =" + columnName + ", tableName = " + tableName + ", opportunity = " + opportunityName 
						+ "opType = " + opType + ", sim = " + sim);
				return false;
			}
		
		}
		else{
			//opportunity is interest, check similarity with category
			ArrayList<String> cat_names = Utils.getCatSubNames(MyParser.currentCategory);
			//double sim = SimilarityCalculator.calculateMaxSim(cat_names, opportunityName, opType);
			double sim = SimilarityCalculator.getWeightedLesk(MyParser.currentCategory, opportunityName);
			
			if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LESK_INTEREST_THRESHOLD){
				System.out.println("ACCEPTED! columnName =" + columnName + ", category = " + MyParser.currentCategory + ", opportunity = " + opportunityName 
						+ ", opType = " + opType + ", sim = " + sim);
				return true;
			}
			else{
				System.out.println("REJECTED! columnName =" + columnName + ", category = " + MyParser.currentCategory + ", opportunity = " + opportunityName 
						+ ", opType = " + opType + ", sim = " + sim);
				return false;
			}
			
		}
			
	}

	/**
	 * Checks line by line. Splits the line into words containing only alphabet characters. Splits word if camelCase and check if they match with the opportunities.
	 * @param fileType 
	 * @param file
	 * @param opportunitiesSynHyperHyponyms 
	 * @return
	 */
	public static ArrayList<FoundOpportunity> simpleParse(int fileType, File file, HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		ArrayList<FoundOpportunity> res = new ArrayList<FoundOpportunity>();
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			
			String line = "";
			
			while((line = bufferedReader.readLine()) != null){
				
				if(!isCreateStatement(line)){
					continue;
				}
				
				ArrayList<String> candidateWords = Utils.cleanText(line);
				
				for(String candWord : candidateWords){
					ArrayList<String> subWords = Utils.getSubNames(candWord);
					
					for(String subWord : subWords){
						
						String opportunityName = Utils.opportunityFullMatch(subWord, opportunitiesSynHyperHyponyms);
						
						if(opportunityName.compareTo(Preferences.NOT_AN_OPPORTUNITY) != 0){
							FoundOpportunity fop = new FoundOpportunity(FoundOpportunity.UNKNOWN, file.getName(), opportunityName, candWord);
							res.add(fop);
						}
					}
				}
			}
	
			bufferedReader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}
}
