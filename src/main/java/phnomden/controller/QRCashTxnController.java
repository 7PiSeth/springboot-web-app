package phnomden.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import phnomden.dto.Data;
import phnomden.service.QRCashTxnService;

@RestController
@RequestMapping("qrcode-cash/api/v1/")
@Log4j2
public class QRCashTxnController {
	
	@GetMapping("welcome")
	public String greeting(@RequestBody Data req) throws Exception {
		log.info("Request Data: {}", req);
		return "Welcome to the homepage.";
	}
	
	@Autowired
	QRCashTxnService qrCashTxnService;

	@PostMapping("/generate-qr")
	public ResponseEntity<Data> generateQR(@RequestBody Data reqData) throws Exception {
		return qrCashTxnService.generateQR(reqData);
	}

	@PostMapping("/inquire-status")
	public ResponseEntity<Data> inquireStatus(@RequestBody Data reqData) throws Exception {
		return qrCashTxnService.inquireStatus(reqData);
	}

	@PostMapping("/update-status")
	public ResponseEntity<Data> updateStatus(@RequestBody Data reqData) throws Exception {
		return qrCashTxnService.updateStatus(reqData);
	}

}