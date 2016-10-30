package database.sqlite.procedures;

public class ProcedureDefinitions {
	public static final String I_LISTEDSTOCKS = "insert into LISTEDSTOCKS "
			+ "(TICKER, "
			+ "FULLNAME) "
			+ "values (?, ?)";
	
	public static final String GET_ALL_LISTEDSTOCKS = "select TICKER, "
			+ "FULLNAME "
			+ "from LISTEDSTOCKS";
	
	public static final String I_PRICEHISTORY = "insert into PRICEHISTORY "
			+ "(TICKER, "
			+ "DATE, "
			+ "OPENPRICE, "
			+ "HIGHPRICE, "
			+ "LOWPRICE, "
			+ "CLOSEPRICE, "
			+ "VOLUME) "
			+ "values (?,?,?,?,?,?,?) ";
	
	public static final String S_PRICEHISTORY = "select TICKER, "
			+ "DATE, "
			+ "OPENPRICE, "
			+ "HIGHPRICE, "
			+ "LOWPRICE, "
			+ "CLOSEPRICE, "
			+ "VOLUME "
			+ "from PRICEHISTORY "
			+ "where TICKER = ? "
			+ "and date(DATE) > ? "
			+ "and date(DATE) < ? "
			+ "order by DATE asc ";
	
	public static final String GET_MOSTRECENT_PRICEHISTORY_DATE = "select max(DATE) as DATE from PRICEHISTORY where TICKER = ? ";
	
	public static final String I_DATAREQUESTHISTORY = "insert into DATAREQUESTHISTORY "
			+ "(TICKER, "
			+ "REQUESTDATE) "
			+ "values (?,?)";
	
	public static final String GET_MOSTRECENT_DATAREQUEST_DATE = "select max(REQUESTDATE) as REQUESTDATE from DATAREQUESTHISTORY where TICKER = ? ";

}