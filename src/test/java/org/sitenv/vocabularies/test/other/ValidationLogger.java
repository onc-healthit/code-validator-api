package org.sitenv.vocabularies.test.other;

public class ValidationLogger {
	
	public static boolean logResults = true;
	
	public static void println() {
		if (logResults)
			System.out.println();
	}

	public static void println(String message) {
		print(message);
		println();
	}

	public static void print(String message) {
		if (logResults)
			System.out.print(message);
	}
	
}
