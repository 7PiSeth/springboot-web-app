package phnomden.common;

public interface ErrorCode {
	
	public static final String GENERAL_FAIL = "000030";
	public static final String TXN_NOT_FOUND = "006035";
	public static final String PARTY_NOT_FOUND = "00066";
	public static final String INVALID_BILL_NUMBER = "00292";
	public static final String NOT_MERCHANT_ACCOUNT = "APPE0054";
	public static final String PAYEE_SUSPEND = "006007";
	public static final String PAYEE_INACTIVE = "00126";
	public static final String PAYEE_BLOCKED = "003170";
	public static final String INVALID_LATIN_EN = "000075";
	public static final String OPERATOR_NOT_FOUND = "000076";
	public static final String MNO_SUSPEND = "02191";
	public static final String INVALID_BANK = "E0228";
	public static final String LOAN_SUBMITTED = "MG00007";
	public static final String PAYEE_NON_KYC = "000077";
	public static final String WRONG_PASSWORD = "00016";
	public static final String INVALID_PHONE_NO_REF = "00970";
	public static final String ERROR_SERVICE_NOT_ALLOW_FOR_CST = "12243";
	public static final String SERVICEKEYWORD_SUSPEND = "00055";
	public static final String ERROR_INTERNAL_CONFIG = "E105";
	public static final String INVALID_TOKEN = "APPE0001";
	public static final String BLACKLIST_NOT_FOUND = "BE004";
	public static final String FUND_RAISING_NOT_EXIST = "API003";
	public static final String VEHICLE_NO_ALREADY_USED = "API004";
	public static final String TOKEN_EXPIRED = "0002";
	public static final String SANCTION_USER = "E0230";
	public static final String SERVICE_NOT_ALLOWED = "006047";
	public static final String SCHEDULE_EXISTS = "E0440";
	public static final String SCHEDULE_NOT_EXISTS = "E0441";
	public static final String INVALID_PATIENT_NAME = "E0442";
	public static final String EXISTING_LOAN_PENDING = "E0443";
	public static final String NO_RECHARGE_VOUCHER_FOUND = "000033";
	public static final String INVALID_MSISDN_NUMBER = "002130";
	public static final String FAILED_CBC_REQ = "E0449";
	public static final String FEE_REQUIRED = "E0444";
	public static final String AMOUNT_REQUIRED = "E0445";
	public static final String ACCOUNT_SUSPEND = "006008";
	public static final String ACCOUNT_BLOCKED = "02117";
	public static final String EXISTING_LOAN_ACTIVE = "E0446";
	public static final String DATA_NOT_FOUND = "00599";	
	
}