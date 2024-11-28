package phnomden.controller;

import phnomden.dto.Data;
import phnomden.service.QRCashService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qrcode-cash/api/v1")
public class QRCashDepositWithdrawalController {
	
	@Autowired
	QRCashService qrCashService;

	@PostMapping("/generate-qr")
	public ResponseEntity<Data> generateQR(@RequestBody Data reqData) throws Exception {
		return qrCashService.generateQR(reqData);
	}

	@PostMapping("/inquire-status")
	public ResponseEntity<Data> inquireStatus(@RequestBody Data reqData) throws Exception {
		return qrCashService.inquireStatus(reqData);
	}

	@PostMapping("/update-status")
	public ResponseEntity<Data> updateStatus(@RequestBody Data reqData) throws Exception {
		return qrCashService.updateStatus(reqData);
	}
}