package com.tarento.walletService.repository;

import com.tarento.walletService.web.models.WalletInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletInfoRepo extends JpaRepository<WalletInfo, String> {

}
