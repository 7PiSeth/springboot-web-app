package phnomden.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "QR_CASH_TXN",  schema = "PUBLIC")
@Setter
@Getter
public class QRCashTxnEntity {
	@Id
	@Column(name = "TXN_REF_NO", length = 36, nullable = false)
	private String txnRefNo;

	@Column(name = "REQ_ID", length = 36, nullable = false)
	private String reqId;

	@Column(name = "ATM_ID", length = 20, nullable = false)
	private String atmId;

	@Column(name = "ATM_LOCATION", length = 255)
	private String atmLocation;

	@Column(name = "GEN_TYPE", length = 20, nullable = false)
	private String genType;

	@Column(name = "GEN_AMOUNT", precision = 25, scale = 10)
	private BigDecimal genAmount;

	@Column(name = "GEN_CURRENCY", length = 3, nullable = false)
	private String genCurrency;

	@Column(name = "GEN_TIMESTAMP", nullable = false)
	private Timestamp genTimestamp;

	@Column(name = "GEN_LANGUAGE", length = 20)
	private String genLanguage;

	@Column(name = "QR_DATA", length = 255, nullable = false)
	private String qrData;

	@Column(name = "QR_SERVICE_TYPE", length = 20, nullable = false)
	private String qrServiceType;

	@Column(name = "QR_STATUS", length = 20, nullable = false)
	private String qrStatus;

	@Column(name = "TXN_ACCOUNT", length = 20)
	private String txnAccount;

	@Column(name = "TXN_AMOUNT", precision = 25, scale = 10)
	private BigDecimal txnAmount;

	@Column(name = "TXN_CURRENCY", length = 3)
	private String txnCurrency;

	@Column(name = "TXN_DATE")
	private Timestamp txnDate;

	@Column(name = "TXN_STATUS_CODE", length = 20)
	private String txnStatusCode;

	@Column(name = "TXN_STATUS_INFO", length = 255)
	private String txnStatusInfo;

	@Column(name = "DAILY_DEPOSIT_AMT", precision = 25, scale = 10)
	private BigDecimal dailyDepositAmt;

	@Column(name = "DAILY_DEPOSIT_AMT_REMAINING", precision = 25, scale = 10)
	private BigDecimal dailyDepositAmtRemaining;

	@Column(name = "CREATED_BY", length = 30)
	private String createdBy;

	@Column(name = "CREATED_ON", nullable = false)
	private Timestamp createdOn;

	@Column(name = "MODIFIED_BY", length = 30)
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name = "ATTR_1_NAME", length = 255)
	private String attr1Name;

	@Column(name = "ATTR_1_VALUE", length = 255)
	private String attr1Value;

	@Column(name = "ATTR_2_NAME", length = 255)
	private String attr2Name;

	@Column(name = "ATTR_2_VALUE", length = 255)
	private String attr2Value;

	@Column(name = "ATTR_3_NAME", length = 255)
	private String attr3Name;

	@Column(name = "ATTR_3_VALUE", length = 255)
	private String attr3Value;
}