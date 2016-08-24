package database.sqlite;

public class Tables {
	
	public static final String DROP_PRICEHISTORY = "drop table if exists PRICEHISTORY ";
	public static final String CREATE_PRICEHISTORY = "create table PRICEHISTORY "
			+ "(TICKER string not null, "
			+ "DATE string not null, "
			+ "OPENPRICE integer, "
			+ "HIGHPRICE integer, "
			+ "LOWPRICE integer, "
			+ "CLOSEPRICE integer, "
			+ "VOLUME integer, "
			+ "primary key(TICKER, DATE) )";
	
}
