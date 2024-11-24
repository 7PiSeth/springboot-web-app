package phnomden.common;

public enum QRCashTxnStatusCode {
    GENERATED(2, "GENERATED"),
    SUCCESS(0, "SUCCESS"),
    COMPLETED(0, "COMPLETED"),
    FAILED(1, "FAILED");

    private final int code;
    private final String description;

    QRCashTxnStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static QRCashTxnStatusCode fromCode(int code) {
        for (QRCashTxnStatusCode status : QRCashTxnStatusCode.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unexpected code: " + code);
    }

    public static QRCashTxnStatusCode fromDescription(String description) {
        for (QRCashTxnStatusCode status : QRCashTxnStatusCode.values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unexpected description: " + description);
    }
}