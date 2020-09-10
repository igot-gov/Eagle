package com.tarento.walletService.service;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.walletService.repository.WalletLedgerRepo;
import com.tarento.walletService.util.Utils;
import com.tarento.walletService.web.models.UserWallet;
import com.tarento.walletService.web.models.WalletLedger;
import com.tarento.walletService.web.models.WalletType;

@Service
public class WalletLedgerService {

	Logger logger = LogManager.getLogger(WalletLedgerService.class);

	@Autowired
	private UserWalletService userWalletService;

	@Autowired
	private WalletLedgerRepo ledgerRepo;

	public List<WalletLedger> getWalletLedgerDetails(String wid, WalletType walletType, long fromDate, long toDate) {
		// First get the UserWalletId details
		UserWallet userWallet = userWalletService.getByIdAndType(wid, walletType);
		if (userWallet != null) {
			List<WalletLedger> walletLedger = ledgerRepo.findLedgerDetails(userWallet.getId(), fromDate, toDate);
			if (!Utils.isListEmpty(walletLedger)) {
				logger.debug("Retrieved {} WalletLedger records.", walletLedger.size());
				walletLedger.forEach((k -> {
					logger.debug(k.toString());
				}));
			}
		}

		return Collections.emptyList();
	}
}
