package com.tarento.walletService.repository;

import com.tarento.walletService.web.models.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWalletRepo extends JpaRepository<UserWallet, String> {

}
