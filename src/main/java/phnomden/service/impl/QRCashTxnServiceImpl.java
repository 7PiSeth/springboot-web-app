package phnomden.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import phnomden.common.ErrorCode;
import phnomden.common.QRCashTxnStatusCode;
import phnomden.dto.Data;
import phnomden.entity.QRCashTxnEntity;
import phnomden.exception.ApplicationException;
import phnomden.repository.QRCashTxnRepository;
import phnomden.service.QRCashTxnService;
import phnomden.util.ValidatorUtil;

@Service
@Log4j2
public class QRCashTxnServiceImpl implements QRCashTxnService {

	@Autowired
	QRCashTxnRepository qrCashTxnRepository;
	
	private final Gson gson = new Gson();

	@Override
	public ResponseEntity<Data> generateQR(Data reqData) throws Exception {
		String key = generateUniqueKey(reqData);
		log.info("{} ===========Start Generate QR===========", key);
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
			response.setInt("statusCode", QRCashTxnStatusCode.SUCCESS.getCode());
			response.setString("statusInfo", QRCashTxnStatusCode.SUCCESS.getDescription());

			// save data to db
			saveQRCashTxn(response);
			
			log.info("{} response: {}", key, response);
			return ResponseEntity.ok(response);
		} catch (ApplicationException ae) {
			log.error("Application error: ", ae);
			return ResponseEntity.ok(respondGenerateQRError(reqData));
		} catch (Exception e) {
			log.error("General error: ", e);
			return ResponseEntity.ok(respondGenerateQRError(reqData));
		}
	}

	@Override
	public ResponseEntity<Data> inquireStatus(Data reqData) throws Exception {
		String key = generateUniqueKey(reqData);
		log.info("{} ===========Start Inquire Status===========", key);
		log.info("{} reqData: {}", key, gson.toJson(reqData));
		try {
			// validate request
			validateInquireStatus(reqData);

			// retrieve txn record from db
			Data qrCashTxnInfo = retrieveQRCashTxn(reqData);
			if(qrCashTxnInfo == null) {
				log.error("<<<< Error: record not found in database");
				throw new ApplicationException(ErrorCode.DATA_NOT_FOUND);
			}

			// set response
			Data response = new Data();
			response.setString("serviceCode", reqData.getString("serviceCode"));
			response.setString("reqID", reqData.getString("reqID"));
			response.setString("atmID", reqData.getString("atmID"));
			response.setString("trxRefNo", reqData.getString("trxRefNo"));
			response.setInt("statusCode", QRCashTxnStatusCode.valueOf(qrCashTxnInfo.getString("qrStatus")).getCode());
			response.setString("statusInfo", QRCashTxnStatusCode.valueOf(qrCashTxnInfo.getString("qrStatus")).getDescription());
			response.setString("trxType", qrCashTxnInfo.getString("genType"));
			response.setString("trxTimeStamp", changeToPartnerDateTimeFormat(qrCashTxnInfo.getString("genTimestamp")));
			response.setString("trxAccountNo", qrCashTxnInfo.getString("txnAccount"));
			response.setString("trxCurrency", qrCashTxnInfo.getString("txnCurrency"));
			response.setInt("trxAmount", qrCashTxnInfo.getInt("txnAmount"));
			switch (qrCashTxnInfo.getString("genType")) {
			case "DEP":
				response.setInt("dialyDepositLimitAmount", qrCashTxnInfo.getInt("dailyDepositAmt"));
				response.setInt("dialyDepositLimitAmountRemaining", qrCashTxnInfo.getInt("dailyDepositAmtRemaining"));
				break;
			}
			
			log.info("{} response: {}", key, response);
			return ResponseEntity.ok(response);
		} catch (ApplicationException ae) {
			log.error("Application error: ", ae);
			return ResponseEntity.ok(respondInquireStatusError(reqData));
		} catch (Exception e) {
			log.error("General error: ", e);
			return ResponseEntity.ok(respondInquireStatusError(reqData));
		}
	}

	@Override
	public ResponseEntity<Data> updateStatus(Data reqData) throws Exception {
		String key = generateUniqueKey(reqData);
		log.info("{} ===========Start Update Status===========", key);
		log.info("{} reqData: {}", key, gson.toJson(reqData));
		try {
			// validate request
			validateUpdateStatus(reqData);

			// below two cases need to separate them
			// 1. withdrawal
			// 2. deposit

			// call to cashin micro-service, b rathanak in charge
			// after got success -> call to micro-service for pushing notification
			// final step update record in database
			
			// set response 
			Data response  = new Data();
			response.setString("serviceCode", reqData.getString("serviceCode"));
			response.setString("reqID", reqData.getString("reqID"));
			response.setString("atmID", reqData.getString("atmID"));
			response.setString("trxRefNo", reqData.getString("trxRefNo"));
			response.setInt("statusCode", QRCashTxnStatusCode.SUCCESS.getCode());
			response.setString("statusInfo", QRCashTxnStatusCode.SUCCESS.getDescription());
			response.setString("trxType", reqData.getString("trxType"));
			response.setString("trxTimeStamp", reqData.getString("trxTimeStamp"));
			response.setString("trxAccountNo", reqData.getString("trxAccountNo"));
			response.setString("trxCurrency", reqData.getString("trxCurrency"));
			response.setInt("trxAmount", reqData.getInt("trxAmount"));
			response.setInt("txnStatusCode", reqData.getInt("txnStatusCode"));
			response.setString("txnStatusInfo", reqData.getString("txnStatusInfo"));

			log.info("{} response: {}", key, response);
			return ResponseEntity.ok(response);
		} catch (ApplicationException ae) {
			log.error("Application error: ", ae);
			return ResponseEntity.ok(respondUpdateStatusError(reqData));
		} catch (Exception e) {
			log.error("General error: ", e);
			return ResponseEntity.ok(respondUpdateStatusError(reqData));
		}
	}

	private Data respondUpdateStatusError(Data reqData) throws Exception {
		Data response  = new Data();
		response.setString("serviceCode", reqData.getString("serviceCode"));
		response.setString("reqID", reqData.getString("reqID"));
		response.setString("atmID", reqData.getString("atmID"));
		response.setString("trxRefNo", reqData.getString("trxRefNo"));
		response.setInt("statusCode", QRCashTxnStatusCode.FAILED.getCode());
		response.setString("statusInfo", QRCashTxnStatusCode.FAILED.getDescription());
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

		String trxCurrency = reqData.getString("trxCurrency");
		String serviceCode = reqData.getString("serviceCode");
		String trxType = reqData.getString("trxType");
		
		if (!serviceCode.equals("STATUS_UPDATE")) {
			log.error("<<<< Error: serviceCode is invalid. serviceCode: {}", serviceCode);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (!(trxType.equals("DEP") || trxType.equals("WDR"))) {
			log.error("<<<< Error: trxType is incorrect. trxType: {}", trxType);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (!(trxCurrency.equals("KHR") || trxCurrency.equals("USD"))) {
			log.error("<<<< Error: trxCurrency is not allowed. trxCurrency: {}", trxCurrency);
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
		
		if (!serviceCode.equals("GENERATE_QR")) {
			log.error("<<<< Error: serviceCode is invalid. serviceCode: {}", serviceCode);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (!(genType.equals("DEP") || genType.equals("WDR"))) {
			log.error("<<<< Error: genType is incorrect. genType: {}", genType);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
		
		if (!(genCurrency.equals("KHR") || genCurrency.equals("USD"))) {
			log.error("<<<< Error: genCurrency is not allowed. genCurrency: {}", genCurrency);
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
		response.setInt("statusCode", QRCashTxnStatusCode.FAILED.getCode());
		response.setString("statusInfo", QRCashTxnStatusCode.FAILED.getDescription());

		return response;
	}

	private Data respondInquireStatusError(Data reqData) throws Exception {
		Data response = new Data();
		response.setString("serviceCode", reqData.getString("serviceCode"));
		response.setString("reqID", reqData.getString("reqID"));
		response.setString("atmID", reqData.getString("atmID"));
		response.setString("trxRefNo", reqData.getString("trxRefNo"));
		response.setInt("statusCode", QRCashTxnStatusCode.FAILED.getCode());
		response.setString("statusInfo", QRCashTxnStatusCode.FAILED.getDescription());

		return response;
	}

	private void validateInquireStatus(Data reqData) throws Exception {
		ValidatorUtil.validateRequireFields(reqData, "serviceCode", "reqID", "atmID", "trxRefNo");

		String serviceCode = reqData.getString("serviceCode");
		if (!serviceCode.equals("INQUIRY_STATUS")) {
			log.error("<<<< Error: serviceCode is invalid. serviceCode: {}", serviceCode);
			throw new ApplicationException(ErrorCode.SERVICE_NOT_ALLOWED);
		}
	}

	private Data retrieveQRCashTxn(Data reqData) throws Exception {
		return qrCashTxnRepository.retrieveQRCashTxn(
				reqData.getString("reqID"),
				reqData.getString("atmID"), 
				reqData.getString("trxRefNo")
				);
	}

	private void saveQRCashTxn(Data response) throws Exception {
		String genType = response.getString("genType");
		
		QRCashTxnEntity transaction = new QRCashTxnEntity();
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
		transaction.setQrStatus(QRCashTxnStatusCode.GENERATED.getDescription());
		transaction.setTxnAccount(null);
		transaction.setTxnAmount(null);
		transaction.setTxnCurrency(null);
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
		qrCashTxnRepository.save(transaction);
	}

	private Timestamp changeToPSPDateTimeFormat(String partnerFormat) throws Exception {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime localDateTime = LocalDateTime.parse(partnerFormat, inputFormatter);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		return Timestamp.valueOf(localDateTime.format(outputFormatter));
	}

	private String changeToPartnerDateTimeFormat(String pspFormat) throws Exception {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date date = inputFormat.parse(pspFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		return outputFormat.format(date);
	}
	
	private String generateUniqueKey(Data data) throws Exception {
		return data.getString("reqID").replace("-", "");
	}
}