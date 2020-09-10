package com.tarento.walletService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarento.walletService.web.models.UserWallet;
import com.tarento.walletService.web.models.WalletType;

@Repository
public interface UserWalletRepo extends JpaRepository<UserWallet, String> {

	List<UserWallet> findByWidAndWalletType(String wid, WalletType walletType);
}
