package utility.logger;

public class Log {
	
	private static final String DEBUG = "[DEBUG] ";
	private static final String ERROR = "[ERROR] ";
	private static final String INFO = "[INFO] ";
	
	public static void debug( String p_message ) {
		System.out.println( DEBUG + p_message );
	}
	
	public static void error( String p_message ) {
		System.out.println( ERROR + p_message );
	}
	
	public static void info( String p_message ) {
		System.out.println( INFO + p_message );
	}

}
