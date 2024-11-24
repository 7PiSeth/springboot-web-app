package phnomden.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import phnomden.dto.Data;

@Service
public interface QRCashTxnService {
	ResponseEntity<Data> generateQR(Data reqData) throws Exception;
	ResponseEntity<Data> inquireStatus(Data reqData) throws Exception;
	ResponseEntity<Data> updateStatus(Data reqData) throws Exception;
}
