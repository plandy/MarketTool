package utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import applicationConstants.StringConstants;

public final class DateUtility {
	
	private DateUtility(){
		
	}
	
	public static Date parseStringToDate( String p_dateString ) {
		
		Date l_parsedDate = null;
		
		DateFormat dateFormat = new SimpleDateFormat( StringConstants.DATE_FORMAT );
		try {
			l_parsedDate = dateFormat.parse( p_dateString );
		} catch (ParseException e) {
			throw new RuntimeException("error parsing date");
		}
		
		return l_parsedDate;
	}
	
	public static String parseDateToString ( Date p_date ) {
		String l_parsedDate = null;
		
		DateFormat dateFormat = new SimpleDateFormat( StringConstants.DATE_FORMAT );
		l_parsedDate = dateFormat.format( p_date );
		
		return l_parsedDate;
	}
	
	public static Date getTodayDate() {
		return Date.from( ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/New_York")).toInstant() );
	}

}
