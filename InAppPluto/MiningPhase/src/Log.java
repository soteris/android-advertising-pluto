import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author soteris
 *
 */
public class Log {

	private String loggingFilename;

	public Log(String loggingFilename) {
		this.loggingFilename = loggingFilename;
	}

	/**
	 * 
	 * @param string
	 */
	public void debug(String message) {
		String debugMessage = "DEBUG: " + message;
		//System.out.println(debugMessage);
		
		logMessage(debugMessage);
		
	}
	
	/**
	 * 
	 * @param string
	 */
	public void error(String message) {
		String errorMessage = "ERROR: " + message;
		//System.out.println(errorMessage);
		
		logMessage(errorMessage);
		
	}
	
	/**
	 * 
	 * @param string
	 */
	public void info(String message) {
		String infoMessage = "INFO: " + message;
		//System.out.println(infoMessage);
		
		logMessage(infoMessage);
		
	}

	/**
	 * 
	 * @param message
	 */
	private void logMessage(String message) {
		// TODO Auto-generated method stub
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File(this.loggingFilename), true /* append = true */));
			
			writer.println(message);
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
