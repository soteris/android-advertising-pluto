
/**
 * DB Table: has String name and String[] columns
 * @author soteris
 *
 */
public class Table {

	public String name;
	public String database;
	public Column[] columns = null;
	
	
	public Table(String name, String database){
		this.database = database;
		this.name = name;
	}
	
	public Table(String name, String database, Column[] columns){
		this.database = database;
		this.name = name;
		this.columns = columns;
	}
}
