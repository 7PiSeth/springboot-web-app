package phnomden.common.enums;

public enum AppCodeMessageEnum {
    SUCCESS("200", "Success"),
    NO_BILLER_HANDLER("00324", "No biller handler found"),
    BILLER_NOT_FOUND("000038", "Biller not found"),
    BILLER_SUSPENDED("00586", "Biller is suspended"),
    ACCOUNT_NUMBER_INVALID("00092", "Invalid account number"),
    ACCOUNT_TYPE_INVALID("ERC003", "Invalid account type"),
    ERROR_CONNECTION("000032", "Connection error"),
    ERROR_PROCESSING_REQUEST("00162", "Error processing request"),
    EXTERNAL_ERROR("APPE0219", "External system error"),
    GENERAL_FAIL_EXCEPTION("000030", "General failure exception"),
    INTERNAL_ERROR("E108", "Internal system error"),
    INVALID_AMOUNT("006034", "Invalid amount"),
    INVALID_CURRENCY("00425", "Invalid currency"),
    INVALID_DATA("0014", "Invalid data"),
    INVALID_VALUE_INPUT("000073", "Invalid input value"),
    PAGE_NOT_FOUND("00324", "Page not found"),
    SERVICE_NOT_ALLOWED("RMSNA", "Service not allowed"),
    SERVICE_UNAVAILABLE("V542", "Service unavailable"),
    SOCKET_ERROR("000032", "Socket error"),
    SOCKET_TIMEOUT("000032", "Socket timeout"),
    TRANSACTION_NOT_FOUND("TXN404", "Transaction not found"),
    TRANSACTION_TIMEOUT("10054", "Transaction timeout"),
    TRANSFER_VALUE_INVALID("00014", "Transfer value less than or equal to zero"),
    UNKNOWN_HOST("00023", "Unknown host"),
    UNKNOWN_PROTOCOL("00324", "Unknown protocol");

    private final String code;
    private final String message;

    AppCodeMessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByCode(String errCode) {
        for (AppCodeMessageEnum error : values()) {
            if (error.getCode().equals(errCode)) {
                return error.getMessage();
            }
        }
        return "Unknown error code"; // Default message for unknown error codes
    }
}
