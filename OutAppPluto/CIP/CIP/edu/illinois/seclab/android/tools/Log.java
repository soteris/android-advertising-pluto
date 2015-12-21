package edu.illinois.seclab.android.tools;

/**
 * 
 * @author soteris
 *
 */
public class Log {
	
	public static void info(String message) {
		System.out.println(message);		
	}

	public static void debug(String message) {
		System.out.println("DEBUG: " + message);	
	}

	public static void error(String message) {
		System.out.println("ERROR: " + message);		
	}

	public static void warning(String message) {
		System.out.println("WARNING: " + message);		
	}
}
