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
	public ResponseEntity<Data> generateQR(@RequestBody Data request) throws Exception {
		return qrCashService.generateQR(request);
	}

	@PostMapping("/inquire-status")
	public ResponseEntity<Data> inquireStatus(@RequestBody Data request) throws Exception {
		return qrCashService.inquireStatus(request);
	}

	@PostMapping("/update-status")
	public ResponseEntity<Data> updateStatus(@RequestBody Data request) throws Exception {
		return qrCashService.updateStatus(request);
	}
}