package com.tarento.walletService.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tarento.walletService.models.RequestInfo;
import com.tarento.walletService.models.WalletLedgerResponse;

@RestController
@RequestMapping("/v1/")
public class WalletController {
	@RequestMapping(value = "/ledgerOfCoins", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<WalletLedgerResponse> ledgerOfCoins(@RequestBody RequestInfo requestInfo) {
		WalletLedgerResponse response = null;

		// TODO - Need to call service layer and construct the response object properly.

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
