package com.tarento.walletService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarento.walletService.repository.UserWalletRepo;
import com.tarento.walletService.util.Utils;
import com.tarento.walletService.web.models.UserWallet;
import com.tarento.walletService.web.models.WalletType;

@Service
public class UserWalletService {

	@Autowired
	private UserWalletRepo userWalletRepo;

	public UserWallet getByIdAndType(String wid, WalletType walletType) {
		List<UserWallet> userWallet = userWalletRepo.findByWidAndWalletType(wid, walletType);
		if (!Utils.isListEmpty(userWallet)) {
			return userWallet.get(0);
		}

		// TODO - Should through custom exception when the entry not found.
		// Exception handler should return appropriate error response based on custom
		// exception.
		return null;
	}
}
