package utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import applicationConstants.StringConstants;

public class DateUtility {
	
	public static Date parseStringToDate( String p_dateString ) {
		
		Date l_parsedDate = null;
		
		DateFormat dateFormat = new SimpleDateFormat( StringConstants.DATE_FORMAT );
		try {
			l_parsedDate = dateFormat.parse( p_dateString );
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("error parsing date");
		}
		
		return l_parsedDate;
	}

}
