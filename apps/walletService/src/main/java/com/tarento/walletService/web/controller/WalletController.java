package com.tarento.walletService.web.controller;

import com.tarento.walletService.service.UserCoinService;
import com.tarento.walletService.service.WalletLedgerService;
import com.tarento.walletService.web.models.WalletLedger;
import com.tarento.walletService.web.models.WalletLedgerResponse;
import com.tarento.walletService.web.models.WalletType;
import com.tarento.walletService.web.models.communication.RequestInfo;
import com.tarento.walletService.web.models.communication.UserCoinRequestInfo;
import com.tarento.walletService.web.models.communication.UserCoinResponseInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/")
public class WalletController {
	Logger logger = LogManager.getLogger(WalletController.class);

	@Autowired
	private WalletLedgerService ledgerService;

	@Autowired
	private UserCoinService userCoinService;

	@RequestMapping(value = "/ledgerOfCoins", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<WalletLedgerResponse> ledgerOfCoins(@RequestBody RequestInfo requestInfo,
			@RequestParam("wids") List<String> wids, @RequestParam("from_date") Long fromDate,
			@RequestParam("to_date") Long toDate) {
		logger.debug("LedgerOfCoins API...");
		WalletLedgerResponse response = null;

		// TODO - Need to call service layer and construct the response object properly.
		List<WalletLedger> walletLedger = ledgerService.getWalletLedgerDetails(wids.get(0),
				WalletType.fromValue("Manager"), fromDate, toDate);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/coins", produces = "application/json")
	public ResponseEntity<List<UserCoinResponseInfo>> getUsersCoin(@RequestHeader("wid") String wid,
																   @RequestBody UserCoinRequestInfo userCoinRequestInfo) throws Exception {
		return new ResponseEntity<>(userCoinService.getUsersCoins(wid, userCoinRequestInfo), HttpStatus.OK);
	}
}
