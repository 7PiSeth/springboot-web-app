package phnomden.service;

import phnomden.dto.Data;
import org.springframework.http.ResponseEntity;

public interface QRCashService {
	ResponseEntity<Data> generateQR(Data reqData) throws Exception;
	ResponseEntity<Data> inquireStatus(Data reqData) throws Exception;
	ResponseEntity<Data> updateStatus(Data reqData) throws Exception;
}