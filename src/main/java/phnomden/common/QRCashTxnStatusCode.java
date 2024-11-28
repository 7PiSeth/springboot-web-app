package phnomden.common;

public enum QRCashTxnStatusCode {
    GENERATED(2, "pending", "GENERATED"),
    SUCCESS(0, "successful", "SUCCESS"),
    COMPLETED(0, "successful", "COMPLETED"),
    FAILED(1, "failed", "FAILED"),
    CANCELLED(3, "customer cancelled on QR scanning screen", "CANCELLED"),
    TIMEOUT (4, "customer timeout on QR scanning screen", "TIMEOUT");

    private final int btiCode;
    private final String btiDescription;
    private final String wingCode;

    QRCashTxnStatusCode(int btiCode, String btiDescription, String wingCode) {
        this.btiCode = btiCode;
        this.btiDescription = btiDescription;
        this.wingCode = wingCode;
    }

    public int getBtiCode() {
        return btiCode;
    }

    public String getBtiDescription() {
        return btiDescription;
    }

    public String getWingCode() {
        return wingCode;
    }

    public static QRCashTxnStatusCode fromBtiCode(int btiCode) {
        for (QRCashTxnStatusCode status : values()) {
            if (status.btiCode == btiCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid btiCode: " + btiCode);
    }

    public static QRCashTxnStatusCode fromWingCode(String wingCode) {
        for (QRCashTxnStatusCode status : values()) {
            if (status.wingCode.equalsIgnoreCase(wingCode)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid wingCode: " + wingCode);
    }

    @Override
    public String toString() {
        return String.format("QRCashTxnStatusCode{btiCode=%d, btiDescription='%s', wingCode='%s'}",
                btiCode, btiDescription, wingCode);
    }
}
