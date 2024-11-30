package phnomden.service;

import phnomden.dto.Data;
import org.springframework.http.ResponseEntity;

public interface QRCashService {
	ResponseEntity<Data> generateQR(Data request) throws Exception;
	ResponseEntity<Data> inquireStatus(Data request) throws Exception;
	ResponseEntity<Data> updateStatus(Data request) throws Exception;
}