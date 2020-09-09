package com.tarento.walletService.repository;

import com.tarento.walletService.web.models.WalletLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletLedgerRepo extends JpaRepository<WalletLedger, String> {

}
