package phnomden.service.impl;

import com.google.gson.Gson;
import phnomden.common.ErrorCode;
import phnomden.common.QRCashTxnStatusCode;
import phnomden.entity.MtxQrCashDepositWithdrawalEntity;
import phnomden.repository.MtxQrCashDepositWithdrawalRepository;
import phnomden.exception.ApplicationException;
import phnomden.dto.*;
import phnomden.service.QRCashService;
import phnomden.util.ValidatorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRCashServiceImpl implements QRCashService {

	@Autowired
	MtxQrCashDepositWithdrawalRepository mtxQrCashDepositWithdrawalRepository;
	
	private final Gson gson = new Gson();
	private String key;
	
	@Value("${integration.atm-cash-in.url}")
	private String atmCashInUrl;

	@Override
	public ResponseEntity<Data> generateQR(Data reqData) throws Exception {
		key = generateUniqueKey(reqData);
		log.info("{} ============Start Generate QR============", key);
		log.info("{} reqData: {}", key, gson.toJson(reqData));
		try {
			// validate request
			validateGenerateQR(reqData);

			// generate QR data
			String uniqueID = UUID.randomUUID().toString();
			String genType = reqData.getString("genType"); // DEP, WDR
			String qrData = genType.equals("DEP") ? "ATMDEPOSIT-" + uniqueID : "ATMWITHDRAWAL-" + uniqueID;

			// set response
			Data response = new Data();
			response.setString("serviceCode", reqData.getString("serviceCode"));
			response.setString("reqID", reqData.getString("reqID"));
			response.setString("atmID", reqData.getString("atmID"));
			response.setString("genTimeStamp", reqData.getString("genTimeStamp"));
			response.setString("genType", reqData.getString("genType"));
			response.setString("genCurrency", reqData.getString("genCurrency"));
			response.setInt("genAmount", reqData.getInt("genAmount"));
			response.setInt("qrType", 1); // 1:text by default
			response.setString("qrData", qrData);
			response.setString("trxRefNo", uniqueID);
			response.setInt("statusCode", QRCashTxnStatusCode.SUCCESS.getBtiCode());
			response.setString("statusInfo", QRCashTxnStatusCode.SUCCESS.getBtiDescription());

			// save data to db
			saveQRCashTxn(response);
			
			log.info("{} response: {}", key, gson.toJson(response));
			return ResponseEntity.ok(response);
		} catch (ApplicationException ae) {
			log.error("{} Application error: ", key, ae);
			return ResponseEntity.ok(respondGenerateQRError(reqData));
		} catch (Exception e) {
			log.error("{} General error: ", key, e);
			return ResponseEntity.ok(respondGenerateQRError(reqData));
		}
	}

	@Override
	public ResponseEntity<Data> inquireStatus(Data reqData) throws Exception {
		key = generateUniqueKey(reqData);
		log.info("{} ============Start Inquire Status============", key);
		log.info("{} reqData: {}", key, gson.toJson(reqData));
		try {
			// validate request
			validateInquireStatus(reqData);

			// retrieve txn record from db
			Data qrCashTxnInfo = retrieveQRCashTxn(reqData);
			if(qrCashTxnInfo == null) {
				log.error("{} <<<< Error: record not found in database");
				throw new ApplicationException(ErrorCode.DATA_NOT_FOUND);
			}
			log.info("{} data from database: {}", key, gson.toJson(qrCashTxnInfo));

			// set response
			Data response = new Data();
			response.setString("serviceCode", reqData.getString("serviceCode"));
			response.setString("reqID", reqData.getString("reqID"));
			response.setString("atmID", reqData.getString("atmID"));
			response.setString("trxRefNo", reqData.getString("trxRefNo"));
			response.setInt("statusCode", QRCashTxnStatusCode.valueOf(qrCashTxnInfo.getString("qrStatus")).getBtiCode());
			response.setString("statusInfo", QRCashTxnStatusCode.valueOf(qrCashTxnInfo.getString("qrStatus")).getBtiDescription());
			response.setString("trxType", qrCashTxnInfo.getString("genType"));
			response.setString("trxTimeStamp", changeToBtiDateTimeFormat(qrCashTxnInfo.getString("genTimestamp")));
			response.setString("trxAccountNo", qrCashTxnInfo.getString("txnAccount"));
			response.setString("trxCurrency", qrCashTxnInfo.getString("txnCurrency"));
			response.setInt("trxAmount", qrCashTxnInfo.getInt("txnAmount"));
			switch (qrCashTxnInfo.getString("genType")) {
			case "DEP":
				response.setInt("dialyDepositLimitAmount", qrCashTxnInfo.getInt("dailyDepositAmt"));
				response.setInt("dialyDepositLimitAmountRemaining", qrCashTxnInfo.getInt("dailyDepositAmtRemaining"));
				break;
			}
			
			log.info("{} response: {}", key, gson.toJson(response));
			return ResponseEntity.ok(response);
		} catch (ApplicationException ae) {
			log.error("{} Application error: ", key, ae);
			return ResponseEntity.ok(respondInquireStatusError(reqData));
		} catch (Exception e) {
			log.error("{} General error: ", key, e);
			return ResponseEntity.ok(respondInquireStatusError(reqData));
		}
	}

	@Override
	public ResponseEntity<Data> updateStatus(Data reqData) throws Exception {
		key = generateUniqueKey(reqData);
		log.info("{} ============Start Update Status============", key);
		log.info("{} reqData: {}", key, gson.toJson(reqData));
		try {
			// validate request
			validateUpdateStatus(reqData);

			String trxType = reqData.getString("trxType");
			switch (trxType) {
			case "WDR":
					updateQRCashTxnStatus(reqData);
				break;
			case "DEP":
					updateQRCashDepositTxn(reqData);
				break;
			}
			// set response 
			Data response  = new Data();
			response.setString("serviceCode", reqData.getString("serviceCode"));
			response.setString("reqID", reqData.getString("reqID"));
			response.setString("atmID", reqData.getString("atmID"));
			response.setString("trxRefNo", reqData.getString("trxRefNo"));
			response.setInt("statusCode", QRCashTxnStatusCode.SUCCESS.getBtiCode());
			response.setString("statusInfo", QRCashTxnStatusCode.SUCCESS.getBtiDescription());
			response.setString("trxType", reqData.getString("trxType"));
			response.setString("trxTimeStamp", reqData.getString("trxTimeStamp"));
			response.setString("trxAccountNo", reqData.getString("trxAccountNo"));
			response.setString("trxCurrency", reqData.getString("trxCurrency"));
			response.setInt("trxAmount", reqData.getInt("trxAmount"));
			response.setInt("txnStatusCode", reqData.getInt("txnStatusCode"));
			response.setString("txnStatusInfo", reqData.getString("txnStatusInfo"));

			log.info("{} response: {}", key, gson.toJson(response));
			return ResponseEntity.ok(response);
		} catch (ApplicationException ae) {
			log.error("{} Application error: ", key, ae);
			return ResponseEntity.ok(respondUpdateStatusError(reqData));
		} catch (Exception e) {
			log.error("{} General error: ", key, e);
			return ResponseEntity.ok(respondUpdateStatusError(reqData));
		}
	}

	private void updateQRCashDepositTxn(Data reqData) throws Exception {
		if(reqData.getInt("txnStatusCode") == 0) { // successful case from ATM
			callToCashIn(reqData); // posting txn at backend mobile and also update status in db
			updateQRCashTxnStatus(reqData); // this is fake
		} else { // failed case from ATM (txn cancelled or timeout)
			updateQRCashTxnStatus(reqData); // update status in db
		}
	}

	private void updateQRCashTxnStatus(Data reqData) throws Exception {
		// lock and fetch the transaction for update
		MtxQrCashDepositWithdrawalEntity transaction = mtxQrCashDepositWithdrawalRepository.findQRCashTxnForUpdate(
				reqData.getString("reqID"), 
				reqData.getString("atmID"), 
				reqData.getString("trxRefNo"));
		log.info("{} Transaction record from db: {}", key, gson.toJson(transaction));

        if (transaction == null) {
        	log.error("{} <<<< Transaction for update not found!", key);
        	log.info("{} reqID: {}, atmID: {}, trxRefNo: {}", key, reqData.getString("reqID"), reqData.getString("atmID"), reqData.getString("trxRefNo"));
            throw new ApplicationException(ErrorCode.TXN_NOT_FOUND);
        }

        // update transaction
        transaction.setTxnStatusCode(reqData.getString("txnStatusCode"));
        transaction.setTxnStatusInfo(reqData.getString("txnStatusInfo"));
        transaction.setModifiedOn(changeToPSPDateTimeFormat(reqData.getString("trxTimeStamp")));
        mtxQrCashDepositWithdrawalRepository.save(transaction);
	}

	private Data callToCashIn(Data response) throws Exception {
		Data reqPostTxn = new Data();
		reqPostTxn.setString("txn_reference_no", response.getString("trxRefNo"));
		reqPostTxn.setString("request_id", response.getString("reqID"));
		reqPostTxn.setString("wing_account", response.getString("trxAccountNo"));
		reqPostTxn.setBigDecimal("txn_amount", response.getBigDecimal("trxAmount"));
		reqPostTxn.setString("txn_currency", response.getString("trxCurrency"));
		reqPostTxn.setString("atm_id", response.getString("atmID"));
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Accept", "application/json");
		log.info("{} =======Post Transaction at Backend mobile=======", key);
		log.info("{} Request URL: {}", key, atmCashInUrl);
		log.info("{} Request Headers: {}", key, headers);
		log.info("{} Request Data: {}", key, gson.toJson(reqPostTxn));
		String resp = "{\"code\":200,\"message\":\"successful\"}";
		
		Data respPostTxn = gson.fromJson(resp, Data.class);
		if(respPostTxn.getString("code") == null) {
			log.error("{} <<<< Error message: {}", key, respPostTxn.getString("message"));
			throw new ApplicationException(respPostTxn.getString("error_code")); // this error_code not use in response 
		}
		
		return respPostTxn;
	}

	private Data respondUpdateStatusError(Data reqData) throws Exception {
		Data response  = new Data();
		response.setString("serviceCode", reqData.getString("serviceCode"));
		response.setString("reqID", reqData.getString("reqID"));
		response.setString("atmID", reqData.getString("atmID"));
		response.setString("trxRefNo", reqData.getString("trxRefNo"));
		response.setInt("statusCode", QRCashTxnStatusCode.FAILED.getBtiCode());
		response.setString("statusInfo", QRCashTxnStatusCode.FAILED.getBtiDescription());
		response.setString("trxType", reqData.getString("trxType"));
		response.setString("trxTimeStamp", reqData.getString("trxTimeStamp"));
		response.setString("trxAccountNo", reqData.getString("trxAccountNo"));
		response.setString("trxCurrency", reqData.getString("trxCurrency"));
		response.setLong("trxAmount", reqData.getLong("trxAmount"));
		response.setString("txnStatusCode", reqData.getString("txnStatusCode"));
		response.setString("txnStatusInfo", reqData.getString("txnStatusInfo"));

		return response;
	}

	private void validateUpdateStatus(Data reqData) throws Exception, ApplicationException {
		ValidatorUtil.validateRequireFields(reqData, 
				"serviceCode",
				"reqID",
				"atmID",
				"trxRefNo",
				"trxType",
				"trxTimeStamp",
				"trxAccountNo",
				"trxCurrency",
				"trxAmount",
				"txnStatusCode",
				"txnStatusInfo"
//				"cashRetractStatus" saw in doc spec but seem not use.
				);

		int trxAmount = reqData.getInt("trxAmount");
		String trxCurrency = reqData.getString("trxCurrency");
		String serviceCode = reqData.getString("serviceCode");
		String trxType = reqData.getString("trxType");
		
		if (!serviceCode.equals("STATUS_UPDATE")) {
			log.error("{} <<<< Error: serviceCode is invalid. serviceCode: {}", key, serviceCode);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (!(trxType.equals("DEP") || trxType.equals("WDR"))) {
			log.error("{} <<<< Error: trxType is incorrect. trxType: {}", key, trxType);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (trxAmount < 0) {
			log.error("{} <<<< Error: trxAmount is less than zero. genAmount: {}", key, trxAmount);
			throw new ApplicationException( ErrorCode.SERVICE_NOT_ALLOWED );
		}
		
		if (!(trxCurrency.equals("KHR") || trxCurrency.equals("USD"))) {
			log.error("{} <<<< Error: trxCurrency is not allowed. trxCurrency: {}", key, trxCurrency);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
	}

	private void validateGenerateQR(Data reqData) throws Exception {
		ValidatorUtil.validateRequireFields(reqData, 
				"serviceCode",
				"reqID",
				"atmID",
				"genTimeStamp",
				"genType",
				"genAmount",
				"genCurrency"
				);

		String serviceCode = reqData.getString("serviceCode");
		String genCurrency = reqData.getString("genCurrency");
		String genType = reqData.getString("genType");
		int genAmount = reqData.getInt("genAmount");
		
		if (!serviceCode.equals("GENERATE_QR")) {
			log.error("{} <<<< Error: serviceCode is invalid. serviceCode: {}", key, serviceCode);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (!(genType.equals("DEP") || genType.equals("WDR"))) {
			log.error("{} <<<< Error: genType is incorrect. genType: {}", key, genType);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (genAmount < 0) {
			log.error("{} <<<< Error: genAmount is less than zero. genAmount: {}", key, genAmount);
			throw new ApplicationException( ErrorCode.SERVICE_NOT_ALLOWED );
		}
		
		if ( genType.equals("DEP") && genAmount != 0) { // based on bti doc spec
			log.error("{} <<<< Error: genAmount must be zero. genAmount: {}", key, genAmount);
			throw new ApplicationException( ErrorCode.SERVICE_NOT_ALLOWED );
		}
		
		if (!(genCurrency.equals("KHR") || genCurrency.equals("USD"))) {
			log.error("{} <<<< Error: genCurrency is not allowed. genCurrency: {}", key, genCurrency);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
	}
	
	private Data respondGenerateQRError(Data reqData) throws Exception {
		Data response = new Data();
		response.setString("serviceCode", reqData.getString("serviceCode"));
		response.setString("reqID", reqData.getString("reqID"));
		response.setString("atmID", reqData.getString("atmID"));
		response.setString("genTimeStamp", reqData.getString("genTimeStamp"));
		response.setString("genType", reqData.getString("genType"));
		response.setString("genCurrency", reqData.getString("genCurrency"));
		response.setInt("qrType", reqData.getInt("qrType"));
		response.setInt("statusCode", QRCashTxnStatusCode.FAILED.getBtiCode());
		response.setString("statusInfo", QRCashTxnStatusCode.FAILED.getBtiDescription());

		return response;
	}

	private Data respondInquireStatusError(Data reqData) throws Exception {
		Data response = new Data();
		response.setString("serviceCode", reqData.getString("serviceCode"));
		response.setString("reqID", reqData.getString("reqID"));
		response.setString("atmID", reqData.getString("atmID"));
		response.setString("trxRefNo", reqData.getString("trxRefNo"));
		response.setInt("statusCode", QRCashTxnStatusCode.FAILED.getBtiCode());
		response.setString("statusInfo", QRCashTxnStatusCode.FAILED.getBtiDescription());

		return response;
	}

	private void validateInquireStatus(Data reqData) throws Exception {
		ValidatorUtil.validateRequireFields(reqData, "serviceCode", "reqID", "atmID", "trxRefNo");

		String serviceCode = reqData.getString("serviceCode");
		if (!serviceCode.equals("INQUIRY_STATUS")) {
			log.error("{} <<<< Error: serviceCode is invalid. serviceCode: {}", key, serviceCode);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
	}

	private Data retrieveQRCashTxn(Data reqData) throws Exception {
		return mtxQrCashDepositWithdrawalRepository.retrieveQRCashTxn(
				reqData.getString("reqID"),
				reqData.getString("atmID"), 
				reqData.getString("trxRefNo")
				);
	}

	private void saveQRCashTxn(Data response) throws Exception {
		String genType = response.getString("genType");
		
		MtxQrCashDepositWithdrawalEntity transaction = new MtxQrCashDepositWithdrawalEntity();
		transaction.setTxnRefNo(response.getString("trxRefNo"));
		transaction.setReqId(response.getString("reqID"));
		transaction.setAtmId(response.getString("atmID"));
		transaction.setAtmLocation(null);
		transaction.setGenType(genType);
		transaction.setGenAmount( new BigDecimal(response.getString("genAmount")));
		transaction.setGenCurrency(response.getString("genCurrency"));
		transaction.setGenTimestamp(changeToPSPDateTimeFormat(response.getString("genTimeStamp")));
		transaction.setGenLanguage(null);
		transaction.setQrData(response.getString("qrData"));
		transaction.setQrServiceType(genType.equals("DEP") ? "ATMDEPOSIT" : "ATMWITHDRAWAL");
		transaction.setQrStatus(QRCashTxnStatusCode.GENERATED.getWingCode());
		transaction.setTxnAccount(null);
		transaction.setTxnAmount(genType.equals("WDR")? new BigDecimal(response.getString("genAmount")): null); // cash withdrawal, txn_amount based on ATM
		transaction.setTxnCurrency(response.getString("genCurrency")); // txn_amount based on ATM
		transaction.setTxnDate(null);
		transaction.setTxnStatusCode(null);
		transaction.setTxnStatusInfo(null);
		transaction.setDailyDepositAmt(null);
		transaction.setDailyDepositAmtRemaining(null);
		transaction.setCreatedBy(null);
		transaction.setCreatedOn(changeToPSPDateTimeFormat(response.getString("genTimeStamp")));
		transaction.setModifiedBy(null);
		transaction.setModifiedOn(null);
		transaction.setAttr1Name(null);
		transaction.setAttr1Value(null);
		transaction.setAttr2Name(null);
		transaction.setAttr2Value(null);
		transaction.setAttr3Name(null);
		transaction.setAttr3Value(null);
		log.info("{} save to database data: {}", key, gson.toJson(transaction) );
		mtxQrCashDepositWithdrawalRepository.save(transaction);
	}

	private Timestamp changeToPSPDateTimeFormat(String partnerFormat) throws Exception {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime localDateTime = LocalDateTime.parse(partnerFormat, inputFormatter);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		return Timestamp.valueOf(localDateTime.format(outputFormatter));
	}

	private String changeToBtiDateTimeFormat(String pspFormat) throws Exception {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date date = inputFormat.parse(pspFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		return outputFormat.format(date);
	}
	
	private String generateUniqueKey(Data data) throws Exception {
		return data.getString("reqID").replace("-", "");
	}
}